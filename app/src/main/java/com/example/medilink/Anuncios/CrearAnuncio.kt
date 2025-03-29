package com.example.medilink.Anuncios

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.view.Menu
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.medilink.Adaptadores.AdaptadorImagenSeleccionada
import com.example.medilink.Constantes
import com.example.medilink.MainActivity
import com.example.medilink.Modelo.ModeloImagenSeleccionada
import com.example.medilink.R
import com.example.medilink.databinding.ActivityCrearAnuncioBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CrearAnuncio : AppCompatActivity() {

    private lateinit var binding : ActivityCrearAnuncioBinding
    private lateinit var  firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    private var imagenUri : Uri?= null

    private lateinit var imagenesSeleccionadasArrayList : ArrayList<ModeloImagenSeleccionada>
    private lateinit var adaptadorImagenSeleccionada : AdaptadorImagenSeleccionada

    private var Edicion = false
    private var idAnuncioEditar = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityCrearAnuncioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Por favor espere")
        progressDialog.setCanceledOnTouchOutside(false)

        val adaptadorCat = ArrayAdapter(this,R.layout.item_categoria, Constantes.categorias)
        binding.Categoria.setAdapter(adaptadorCat)

        Edicion = intent.getBooleanExtra("Edicion",false)

        if(Edicion){
            idAnuncioEditar = intent.getStringExtra("idAnuncio")?:""
            cargarDetalles()
            binding.BtnCrearAnuncio.text = "Actualizar anuncio"
        }else{
            binding.BtnCrearAnuncio.text = "Crear anuncio"

        }

        imagenesSeleccionadasArrayList = ArrayList()
        cargarImagenes()

        binding.agregarImg.setOnClickListener{
            mostrarOpciones()
        }

        binding.BtnCrearAnuncio.setOnClickListener{
            validarDatos()
            }

        binding.EtFechaACaducar.apply {
            inputType = InputType.TYPE_NULL
            setOnClickListener {
                establecerFecha()
            }
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun establecerFecha(){
        val miCalendario = Calendar.getInstance()
        val datePicker = DatePickerDialog.OnDateSetListener{datePicker,anio,mes,dia->

            miCalendario.set(Calendar.YEAR,anio)
            miCalendario.set(Calendar.MONTH,mes)
            miCalendario.set(Calendar.DAY_OF_MONTH,dia)

            val formato = "dd/MM/yyyy"
            val sdf = SimpleDateFormat(formato, Locale.ENGLISH)
                binding.EtFechaACaducar.setText(sdf.format(miCalendario.time))
        }
        DatePickerDialog(this,datePicker,miCalendario.get(Calendar.YEAR),
            miCalendario.get(Calendar.MONTH),miCalendario.get(Calendar.DAY_OF_MONTH)).show()
    }


    private fun cargarDetalles() {
        val ref = FirebaseDatabase.getInstance().getReference("Anuncios")
        ref.child(idAnuncioEditar)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val marca = "${snapshot.child("marca").value}"
                    val descripcion = "${snapshot.child("descripcion").value}"
                    val categoria = "${snapshot.child("categoria").value}"
                    val nombreproducto = "${snapshot.child("nombreproducto").value}"
                    val fecha_vencimiento = "${snapshot.child("fecha_vencimiento").value}"
                    val localidad = "${snapshot.child("localidad").value}"
                    val cantidad = "${snapshot.child("cantidad").value}"

                    binding.EtMarca.setText(marca)
                    binding.EtDescripcion.setText(descripcion)
                    binding.Categoria.setText(categoria)
                    binding.Categoria.isEnabled = false
                    binding.EtNombreMedicamento.setText(nombreproducto)
                    binding.EtFechaACaducar.setText(fecha_vencimiento)
                    binding.EtLocacion.setText(localidad)
                    binding.EtCantidad.setText(cantidad)

                    val refImagenes = snapshot.child("Imagenes").ref
                    refImagenes.addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for(ds in snapshot.children){
                                val id = "${ds.child("id").value}"
                                val imagenUrl = "${ds.child("imagenUrl").value}"

                                val modeloImgSelect = ModeloImagenSeleccionada(
                                    id,null,imagenUrl,true
                                )
                                imagenesSeleccionadasArrayList.add(modeloImgSelect)
                            }
                            cargarImagenes()
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }
                    })


                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    private var nombreproducto = ""
    private var categoria = ""
    private var marca = ""
    private var descripcion = ""
    private var fecha_vencimiento = ""
    private var localidad = ""
    private var cantidad = ""
    private var latitud = 0.0
    private var longitud = 0.0


    private fun validarDatos(){
        nombreproducto = binding.EtNombreMedicamento.text.toString().trim()
        categoria = binding.Categoria.text.toString().trim()
        marca = binding.EtMarca.text.toString().trim()
        descripcion = binding.EtDescripcion.text.toString().trim()
        fecha_vencimiento = binding.EtFechaACaducar.text.toString().trim()
        localidad = binding.EtLocacion.text.toString().trim()
        cantidad = binding.EtCantidad.text.toString().trim()

        if (nombreproducto.isEmpty()){
            binding.EtNombreMedicamento.error = "Ingrese el nombre del medicamento"
            binding.EtNombreMedicamento.requestFocus()
        }
        else if(categoria.isEmpty()){
            binding.EtNombreMedicamento.error = "Ingrese la categoria del medicamento"
            binding.EtNombreMedicamento.requestFocus()
        }
        else if(marca.isEmpty()){
            binding.EtNombreMedicamento.error = "Ingrese la marca del medicamento"
            binding.EtNombreMedicamento.requestFocus()
        }
        else if(descripcion.isEmpty()){
            binding.EtNombreMedicamento.error = "Ingrese la descripcion del medicamento"
            binding.EtNombreMedicamento.requestFocus()
        }
        else if(fecha_vencimiento.isEmpty()){
            binding.EtNombreMedicamento.error = "Ingrese la fecha de vencimiento del medicamento"
            binding.EtNombreMedicamento.requestFocus()
        }
        else if(cantidad.isEmpty()){
            binding.EtNombreMedicamento.error = "Ingrese la cantidad del medicamento"
            binding.EtNombreMedicamento.requestFocus()
        }
        else{
            if(Edicion){
                actualizarAnuncio()
            }else{
                 if(imagenUri == null){
                    Toast.makeText(this,"Seleccione una imagen",Toast.LENGTH_SHORT).show()
                }else{
                     subirAnuncio()
                 }
            }

        }


    }

    private fun actualizarAnuncio() {
        progressDialog.setMessage("Actualizando anuncio")
        progressDialog.show()

        val hashMap = HashMap<String,Any>()
        hashMap["nombreproducto"] = "${nombreproducto}"
        hashMap["categoria"] = "${categoria}"
        hashMap["marca"] = "${marca}"
        hashMap["descripcion"] = "${descripcion}"
        hashMap["fecha_vencimiento"] = "${fecha_vencimiento}"
        hashMap["localidad"] = "${localidad}"
        hashMap["cantidad"] = "${cantidad}"
        hashMap["latitud"] = latitud
        hashMap["longitud"] = longitud

        val ref = FirebaseDatabase.getInstance().getReference("Anuncios")
        ref.child(idAnuncioEditar)
            .updateChildren(hashMap)
            .addOnSuccessListener{
                progressDialog.dismiss()
                cargarImagenesStorage(idAnuncioEditar)
            }
            .addOnFailureListener {
                progressDialog.dismiss()
                Toast.makeText(this,"No se pudo actualizar el anuncio",Toast.LENGTH_SHORT).show()
            }
    }

    private fun subirAnuncio() {
        progressDialog.setMessage("Subiendo anuncio")
        progressDialog.show()

        val tiempo = Constantes.obtenerTiempoDis()

        val ref = FirebaseDatabase.getInstance().getReference("Anuncios")
        val keyId = ref.push().key

        val hashMap = HashMap<String,Any>()
        hashMap["id"] ="${keyId}"
        hashMap["uid"] ="${firebaseAuth.uid}"
        hashMap["nombreproducto"] = "${nombreproducto}"
        hashMap["categoria"] = "${categoria}"
        hashMap["marca"] = "${marca}"
        hashMap["descripcion"] = "${descripcion}"
        hashMap["fecha_vencimiento"] = "${fecha_vencimiento}"
        hashMap["localidad"] = "${localidad}"
        hashMap["tiempo"] = tiempo
        hashMap["cantidad"] = "${cantidad}"
        hashMap["latitud"] = latitud
        hashMap["longitud"] = longitud
        hashMap["estado"] = "${Constantes.anuncio_disponible}"

        ref.child(keyId!!)
            .setValue(hashMap)
            .addOnSuccessListener{
                cargarImagenesStorage(keyId)
            }
            .addOnFailureListener {
                Toast.makeText(this,"No se pudo subir el anuncio",Toast.LENGTH_SHORT).show()
            }



    }

    private fun cargarImagenesStorage(keyId: String){
        for ( i in imagenesSeleccionadasArrayList.indices) {
            val modeloImagenSeleccionada = imagenesSeleccionadasArrayList[i]

            if (!modeloImagenSeleccionada.deInternet) {
                val nombreImagen = modeloImagenSeleccionada.id
                val rutaNombreImagen = "Anuncios/$nombreImagen"

                val storageReference = FirebaseStorage.getInstance().getReference(rutaNombreImagen)
                storageReference.putFile(modeloImagenSeleccionada.imagenUri!!)
                    .addOnSuccessListener { taskSnapshot ->
                        val uriTask = taskSnapshot.storage.downloadUrl
                        while (!uriTask.isSuccessful);
                        val urlImagenCargada = "${uriTask.result}"

                        if (uriTask.isSuccessful) {
                            val hashMap = HashMap<String, Any>()
                            hashMap["id"] = "${modeloImagenSeleccionada.id}"
                            hashMap["imagenUrl"] = "$urlImagenCargada"

                            val ref = FirebaseDatabase.getInstance().getReference("Anuncios")
                            ref.child(keyId).child("Imagenes")
                                .child(nombreImagen)
                                .updateChildren(hashMap)
                        }
                        if (Edicion){
                            progressDialog.dismiss()
                            val intent = Intent(this@CrearAnuncio,MainActivity::class.java)
                            startActivity(intent)
                            Toast.makeText(this,"Anuncio actualizado correctamente",Toast.LENGTH_SHORT).show()
                            finishAffinity()
                        }else{
                            progressDialog.dismiss()
                            Toast.makeText(this, "Anuncio subido correctamente", Toast.LENGTH_SHORT)
                                .show()
                            limpiarCampos()

                        }

                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "No se pudo subir la imagen", Toast.LENGTH_SHORT)
                            .show()
                    }

            }


        }

    }

    private fun limpiarCampos() {
        imagenesSeleccionadasArrayList.clear()
        adaptadorImagenSeleccionada.notifyDataSetChanged()
        binding.EtNombreMedicamento.setText("")
        binding.Categoria.setText("")
        binding.EtMarca.setText("")
        binding.EtDescripcion.setText("")
        binding.EtFechaACaducar.setText("")
        binding.EtLocacion.setText("")
        binding.EtCantidad.setText("")

    }

    private fun mostrarOpciones() {
        val popupMenu = PopupMenu(this,binding.agregarImg)

        popupMenu.menu.add(Menu.NONE,1,1,"Camara")
        popupMenu.menu.add(Menu.NONE,2,2,"Galeria")

        popupMenu.show()

        popupMenu.setOnMenuItemClickListener { item ->
            val item_id = item.itemId
            if (item_id == 1) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permisoCamara.launch(kotlin.arrayOf(android.Manifest.permission.CAMERA))
                }else{
                    permisoCamara.launch(kotlin.arrayOf(
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ))
                }
            } else if (item_id == 2) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    imagenGaleria()
                }else{
                    SolicitarPermisoAlmacenamiento.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }

            }
            true
        }
    }

    private val SolicitarPermisoAlmacenamiento = registerForActivityResult(
        ActivityResultContracts.RequestPermission()){esConcedido->
        if (esConcedido){
            imagenGaleria()
        }else{
            Toast.makeText(
                this,
                "No se otorgaron permisos al almacenamiento",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    private val permisoCamara = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()){resultado->
        var todosConcedidos = true
        for (seConcedio in resultado.values){
            todosConcedidos = todosConcedidos && seConcedio
        }
        if(todosConcedidos) {
            imageCamara()

        }else
            Toast.makeText(
                this,
                "No se otorgaron permisos a la camara",
                Toast.LENGTH_SHORT
            ).show()
    }

    private val resultadoGaleria_ARL =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { resultado ->
            if (resultado.resultCode == RESULT_OK) {
                val data = resultado.data
                imagenUri = data?.data

                val tiempo = "${Constantes.obtenerTiempoDis()}"
                val modeloImgSelect = ModeloImagenSeleccionada(
                    tiempo,imagenUri,null,false
                )
                imagenesSeleccionadasArrayList.add(modeloImgSelect)
                cargarImagenes()

            } else {
                Toast.makeText(this, "No se seleccionó ninguna imagen", Toast.LENGTH_SHORT).show()
            }
        }

    private fun imageCamara() {
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE, "Titulo_imagen")
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Descripcion_imagen")
        imagenUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imagenUri)
        resultadoCamara_ARL.launch(intent)
    }

    private val resultadoCamara_ARL =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { resultado ->
            if (resultado.resultCode == RESULT_OK) {
                val tiempo = "${Constantes.obtenerTiempoDis()}"
                val modeloImgSelect = ModeloImagenSeleccionada(
                    tiempo,imagenUri,null,false
                )
                imagenesSeleccionadasArrayList.add(modeloImgSelect)
                cargarImagenes()
            }
            else {

                Toast.makeText(this, "No se seleccionó ninguna imagen", Toast.LENGTH_SHORT).show()
            }
        }

    private fun imagenGaleria() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        resultadoGaleria_ARL.launch(intent)
    }

    private fun cargarImagenes() {
        adaptadorImagenSeleccionada = AdaptadorImagenSeleccionada(this,imagenesSeleccionadasArrayList,idAnuncioEditar)
        binding.RVImagenes.adapter = adaptadorImagenSeleccionada

    }
}