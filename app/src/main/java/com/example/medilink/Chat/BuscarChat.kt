package com.example.medilink.Chat

import android.widget.Filter
import com.example.medilink.Adaptadores.AdaptadorChats
import com.example.medilink.Modelo.ModeloChat
import com.example.medilink.Modelo.ModeloChats
import java.util.Locale

class BuscarChat : Filter {

    private val adaptadorChats : AdaptadorChats
    private val filtroLista : ArrayList<ModeloChats>

    constructor(adaptadorChats: AdaptadorChats, filtroLista: ArrayList<ModeloChats>) : super() {
        this.adaptadorChats = adaptadorChats
        this.filtroLista = filtroLista
    }

    override fun performFiltering(filtro: CharSequence?): FilterResults {
        var filtro: CharSequence? = filtro
        val resultados = FilterResults()

        if (!filtro.isNullOrEmpty()) {
            filtro = filtro.toString().uppercase(Locale.getDefault())
            val filtroModelos = ArrayList<ModeloChats>()
            for (i in filtroLista.indices) {
                if (filtroLista[i].nombre.uppercase().contains(filtro)) {
                    filtroLista.add(filtroLista[i])
                }
            }
            resultados.count = filtroModelos.size
            resultados.values = filtroModelos
        }else{
            resultados.count = filtroLista.size
            resultados.values = filtroLista
        }
        return resultados

    }

    override fun publishResults(filtro: CharSequence?, resultados: FilterResults){
        adaptadorChats.chatsArrayList = resultados.values as ArrayList<ModeloChats>
        adaptadorChats.notifyDataSetChanged()

    }
}