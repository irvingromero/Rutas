package com.irv.rutas

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.ListView
import android.widget.ArrayAdapter
import android.widget.AdapterView
import android.widget.Toast
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.irv.rutas.Rutas.Eje
import com.irv.rutas.Rutas.Nacionalista
import com.irv.rutas.Rutas.Ruta9
import kotlinx.android.synthetic.main.activity_maps.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var miUbicacion: Location? = null
    private var chipgroup: ChipGroup? = null
    private var btnUbicacion : FloatingActionButton? = null
    private var fbaZoomMenos : FloatingActionButton? = null
    private var fbaZoomMas : FloatingActionButton? = null
    private var efabRutas : ExtendedFloatingActionButton? = null
    private var efabPunto : ExtendedFloatingActionButton? = null
    private var listaRutas : ArrayList<String>? = null
    private var rutaSeleccionada : ArrayList<String> = ArrayList()
    private var points: ArrayList<LatLng>? = null
    private var polylineOptions: PolylineOptions? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        supportActionBar?.hide()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        chipgroup = findViewById(R.id.cg_MapsActivity)
        btnUbicacion = findViewById(R.id.btnUbicacion)
        fbaZoomMenos = findViewById(R.id.fabZoomMenos)
        fbaZoomMas = findViewById(R.id.fabZoomMas)
        efabRutas = findViewById(R.id.btnRutas_maps)
        efabPunto = findViewById(R.id.efabPunto)

        ///// RUTAS /////
        listaRutas = ArrayList()
        listaRutas?.add("Ruta 9")
        listaRutas?.add("Nacionalista")
        listaRutas?.add("Eje")
        listaRutas?.sort() //ORDENA ALFABETICAMENTE //
    }

    override fun onStart() {
        super.onStart()

        efabRutas?.setOnClickListener {
            ventanaRutas()
        }

        efabPunto?.setOnClickListener {
            startActivity(Intent(this, Puntos::class.java))
        }

        btnUbicacion?.setOnClickListener {
            camaraAubicacion()
        }

        fabZoomMenos?.setOnClickListener {
            mMap.animateCamera(CameraUpdateFactory.zoomTo(mMap.cameraPosition.zoom - 0.9f))
        }

        fabZoomMas?.setOnClickListener {
            mMap.animateCamera(CameraUpdateFactory.zoomTo(mMap.cameraPosition.zoom + 0.9f))
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isMyLocationButtonEnabled = false
        mMap.uiSettings.isZoomControlsEnabled = false
        mMap.uiSettings.isCompassEnabled = true
        mMap.setMinZoomPreference(11.0f)

        permiso()
        camaraAubicacion()

        mMap.setOnMapClickListener {
            if(btnUbicacion?.visibility == View.VISIBLE){
                btnUbicacion?.hide()
                fabZoomMenos.hide()
                fabZoomMas.hide()
                efabRutas?.hide()
                efabPunto?.hide()
            }

            if(btnUbicacion?.visibility == View.GONE){
                btnUbicacion?.show()
                fabZoomMenos.show()
                fabZoomMas.show()
                efabRutas?.show()
                efabPunto?.show()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun permiso() {
        val permisoActivado: Boolean

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permisoActivado = estadoPermisoUbicacion()

            if (permisoActivado == false) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    //// MIUESTRA EL DIALOG PARA EL PERMISO ////
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 10)
                }
            } else { // PERMISO YA DADO
                mMap.isMyLocationEnabled = true
                camaraAubicacion()
            }
        } else {// VERSION MENOR A 6.0
            mMap.isMyLocationEnabled = true
            camaraAubicacion()
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == 10) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mMap.isMyLocationEnabled = true
            }
        }
    }

    private fun estadoPermisoUbicacion(): Boolean {
        val resultado = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        return resultado == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun camaraAubicacion() {
        val locationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val criteria = Criteria()
        miUbicacion = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false))
        try {
            val latitude = miUbicacion?.latitude
            val longitud = miUbicacion?.longitude

            if(latitude != null && longitud != null){
                val latlog = LatLng(latitude, longitud)
                val cu = CameraUpdateFactory.newLatLng(latlog)
                mMap.animateCamera(cu)
                mMap.setMinZoomPreference(11.0f)
            } else {
                Snackbar.make(findViewById(R.id.map), "Ubicacion no disponible", Snackbar.LENGTH_LONG).show()
            }
        } catch (e: Exception) { }
    }

    private fun ventanaRutas() {
        val ventana = AlertDialog.Builder(this, R.style.CustomDialogTheme)
        // CARGA EL LAYOUT PERSONALIZADO//
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.ventana_rutas, null)
        ventana.setView(dialogView)
        ventana.setTitle(R.string.rutasDisponibles_str)

        val dialog: AlertDialog = ventana.create()

        val listaview = dialogView.findViewById<ListView>(R.id.lvRutas)
        val a = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listaRutas!!)
        listaview.adapter = a

        dialog.show()

        listaview.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val ruta = parent.getItemAtPosition(position)
            dialog.dismiss()
            chipRuta(ruta.toString())
            seleccionRuta(ruta.toString())
        }
    }

    private fun chipRuta(ruta: String) {
        val i = LayoutInflater.from(this)
        val chipItem = i.inflate(R.layout.chip, null, false) as Chip

        if(!rutaSeleccionada.contains(ruta)){ // VALIDA QUE NO ESTE SELECCIONADA LA RUTA //
            rutaSeleccionada.add(ruta)

            chipItem.text = ruta
            chipgroup?.addView(chipItem)
            chipgroup?.visibility = View.VISIBLE

            if(ruta == "Eje"){
                chipItem.chipBackgroundColor = ColorStateList.valueOf(Color.BLUE)
            }
            if(ruta == "Ruta 9"){
                chipItem.chipBackgroundColor = ColorStateList.valueOf(Color.RED)

            }
            if(ruta == "Nacionalista"){
                chipItem.chipBackgroundColor = ColorStateList.valueOf(resources.getColor(R.color.colorNaranja))
            }
        } else {
            val toast = Toast(applicationContext)
            //// CARGA EL LAYOUT A UNA VISTA ////
            val view = layoutInflater.inflate(R.layout.mensaje, null)
            toast.view = view
            toast.duration = Toast.LENGTH_LONG
            view.findViewById<TextView>(R.id.tvToastMensaje).text = getString(R.string.yaSeleccionada_str)
            toast.show()
        }

        chipItem.setOnClickListener {
            ventanaRutas()
        }

        chipItem.setOnCloseIconClickListener {
            val anim = AlphaAnimation(1f, 1f)
            anim.duration = 2
            anim.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    chipgroup?.removeView(it)
                }
                override fun onAnimationStart(animation: Animation?) {}
            })
            it.startAnimation(anim)

            val nombre = chipItem.text
            rutaSeleccionada.remove(nombre)

            if(nombre == "Eje"){
                mMap.clear()
                if (rutaSeleccionada.contains("Ruta 9")){
                    ruta9()
                }
                if (rutaSeleccionada.contains("Nacionalista")){
                    nacionalista()
                }
            }
            if(nombre == "Ruta 9"){
                mMap.clear()
                if (rutaSeleccionada.contains("Eje")){
                    eje()
                }
                if (rutaSeleccionada.contains("Nacionalista")){
                    nacionalista()
                }
            }
            if(nombre == "Nacionalista"){
                mMap.clear()
                if (rutaSeleccionada.contains("Ruta 9")){
                    ruta9()
                }
                if (rutaSeleccionada.contains("Eje")){
                    eje()
                }
            }
        }
    }

    private fun seleccionRuta(ruta: String) {
        when (ruta) {
            "Ruta 9" -> ruta9()
            "Nacionalista" -> nacionalista()
            "Eje" -> eje()
        }
    }

    private fun ruta9() {
        val r = Ruta9()
        val r9 = r.total()

        r9.forEach {
            points = r.decodePoly(it)

            polylineOptions = PolylineOptions()
            polylineOptions?.color(Color.RED)
            polylineOptions?.width(7.5f)
            polylineOptions!!.addAll(points)
            mMap.addPolyline(polylineOptions)
        }
        points?.clear()
    }

    private fun nacionalista() {
        val nacionalista = Nacionalista()
        val totalPuntos = nacionalista.total()

        totalPuntos.forEach {
            points = nacionalista.decodePoly(it)

            polylineOptions = PolylineOptions()
            polylineOptions?.color(resources.getColor(R.color.colorNaranja))
            polylineOptions?.width(7.5f)
            polylineOptions!!.addAll(points)
            mMap.addPolyline(polylineOptions)
        }
        points?.clear()
    }

    private fun eje(){
        val eje = Eje()
        val totalPuntos = eje.total()

        totalPuntos.forEach {
            points = eje.decodePoly(it)

            polylineOptions = PolylineOptions()
            polylineOptions?.color(Color.BLUE)
            polylineOptions?.width(7.5f)
            polylineOptions!!.addAll(points)
            mMap.addPolyline(polylineOptions)
        }
        points?.clear()
    }
}