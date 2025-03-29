package com.example.medilink.Adaptadores

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.medilink.Modelo.ModeloImgSlider
import com.example.medilink.R
import com.example.medilink.databinding.ItemImagenSliderBinding
import com.google.android.material.imageview.ShapeableImageView

class AdaptadorImgSlider : RecyclerView.Adapter<AdaptadorImgSlider.HolderImgSlider> {

    private lateinit var binding :ItemImagenSliderBinding
    private var context : Context
    private var imagenArrayList : ArrayList<ModeloImgSlider>

    constructor(context: Context, imagenArrayList: ArrayList<ModeloImgSlider>) {
        this.context = context
        this.imagenArrayList = imagenArrayList
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderImgSlider {
        binding = ItemImagenSliderBinding.inflate(LayoutInflater.from(context),parent,false)
        return HolderImgSlider(binding.root)
    }

    override fun getItemCount(): Int {
        return imagenArrayList.size

    }

    override fun onBindViewHolder(holder: HolderImgSlider, position: Int) {
        val modeloImgSlider = imagenArrayList[position]

        val imagenUrl = modeloImgSlider.imagenUrl
        val imagenContador = "${position+1}/${imagenArrayList.size}" //1/4

        holder.imagenContador.text = imagenContador

        try {
            Glide.with(context)
                .load(imagenUrl)
                .placeholder(R.drawable.ic_imagee)
                .into(holder.imagenIv)
        }catch (e : Exception){

        }

        holder.itemView.setOnClickListener{

        }

    }

    inner class HolderImgSlider(itemView: View): RecyclerView.ViewHolder(itemView){
        var imagenIv : ShapeableImageView = binding.ImagenIv
        var imagenContador : TextView = binding.imagenContador
    }

}