package com.example.medilink

import android.Manifest
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
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.medilink.databinding.ActivityEditarPerfilBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditarPerfil : AppCompatActivity() {

    private lateinit var binding: ActivityEditarPerfilBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    private var imageUri: Uri? = null

    private val permisosCamara =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { resultado ->
            var concedioTodos = true
            for (seConcede in resultado.values) {
                concedioTodos = concedioTodos && seConcede
            }
            if (concedioTodos) {
                imageCamara()
            } else {
                Toast.makeText(this, "No se aceptaron los permisos", Toast.LENGTH_SHORT).show()
            }
        }

    private val resultadoCamara_ARL =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { resultado ->
            if (resultado.resultCode == RESULT_OK) {

                subirImagenStorage()
                /*try {
                    Glide.with(this)
                        .load(imageUri)
                        .placeholder(R.drawable.imagen_perfil)
                        .into(binding.imgPerfil)
                } catch (e: Exception) {
                    Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show()
                }*/
            }
             else {

                Toast.makeText(this, "No se seleccionó ninguna imagen", Toast.LENGTH_SHORT).show()
            }
        }

    private val concederPermisoAlmacenamiento =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { esConcedido ->
            if (esConcedido) {
                imagenGaleria()
            } else {
                Toast.makeText(this, "El permiso de almacenamiento no fue concedido", Toast.LENGTH_SHORT).show()
            }
        }

    private val resultadoGaleria_ARL =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { resultado ->
            if (resultado.resultCode == RESULT_OK) {
                val data = resultado.data
                imageUri = data?.data
                subirImagenStorage()

                /*try {
                    Glide.with(this)
                        .load(imageUri)
                        .placeholder(R.drawable.imagen_perfil)
                        .into(binding.imgPerfil)
                } catch (e: Exception) {
                    Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show()
                }*/
            } else {
                Toast.makeText(this, "No se seleccionó ninguna imagen", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityEditarPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        // Cargar información del usuario
        cargarInf()
        binding.BtnActualizar.setOnClickListener{
            validarInf()
        }

        binding.FABCambiarImg.setOnClickListener {
            Selec_Img()
        }

        binding.EtFechaNac.apply {
            inputType = InputType.TYPE_NULL
            setOnClickListener {
                establecerFecha()
            }
        }

    }

    private var nombre = ""
    private var cp = ""
    private var fecha_nac = ""
    private var localidad = ""
    private var telefono = ""

    private fun validarInf() {
        nombre = binding.EtNombres.text.toString().trim()
        cp = binding.EtCp.text.toString().trim()
        fecha_nac = binding.EtFechaNac.text.toString().trim()
        localidad = binding.EtDireccion.text.toString().trim()
        telefono = binding.EtTelefono.text.toString().trim()

        if (nombre.isEmpty()) {
         Toast.makeText(this,"Ingrese su nombre",Toast.LENGTH_SHORT).show()
        }else if (fecha_nac.isEmpty()) {
            Toast.makeText(this,"Ingrese su fecha de nacimiento",Toast.LENGTH_SHORT).show()
        }else if(cp.isEmpty()){
            Toast.makeText(this,"Ingrese su codigo postal",Toast.LENGTH_SHORT).show()
        }else if(localidad.isEmpty()){
            Toast.makeText(this,"Ingrese su direccion(Colonia)",Toast.LENGTH_SHORT).show()
        }else if(telefono.isEmpty()){
            Toast.makeText(this,"Ingrese su telefono",Toast.LENGTH_SHORT).show()
        }else{
            actualizarInf()
        }

    }

    private fun establecerFecha(){
        val miCalendario = Calendar.getInstance()
        val datePicker = DatePickerDialog.OnDateSetListener{ datePicker, anio, mes, dia->

            miCalendario.set(Calendar.YEAR,anio)
            miCalendario.set(Calendar.MONTH,mes)
            miCalendario.set(Calendar.DAY_OF_MONTH,dia)

            val formato = "dd/MM/yyyy"
            val sdf = SimpleDateFormat(formato, Locale.ENGLISH)
            binding.EtFechaNac.setText(sdf.format(miCalendario.time))
        }
        DatePickerDialog(this,datePicker,miCalendario.get(Calendar.YEAR),
            miCalendario.get(Calendar.MONTH),miCalendario.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun actualizarInf() {
        progressDialog.setMessage("Actualizando Información")

        val hashMap : HashMap<String,Any> = HashMap()

        hashMap["nombre"] = "${nombre}"
        hashMap["cp"] = "${cp}"
        hashMap["fecha_nac"] = "${fecha_nac}"
        hashMap["localidad"] = "${localidad}"
        hashMap["telefono"] = "${telefono}"

        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(firebaseAuth.uid!!)
            .updateChildren(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(applicationContext,"Información Actualizada Correctamente",Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                progressDialog.dismiss()
                Toast.makeText(applicationContext,"Información No Actualizada",Toast.LENGTH_SHORT).show()
            }
    }

    private fun cargarInf() {
        val uid = firebaseAuth.uid
        if (uid.isNullOrEmpty()) {
            Toast.makeText(this, "Error: Usuario no autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.exists()) {
                        /*Toast.makeText(
                            this@EditarPerfil,
                            "Usuario no encontrado",
                            Toast.LENGTH_SHORT
                        ).show()
                        return*/
                    }

                    val nombre = snapshot.child("nombre").getValue(String::class.java) ?: ""
                    val cp = snapshot.child("cp").getValue(String::class.java) ?: ""
                    val imagen = snapshot.child("url_imagenperfil").getValue(String::class.java) ?: ""
                    val fecha_nac = snapshot.child("fecha_nac").getValue(String::class.java) ?: ""
                    val localidad = snapshot.child("localidad").getValue(String::class.java) ?: ""
                    val telefono = snapshot.child("telefono").getValue(String::class.java) ?: ""

                    // Mostrar datos en los campos de edición
                    binding.EtNombres.setText(nombre)
                    binding.EtCp.setText(cp)
                    binding.EtFechaNac.setText(fecha_nac)
                    binding.EtDireccion.setText(localidad)
                    binding.EtTelefono.setText(telefono)

                    // Cargar imagen con Glide
                    try {
                        Glide.with(this@EditarPerfil)
                            .load(imagen)
                            .placeholder(R.drawable.imagen_perfil)
                            .into(binding.imgPerfil)
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@EditarPerfil,
                            "Error al cargar imagen: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        this@EditarPerfil,
                        "Error en Firebase: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun subirImagenStorage() {
        progressDialog.setMessage("Subiendo Imagen")
        progressDialog.show()

        val rutaImagen = "Imagenes_Perfil/" + firebaseAuth.uid
        val ref = FirebaseStorage.getInstance().getReference(rutaImagen)
        ref.putFile(imageUri!!)
            .addOnSuccessListener { taskSnapshot ->
                val uriTask = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val urlImagenCargada = uriTask.result.toString()
                if (uriTask.isSuccessful){
                    actualizarImagenDB(urlImagenCargada)
                }
            }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                Toast.makeText(applicationContext,"${e.message}",Toast.LENGTH_SHORT).show()
            }
    }

    private fun actualizarImagenDB(urlImagenCargada: String) {
        progressDialog.setMessage("Actualizando Imagen")
        progressDialog.show()

        val hashMap : HashMap<String,Any> = HashMap()
        if (imageUri != null){
            hashMap["url_imagenperfil"] = urlImagenCargada
        }

        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(firebaseAuth.uid!!)
            .updateChildren(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(applicationContext,"Imagen Actualizada Correctamente",Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                Toast.makeText(applicationContext,"Imagen No Actualizada",Toast.LENGTH_SHORT).show()
            }

    }

    private fun Selec_Img() {
        val popMenu = PopupMenu(this, binding.FABCambiarImg)

        popMenu.menu.add(Menu.NONE, 1, 1, "Cámara")
        popMenu.menu.add(Menu.NONE, 2, 2, "Galería")

        popMenu.show()

        popMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                1 -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permisosCamara.launch(arrayOf(Manifest.permission.CAMERA))
                    } else {
                        permisosCamara.launch(
                            arrayOf(
                                Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            )
                        )
                    }
                }

                2 -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        imagenGaleria()
                    } else {
                        concederPermisoAlmacenamiento.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    }
                }
            }
            true
        }
    }

    private fun imageCamara() {
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE, "Titulo_imagen")
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Descripcion_imagen")
        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        resultadoCamara_ARL.launch(intent)
    }

    private fun imagenGaleria() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        resultadoGaleria_ARL.launch(intent)
    }
}
