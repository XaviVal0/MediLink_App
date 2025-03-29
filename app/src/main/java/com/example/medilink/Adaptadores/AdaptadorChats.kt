package com.example.medilink.Adaptadores

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.medilink.Chat.BuscarChat
import com.example.medilink.Chat.ChatActivity
import com.example.medilink.Constantes
import com.example.medilink.Modelo.ModeloChats
import com.example.medilink.R
import com.example.medilink.databinding.ItemChatsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdaptadorChats : RecyclerView.Adapter<AdaptadorChats.HolderChats>,Filterable{

    private var context : Context
    var chatsArrayList : ArrayList<ModeloChats>
    private lateinit var binding : ItemChatsBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var miUid = ""
    private var filtroLista : ArrayList<ModeloChats>
    private var filtro : BuscarChat? = null

    constructor(context: Context, chatsArrayList: ArrayList<ModeloChats>) {
        this.context = context
        this.filtroLista = chatsArrayList
        this.chatsArrayList = chatsArrayList
        firebaseAuth = FirebaseAuth.getInstance()
        miUid = firebaseAuth.uid!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderChats {
        binding = ItemChatsBinding.inflate(LayoutInflater.from(context),parent,false)
        return HolderChats(binding.root)
    }

    override fun getItemCount(): Int {
        return chatsArrayList.size
    }

    override fun onBindViewHolder(holder: HolderChats, position: Int) {
        val modeloChats = chatsArrayList[position]

        cargarUltimoMensaje(modeloChats,holder)

        holder.itemView.setOnClickListener{
            val uidRecibimos = modeloChats.uidRecibimos
            if(uidRecibimos!=miUid){
                val intent = Intent(context, ChatActivity::class.java)
                intent.putExtra("uidVendedor",uidRecibimos)
                context.startActivity(intent)

            }
        }
    }

    private fun cargarUltimoMensaje(modeloChats: ModeloChats, holder: AdaptadorChats.HolderChats) {
        val chatKey = modeloChats.keyChat
        
        val ref = FirebaseDatabase.getInstance().getReference("Chats")
        ref.child(chatKey).limitToLast(1)
            .addValueEventListener(object :  ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (ds in snapshot.children){
                        val emisorUid = "${ds.child("emisorUid").value}"
                        val idMensaje = "${ds.child("idMensaje").value}"
                        val mensaje = "${ds.child("mensaje").value}"
                        val receptorUid = "${ds.child("receptorUid").value}"
                        val tiempo = ds.child("tiempo").value as Long
                        val tipoMensaje = "${ds.child("tipoMensaje").value}"

                        val formatoHora = Constantes.obtenerHora(tiempo)

                        modeloChats.emisorUid = emisorUid
                        modeloChats.idmensaje = idMensaje
                        modeloChats.mensaje = mensaje
                        modeloChats.receptorUid = receptorUid
                        modeloChats.tipoMensaje = tipoMensaje

                        holder.Tv_Fecha.text = "$formatoHora"

                        if(tipoMensaje == Constantes.MENSAJE_TIPO_TEXTO){
                            holder.Tv_UltimoMensaje.text = mensaje
                        }else{
                            holder.Tv_UltimoMensaje.text = "Imagen"

                        }

                        cargarInfoUsuarioRecibimos(modeloChats,holder)


                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

    }

    private fun cargarInfoUsuarioRecibimos(modeloChats: ModeloChats, holder: AdaptadorChats.HolderChats) {
        val emisorUid = modeloChats.emisorUid
        val receptorUid = modeloChats.receptorUid

        var uidRecibimos = ""
        if(emisorUid == miUid){
            uidRecibimos = receptorUid
        }else{
            uidRecibimos = emisorUid
        }

        modeloChats.uidRecibimos = uidRecibimos

        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(uidRecibimos)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val nombre = "${snapshot.child("nombre").value}"
                    val imagen = "${snapshot.child("url_imagenperfil").value}"

                     modeloChats.nombre = nombre
                     modeloChats.urlImagenPerfil = imagen

                    holder.Tv_Nombre.text = nombre

                    try{
                        Glide.with(context)
                            .load(imagen)
                            .placeholder(R.drawable.imagen_perfil)
                            .into(holder.Iv_Perfil)
                    }catch (e: Exception){
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

    }

    inner class HolderChats(itemView : View): RecyclerView.ViewHolder(itemView){

        var Iv_Perfil = binding.IvPerfil
        var Tv_Nombre = binding.TvNombre
        var Tv_UltimoMensaje = binding.TvUltimoMensaje
        var Tv_Fecha = binding.TvFecha


    }

    override fun getFilter(): Filter {
        if(filtro == null){
            filtro = BuscarChat(this,filtroLista)
        }
        return filtro!!
    }


}