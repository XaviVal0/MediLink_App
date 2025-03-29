package com.example.medilink.Adaptadores

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.medilink.Constantes
import com.example.medilink.DetalleAnuncio.DetalleAnuncio
import com.example.medilink.Filtro.FiltroAnuncio
import com.example.medilink.Modelo.ModeloAnuncio
import com.example.medilink.R
import com.example.medilink.databinding.ItemAnuncioBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class AdaptadorAnuncio(
    private val context: Context,
    var anuncioArrayList: ArrayList<ModeloAnuncio>
) : RecyclerView.Adapter<AdaptadorAnuncio.HolderAnuncio>(), Filterable {

    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var filtroLista: ArrayList<ModeloAnuncio> = anuncioArrayList
    private var filtro: FiltroAnuncio? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderAnuncio {
        val binding = ItemAnuncioBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderAnuncio(binding)
    }

    override fun getItemCount(): Int {
        return anuncioArrayList.size
    }

    override fun onBindViewHolder(holder: HolderAnuncio, position: Int) {
        val modeloAnuncio = anuncioArrayList[position]

        holder.binding.apply {
            TvTitulo.text = modeloAnuncio.nombreproducto
            TvEstado.text = modeloAnuncio.estado

        }



        cargarImagen(modeloAnuncio, holder)

        comprobarFavorito(modeloAnuncio, holder)

        holder.itemView.setOnClickListener{
            val intent = Intent(context, DetalleAnuncio::class.java)
            intent.putExtra("idAnuncio", modeloAnuncio.id)
            context.startActivity(intent)
        }

        if (modeloAnuncio.estado.equals("Disponible")) {
            holder.binding.TvEstado.setTextColor(Color.parseColor("#007A33"))
        } else {
            holder.binding.TvEstado.setTextColor(Color.RED)
        }

        holder.binding.IbButton.setOnClickListener {
            val favorito = modeloAnuncio.favorito
            if (favorito) {
                Constantes.eliminarAnuncioFav(context, modeloAnuncio.id)
            } else {
                Constantes.agregarAnuncioFav(context, modeloAnuncio.id)
            }
        }

    }

    private fun comprobarFavorito(modeloAnuncio: ModeloAnuncio, holder: AdaptadorAnuncio.HolderAnuncio) {
        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(firebaseAuth.uid!!).child("Favoritos").child(modeloAnuncio.id)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val favorito = snapshot.exists()
                    modeloAnuncio.favorito = favorito

                    // Cambiar texto del botón según si es favorito o no
                    if (favorito) {
                        holder.binding.IbButton.text = "Borrar de Favoritos"
                        holder.binding.IbButton.backgroundTintList = ContextCompat.getColorStateList(context, R.color.red)  // Rojo si es favorito
                    } else {
                        holder.binding.IbButton.text = "Agregar a Favoritos"
                        holder.binding.IbButton.backgroundTintList = ContextCompat.getColorStateList(context, R.color.orange)  // Naranja si no es favorito
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Manejo de error si es necesario
                }
            })
    }


    private fun cargarImagen(modeloAnuncio: ModeloAnuncio, holder: HolderAnuncio) {
        val ref = FirebaseDatabase.getInstance().getReference("Anuncios")
        ref.child(modeloAnuncio.id).child("Imagenes").limitToFirst(1)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (ds in snapshot.children) {
                        val imagenUrl = ds.child("imagenUrl").value.toString()
                        Glide.with(context)
                            .load(imagenUrl)
                            .placeholder(R.drawable.ic_imagee)
                            .into(holder.binding.imagenIv)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    println("Error al cargar imagen: ${error.message}")
                }
            })
    }

    inner class HolderAnuncio(val binding: ItemAnuncioBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getFilter(): Filter {
        if (filtro == null) {
            filtro = FiltroAnuncio(this, filtroLista)
        }
        return filtro as FiltroAnuncio
    }
}
