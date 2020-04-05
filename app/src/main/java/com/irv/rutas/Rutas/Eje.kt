package com.irv.rutas.Rutas

import com.google.android.gms.maps.model.LatLng

class Eje {

    private val rutaPuntos: ArrayList<String> = ArrayList()
    private var poly: ArrayList<LatLng>? = null//decodificador//

    init {
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