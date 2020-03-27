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
import android.widget.ListView
import android.widget.ArrayAdapter
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
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_maps.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var estadoDrawer = false // Bandera para saber el estado del drawer Abierto o Cerrado ///
    private var drawerLayout: DrawerLayout? = null
    private var content: LinearLayout? = null //// LAYOUT QUE CONTIENE AL MAPA ////
    private var miUbicacion: Location? = null
    private var listaRutas: ArrayList<String>? = null
    private var chipgroup: ChipGroup? = null
    private var btnRutas: MaterialButton? = null
    private var btnUbicacion : FloatingActionButton? = null
    private var fbaZoomMenos : FloatingActionButton? = null
    private var fbaZoomMas : FloatingActionButton? = null

    private var poly: ArrayList<LatLng>? = null
    private val rutaPuntos: ArrayList<String> = ArrayList()
    private var points: ArrayList<LatLng>? = null
    private var polylineOptions: PolylineOptions? = null

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
        btnUbicacion = findViewById(R.id.btnUbicacion)
        fbaZoomMenos = findViewById(R.id.fabZoomMenos)
        fbaZoomMas = findViewById(R.id.fabZoomMas)

        menuSlide()

        ///// RUTAS /////
        listaRutas = ArrayList()
        listaRutas?.add("Ruta 9")
        listaRutas?.add("Nacionalista")
        listaRutas?.add("Eje")
        listaRutas?.sort() //ORDENA ALFABETICAMENTE //
    }

    override fun onStart() {
        super.onStart()

        findViewById<ImageButton>(R.id.btnMenu_maps).setOnClickListener {
            drawerLayout?.openDrawer(GravityCompat.START)
            estadoDrawer = true
        }

        btnRutas?.setOnClickListener {
            ventanaRutas()
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

    private fun menuSlide() {
        drawerLayout?.addDrawerListener(object : ActionBarDrawerToggle(this, drawerLayout, 0, 0) {

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

    private fun cerrarDrawer() {
        drawerLayout?.closeDrawer(GravityCompat.START)
        estadoDrawer = false
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isMyLocationButtonEnabled = false
        mMap.uiSettings.isZoomControlsEnabled = false
        mMap.uiSettings.isCompassEnabled = true
        mMap.setMinZoomPreference(11.0f)
        mMap.setPadding(0, 90, 0, 0)

        permiso()
        camaraAubicacion()

        mMap.setOnMapClickListener {
            if(btnUbicacion?.visibility == View.VISIBLE){
                btnUbicacion?.hide()
                fabZoomMenos.hide()
                fabZoomMas.hide()
            }

            if(btnUbicacion?.visibility == View.GONE){
                btnUbicacion?.show()
                fabZoomMenos.show()
                fabZoomMas.show()
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
            mMap.clear()
            val ruta = parent.getItemAtPosition(position)
            dialog.dismiss()
            chipRuta(view, ruta.toString())
            btnRutas?.isEnabled = false
            seleccionRuta(ruta.toString())
        }
    }

    private fun chipRuta(vista: View, ruta: String) {
        val i = LayoutInflater.from(this)
        val chipItem = i.inflate(R.layout.chip, null, false) as Chip

        chipItem.text = ruta
        chipgroup?.addView(chipItem)
        chipgroup?.visibility = View.VISIBLE

        chipItem.setOnClickListener {
            mMap.clear()
            ventanaRutas()
            btnRutas?.isEnabled = true

            val anim = AlphaAnimation(1f, 0f)
            anim.duration = 1
            anim.setAnimationListener(object : Animation.AnimationListener {
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
            mMap.clear()
        }
    }

    override fun onBackPressed() {
        if (estadoDrawer) {
            cerrarDrawer()
        } else {
            super.onBackPressed()
        }
    }

    private fun decodePoly(encoded: String): ArrayList<LatLng>? {
        poly = ArrayList()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0
        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng
            val p = LatLng(lat.toDouble() / 1E5, lng.toDouble() / 1E5)
            poly?.add(p)
        }
        return poly
    }

    private fun seleccionRuta(ruta: String) {
        when (ruta) {
            "Ruta 9" -> ruta9()
            "Nacionalista" -> nacionalista()
            "Eje" -> eje()
        }
    }

    private fun ruta9() {
        // OXXO LUCERNA A CETIS 18 //
        rutaPuntos.add("cdifEn_b`UbC@vCEfC?vDBtAAtA?`E@tL?tKHdE?xPBfD?`DB`H?tN@jLBvCAR?FFD@`@H|@Xf@BpBA~A?vDDF?pAG|Ba@b@CLEfA@fBBxAC|DDz@FfB`@l@\\|@f@`An@d@Rp@Pp@Jn@BbB?`AHrATj@JdBFnIA|@A")
        // DE CETIS 18 A GLORIETA //
        rutaPuntos.add("{lbfEthb`UT??aF@wSFub@?qESuCWiB]sAu@eCoCwIoA{Dq@qCOiACu@")
        // GLORIETA A TERAN TERAN //
        rutaPuntos.add("aybfErz~_UJWH_AJQV_@d@Q~@Kd@KJKN?|A?`B?|BJ|@L`BZdAX|An@^NIo@?_@BULWZKnKGfC?lD?tFEnD?")
        // TODA TERAN TERAN HASTA COSTCO //
        rutaPuntos.add("ux`fEjt~_UX@@s@FqLAeFEgI?wD@_EAgTBcKEaMHuOJ}CNmBPkBd@{Ch@{D^cD`Fa[n@mDb@eBvDkNjDsL^}Af@eBzAkFpBaH`@}At@kCr@wCh@oCRgBJsC\\oPj@aYh@_XTyKXkKRuJFqE`@uMFmDLuFBs@HkA\\eCXiAf@wAf@aAr@kAbA}A|@aBd@uANy@Fs@@k@AyAGy@i@mDm@{Dw@sEc@qAcFgH_E}FW[ED")
        // DE COSTCO A PEDREGAL //
        rutaPuntos.add("in_fErep_UIHVZjPwRtCuDnEoG~AoCvAcC|@uBnCcFjEwJtC{HrCiJ|C_M`CuJpDmNxD_PfEqPxBaJhG_Wj@oBj@iCjDyM`@kA`@gAT{@f@oBN}@|BeJt@gEhBiHhAeEnBeI|BmJxBuJj@wDj@yDzByJfCcKrBiIxK}a@hDsNxB{IpAaFbEaPtAoFxCwLBQ")
        // CALZADA ROSA DEL DESIERTO (PEDREGAL) //
        rutaPuntos.add("wvyeEvcb_U@E]MqDyAaJkDo@]y@s@iBgBiAeAo@c@eAe@}C_A}DqAmBk@qC_AaEqAeB_AeAs@eC}BkAaA")
        // TODA LA LAZARO CARDENAS DESDE PEDRAGAL HASTA GLORIETA //
        rutaPuntos.add("wy{eEn|`_U_@]g@c@OVgMrTyBzDgBzCsBjDgD|FmB~CqDvGcMhTeIlNoCzE{BtDmHrM_BxCmD~FwDvG}KtRmItNqM`U{CrF_@n@iHzLgDbGsE`Io@nA_BfCiBbDqBhDmCvEmQ|Z{@zAeFzIWh@_A|AuBvDcAlBMZq@xA{@xAc@f@iA~AkAlB_BdDc@pAg@nBWjBQtAIfAGpB?~BH`CJ~Q?bD?dJCxFBhGDrN@bLD`PCbDCxCB|CBzCDtJFnIPra@H~J")
        // GLORIETA LAZARO CARDENAS Y JUSTO SIERRA A CALLE INDEPENDENCIA //
        rutaPuntos.add("k_cfE~dr_U?NEPCRAlEAf@Gn@APGDIPg@`@a@X]PoJlCaFrA}DhA}ErAuBf@eEjAkBd@uCz@_Cn@kBj@kKlCsCz@PbA")
        // TODA LA CALLE INDEPENDENCIA HASTA CALLE DEL HOSPITAL//
        rutaPuntos.add("cmefErqs_UpBbLx@~ETx@GXBb@LjBDjA?nASnIAl@DPGpCWbJI`D@rGU|JIrBQvA[fEM~DI`ACr@OfGM`BM`A[bB[lAwApDs@dAQj@}@rAcGdI_BzBaBxBkEzFeFbHgArA")
        // CALLE DEL HOSPITAL HASTA LOPEZ MATEOS//
        rutaPuntos.add("wsffEdby_UgDk@SGc@U_BeBwCoEk@q@oFkFiAaAe@]iC}AmA}@s@k@MMW_@EIJE")
        // TODA LOPEZ MATEOS HASTA ALTAMIRANO -CENTRO //
        rutaPuntos.add("}xgfEbax_U{G`Cs@PYAu@Pa@Ta@h@oAvAg@`@iBhAyCbCmBbB{B`CoArAm@v@mBnCaCtDqA|BqAlCeAfCkA|CiB`FwA~DiG~QmFzOkF`Ph@?F?")
        // CALLE ALTAMIRANO HASTA MICHOACAN //
        rutaPuntos.add("_ljfEx{{_U~DAlGElG?xBEh@rCh@rC`@bAjApDJ\\lBA~A@")
        // TODA LA MICHOACAN //
        rutaPuntos.add("mdifEfm|_UL@@~F@~N?`OA~NA~N@l^DzF?hGA`L?fE?jL?|L@rD@fEM?")

        rutaPuntos.forEach {
            points = decodePoly(it)

            polylineOptions = PolylineOptions()
            polylineOptions?.color(Color.RED)
            polylineOptions?.width(7.5f)
            polylineOptions!!.addAll(points)
            mMap.addPolyline(polylineOptions)
        }

        rutaPuntos.clear()
        points?.clear()
        poly!!.clear()
    }

    private fun nacionalista() {
        // DESDE EL CENTRO (SALIDA) A CALLE DEL HOSPITAL TODA LA LOPEZ MATEOS//
        rutaPuntos.add("crjfEhi|_UrImWnH}ThDcKl@cB^mAh@yAhCcHtCuHj@oAnBoDpBaDfAaBzBwCdCmCbD}CzAqAlDsCfBmAr@_@VEd@STO\\c@xA_AzB}@fCgA")
        // CALLE DEL HOSPITAL //
        rutaPuntos.add("cxgfE|bx_Uf@n@nA|@lBpApAv@z@n@jGdGd@f@jAdBxAzBjApAJJXRZL`@JnCb@")
        //  INDEPENDENCIA! //
        rutaPuntos.add("wsffEdby_U`BuBpFsHvFoH~CmEfFaHl@}@HCNIDE")
        // ANAHUAC //
        rutaPuntos.add("urefEhyw_UHKT~@|@zDTx@Zf@t@`@hEjBtGbCrB~@tAdAfIpKxAlBl@p@XVjAx@fAd@rBj@x@NXT`ANrCb@|Cf@~KjBzB\\x@Jh@\\XXV\\^|@Pf@?zA?dA")
        // LAZARO CARDENAS HASTA GLORIETA //
        rutaPuntos.add("gzbfE~zy_U?`U?pI?fL?lF@fF?vDCz_@B`p@KdBCDEFIT?\\HZFFHFXDVGHEBEXG|@Mf@ChA?p@?")
        // CALZADA DEL SOL //
        rutaPuntos.add("mpbfErt~_UlC?|BJ|@L`BZdAX|An@hBbAzBpBl@n@j@z@z@vA`EhJx@tBt@jBb@[Qc@")
        // PAPAGO HASTA GLORIETA //
        rutaPuntos.add("}gafExt_`UoBuEoA_DkBcEeBgCgAcAcA}@iBiAw@c@k@Wo@WkAWoB[iBKcBCkA?oFIOESAACACCCIKYGWDMJEFIT?\\HZPNFx@?~@@v@")
        // DESDE GLORIETA (GALERIAS) A YUGOSLAVIA //
        rutaPuntos.add("_zbfEl{~_UBr@RvAb@bBz@xCdA~C`B~EpAxEPpAPrBBlCD~BCfMHbS@tAE`K@jME?")
        // YUGOSLAVIA //
        rutaPuntos.add("ambfEthb`Uw@@oI@eBGk@KsAUaAIcB?o@Cq@Kq@Qe@SaAo@}@g@m@]gBa@{@GaBC{AAyABgBCgAAWEYAcCBqDA{DAuA?W?m@By@ASEQ?IHsB@qDAkG?uECwM?oPCcRA@\\FP")
        // AVENIDA SANTA ISABEL //
        rutaPuntos.add("}hgfEjab`UMrXN|J@jCHlQ")
        // GOMEZ MORIN (PLAZA DEL SOL) //
        rutaPuntos.add("ohgfEv}c`U?TYx@]t@OLm@RWD}AEkD@")
        // AV VALLECITOS
        rutaPuntos.add("gugfE`cd`UDgI?uLK?")
        // CALZADA LUIS DONALDO COLOSIO MURRIETA//
        rutaPuntos.add("mugfEbkc`U}M@{D?_BA?V")
        // REAL DELCASTILLO //
        rutaPuntos.add("gmhfEvkc`UBfFEfLCzAoC?wADgCG?P")
        rutaPuntos.add("}xhfErcd`U?T|B?xB?jBCAhC")
        // COLOMBAR Y MISION PIAROLA //
        rutaPuntos.add("{mhfEnhd`UEfUlD?`@?@fL")
        // COCOIT //
        rutaPuntos.add("sghfE~ke`UoJ?{DA?l@")
        // OAXACA //
        rutaPuntos.add("_yhfEjme`U?zK?hJ?zMQl@?bB?|AE~K@zE")

        rutaPuntos.forEach {
            points = decodePoly(it)

            polylineOptions = PolylineOptions()
            polylineOptions?.color(Color.RED)
            polylineOptions?.width(7.5f)
            polylineOptions!!.addAll(points)
            mMap.addPolyline(polylineOptions)
        }

        rutaPuntos.clear()
        points?.clear()
        poly!!.clear()
    }

    private fun eje(){
        // ORIGEN(Progreso) A LAZARO CARDENAS //
        rutaPuntos.add("ywzeEx}m`U_D}AcEyBaCkAsM{GeG_DaMqGu@a@gJ{E_CiAgIcEiDeBqC}A{EkDuAaAWMi@Se@Ig@CmBAig@HmA?oEAaPCoOAuB@qBPqBNmBDwQ?mLAiCYoCAWQ]e@AgA")
        // INICIO DE LAZARO A COCA COLA FRENTE A PLAZA CARRANZA //
        rutaPuntos.add("gnbfEd{k`U@qD?gS?sFC{AFwDDuJ@cD@yGC{DCgE?{F?sp@FaAAcBMuMCaIE_HAmGEgSN}ZHiIJsCTyMRoMFkGAkEAiH?iD?aRBkLDa^?qESuCWiB]sAu@eCc@yAgC_Is@yBq@qCOiACu@ByAFm@BUBABEJY@_@IUEGACG[I_A@kF?cOAaY?qGB}HAsKAcB@{FC_E?wPBmOAyBAgNAiC@eD?qK?oDAmA?sMBkDGyK@aE@{EBeJCaFA_J@{BEgBYsDGu@i@eIc@_Hu@iKScDB}A?W@i@\\{G?aDC{TEqIG_TAeUAsM@cMKkBCqKEqGAwECiE?mC?yDCeUGsYCiIO{LAg\\BoD")
        // HASTA FINAL DE LAZARO CARDENAS //
        rutaPuntos.add("m`cfExel_U@mBHqBP_Bh@_DVkAf@wAh@oAd@w@x@}Al@aA`AaB~A{Cx@gBj@kApAqBv@aA\\k@nBgDzAsC`BsCxA}BhCqE|@yAnBoDhAsBnEyH|D_HdGsKpJgPzHgN|So^tHuM~HiN`E_HbIsNfFkI~EwI~DcHjB}CtAeCzImOzMwUh@w@pBqDbGoKjI{Nn@_AfB{CvIeO|IsO")
        rutaPuntos.add("{g{eE|~__Uf@{@f@m@x@m@v@[dAO|CEzBAlMWhJMjJGjEMVBrCC`DAf@Bb@HPLLNPf@Bf@Gt@kAbF^LdBbA~EtCzJ~FbI|E~ErCpMpHbDpB|G~D|AbAxGtDlC`B")
        // AV CASCADA LA BALSA //
        rutaPuntos.add("mjveEb|a_URLa@hB_CbKeA`Eu@|Ca@bBOfAYjCOv@cApE{CvMaBzHwBrIaAnDs@Y")
        // CALLE TLAXCALTECAS //
        rutaPuntos.add("cgweE~pd_UyDyAwCqA_DkAoAi@EP")
        // AVENIDA TECAMACHALCO //
        rutaPuntos.add("kyweEnhd_U{CrLyBrIy@lC")

        rutaPuntos.forEach {
            points = decodePoly(it)

            polylineOptions = PolylineOptions()
            polylineOptions?.color(Color.RED)
            polylineOptions?.width(7.5f)
            polylineOptions!!.addAll(points)
            mMap.addPolyline(polylineOptions)
        }

        rutaPuntos.clear()
        points?.clear()
        poly!!.clear()
    }
}