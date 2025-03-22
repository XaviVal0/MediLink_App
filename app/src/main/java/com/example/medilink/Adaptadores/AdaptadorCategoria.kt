package com.example.medilink.Adaptadores

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.medilink.Modelo.ModeloCategoria
import com.example.medilink.RvListenerCategoria
import com.example.medilink.databinding.ItemCategoriaBinding
import com.example.medilink.databinding.ItemCategoriaInicioBinding
import kotlin.random.Random

class AdaptadorCategoria(

    private val context : Context,
    private val categoriaArrayList : ArrayList<ModeloCategoria>,
    private val rvListenerCategoria: RvListenerCategoria
): Adapter<AdaptadorCategoria.HolderCategoria>() {

    private lateinit var binding : ItemCategoriaInicioBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderCategoria {
        binding = ItemCategoriaInicioBinding.inflate(LayoutInflater.from(context),parent,false)
        return HolderCategoria(binding.root)
    }

    override fun getItemCount(): Int {
        return categoriaArrayList.size
    }

    override fun onBindViewHolder(holder: HolderCategoria, position: Int) {
        val modeloCategoria = categoriaArrayList[position]

        val icon = modeloCategoria.icom
        val categoria = modeloCategoria.categoria

        val random = java.util.Random()
        val color = Color.WHITE


        holder.categoriaIconIV.setImageResource(icon)
        holder.categoriaIconIV.setBackgroundColor(color)
        holder.categoria.text = categoria

        holder.itemView.setOnClickListener {
            rvListenerCategoria.onCategoriaClick(modeloCategoria)

        }

    }

    inner class HolderCategoria(itemView : View):ViewHolder(itemView){
        var categoriaIconIV = binding.CategoriaIcon
        var categoria = binding.TvCategoria

    }



}