package com.example.medilink

import android.text.format.DateFormat
import java.util.Locale
import java.util.Calendar

object Constantes {

    fun obtenerTiempoDis() : Long{
        return System.currentTimeMillis()
    }

    fun obtenerFecha(tiempo : Long) : String{
        val calendario = Calendar.getInstance(Locale.ENGLISH)
            calendario.timeInMillis = tiempo

        return DateFormat.format("dd/MM/yyyy", calendario).toString()
    }
}