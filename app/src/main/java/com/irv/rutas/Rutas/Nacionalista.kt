package com.irv.rutas.Rutas

import com.google.android.gms.maps.model.LatLng

class Nacionalista {

    private val rutaPuntos: ArrayList<String> = ArrayList()
    private var poly: ArrayList<LatLng>? = null//decodificador//

    init {
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