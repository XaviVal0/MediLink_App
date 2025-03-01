package com.example.medilink.Login

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.medilink.Constantes
import com.example.medilink.MainActivity
import com.example.medilink.R
import com.example.medilink.databinding.ActivityRegistrocorreoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class registrocorreo : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrocorreoBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegistrocorreoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.Btnregistrar.setOnClickListener{
            validarInfo()
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private var primerapellido = ""
    private var segundoapellido = ""
    private var nombre = ""
    private var telefono = ""
    private var cp = ""
    private var email = ""
    private var password = ""
    private var r_password = ""
    private fun validarInfo(){

        primerapellido = binding.EtPrimerapellido.text.toString().trim()
        segundoapellido = binding.EtSegundoapellido.text.toString().trim()
        nombre = binding.EtNombre.text.toString().trim()
        telefono = binding.EtTelefono.text.toString().trim()
        cp = binding.EtCp.text.toString().trim()
        email = binding.EtEmail.text.toString().trim()
        password = binding.EtPassword.text.toString().trim()
        r_password = binding.EtRPassword.text.toString().trim()

        if (primerapellido.isEmpty()) {
            binding.EtPrimerapellido.error = "Ingrese su primer apellido"
            binding.EtPrimerapellido.requestFocus()
        }
        else if (segundoapellido.isEmpty()) {
            binding.EtSegundoapellido.error = "Ingrese su segundo apellido"
            binding.EtSegundoapellido.requestFocus()
        }
        else if (nombre.isEmpty()) {
            binding.EtNombre.error = "Ingrese su nombre"
            binding.EtNombre.requestFocus()
        }
        else if (telefono.isEmpty()) {
            binding.EtTelefono.error = "Ingrese su teléfono"
            binding.EtTelefono.requestFocus()
        }
        else if (cp.isEmpty()) {
            binding.EtCp.error = "Ingrese su código postal"
            binding.EtCp.requestFocus()
        }
        else if (email.isEmpty()) {
            binding.EtEmail.error = "Ingrese su correo electrónico"
            binding.EtEmail.requestFocus()
        }
        else if (password.isEmpty()) {
            binding.EtPassword.error = "Ingrese una contraseña"
            binding.EtPassword.requestFocus()
        }
        else if (r_password.isEmpty()) {
            binding.EtRPassword.error = "Confirme su contraseña"
            binding.EtRPassword.requestFocus()
        }
        else if (password != r_password) {
            binding.EtRPassword.error = "No coinciden las contraseñas"
            binding.EtRPassword.requestFocus()
        }
        else {
            RegistrarUsuario()
        }


    }
    private fun RegistrarUsuario(){
        progressDialog.setMessage("Creando su cuenta")
        progressDialog.show()

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener{
                llenarInfoDB()
            }


            .addOnFailureListener{e->
                progressDialog.dismiss()
                Toast.makeText(
                    this,
                    "No se registro el Usuario debido a ${e.message}",
                    Toast.LENGTH_SHORT).show()
            }

    }

    private fun llenarInfoDB(){
        progressDialog.setMessage("Guardando informacion")

        val tiempo = Constantes.obtenerTiempoDis()
        val emailUsuario = firebaseAuth.currentUser!!.email
        val uidUsuario = firebaseAuth.uid

        val hashMap = HashMap<String,Any>()
        hashMap["primerapellido"] = primerapellido
        hashMap["segundoapellido"] = segundoapellido
        hashMap["nombre"] = nombre
        hashMap["telefono"] = telefono
        hashMap["cp"] = cp
        hashMap["url_imagenperfil"] = ""
        hashMap["proveedor"] = "Email"
        hashMap["escribiendo"] = ""
        hashMap["tiempo"] = tiempo
        hashMap["online"] = true
        hashMap["email"] = "${emailUsuario}"
        hashMap["uid"] = "${uidUsuario}"
        hashMap["fecha_nac"] = ""
        hashMap ["localidad"]= ""


        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(uidUsuario!!)
            .setValue(hashMap)
            .addOnSuccessListener{
                progressDialog.dismiss()
                startActivity(Intent(this,MainActivity::class.java))
                finishAffinity()

            }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                Toast.makeText(
                    this,
                    "No se registro debido a ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }




    }
}