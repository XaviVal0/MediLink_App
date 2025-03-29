package com.example.medilink.Modelo

class ModeloImgSlider {

    var id : String = ""
    var imagenUrl : String=""

    constructor()

    constructor(imagenUrl: String, id: String) {
        this.imagenUrl = imagenUrl
        this.id = id
    }


}