package com.example.proyecto_ppg

import android.os.Parcel
import android.os.Parcelable

/***********************************************************************************************************************
 *  CLASE: Mochila
 *  CONSTRUCTOR:
 *      pesoMochila      - > Peso máximo que puede soportar la mochila (Int)
 *
 *  ATRIBUTOS
 *      contenido: ArrayList de Articulos
 *  METODOS
 *      getPesoMochila()        - > Devuelve el peso máximo como Int
 *      addArticulo()           - > Añade un artículo (clase Articulo) a la mochila, comprobando que:
 *                                  - El peso del artículo sumado al peso total del resto de artículos de la Mochila no
 *                                  supere el límite (pesoMochila).
 *                                  - Si el tipoArticulo es distinto a los aceptados, no incluir el objeto en la Mochila
 *                                  - Si el nombre del Articulo es distinto a los aceptados, no incluir el objeto en la
 *                                    Mochila
 *                                  - Considerar que cada tipoArticulo tiene asociados uno Nombres (Enum). Si no se
 *                                    cumplen las relaciones del enunciado, no se añade el artículo a la Mochila.
 *      getContenido()          - > Devuelve el ArrayList de artículos que contiene la mochila
 *      findObjeto(nombre: Articulo.Nombre)     - > Devuelve la posición del artículo que cuyo nombre (Articulo.Nombre)
 *                                                  pasamos como entrada o -1 si no lo encuentra
 *      toString()              - > Sobrescribimos el método toString para darle formato a la visualización del
 *                                  contenido de la mochila.
 *
 **********************************************************************************************************************/
class Mochila(
    private val pesoMochila : Int
) : Parcelable {
    private var contenido = ArrayList<Articulo>()

    constructor(parcel: Parcel) : this(parcel.readInt()) {
        parcel.readTypedList(contenido, Articulo.CREATOR)
    }

    fun getPesoMochila(): Int {
        return pesoMochila
    }

    /*
    Devuelve
    0 si se añade correctamente a la mochila
    1 si el articulo no cabe en la mochila
    -1 si el Articulo.tipoArticulo no coincide con el Articulo.nombre
    * */
    fun addArticulo(articulo: Articulo): Int{
        var retorno = 0
        if (articulo.getPeso() <= getPesoLibreMochila()) {
            when (articulo.getTipoArticulo()) {
                Articulo.TipoArticulo.ARMA -> {
                    when(articulo.getNombre()) {
                        Articulo.Nombre.BASTON, Articulo.Nombre.ESPADA, Articulo.Nombre.DAGA, Articulo.Nombre.MARTILLO, Articulo.Nombre.GARRAS -> contenido.add(articulo)
                        else -> {
                            println("Nombre del artículo no válido para el tipo ARMA.")
                            retorno = -1
                        }
                    }
                }
                Articulo.TipoArticulo.PROTECCION -> {
                    when(articulo.getNombre()) {
                        Articulo.Nombre.ESCUDO, Articulo.Nombre.ARMADURA -> contenido.add(articulo)
                        else -> {
                            println("Nombre del artículo no válido para el tipo PROTECCION.")
                            retorno = -1
                        }
                    }
                }
                Articulo.TipoArticulo.OBJETO -> {
                    when(articulo.getNombre()) {
                        Articulo.Nombre.POCION, Articulo.Nombre.IRA -> contenido.add(articulo)
                        else -> {
                            println("Nombre del artículo no válido para el tipo OBJETO.")
                            retorno = -1
                        }
                    }
                }

                Articulo.TipoArticulo.ORO -> {
                    when (articulo.getNombre()) {
                        Articulo.Nombre.MONEDA -> {
                            val oroActual = contenido.find { Articulo.TipoArticulo.ORO == it.getTipoArticulo() && Articulo.Nombre.MONEDA == it.getNombre()} ?: Articulo(Articulo.TipoArticulo.ORO, Articulo.Nombre.MONEDA, 0, 0)
                            contenido.remove(oroActual)
                            val oroSuma = oroActual.getPrecio() + articulo.getPrecio()
                            contenido.add(Articulo(Articulo.TipoArticulo.ORO, Articulo.Nombre.MONEDA, 0, oroSuma))

                        }
                        else -> {
                            println("Nombre del artículo no válido para el tipo ORO.")
                            retorno = -1
                        }
                    }
                }
            }
        }else {
            print("No cabe en la mochila papu")
            retorno = 1
        }
        return retorno
    }

    fun delArticulo(articulo: Articulo){

        val precio: Int

        if(articulo.getTipoArticulo() != Articulo.TipoArticulo.ORO) {
            precio = (articulo.getPrecio() / 2) //Lo vendemos a la mitad de precio
            contenido.remove(articulo)
        }else{
            precio = -articulo.getPrecio() //Para restar al oro
        }
        val oro = Articulo(Articulo.TipoArticulo.ORO, Articulo.Nombre.MONEDA, 0, precio)
        addArticulo(oro)

    }

    fun getPesoLibreMochila(): Int {
        var pesoContenido = 0
        for (item in contenido)
            pesoContenido += item.getPeso()
        return pesoMochila - pesoContenido
    }

    fun getContenido(): ArrayList<Articulo>{
        return contenido
    }

    fun setContenido(contenido: ArrayList<Articulo>){
        this.contenido = contenido
    }

    fun findObjeto(nombre: Articulo.Nombre) : Int {
        return contenido.indexOfFirst { nombre == it.getNombre() }
    }

    override fun toString(): String {
        return "Peso Máximo de la Mochila: $pesoMochila, Peso Libre de la Mochila: ${this.getPesoLibreMochila()} Contenido:$contenido"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(pesoMochila)
        parcel.writeTypedList(contenido)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Mochila> {
        override fun createFromParcel(parcel: Parcel): Mochila {
            return Mochila(parcel)
        }

        override fun newArray(size: Int): Array<Mochila?> {
            return arrayOfNulls(size)
        }
    }

}