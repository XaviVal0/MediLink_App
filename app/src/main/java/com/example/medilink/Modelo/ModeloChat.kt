package com.example.medilink.Modelo

class ModeloChat {

    var idMensaje : String = ""
    var nombre : String = ""
    var tipoMensaje : String = ""
    var mensaje : String = ""
    var emisorUid : String = ""
    var receptorUid : String = ""
    var tiempo : Long = 0

    constructor()
    constructor(
        idMensaje: String,
        nombre: String,
        tipoMensaje: String,
        mensaje: String,
        emisorUid: String,
        receptorUid: String,
        tiempo: Long
    ) {
        this.idMensaje = idMensaje
        this.tipoMensaje = tipoMensaje
        this.mensaje = mensaje
        this.emisorUid = emisorUid
        this.receptorUid = receptorUid
        this.tiempo = tiempo
        this.nombre = nombre
    }


}