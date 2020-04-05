package com.irv.rutas.Rutas

import com.google.android.gms.maps.model.LatLng

class Ruta9 {

    private val rutaPuntos: ArrayList<String> = ArrayList()
    private var poly: ArrayList<LatLng>? = null//decodificador//

    init {
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
    }

    fun total() : ArrayList<String>{
        return rutaPuntos
    }

    fun decodePoly(encoded : String): ArrayList<LatLng>? {
        poly  = ArrayList()
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
}