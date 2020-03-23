package com.irv.rutas

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
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
import android.widget.LinearLayout
import android.widget.ImageButton
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.AdapterView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var estadoDrawer = false // Bandera para saber el estado del drawer Abierto o Cerrado ///
    private var drawerLayout : DrawerLayout? = null
    private var content : LinearLayout? = null //// LAYOUT QUE CONTIENE AL MAPA ////
    private var miUbicacion : Location? = null
    private var listaRutas : ArrayList<String>? = null
    private var chipgroup : ChipGroup? = null
    private var btnRutas : MaterialButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        supportActionBar?.hide()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        drawerLayout = findViewById(R.id.drawerLayout)
        drawerLayout?.setScrimColor(Color.TRANSPARENT)
        content = findViewById(R.id.content)
        chipgroup = findViewById(R.id.cg_MapsActivity)
        btnRutas = findViewById(R.id.btnRutas_maps)

        menuSlide()
        findViewById<ImageButton>(R.id.btnMenu_maps).setOnClickListener {
            drawerLayout?.openDrawer(GravityCompat.START)
            estadoDrawer = true
        }

        findViewById<MaterialButton>(R.id.btnRutas_maps).setOnClickListener {
            ventanaRutas()
        }

            ///// RUTAS /////
        listaRutas = ArrayList()
        listaRutas?.add("Ruta 4")
        listaRutas?.add("Ruta 9")
        listaRutas?.add("Eje")
        listaRutas?.add("Nacionalista")
        listaRutas?.add("Ejido puebla")
        listaRutas?.add("Ruta 5")
        listaRutas?.add("Robledo")
        listaRutas?.add("Colosio")
        listaRutas?.add("Progreso")
        listaRutas?.add("Comandancia")
        listaRutas?.sort() //ORDENA ALFABETICAMENTE //
    }

    private fun menuSlide(){
        drawerLayout?.addDrawerListener(object : ActionBarDrawerToggle(this, drawerLayout,0,0){
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                super.onDrawerSlide(drawerView, slideOffset)
                val slideX = drawerView.width * slideOffset
                content?.translationX = slideX
            }

            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                estadoDrawer = false // MENU CERRADO
            }
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                estadoDrawer = true // MENU SE ENCUENTRA ABIERTO
            }
        })
    }

    private fun cerrarDrawer(){
        drawerLayout?.closeDrawer(GravityCompat.START)
        estadoDrawer = false
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isCompassEnabled = false
        mMap.setMinZoomPreference(11.0f)

        permiso()
        camaraAubicacion()
    }

    @SuppressLint("MissingPermission")
    private fun permiso() {
        val permisoActivado: Boolean

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permisoActivado = estadoPermisoUbicacion()

            if (permisoActivado == false) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))
                {
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
    private fun camaraAubicacion(){
        val locationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val criteria = Criteria()
        miUbicacion = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false))
        try {
            val latitude = miUbicacion?.latitude
            val longitud = miUbicacion?.longitude

            val latlog = LatLng(latitude!!, longitud!!)
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latlog))
            mMap.setMinZoomPreference(11.0f)
        } catch (e: Exception) { }
    }

    private fun ventanaRutas() {
        cerrarDrawer()

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
            chipRuta(view, ruta.toString())
            mMap.clear()
            btnRutas?.isEnabled = false
        }
    }

    private fun chipRuta(vista : View , ruta : String){
        val i = LayoutInflater.from(this)
        val chipItem = i.inflate(R.layout.chip, null , false) as Chip

        chipItem.text = ruta
        chipgroup?.addView(chipItem)
        chipgroup?.visibility = View.VISIBLE

        chipItem.setOnClickListener {
            ventanaRutas()
            btnRutas?.isEnabled = true

            val anim = AlphaAnimation(1f,0f)
            anim.duration = 1
            anim.setAnimationListener(object : Animation.AnimationListener
            {
                override fun onAnimationRepeat(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    chipgroup?.removeView(it)
                }
                override fun onAnimationStart(animation: Animation?) {}
            })
            it.startAnimation(anim)
        }

        chipItem.setOnCloseIconClickListener {
            chipgroup?.removeView(chipItem)
            btnRutas?.isEnabled = true
        }
    }

    override fun onBackPressed() {
        if(estadoDrawer){
            cerrarDrawer()
        } else {
            super.onBackPressed()
        }
    }
}