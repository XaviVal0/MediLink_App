package com.example.medilink.Adaptadores

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.medilink.Constantes
import com.example.medilink.Modelo.ModeloChat
import com.example.medilink.R
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.core.Context

class AdaptadorChat : RecyclerView.Adapter<AdaptadorChat.HolderChat> {

    private val context : android.content.Context
    private val chatArrayList : ArrayList<ModeloChat>
    private val firebaseAuth : FirebaseAuth

    companion object{
        private const val  MENSAJE_IZQUIERDO = 0
        private const val  MENSAJE_DERECHO = 1
    }

    constructor(context: android.content.Context, chatArrayList: ArrayList<ModeloChat>) {
        this.context = context
        this.chatArrayList = chatArrayList
        firebaseAuth = FirebaseAuth.getInstance()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderChat {
        if(viewType == MENSAJE_DERECHO){
            val view = LayoutInflater.from(context).inflate(R.layout.item_chat_derecho,parent,false)
            return HolderChat(view)
        }else{
            val view = LayoutInflater.from(context).inflate(R.layout.item_chat_izquierdo,parent,false)
            return HolderChat(view)
        }
    }

    override fun getItemCount(): Int {
        return chatArrayList.size
    }

    override fun getItemViewType(position: Int): Int {
        if(chatArrayList[position].emisorUid == firebaseAuth.uid){
            return MENSAJE_DERECHO
        }else{
            return MENSAJE_IZQUIERDO
        }
    }

    override fun onBindViewHolder(holder: HolderChat, position: Int) {
        val modeloChat = chatArrayList[position]

        val mensaje = modeloChat.mensaje
        val tipoMensaje = modeloChat.tipoMensaje
        val tiempo = modeloChat.tiempo

        Log.d("AdaptadorChat", "Mensaje en posición $position: $mensaje")


        val hora = Constantes.obtenerHora(tiempo)
        holder.Tv_Hora.text = hora

        if(tipoMensaje == Constantes.MENSAJE_TIPO_TEXTO){
            holder.Tv_Mensaje.visibility = View.VISIBLE
            holder.Iv_Mensaje.visibility = View.GONE
            holder.Tv_Mensaje.text = mensaje
        }else{
            holder.Tv_Mensaje.visibility = View.GONE
            holder.Iv_Mensaje.visibility = View.VISIBLE

            try{
                Glide.with(context)
                    .load(mensaje)
                    .placeholder(R.drawable.ic_imagee)
                    .into(holder.Iv_Mensaje)
            }catch (e: Exception){
            }

            }

    }

    inner class HolderChat(itemView: View) : RecyclerView.ViewHolder(itemView){
        var Tv_Mensaje : TextView = itemView.findViewById(R.id.Tv_Mensaje)
        var Iv_Mensaje : ShapeableImageView = itemView.findViewById(R.id.Iv_Mensaje)
        var Tv_Hora : TextView = itemView.findViewById(R.id.Tv_Tiempo)

    }


}