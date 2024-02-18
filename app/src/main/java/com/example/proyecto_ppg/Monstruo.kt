package com.example.proyecto_ppg

class Monstruo(
    private var nombre: String,
    private var nivel: Int
) {
    private var salud: Int = 0
    private var ataque: Int = 0

    init {
        calcularSalud() // Inicializar la salud según el nivel al nivel 1
        calcularAtaque() // Inicializar el ataque según el nivel al nivel 1
    }

    fun getNombre(): String {
        return nombre
    }
    fun setNombre(nuevoNombre:String) {
        nombre = nuevoNombre
    }
    fun getNivel(): Int {
        return nivel
    }
    fun setNivel(nuevoNivel:Int) {
        nivel = nuevoNivel
    }
    fun getSalud(): Int {
        return salud
    }
    fun setSalud(nuevaSalud: Int) {
        salud = nuevaSalud
    }
    fun getAtaque(): Int {
        return ataque
    }
    fun setAtaque(nuevoAtaque: Int) {
        ataque = nuevoAtaque
    }
    private fun calcularSalud() {
        salud = when (nivel) {
            1 -> 100
            2 -> 125
            3 -> 150
            4 -> 200
            5 -> 250
            6 -> 350
            7 -> 400
            8 -> 600
            9 -> 800
            10 -> 1000
            else -> 100 // Valor por defecto si el nivel está fuera del rango especificado
        }
    }
    private fun calcularAtaque() {
        ataque = when (nivel) {
            1 -> 5
            2 -> 10
            3 -> 15
            4 -> 20
            5 -> 50
            6 -> 60
            7 -> 120
            8 -> 200
            9 -> 350
            10 -> 400
            else -> 5 // Valor por defecto si el nivel está fuera del rango especificado
        }
    }
    override fun toString(): String {
        return "Monstruo: Nombre: $nombre, Nivel: $nivel, Salud: $salud, Ataque: $ataque"
    }

}