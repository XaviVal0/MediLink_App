package com.example.medilink

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
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
                else -> {
                    false
                }
            }
        }

        binding.FAB.setOnClickListener {
            startActivity(Intent(this, CrearAnuncio::class.java))
        }
    }

    private fun comprobarSesion() {
        if (firebaseAuth.currentUser == null) {
            startActivity(Intent(this, correologin::class.java))
            finishAffinity()
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
}
