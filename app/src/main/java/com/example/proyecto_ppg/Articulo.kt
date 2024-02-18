package com.example.proyecto_ppg

import android.os.Parcel
import android.os.Parcelable
import java.util.Objects

/***********************************************************************************************************************
 *  CLASE: Articulo
 *  CONSTRUCTOR:
 *      tipoArticulo  - > Enumeración con valores ARMA, OBJETO, PROTECCION
 *      nombre        - > Enumeración con valores BASTON, ESPADA, DAGA, MARTILLO, GARRAS, POCION, IRA, ESCUDO, ARMADURA
 *      peso          - > Peso del artículo
 *
 *  METODOS
 *      getPeso()           - > Devuelve el peso como Int
 *      getNombre()         - > Devuelve el nombre del artículo
 *      getTipoArticulo()   - > Devuelve el tipo del artículo
 *      toString()          - > Sobreescribimos el método toString para darle formato a la visualización de los
 *                              artículos y su información
 *      getAumentoAtaque()  - > Devuelve el aumento de ataque según el nombre del arma o si el objeto es IRA
 *      getAumentoDefensa() - > Devuelve el aumento de defensa según el nombre de la proteccion
 *      getAumentoVida()    - > Devuelve el aumento de vida si el objeto es POCION
 *
 *
 **********************************************************************************************************************/

class Articulo(
    private var tipoArticulo :TipoArticulo,
    private var nombre :Nombre,
    private var peso :Int,
    private var precio: Int //float por ahora no
) : Parcelable {
    enum class TipoArticulo {ARMA, OBJETO, PROTECCION, ORO}
    enum class Nombre {BASTON, ESPADA, DAGA, MARTILLO, GARRAS, POCION, IRA, ESCUDO, ARMADURA, MONEDA}

    constructor(parcel: Parcel) : this(
        TipoArticulo.valueOf(parcel.readString()!!),
        Nombre.valueOf(parcel.readString()!!),
        parcel.readInt(),
        parcel.readInt()
    )

    fun getPeso() :Int{
        return peso
    }
    fun getNombre() :Nombre{
        return nombre
    }
    fun getTipoArticulo() :TipoArticulo{
        return tipoArticulo
    }

    fun getPrecio() : Int {
        return precio
    }

    fun getAumentoAtaque() :Int{
        var ataque = 0
        when (nombre){
            Nombre.BASTON -> ataque = 10
            Nombre.ESPADA -> ataque = 20
            Nombre.DAGA -> ataque = 15
            Nombre.MARTILLO -> ataque = 25
            Nombre.GARRAS -> ataque = 30
            Nombre.IRA -> ataque = 80
            Nombre.POCION,
            Nombre.ESCUDO,
            Nombre.ARMADURA,
            Nombre.MONEDA-> {}
        }
        return ataque
    }
    fun getAumentoDefensa() :Int{
        var defensa = 0
        when (nombre){
            Nombre.ESCUDO -> defensa = 10
            Nombre.ARMADURA -> defensa = 20
            Nombre.BASTON,
            Nombre.ESPADA,
            Nombre.DAGA,
            Nombre.MARTILLO,
            Nombre.GARRAS,
            Nombre.IRA,
            Nombre.POCION,
            Nombre.MONEDA-> {}
        }
        return defensa
    }

    fun getAumentoVida() :Int{
        var vida = 0
        when (nombre){
            Nombre.POCION -> vida = 100
            Nombre.BASTON,
            Nombre.ESPADA,
            Nombre.DAGA,
            Nombre.MARTILLO,
            Nombre.GARRAS,
            Nombre.IRA,
            Nombre.ESCUDO,
            Nombre.ARMADURA,
            Nombre.MONEDA-> {}
        }
        return vida
    }

    override fun toString(): String {
        return  "Tipo Artículo: $tipoArticulo - Nombre: $nombre - Peso: $peso - Precio $precio"
    }

    override fun hashCode(): Int {
        return Objects.hash(tipoArticulo, nombre)
    }

    override fun equals(other: Any?): Boolean {
        var flagRetorno = false
        val obj = other as Articulo
        if (tipoArticulo == obj.getTipoArticulo() && nombre == obj.getNombre())
            flagRetorno = true
        return flagRetorno
    }
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(tipoArticulo.toString())
        parcel.writeString(nombre.toString())
        parcel.writeInt(peso)
        parcel.writeInt(precio)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Articulo> {
        override fun createFromParcel(parcel: Parcel): Articulo {
            return Articulo(parcel)
        }

        override fun newArray(size: Int): Array<Articulo?> {
            return arrayOfNulls(size)
        }
    }

}