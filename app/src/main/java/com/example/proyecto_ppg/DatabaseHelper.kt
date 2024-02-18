package com.example.proyecto_ppg

import Personaje
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.util.GregorianCalendar

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE, null, DATABASE_VERSION) {
    private val listaObjetos = arrayListOf(
        Articulo(Articulo.TipoArticulo.ARMA, Articulo.Nombre.BASTON, 0, 2, 4),
        Articulo(Articulo.TipoArticulo.ARMA, Articulo.Nombre.ESPADA, 0, 2, 4),
        Articulo(Articulo.TipoArticulo.ARMA, Articulo.Nombre.DAGA, 0, 2, 4),
        Articulo(Articulo.TipoArticulo.ARMA, Articulo.Nombre.MARTILLO, 0, 2, 4),
        Articulo(Articulo.TipoArticulo.ARMA, Articulo.Nombre.GARRAS, 0, 2, 4),
        Articulo(Articulo.TipoArticulo.PROTECCION, Articulo.Nombre.ESCUDO, 0, 2, 4),
        Articulo(Articulo.TipoArticulo.PROTECCION, Articulo.Nombre.ARMADURA, 0, 2, 4),
        Articulo(Articulo.TipoArticulo.OBJETO, Articulo.Nombre.POCION, 0, 2, 4),
        Articulo(Articulo.TipoArticulo.OBJETO, Articulo.Nombre.IRA, 0, 2, 4),
    )

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE = "OBJETOS_ALEATORIOS"
        private const val TABLA_OBJETOS = "objetos_aleatorios"
        private const val KEY_ID_OBJETOS_ALEATORIOS = "_id"
        private const val COLUMN_TIPO_ARTICULO_OBJETOS_ALEATORIOS = "tipo_articulo"
        private const val COLUMN_NOMBRE_OBJETOS_ALEATORIOS = "nombre"
        private const val COLUMN_NIVEL_OBJETOS_ALEATORIOS = "nivel"
        private const val COLUMN_PESO_OBJETOS_ALEATORIOS = "peso"
        private const val COLUMN_URL_IMG_OBJETOS_ALEATORIOS = "url_img"
        private const val COLUMN_UNIDADES_OBJETOS_ALEATORIOS = "unidades"
        private const val COLUMN_PRECIO_OBJETOS_ALEATORIOS = "precio"

        private const val TABLA_PERSONAJES = "personajes"
        private const val KEY_ID = "_id"
        private const val COLUMN_NOMBRE = "nombre"
        private const val COLUMN_RAZA = "raza"
        private const val COLUMN_CLASE = "clase"
        private const val COLUMN_ESTADOVITAL = "estadoVital"
        private const val COLUMN_SALUD = "salud"
        private const val COLUMN_ATAQUE = "ataque"
        private const val COLUMN_EXPERIENCIA = "experiencia"
        private const val COLUMN_NIVEL = "nivel"
        private const val COLUMN_SUERTE = "suerte"
        private const val COLUMN_DEFENSA = "defensa"
        private const val COLUMN_ARMA = "arma"
        private const val COLUMN_PROTECCION = "proteccion"
        private const val COLUMN_PESO_MOCHILA = "pesoMochila"

        private const val TABLA_CONTENIDO_MOCHILA = "contenidoMochila"
        private const val COLUMN_TIPO_ARTICULO = "tipo_articulo"
        private const val COLUMN_NOMBRE_ARTICULO = "nombre_articulo"
        private const val COLUMN_NIVEL_ARTICULO = "nivel"
        private const val COLUMN_PESO = "peso"
        private const val COLUMN_PRECIO = "precio"
        private const val COLUMN_UNIDADES = "unidades"
        private const val FK_ID = "_fk_id"

        private const val TABLA_ORO = "fecha_Oro"
        private const val COLUMN_ULTIMO_DIA_RECOGIDO = "pueeseso"
        private const val FK_ID_AUTH = "disponible_para_recoger"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableObjetos = "CREATE TABLE IF NOT EXISTS $TABLA_OBJETOS (" +
                "$KEY_ID_OBJETOS_ALEATORIOS INTEGER PRIMARY KEY," +
                " $COLUMN_TIPO_ARTICULO_OBJETOS_ALEATORIOS TEXT," +
                "$COLUMN_NOMBRE_OBJETOS_ALEATORIOS TEXT," +
                "$COLUMN_NIVEL_OBJETOS_ALEATORIOS INTEGER," +
                " $COLUMN_PESO_OBJETOS_ALEATORIOS INTEGER," +
                " $COLUMN_PRECIO_OBJETOS_ALEATORIOS INTEGER," +
                " $COLUMN_UNIDADES_OBJETOS_ALEATORIOS INTEGER," +
                " $COLUMN_URL_IMG_OBJETOS_ALEATORIOS INTEGER)"
        val createTablePersonaje =
            "CREATE TABLE IF NOT EXISTS $TABLA_PERSONAJES (" +
                    "$KEY_ID TEXT PRIMARY KEY," +
                    "$COLUMN_NOMBRE TEXT," +
                    "$COLUMN_RAZA TEXT," +
                    "$COLUMN_CLASE TEXT," +
                    "$COLUMN_ESTADOVITAL TEXT," +
                    "$COLUMN_SALUD INTEGER," +
                    "$COLUMN_ATAQUE INTEGER," +
                    "$COLUMN_EXPERIENCIA INTEGER," +
                    "$COLUMN_NIVEL INTEGER," +
                    "$COLUMN_SUERTE INTEGER," +
                    "$COLUMN_DEFENSA INTEGER," +
                    "$COLUMN_ARMA TEXT," +
                    "$COLUMN_PROTECCION TEXT," +
                    "$COLUMN_PESO_MOCHILA INTEGER)"

        val createTableMochila = "CREATE TABLE IF NOT EXISTS $TABLA_CONTENIDO_MOCHILA (" +
                "$COLUMN_TIPO_ARTICULO TEXT," +
                " $COLUMN_NOMBRE_ARTICULO TEXT," +
                "$COLUMN_NIVEL_ARTICULO INTEGER," +
                "$COLUMN_PESO INTEGER," +
                " $COLUMN_PRECIO INTEGER," +
                " $COLUMN_UNIDADES INTEGER," +
                "$FK_ID TEXT, " +
                "FOREIGN KEY($FK_ID) REFERENCES $TABLA_PERSONAJES($KEY_ID)" +
                ")"

        val createTableOro = "CREATE TABLE IF NOT EXISTS $TABLA_ORO (" +
                "$COLUMN_ULTIMO_DIA_RECOGIDO TEXT, " +
                "$FK_ID_AUTH TEXT, " +
                "FOREIGN KEY($FK_ID_AUTH) REFERENCES $TABLA_PERSONAJES($KEY_ID)" +
                ")"


        db.execSQL(createTablePersonaje)
        db.execSQL(createTableMochila)
        db.execSQL(createTableObjetos)
        db.execSQL(createTableOro)

//        val xd = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ULTIMO_DIA_RECOGIDO, "0")
        }
        db.insert(TABLA_ORO, null, values)
