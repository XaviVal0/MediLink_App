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
import com.example.medilink.databinding.ItemCategoriaInicioBinding

class AdaptadorCategoria(
    private val context: Context,
    private val categoriaArrayList: ArrayList<ModeloCategoria>,
    private val rvListenerCategoria: RvListenerCategoria
) : Adapter<AdaptadorCategoria.HolderCategoria>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderCategoria {
        val binding = ItemCategoriaInicioBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderCategoria(binding)
    }

    override fun getItemCount(): Int {
        return categoriaArrayList.size
    }

    override fun onBindViewHolder(holder: HolderCategoria, position: Int) {
        val modeloCategoria = categoriaArrayList[position]
        holder.categoria.text = modeloCategoria.categoria
        holder.itemView.setOnClickListener {
            rvListenerCategoria.onCategoriaClick(modeloCategoria)
        }
    }

    inner class HolderCategoria(private val binding: ItemCategoriaInicioBinding) : ViewHolder(binding.root) {
        val categoria = binding.TvCategoria
    }
}
