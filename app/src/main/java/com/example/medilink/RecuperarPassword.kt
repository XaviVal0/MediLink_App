package com.example.medilink

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.medilink.databinding.ActivityRecuperarPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class RecuperarPassword : AppCompatActivity() {

    private lateinit var binding: ActivityRecuperarPasswordBinding
    private lateinit var progressDialog: ProgressDialog
    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRecuperarPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Por favor espere")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.IbBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.BtnInstrucciones.setOnClickListener {
            validarEmail()
        }

    }

    private var email = ""
    private fun validarEmail() {
        email = binding.EtEmail.text.toString().trim()
        if(email.isEmpty()){
            Toast.makeText(this,"Ingrese su correo electronico", Toast.LENGTH_SHORT).show()
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this,"Correo electronico invalido", Toast.LENGTH_SHORT).show()
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.EtEmail.error = "Correo electronico invalido"
            binding.EtEmail.requestFocus()
        }else{
            enviarInstrucciones()
        }

    }

    private fun enviarInstrucciones() {
        progressDialog.setMessage("Enviando instrucciones a email ${email}")
        progressDialog.dismiss()

        firebaseAuth.sendPasswordResetEmail(email)
            .addOnSuccessListener{
                progressDialog.dismiss()
                Toast.makeText(this,"Instrucciones enviadas", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                progressDialog.dismiss()
                Toast.makeText(this,"No se pudo enviar instrucciones", Toast.LENGTH_SHORT).show()
            }
    }
}