package com.example.medilink.Fragmentos

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.medilink.Login.correologin
import com.example.medilink.R
import com.example.medilink.databinding.FragmentCuentaBinding
import com.google.firebase.auth.FirebaseAuth
import android.content.Context
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.medilink.Constantes
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FragmentCuenta : Fragment() {

    private lateinit var binding: FragmentCuentaBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCuentaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        LeerInf()

        binding.BtnCerrarSesion.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(requireContext(), correologin::class.java))
            activity?.finishAffinity()
        }
    }

    private fun LeerInf() {
        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child("${firebaseAuth.uid}")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val email = "${snapshot.child("email").value}"
                    val nombre = "${snapshot.child("nombre").value}"
                    val primerapellido = "${snapshot.child("primerapellido").value}"
                    val segundoapellido = "${snapshot.child("segundoapellido").value}"
                    val cp = "${snapshot.child("cp").value}"
                    val telefono = "${snapshot.child("telefono").value}"
                    val imagen = "${snapshot.child("url_imagenperfil").value}"
                    val fecha_nac = "${snapshot.child("fecha_nac").value}"
                    var tiempo = "${snapshot.child("tiempo").value}"
                    val localidad = "${snapshot.child("localidad").value}"

                    if (tiempo == "null"){
                        tiempo = "0"
                    }

                    val fort_tiempo = Constantes.obtenerFecha(tiempo.toLong())

                    binding.TvLocalidad.text = localidad
                    binding.TvEmail.text = email
                    binding.TvNombre.text = nombre
                    binding.TvPrimerapellido.text = primerapellido
                    binding.TvSegundoapellido.text = segundoapellido
                    binding.TvCp.text = cp
                    binding.TvTelefono.text = telefono
                    binding.TvFechaNac.text = fecha_nac

                    try {
                        Glide.with(requireContext())
                            .load(imagen)
                            .placeholder(R.drawable.ic_cuenta)
                            .into(binding.TvPerfil)
                    } catch (e: Exception) {
                        Toast.makeText(
                            requireContext(),
                            "${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }
}
