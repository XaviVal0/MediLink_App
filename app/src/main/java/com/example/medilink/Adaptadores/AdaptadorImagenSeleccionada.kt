package com.example.medilink.Adaptadores

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.medilink.Modelo.ModeloImagenSeleccionada
import com.example.medilink.R
import com.example.medilink.databinding.ItemImagenesSeleccionadasBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class AdaptadorImagenSeleccionada(

    private val context : Context,
    private val imagenesSeleccionadasArrayList : ArrayList<ModeloImagenSeleccionada>,
    private val idAnuncio : String
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

        if(modelo.deInternet){
            try{
                val imagenUrl = modelo.imagenUrl
                Glide.with(context)
                    .load(imagenUrl)
                    .placeholder(R.drawable.ic_imagenes)
                    .into(holder.item_imagen)
            }catch (e : Exception){

            }
        }else{
            try {
                val imagenUri = modelo.imagenUri
                Glide.with(context)
                    .load(imagenUri)
                    .placeholder(R.drawable.ic_imagenes)
                    .into(holder.item_imagen)
            }catch (e:Exception){

            }
        }



        holder.btn_cerrar.setOnClickListener {
            if(modelo.deInternet){
                eliminarImgFirebase(modelo,holder,position)
            }else{
                imagenesSeleccionadasArrayList.removeAt(holder.adapterPosition)
                notifyDataSetChanged()
            }

        }

    }

    private fun eliminarImgFirebase(modelo: ModeloImagenSeleccionada, holder: AdaptadorImagenSeleccionada.HolderImagenSeleccionada, position: Int) {
        val idImagen = modelo.id

        val ref = FirebaseDatabase.getInstance().getReference("Anuncios")
        ref.child(idAnuncio).child("Imagenes").child(idImagen)
            .removeValue()
            .addOnFailureListener {
                try{
                    imagenesSeleccionadasArrayList.remove(modelo)
                    eliminarImgStorage(modelo)
                    notifyItemRemoved(position)
            }catch (e: Exception){


                }            }
            .addOnFailureListener {
                Toast.makeText(context,"No se pudo eliminar la imagen",Toast.LENGTH_SHORT).show()
            }
    }

    private fun eliminarImgStorage(modelo: ModeloImagenSeleccionada) {
        val rutaImagen = "Anuncios/"+modelo.id

        val ref = FirebaseStorage.getInstance().getReference(rutaImagen)
        ref.delete()
            .addOnSuccessListener{
                Toast.makeText(context,"Imagen eliminada",Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context,"No se pudo eliminar la imagen",Toast.LENGTH_SHORT).show()

            }


    }

    inner class HolderImagenSeleccionada(itemView : View): ViewHolder(itemView){
        var item_imagen = binding.itemImage
        var btn_cerrar = binding.cerrarItem
    }



}