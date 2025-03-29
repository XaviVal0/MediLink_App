package com.example.medilink.DetalleVendedor

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.medilink.Adaptadores.AdaptadorAnuncio
import com.example.medilink.Modelo.ModeloAnuncio
import com.example.medilink.R
import com.example.medilink.databinding.ActivityDetalleVendedorBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DetalleVendedor : AppCompatActivity() {

    private lateinit var binding: ActivityDetalleVendedorBinding
    private var uidVendedor = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetalleVendedorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        uidVendedor = intent.getStringExtra("uidVendedor").toString()
        cargarDatosVendedor()
        cargarAnunciosVendedor()

    }

    private fun cargarAnunciosVendedor() {
        val anuncioArrayList : ArrayList<ModeloAnuncio> = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Anuncios")
        ref.orderByChild("uid").equalTo(uidVendedor)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    anuncioArrayList.clear()
                    for (ds in snapshot.children){
                     try{
                         val modeloAnuncio = ds.getValue(ModeloAnuncio::class.java)
                         anuncioArrayList.add(modeloAnuncio!!)

                     }catch (e:Exception){

                     }
                    }
                    val adaptador = AdaptadorAnuncio(this@DetalleVendedor,anuncioArrayList)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    private fun cargarDatosVendedor() {
        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(uidVendedor)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val nombre = "${snapshot.child("nombre").value}"
                    val imagenPerfil = "${snapshot.child("url_imagenperfil").value}"
                    val primerApellido = "${snapshot.child("primerapellido").value}"
                    val segundoApellido = "${snapshot.child("segundoapellido").value}"
                    val telefono = "${snapshot.child("telefono").value}"


                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }
}