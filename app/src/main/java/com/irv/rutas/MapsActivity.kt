package com.irv.rutas

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var estadoDrawer = false // Bandera para saber el estado del drawer Abierto o Cerrado ///
    private var drawerLayout : DrawerLayout? = null
    private var content : LinearLayout? = null //// LAYOUT QUE CONTIENE AL MAPA ////

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        supportActionBar?.hide()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        drawerLayout = findViewById(R.id.drawerLayout)
        drawerLayout?.setScrimColor(Color.TRANSPARENT)
        content = findViewById(R.id.content)

        menuSlide()
        findViewById<ImageButton>(R.id.btnMenu_maps).setOnClickListener {
            drawerLayout?.openDrawer(GravityCompat.START)
            estadoDrawer = true
        }
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

    }

    override fun onBackPressed() {
        if(estadoDrawer){
            cerrarDrawer()
        } else {
            super.onBackPressed()
        }
    }
}