//        db.close()
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLA_PERSONAJES")
        db.execSQL("DROP TABLE IF EXISTS $TABLA_CONTENIDO_MOCHILA")
        db.execSQL("DROP TABLE IF EXISTS $TABLA_OBJETOS")
        db.execSQL("DROP TABLE IF EXISTS $TABLA_ORO")

        onCreate(db)
    }

    @SuppressLint("Range")
    fun oroDisponible(uid: String): Boolean {
        var flagRetorno = false

        val fechaActual = GregorianCalendar()
        val fechaInMillis = fechaActual.timeInMillis
        //tiempo para que vuelva a estar disponible en segundos
        val segundosAEsperar = 24 * 60 * 60 //un dia en segundos

        //lo pasamos a milisegundos
        val tiempoEstimado = (segundosAEsperar * 1000).toLong()


        val db = this.readableDatabase
        val countQuery = "SELECT * FROM $TABLA_ORO WHERE $FK_ID_AUTH = '$uid'"
        val cursor = db.rawQuery(countQuery, null)
        var ultimaFechaRecogida = (0).toLong()
        if(cursor.moveToFirst()) {
            ultimaFechaRecogida = cursor.getString(cursor.getColumnIndex(COLUMN_ULTIMO_DIA_RECOGIDO)).toLong()
        }else{
            val values = ContentValues().apply {
                put(FK_ID_AUTH, uid)
                put(COLUMN_ULTIMO_DIA_RECOGIDO, fechaInMillis.toString())
            }
            db.insert(TABLA_ORO, null, values)
            flagRetorno = true
        }
        cursor.close()
        db.close()

        if (fechaInMillis >= (ultimaFechaRecogida + tiempoEstimado))
            flagRetorno = true


        return flagRetorno
    }

    fun recogerOro(precio: Int, uid: String): Articulo? {
        var articuloOro: Articulo? = null
        if (oroDisponible(uid)) {
            articuloOro = Articulo(Articulo.TipoArticulo.ORO, Articulo.Nombre.MONEDA, 0, 0, precio)
            val db = this.writableDatabase
            val selectQuery = "UPDATE $TABLA_ORO SET $COLUMN_ULTIMO_DIA_RECOGIDO = '${GregorianCalendar().timeInMillis}' WHERE $FK_ID_AUTH = '$uid'"
            db.execSQL(selectQuery)
            db.close()
        }
        return articuloOro
    }

    fun insertarPersonaje(personaje: Personaje){
        //Eliminamos y volvemos añadir para simular que sobreescribimos
        eliminarPersonaje(personaje.getUid()!!)
        val db = this.writableDatabase
        var values = ContentValues().apply {
            put(KEY_ID, personaje.getUid()?: "NULL")
            Log.d("uid", "El uid es: ${personaje.getUid()}")
            put(COLUMN_NOMBRE, personaje.getNombre())
            put(COLUMN_RAZA, personaje.getRaza().toString())
            put(COLUMN_CLASE, personaje.getClase().toString())
            put(COLUMN_ESTADOVITAL, personaje.getEstadoVital().toString())
            put(COLUMN_SALUD, personaje.getSalud())
            put(COLUMN_ATAQUE, personaje.getAtaque())
            put(COLUMN_EXPERIENCIA, personaje.getExperiencia())
            put(COLUMN_NIVEL, personaje.getNivel())
            put(COLUMN_SUERTE, personaje.getSuerte())
            put(COLUMN_DEFENSA, personaje.getDefensa())
            put(COLUMN_ARMA, personaje.getArma().toString())
            put(COLUMN_PROTECCION, personaje.getProteccion().toString())
            put(COLUMN_PESO_MOCHILA, personaje.getMochila().getPesoMochila())
        }
        db.insert(TABLA_PERSONAJES, null, values)

        val contenidoMochila = personaje.getMochila().getContenido()
        val noRepetidosContenidoMochila = contenidoMochila.toHashSet()
        noRepetidosContenidoMochila.forEach { item ->
            values = ContentValues().apply {
                put(COLUMN_TIPO_ARTICULO, item.getTipoArticulo().toString())
                put(COLUMN_NOMBRE_ARTICULO, item.getNombre().toString())
                put(COLUMN_NIVEL_ARTICULO,0)
                put(COLUMN_PESO, item.getPeso())
                put(COLUMN_PRECIO, item.getPrecio())
                put(COLUMN_UNIDADES, contenidoMochila.count { item == it })
                put(FK_ID, personaje.getUid()?: "NULL")
            }
            db.insert(TABLA_CONTENIDO_MOCHILA, null, values)
        }
        db.close()
    }
    //Devuelve null si no lo encuentra
    @SuppressLint("Range")
    fun getPersona(uid: String): Personaje? {
        var personaje: Personaje? = null
        val contenido = ArrayList<Articulo>()
        if (getNumRegistros() == 0){
            return null
        }
        val selectQuery = "SELECT * FROM $TABLA_PERSONAJES WHERE $KEY_ID = ?"
        val db = this.readableDatabase

        val cursor = db.rawQuery(selectQuery, arrayOf(uid))
        if (cursor.moveToFirst()) {
            do {
//                val id = cursor.getString(cursor.getColumnIndex(KEY_ID))
                val nombre = cursor.getString(cursor.getColumnIndex(COLUMN_NOMBRE))
                val raza = Personaje.Raza.valueOf(cursor.getString(cursor.getColumnIndex(COLUMN_RAZA)))
                val clase =
                    Personaje.Clase.valueOf(cursor.getString(cursor.getColumnIndex(COLUMN_CLASE)))
                val estadoVital = Personaje.EstadoVital.valueOf(cursor.getString(cursor.getColumnIndex(
                    COLUMN_ESTADOVITAL
                )))
                personaje = Personaje(nombre, raza, clase, estadoVital)
                personaje.setUid(uid)
                val mochila = Mochila(cursor.getInt(cursor.getColumnIndex(COLUMN_PESO_MOCHILA)))

                val selectQueryMochila = "SELECT * FROM $TABLA_CONTENIDO_MOCHILA WHERE $FK_ID = ?"
                val cursorMochila = db.rawQuery(selectQueryMochila, arrayOf(uid))
                if (cursorMochila.moveToFirst()) {
                    do {
                        val tipoArticulo = Articulo.TipoArticulo.valueOf(
                            cursorMochila.getString(
                                cursorMochila.getColumnIndex(COLUMN_TIPO_ARTICULO)
                            )
                        )
                        val nombreArticulo = Articulo.Nombre.valueOf(
                            cursorMochila.getString(
                                cursorMochila.getColumnIndex(COLUMN_NOMBRE_ARTICULO)
                            )
                        )
                        val nivel = cursorMochila.getInt(cursorMochila.getColumnIndex(
                            COLUMN_NIVEL_ARTICULO))
                        val peso = cursorMochila.getInt(cursorMochila.getColumnIndex(COLUMN_PESO))
                        val precio =
                            cursorMochila.getInt(cursorMochila.getColumnIndex(COLUMN_PRECIO))
                        val articulo = Articulo(tipoArticulo, nombreArticulo, nivel, peso, precio)
                        contenido.add(articulo)
                    } while (cursorMochila.moveToNext())
                }
                cursorMochila.close()
                mochila.setContenido(contenido)
                personaje.setMochila(mochila)
            } while (cursor.moveToNext())
            cursor.close()
            db.close()
        }

        return personaje
    }

    private fun getNumRegistros(): Int {
        val db = this.readableDatabase
        val countQuery = "SELECT COUNT(*) FROM $TABLA_PERSONAJES"
        val cursor = db.rawQuery(countQuery, null)

        var count = 0

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }

        cursor.close()
        db.close()
        return count
    }

    //Segun el pdf en el aulavirtual, el onUpgrade se ejecuta automaticamente si ve alguna
    fun crearTablasSiNoExisten() {
        //Para eliminar la bbdd para pruebas
        val db = this.writableDatabase
//        this.onUpgrade(db, 0, 0)
        this.onCreate(db)
        this.close()
//        listaObjetos.forEach { insertarObjetos(it, 3) }
    }

    /*Funcion que inserta un objeto Articulo, con unidades
    * Una idea seria no dejar que ingrese un objeto que ya se encuentre en la bbdd
    * por ejemplo MONEDA ORO == MONEDA ORO*/
    fun insertarObjetos(){
        val db = this.writableDatabase
        db.delete(TABLA_OBJETOS, null, null)
        listaObjetos.forEach { articulo ->
            val values = ContentValues().apply {
                put(COLUMN_TIPO_ARTICULO_OBJETOS_ALEATORIOS, articulo.getTipoArticulo().toString())
                put(COLUMN_NOMBRE_OBJETOS_ALEATORIOS, articulo.getNombre().toString())
                put(COLUMN_NIVEL_OBJETOS_ALEATORIOS,0)
                put(COLUMN_PESO_OBJETOS_ALEATORIOS, articulo.getPeso())
                put(COLUMN_PRECIO_OBJETOS_ALEATORIOS, articulo.getPrecio())
                put(COLUMN_UNIDADES_OBJETOS_ALEATORIOS, 3)
                put(COLUMN_URL_IMG_OBJETOS_ALEATORIOS, getUrl(articulo))
            }
            db.insert(TABLA_OBJETOS, null, values)
        }
        db.close()
    }

    /*Se elimina el articulo y se devuelve las filas Eliminadas, si es 0 porque no existia el articulo*/
    fun eliminarObjetos(articulo: Articulo): Int {
        val db = this.writableDatabase
        val whereClause =
            "$COLUMN_TIPO_ARTICULO_OBJETOS_ALEATORIOS = ? AND $COLUMN_NOMBRE_OBJETOS_ALEATORIOS = ?"
        val whereArgs = arrayOf(
            articulo.getTipoArticulo().toString(),
            articulo.getNombre().toString()
        )
        val filasEliminadas = db.delete(TABLA_OBJETOS, whereClause, whereArgs)
        db.close()
        return filasEliminadas
    }

    /*Se elimina el articulo y se devuelve las filas Eliminadas, si es 0 porque no existia el articulo*/
    private fun eliminarPersonaje(uid: String): Int {
        val db = this.writableDatabase
        val whereClause =
            "$KEY_ID = ?"
        val whereArgs = arrayOf(
            uid
        )
        val filasEliminadas = db.delete(TABLA_PERSONAJES, whereClause, whereArgs)
        db.delete(TABLA_CONTENIDO_MOCHILA, "$FK_ID = ?", arrayOf(uid))
        db.close()
        return filasEliminadas
    }

    /*Devuelve la url que es un Int, y -1 si el tipoArticulo no coincide con el nombre
    El -1 lo podemos implementar para mostrar una imagen de error generica */
    fun getUrl(item: Articulo): Int {
        val urlImagen = when (item.getTipoArticulo()) {
            Articulo.TipoArticulo.ARMA -> {
                when (item.getNombre()) {
                    Articulo.Nombre.BASTON -> {
                        R.drawable.obj_arma_baston
                    }

                    Articulo.Nombre.ESPADA -> {
                        R.drawable.obj_arma_espada
                    }

                    Articulo.Nombre.DAGA -> {
                        R.drawable.obj_arma_daga
                    }

                    Articulo.Nombre.MARTILLO -> {
                        R.drawable.obj_arma_martillo
                    }

                    Articulo.Nombre.GARRAS -> {
                        R.drawable.obj_arma_garras
                    }

                    else -> {
                        -1
                    }
                }
            }

            Articulo.TipoArticulo.PROTECCION -> {
                when (item.getNombre()) {
                    Articulo.Nombre.ESCUDO -> {
                        R.drawable.obj_proteccion_escudo
                    }

                    Articulo.Nombre.ARMADURA -> {
                        R.drawable.obj_proteccion_armadura
                    }

                    else -> -1
                }
            }

            Articulo.TipoArticulo.OBJETO -> {
                when (item.getNombre()) {
                    Articulo.Nombre.POCION -> {
                        R.drawable.obj_objeto_pocion
                    }

                    Articulo.Nombre.IRA -> {
                        R.drawable.obj_objeto_ira
                    }

                    else -> -1
                }
            }

            Articulo.TipoArticulo.ORO -> {
                when (item.getNombre()) {
                    Articulo.Nombre.MONEDA -> {
                        R.drawable.obj_oro_moneda
                    }

                    else -> -1
                }
            }
        }
        return urlImagen
    }

    /*Devuelva una ArrayList de todos los Articulos que haya en la bd*/
    @SuppressLint("Range")
    fun getObjetos(): ArrayList<Articulo> {
        var objetos = ArrayList<Articulo>()
        val selectQuery = "SELECT * FROM $TABLA_OBJETOS"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        //Si no hay registros llamar a insertarObjetos con foreeach
        //Si no hay unidades en alguno, una pena.
        //Si todas las unidades estan a 0, llamar con un foreeach a setunidades


        if (cursor.moveToFirst()) {
            do {
//                val id = cursor.getInt(cursor.getColumnIndex(KEY_ID))
                val tipoArticulo = Articulo.TipoArticulo.valueOf(
                    cursor.getString(
                        cursor.getColumnIndex(COLUMN_TIPO_ARTICULO_OBJETOS_ALEATORIOS)
                    )
                )
                val nombre = Articulo.Nombre.valueOf(
                    cursor.getString(
                        cursor.getColumnIndex(COLUMN_NOMBRE_OBJETOS_ALEATORIOS)
                    )
                )
                val nivel = cursor.getInt(cursor.getColumnIndex(COLUMN_NIVEL_OBJETOS_ALEATORIOS))
                val peso = cursor.getInt(cursor.getColumnIndex(COLUMN_PESO_OBJETOS_ALEATORIOS))
                val precio =
                    cursor.getInt(cursor.getColumnIndex(COLUMN_PRECIO_OBJETOS_ALEATORIOS)) //Podriamos poner una funcion en el init de la clase Articulo para que el precio lo haga alli y no poner el precio en el sql
//                val unidades = cursor.getInt(cursor.getColumnIndex(COLUMN_UNIDADES))
//                val urlImage = cursor.getInt(cursor.getColumnIndex(COLUMN_URL_IMG))
                objetos.add(Articulo(tipoArticulo, nombre, nivel, peso, precio))
            } while (cursor.moveToNext())
        }else {
            //Si es la primera vez hace una funcion recursiva para insertar objetos en la tabla
            insertarObjetos()
            objetos = getObjetos()
        }

        cursor.close()
        db.close()
        return objetos
    }


    @SuppressLint("Range")
    fun getUnidades(articulo: Articulo): Int {
        val selectQuery = "SELECT $COLUMN_UNIDADES_OBJETOS_ALEATORIOS FROM $TABLA_OBJETOS " +
                "WHERE $COLUMN_TIPO_ARTICULO_OBJETOS_ALEATORIOS = '${articulo.getTipoArticulo()}'" +
                "AND $COLUMN_NOMBRE_OBJETOS_ALEATORIOS = '${articulo.getNombre()}'"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        var unidades = 0

        if (cursor.moveToFirst())
            unidades = cursor.getInt(cursor.getColumnIndex(COLUMN_UNIDADES_OBJETOS_ALEATORIOS))

        cursor.close()
        db.close()
        return unidades
    }

    /*
    Por ahora solo cambia las unidades del articulo que se encuentre en la bd
    Pero en un futuro devolvera true si se ha cambiado exitosamente
    y false si no se ha cambiado por X motivo,
    como por ejemlo que no exista el articulo
    Esto para futuras excepciones*/
    fun setUnidades(articulo: Articulo, unidades: Int) {
        val selectQuery =
            "UPDATE $TABLA_OBJETOS SET $COLUMN_UNIDADES_OBJETOS_ALEATORIOS = $unidades " +
                    "WHERE $COLUMN_TIPO_ARTICULO_OBJETOS_ALEATORIOS = '${articulo.getTipoArticulo()}' " +
                    "AND $COLUMN_NOMBRE_OBJETOS_ALEATORIOS = '${articulo.getNombre()}'"
        writableDatabase.use { db ->
            db.execSQL(selectQuery)
        }
        if (getUnidades(articulo) < 1)
            eliminarObjetos(articulo)

    }
}