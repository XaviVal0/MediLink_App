package com.example.medilink.DetalleAnuncio

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.medilink.Adaptadores.AdaptadorImgSlider
import com.example.medilink.Anuncios.CrearAnuncio
import com.example.medilink.Chat.ChatActivity
import com.example.medilink.Constantes
import com.example.medilink.MainActivity
import com.example.medilink.Modelo.ModeloAnuncio
import com.example.medilink.Modelo.ModeloImgSlider
import com.example.medilink.R
import com.example.medilink.databinding.ActivityDetalleAnuncioBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DetalleAnuncio : AppCompatActivity() {

    private lateinit var binding: ActivityDetalleAnuncioBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var idAnuncio = ""

    private var uidVendedor = ""
    private var telVendedor = ""

    private var favorito = false

    private lateinit var imagenSliderArrayList: ArrayList<ModeloImgSlider>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetalleAnuncioBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.IbEditar.visibility = View.GONE
        binding.IbBasura.visibility = View.GONE
        binding.IbComunicarse.visibility = View.GONE


        firebaseAuth = FirebaseAuth.getInstance()

        idAnuncio = intent.getStringExtra("idAnuncio").toString()

        binding.IbRegresar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        comprobarAnuncioFav()
        cargarInfoAnuncio()
        cargarImagenesAnuncio()

        binding.IbEditar.setOnClickListener{
            opcionesDialog()
        }

        binding.IbFavorite.setOnClickListener {
            if (favorito) {
                Constantes.eliminarAnuncioFav(this, idAnuncio)
            } else {
                Constantes.agregarAnuncioFav(this, idAnuncio)
            }
        }
            binding.IbBasura.setOnClickListener {
                val mAlertDialog = MaterialAlertDialogBuilder(this)
                mAlertDialog.setTitle("Eliminar Anuncio")
                    .setMessage("¿Estas seguro de eliminar este anuncio?")
                    .setPositiveButton("Si") { dialog, which ->
                        eliminarAnuncio()
                    }
                    .setNegativeButton("No") { dialog, which ->
                        dialog.dismiss()
                    }.show()


        }

        binding.IbComunicarse.setOnClickListener{
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("uidVendedor",uidVendedor)
            startActivity(intent)
        }

    }

    private fun opcionesDialog() {
        val popupMenu = PopupMenu(this, binding.IbEditar)

        popupMenu.menu.add(Menu.NONE, 0, 0, "Editar")
        popupMenu.menu.add(Menu.NONE, 1, 1, " Marcar como vendido")

        popupMenu.show()

        popupMenu.setOnMenuItemClickListener { item ->
            val itemId = item.itemId

            if (itemId == 0){
                val intent = Intent(this, CrearAnuncio::class.java)
                intent.putExtra("Edicion",true)
                intent.putExtra("idAnuncio",idAnuncio)
                startActivity(intent)
            }else if(itemId == 1){
                marcarComoVendido()
            }
            return@setOnMenuItemClickListener true
        }
    }

    private fun cargarInfoAnuncio() {
        var ref = FirebaseDatabase.getInstance().getReference("Anuncios")
        ref.child(idAnuncio)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    try{
                        val modeloAnuncio = snapshot.getValue(ModeloAnuncio::class.java)

                        uidVendedor = "${modeloAnuncio!!.uid}"
                        val titulo = modeloAnuncio.nombreproducto
                        val descripcion = modeloAnuncio.descripcion
                        val categoria = modeloAnuncio.categoria
                        val fecha_vencimiento = modeloAnuncio.fecha_vencimiento
                        val marca = modeloAnuncio.marca
                        val cantidad = modeloAnuncio.cantidad
                        val estado = modeloAnuncio.estado
                        val localidad = modeloAnuncio.localidad

                        if (uidVendedor == firebaseAuth.uid){
                            binding.IbEditar.visibility = View.VISIBLE
                            binding.IbBasura.visibility = View.VISIBLE
                            binding.IbComunicarse.visibility = View.GONE
                        }else{
                            binding.IbEditar.visibility = View.GONE
                            binding.IbBasura.visibility = View.GONE
                            binding.IbComunicarse.visibility = View.VISIBLE
                        }
                        binding.TvTitulo.text = titulo
                        binding.EtDescripcion.text = descripcion
                        binding.EtMarca.text = marca
                        binding.TvCategoria.text = categoria
                        binding.Disponible.text = estado
                        binding.EtCantidad.text = cantidad
                        binding.EtLocalidad.text = localidad

                        if(estado.equals("Disponible")){
                            binding.Disponible.setTextColor(Color.parseColor("#007A33"))
                        }else{
                            binding.Disponible.setTextColor(Color.RED)

                        }


                        cargarInfoVendedor()

                    }catch (e : Exception){

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    private fun marcarComoVendido() {
        val hashMap = HashMap<String,Any>()
        hashMap["estado"] = "${Constantes.anuncio_no_disponible}"

        val ref = FirebaseDatabase.getInstance().getReference("Anuncios")
        ref.child(idAnuncio)
            .updateChildren(hashMap)
            .addOnSuccessListener{
                Toast.makeText(this,"Anuncio marcado como vendido",Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {e->
                Toast.makeText(this,"No se pudo marcar como vendido",Toast.LENGTH_SHORT).show()
            }
    }

    private fun cargarInfoVendedor() {
        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(uidVendedor)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val nombre = "${snapshot.child("nombre").value}"
                    val telefono = "${snapshot.child("telefono").value}"
                    val imagenPerfil = "${snapshot.child("urlImagenPerfil").value}"

                    binding.TvNombre.text = nombre

                    try {
                        Glide.with(this@DetalleAnuncio)
                            .load(imagenPerfil)
                            .placeholder(R.drawable.imagen_perfil)
                            .into(binding.imagenPerfil)
                    }catch (e: Exception){

                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    private fun cargarImagenesAnuncio(){
        imagenSliderArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Anuncios")
        ref.child(idAnuncio).child("Imagenes")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    imagenSliderArrayList.clear()
                    for (ds in snapshot.children){
                        try{
                            val modeloImgSlider = ds.getValue(ModeloImgSlider::class.java)
                            imagenSliderArrayList.add(modeloImgSlider!!)
                        }catch (e: Exception){

                        }
                    }

                    val adaptadorImgSlider = AdaptadorImgSlider(this@DetalleAnuncio, imagenSliderArrayList)
                    binding.imagenSliderVP.adapter = adaptadorImgSlider

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    private fun comprobarAnuncioFav(){
        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child("${firebaseAuth.uid}").child("Favoritos").child(idAnuncio)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    favorito = snapshot.exists()

                    if(favorito){
                        binding.IbFavorite.setImageResource(R.drawable.ic_sifavorito)
                    }else{
                        binding.IbFavorite.setImageResource(R.drawable.ic_favorite)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

    }


    private fun eliminarAnuncio(){
        val ref = FirebaseDatabase.getInstance().getReference("Anuncios")
        ref.child(idAnuncio)
            .removeValue()
            .addOnSuccessListener{
                startActivity(Intent(this@DetalleAnuncio,MainActivity::class.java))
                finishAffinity()
                Toast.makeText(this, "Anuncio Eliminado", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "No se pudo eliminar el anuncio", Toast.LENGTH_SHORT).show()

            }

    }

}