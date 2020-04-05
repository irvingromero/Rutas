package com.irv.rutas

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.Toast
import android.widget.ListView
import android.widget.ArrayAdapter
import android.widget.AdapterView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.irv.rutas.Rutas.Eje
import com.irv.rutas.Rutas.Nacionalista
import com.irv.rutas.Rutas.Ruta9

class Puntos : AppCompatActivity(), OnMapReadyCallback, LocationListener {

    private lateinit var mMap: GoogleMap
    private var lon : Double? = null
    private var lat : Double? = null
    private var chipgroup: ChipGroup? = null
    private var fabUbicacion : FloatingActionButton? = null
    private var fabZoomMenos : FloatingActionButton? = null
    private var fabZoomMas : FloatingActionButton? = null
    private var efabRutas : ExtendedFloatingActionButton? = null

    private var listaRutas : ArrayList<String>? = null
    private var rutaSeleccionada : ArrayList<String> = ArrayList()

    private var posicion : Location? = null
    private var marcador : Location? = null
    private var banderaMarcador = false
    private var punto : Marker? = null
    private var distancia : Double? = null
    private var distanciaUsuario : Double? = null

    private var points: ArrayList<LatLng>? = null
    private var polylineOptions: PolylineOptions? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_puntos)
        supportActionBar?.hide()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        chipgroup = findViewById(R.id.cg_Puntos)
        fabUbicacion = findViewById(R.id.fabUbicacion_puntos)
        fabZoomMas = findViewById(R.id.fabZoomMas_puntos)
        fabZoomMenos = findViewById(R.id.fabZoomMenos_puntos)
        efabRutas = findViewById(R.id.efabRutas_puntos)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),1000)
        } else {
            locationStart()
        }

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

        fabUbicacion?.setOnClickListener {
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
        mMap.uiSettings.isMapToolbarEnabled = false
        mMap.setMinZoomPreference(11.0f)
        mMap.isMyLocationEnabled = true

        mMap.setOnMapClickListener {
            if(fabUbicacion?.visibility == View.VISIBLE){
                fabUbicacion?.hide()
                fabZoomMenos?.hide()
                fabZoomMas?.hide()
                efabRutas?.hide()
            }

            if(fabUbicacion?.visibility == View.GONE){
                fabUbicacion?.show()
                fabZoomMenos?.show()
                fabZoomMas?.show()
                efabRutas?.show()
            }
        }

        mMap.setOnMapLongClickListener {
            if(!banderaMarcador){
                ventanaDistancia()

                punto = mMap.addMarker(MarkerOptions().position(it))

                marcador = Location("Test")
                marcador?.latitude = it.latitude
                marcador?.longitude = it.longitude

                val dis = posicion?.distanceTo(marcador)
                Toast.makeText(applicationContext, "$dis Metros", Toast.LENGTH_SHORT).show()
            } else {

            }
        }
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

    private fun ventanaDistancia() {
        val ventana = AlertDialog.Builder(this, R.style.CustomDialogTheme)
        // CARGA EL LAYOUT PERSONALIZADO//
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.ventana_distancia, null)
        ventana.setCancelable(false)
        ventana.setView(dialogView)

        val campoDistancia = dialogView.findViewById<TextInputEditText>(R.id.tietDistancia)

        ventana.setPositiveButton(R.string.aceptar_str){ _, _ ->
            banderaMarcador = true
            distanciaUsuario = campoDistancia.text.toString().toDouble()
            mMap.addCircle(CircleOptions().center(LatLng(marcador!!.latitude, marcador!!.longitude)).radius(campoDistancia.text.toString().toDouble()).strokeWidth(3f).strokeColor(Color.RED).fillColor(Color.argb(70, 150, 50, 50)))
        }

        ventana.setNeutralButton(R.string.cancelar_str){ _, _ ->
            punto?.remove()
            banderaMarcador = false
        }

        val dialog: AlertDialog = ventana.create()

        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false

        campoDistancia.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = campoDistancia.length() > 0
            }
        })
    }

    private fun camaraAubicacion() {
        if(lat != null && lon != null) {
            val latlog = LatLng(lat!!, lon!!)
            val cu = CameraUpdateFactory.newLatLng(latlog)
            mMap.animateCamera(cu)
            mMap.setMinZoomPreference(11.0f)
        } else {
            Snackbar.make(findViewById(R.id.map), "Ubicacion no disponible", Snackbar.LENGTH_LONG).show()
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

    private fun locationStart() {
        val mlocManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1000)
            return
        }
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f, this)
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)
    }

    override fun onLocationChanged(p0: Location?) {
        lat = p0?.latitude
        lon = p0?.longitude

        posicion = p0

        if(banderaMarcador){
            val dis = posicion?.distanceTo(marcador)!!.toDouble()

            if(dis!! < distanciaUsuario!!){
                Toast.makeText(applicationContext, "AVISO!", Toast.LENGTH_LONG).show()
            }
        }
    }
    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {}
    override fun onProviderEnabled(p0: String?) {}
    override fun onProviderDisabled(p0: String?) {}

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}