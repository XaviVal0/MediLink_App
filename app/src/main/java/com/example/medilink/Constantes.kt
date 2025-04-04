package com.example.medilink

import android.content.Context
import android.text.format.DateFormat
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.Arrays
import java.util.Locale
import java.util.Calendar

object Constantes {

    const val MENSAJE_TIPO_TEXTO = "TEXTO"
    const val MENSAJE_TIPO_IMAGEN = "IMAGEN"

    const val anuncio_disponible = "Disponible"
    const val anuncio_no_disponible = "No Disponible"

    const val NOTIFICACION_DE_NUEVO_MENSAJE = "NOTIFICACION_DE_NUEVO_MENSAJE"

    const val FCM_SERVER_KEY =
        "AAAAESoUZA0:APA91bFpSSXr9G0_8U0r-_xLjMk97cncaDcwWva_9hy-yKoQFDvEFBvgS6wb-EibhkUbWhxH5RgMOHQeWdIMt0fbXuVbAUxgNLwN4v4_fM5YAs9yxF6scuxJoHSTemz4Eidi-x-2roJ-"

    val categorias = arrayOf(
        "Analgesicos y antiflamatorios",
        "Problemas digestivos",
        "Antihistamínicos y Antialérgicos",
        "Vitaminas y Suplementos",
        "Medicamentos para síntomas de resfriado y gripe",
        "Antisépticos y Desinfectantes",
        "Medicamentos para el cuidado de la piel",
        "Sueros y Rehidratantes",
        "Medicamentos para problemas musculares menores",
        "Colirios y gotas oftálmicas",
        "Productos para el cuidado bucal"

    )

    val categoriaIconos = arrayOf(
        R.drawable.ic_paracetamol,
        R.drawable.ic_digestivo_final,
        R.drawable.ic_paracetamol,
        R.drawable.ic_paracetamol,
        R.drawable.ic_paracetamol,
        R.drawable.ic_paracetamol,
        R.drawable.ic_paracetamol,
        R.drawable.ic_paracetamol,
        R.drawable.ic_paracetamol,
        R.drawable.ic_paracetamol,
        R.drawable.ic_paracetamol


    )

    fun obtenerTiempoDis() : Long{
        return System.currentTimeMillis()
    }

    fun obtenerFecha(tiempo : Long) : String{
        val calendario = Calendar.getInstance(Locale.ENGLISH)
            calendario.timeInMillis = tiempo

        return DateFormat.format("dd/MM/yyyy", calendario).toString()
    }

    fun obtenerHora(tiempo : Long) : String{
        val calendario = Calendar.getInstance(Locale.ENGLISH)
            calendario.timeInMillis = tiempo
        return DateFormat.format("hh:mm a", calendario).toString()
    }


    fun agregarAnuncioFav (context: Context,idAnuncio : String){
        val firebaseAuth = FirebaseAuth.getInstance()
        val tiempo = obtenerTiempoDis()

        val hashMap = HashMap<String,Any>()
        hashMap["idAnuncio"] = idAnuncio
        hashMap["tiempo"] = tiempo

        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(firebaseAuth.uid!!).child("Favoritos").child(idAnuncio)
            .setValue(hashMap)
            .addOnSuccessListener{

                Toast.makeText(context,"Agregado a Favoritos",Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e->
                Toast.makeText(context,"${e.message}",Toast.LENGTH_SHORT).show()

            }

    }

    fun eliminarAnuncioFav (context: Context,idAnuncio : String){
        val firebaseAuth = FirebaseAuth.getInstance()

        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(firebaseAuth.uid!!).child("Favoritos").child(idAnuncio)
            .removeValue()
            .addOnSuccessListener {
                Toast.makeText(context,"Eliminado de Favoritos",Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e->
                Toast.makeText(context,"${e.message}",Toast.LENGTH_SHORT).show()

            }
    }

    fun RutaChat(receptorUid : String,emisorUid : String) : String{
        val arrayUid = arrayOf(receptorUid,emisorUid)
        Arrays.sort(arrayUid)
        return "${arrayUid[0]}_${arrayUid[1]}"
    }

}