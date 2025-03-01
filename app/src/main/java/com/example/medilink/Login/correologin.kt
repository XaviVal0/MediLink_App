package com.example.medilink.Login

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.medilink.Constantes
import com.example.medilink.MainActivity
import com.example.medilink.R
import com.example.medilink.databinding.ActivityCorreologinBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase

class correologin : AppCompatActivity() {

    private lateinit var binding: ActivityCorreologinBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityCorreologinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firebaseAuth = FirebaseAuth.getInstance()
        comprobarSesion()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.BtnIngresar.setOnClickListener {
            validarInfo()
        }

        binding.BtnIngresarGoogle.setOnClickListener {
            googlelogin()
        }

        binding.TxtRegistrarme.setOnClickListener {
            startActivity(Intent(this@correologin, registrocorreo::class.java))
        }
    }

    private fun googlelogin() {
        val googleSignInIntent = mGoogleSignInClient.signInIntent
        googleSignInARL.launch(googleSignInIntent)
    }

    private val googleSignInARL = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { resultado ->
        if (resultado.resultCode == RESULT_OK) {
            val data = resultado.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val cuenta = task.getResult(ApiException::class.java)
                autenticacionGoogle(cuenta.idToken)
            } catch (e: Exception) {
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun autenticacionGoogle(idToken: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener { resultadoAuth ->
                if (resultadoAuth.additionalUserInfo!!.isNewUser) {
                    llenarInfBD()
                } else {
                    startActivity(Intent(this, MainActivity::class.java))
                    finishAffinity()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
    }

    private fun llenarInfBD() {
        progressDialog.setMessage("Guardando información")
        progressDialog.show()

        val tiempo = Constantes.obtenerTiempoDis()
        val emailUsuario = firebaseAuth.currentUser?.email ?: ""
        val uidUsuario = firebaseAuth.currentUser?.uid ?: ""
        val nombreCompleto = firebaseAuth.currentUser?.displayName ?: "Desconocido"

        val partes = nombreCompleto.split(" ")
        val nombre = if (partes.isNotEmpty()) partes[0] else ""
        val primerApellido = if (partes.size > 1) partes[1] else ""
        val segundoApellido = if (partes.size > 2) partes[2] else ""

        val hashMap = hashMapOf(
            "primerapellido" to primerApellido,
            "segundoapellido" to segundoApellido,
            "nombre" to nombre,
            "telefono" to "",
            "cp" to "cp",
            "url_imagenperfil" to "",
            "proveedor" to "Google",
            "escribiendo" to "",
            "tiempo" to tiempo,
            "online" to true,
            "email" to emailUsuario,
            "uid" to uidUsuario,
            "fecha_nac" to "",
            "localidad" to "",
        )

        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(uidUsuario)
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this, "No se registró debido a ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private var email = ""
    private var password = ""

    private fun validarInfo() {
        email = binding.EtEmail.text.toString().trim()
        password = binding.EtPassword.text.toString().trim()

        when {
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.EtEmail.error = "Email inválido"
                binding.EtEmail.requestFocus()
            }
            email.isEmpty() -> {
                binding.EtEmail.error = "Ingrese Email"
                binding.EtEmail.requestFocus()
            }
            password.isEmpty() -> {
                binding.EtPassword.error = "Ingrese password"
                binding.EtPassword.requestFocus()
            }
            else -> {
                loginUsuario()
            }
        }
    }

    private fun loginUsuario() {
        progressDialog.setMessage("Iniciando sesión")
        progressDialog.show()

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                progressDialog.dismiss()
                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()
                Toast.makeText(this, "Bienvenido(a)", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this, "No se pudo iniciar sesión debido a ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun comprobarSesion() {
        if (firebaseAuth.currentUser != null && GoogleSignIn.getLastSignedInAccount(this) != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finishAffinity()
        }
    }
}
