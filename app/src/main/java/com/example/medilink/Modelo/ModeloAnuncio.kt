package com.example.medilink.Modelo

class ModeloAnuncio {

    var id: String = ""
    var uid: String = ""
    var marca: String = ""
    var categoria : String = ""
    var localidad: String = ""
    var nombreproducto: String = ""
    var estado: String = ""
    var descripcion: String = ""
    var tiempo: Long = 0
    var fecha_vencimiento: String = ""
    var cantidad: String = ""
    var latitud = 0
    var longitud = 0
    var favorito = false

    constructor()
    constructor(
        id: String,
        uid: String,
        marca: String,
        categoria: String,
        localidad: String,
        nombreproducto: String,
        descripcion: String,
        tiempo: Long,
        fecha_vencimiento: String,
        cantidad: String,
        latitud: Int,
        longitud: Int,
        favorito: Boolean,
        estado : String
    ) {
        this.id = id
        this.uid = uid
        this.marca = marca
        this.categoria = categoria
        this.localidad = localidad
        this.nombreproducto = nombreproducto
        this.descripcion = descripcion
        this.tiempo = tiempo
        this.fecha_vencimiento = fecha_vencimiento
        this.cantidad = cantidad
        this.latitud = latitud
        this.longitud = longitud
        this.favorito = favorito
        this.estado = estado
    }


}