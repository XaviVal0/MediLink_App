package com.example.medilink

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.medilink.Anuncios.CrearAnuncio
import com.example.medilink.Fragmentos.FragmentChats
import com.example.medilink.Fragmentos.FragmentCuenta
import com.example.medilink.Fragmentos.FragmentInicio
import com.example.medilink.Fragmentos.FragmentMisProductos
import com.example.medilink.Login.correologin
import com.example.medilink.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firebaseAuth = FirebaseAuth.getInstance()
        comprobarSesion()

        verFragmentInicio() // Fragmento inicial
        binding.BottomNV.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.Item_Incio -> {
                    verFragmentInicio()
                    true
                }

                R.id.Item_Chats -> {
                    verFragmentChats()
                    true
                }

                R.id.Item_Mis_Productos -> {
                    verFragmentMisProductos()
                    true
                }

                R.id.Item_Cuenta -> {
                    verFragmentCuenta()
                    true
                }

                R.id.Item_Publicar -> {
                    verFragmentPublicar()
                    true
                }

                else -> {
                    false
                }
            }
        }

    }

    private fun comprobarSesion() {
        if (firebaseAuth.currentUser == null) {
            startActivity(Intent(this, correologin::class.java))
            finishAffinity()
        }else{
            agergarFcmToken()
            solictarNotificaciones() 
        }
    }

    private fun verFragmentInicio() {
        val fragment = FragmentInicio()
        supportFragmentManager.beginTransaction()
            .replace(binding.FragmentL1.id, fragment, "FragmentInicio")
            .commit()
    }

    private fun verFragmentChats() {
        val fragment = FragmentChats()
        supportFragmentManager.beginTransaction()
            .replace(binding.FragmentL1.id, fragment, "FragmentChats")
            .commit()
    }

    private fun verFragmentMisProductos() {
        val fragment = FragmentMisProductos()
        supportFragmentManager.beginTransaction()
            .replace(binding.FragmentL1.id, fragment, "FragmentMisProductos")
            .commit()
    }

    private fun verFragmentCuenta() {
        val fragment = FragmentCuenta()
        supportFragmentManager.beginTransaction()
            .replace(binding.FragmentL1.id, fragment, "FragmentCuenta")
            .commit()
    }

    private fun verFragmentPublicar() {
        val intent = Intent(this, CrearAnuncio::class.java)
        intent.putExtra("Edicion", false)
        startActivity(intent)
    }

    private fun agergarFcmToken() {
        val miUid = "${firebaseAuth.uid}"
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { fcmToken ->
                val hasMap = HashMap<String, Any>()
                hasMap["fcmToken"] = "$fcmToken"
                val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
                ref.child(miUid)
                    .updateChildren(hasMap)
                    .addOnSuccessListener {

                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
                    }

            }

    }

    private fun solictarNotificaciones() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.POST_NOTIFICATIONS)==
                        PackageManager.PERMISSION_DENIED){
                        permisoNotificacion.launch(android.Manifest.permission.POST_NOTIFICATIONS)

            }
        }
    }

    private val permisoNotificacion =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){esConcedido->

        }

}
