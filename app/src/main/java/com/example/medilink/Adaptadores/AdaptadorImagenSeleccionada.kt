package com.example.medilink.Adaptadores

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.medilink.Modelo.ModeloImagenSeleccionada
import com.example.medilink.R
import com.example.medilink.databinding.ItemImagenesSeleccionadasBinding

class AdaptadorImagenSeleccionada(

    private val context : Context,
    private val imagenesSeleccionadasArrayList : ArrayList<ModeloImagenSeleccionada>
    ):Adapter<AdaptadorImagenSeleccionada.HolderImagenSeleccionada>() {

    private lateinit var binding : ItemImagenesSeleccionadasBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderImagenSeleccionada {
        binding = ItemImagenesSeleccionadasBinding.inflate(LayoutInflater.from(context),parent,false)
        return HolderImagenSeleccionada(binding.root)
    }

    override fun getItemCount(): Int {
        return imagenesSeleccionadasArrayList.size
    }

    override fun onBindViewHolder(holder: HolderImagenSeleccionada, position: Int) {
        val modelo = imagenesSeleccionadasArrayList[position]

        val imagenUri = modelo.imagenUri

        try {
            Glide.with(context)
                .load(imagenUri)
                .placeholder(R.drawable.ic_imagenes)
                .into(holder.item_imagen)
        }catch (e:Exception){

        }
        holder.btn_cerrar.setOnClickListener {
            imagenesSeleccionadasArrayList.removeAt(holder.adapterPosition)
            notifyDataSetChanged()
        }

    }

    inner class HolderImagenSeleccionada(itemView : View): ViewHolder(itemView){
        var item_imagen = binding.itemImage
        var btn_cerrar = binding.cerrarItem
    }



}