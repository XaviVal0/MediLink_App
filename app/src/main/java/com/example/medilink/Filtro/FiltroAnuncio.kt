package com.example.medilink.Filtro

import android.widget.Filter
import com.example.medilink.Adaptadores.AdaptadorAnuncio
import com.example.medilink.Modelo.ModeloAnuncio
import java.util.Locale

class FiltroAnuncio(
    private val adaptador: AdaptadorAnuncio,
    private val filtroLista: ArrayList<ModeloAnuncio>
) : Filter() {

    override fun performFiltering(filtro: CharSequence?): FilterResults {
        val resultado = FilterResults()

        if (!filtro.isNullOrEmpty()) {
            val filtroTexto = filtro.toString().uppercase(Locale.getDefault())
            val filtroModelo = filtroLista.filter { anuncio ->
                anuncio.marca.uppercase(Locale.getDefault()).contains(filtroTexto) ||
                        anuncio.nombreproducto.uppercase(Locale.getDefault()).contains(filtroTexto) ||
                        anuncio.categoria.uppercase(Locale.getDefault()).contains(filtroTexto)
            }

            resultado.count = filtroModelo.size
            resultado.values = ArrayList(filtroModelo) // Convertimos a ArrayList
        } else {
            resultado.count = filtroLista.size
            resultado.values = filtroLista
        }
        return resultado
    }

    override fun publishResults(filtro: CharSequence?, results: FilterResults) {
        adaptador.anuncioArrayList = results.values as ArrayList<ModeloAnuncio>
        adaptador.notifyDataSetChanged()
    }
}
