import android.os.Parcel
import android.os.Parcelable
import com.example.proyecto_ppg.Articulo
import com.example.proyecto_ppg.Mochila
import com.example.proyecto_ppg.Monstruo

/**
 * Clase que representa un personaje en el juego.
 *
 * Atributos:
 * - nombre: El nombre del personaje.
 * - raza: La raza del personaje (Humano, Elfo, Enano, Maldito).
 * - clase: La clase del personaje (Brujo, Mago, Guerrero).
 * - salud: La salud actual del personaje.
 * - ataque: La capacidad de ataque del personaje.
 * - experiencia: La experiencia acumulada por el personaje.
 * - nivel: El nivel actual del personaje.
 * - suerte: El nivel de suerte del personaje (entre 0 y 10).
 * - defensa: El nivel de defensa del personaje.
 *
 * Enumeraciones:
 * - Raza: Enumeración para el tipo de raza del personaje.
 * - Clase: Enumeración para el tipo de clase del personaje.
 *
 * Métodos:
 * - getNombre(): Obtiene el nombre del personaje.
 * - setNombre(nuevoNombre: String): Establece un nuevo nombre para el personaje.
 * - getRaza(): Obtiene la raza del personaje.
 * - getSalud(): Obtiene la salud actual del personaje.
 * - setSalud(nuevaSalud: Int): Establece una nueva salud para el personaje.
 * - getAtaque(): Obtiene la capacidad de ataque del personaje.
 * - setAtaque(nuevoAtaque: Int): Establece una nueva capacidad de ataque para el personaje.
 * - getClase(): Obtiene la clase del personaje.
 * - setClase(nuevaClase: Clase): Establece una nueva clase para el personaje.
 * - getExperiencia(): Obtiene la experiencia acumulada por el personaje.
 * - setExperiencia(experienciaGanada: Int): Añade experiencia al personaje y gestiona el nivel.
 * - getNivel(): Obtiene el nivel actual del personaje.
 * - subirNivel(): Incrementa el nivel del personaje y ajusta salud y ataque.
 * - calcularSalud(): Calcula el valor de salud en función del nivel.
 * - calcularAtaque(): Calcula el valor de ataque en función del nivel.
 * - calcularDefensa(): Calcula el valor de defensa en función del nivel.
 * - pelea(monstruo: Monstruo): Simula una pelea entre el personaje y un monstruo.
 * - habilidad(): Activa la habilidad especial del personaje según su clase.
 * - entrenar(tiempoDeEntrenamiento: Int): Simula el entrenamiento del personaje.
 * - realizarMision(tipoMision: String, dificultad: String): Simula la participación del personaje en una misión.
 * - toString(): Genera una representación en texto del personaje.
 */
@Suppress("DEPRECATION")
class Personaje(
    private var nombre: String,
    private var raza: Raza,
    private var clase: Clase,
    private var estadoVital : EstadoVital
) : Parcelable {
    private var salud: Int = 0
    private var ataque: Int = 0
    private var experiencia: Int
    private var nivel: Int
    private var suerte: Int
    private var defensa: Int = 0

    // Enumeración para el tipo de raza y clase
    enum class Raza { Humano, Elfo, Enano, Maldito }
    enum class Clase { Brujo, Mago, Guerrero }
    enum class EstadoVital{Anciano, Joven, Adulto}
    private var mochila = Mochila(22) // Ejemplo de peso máximo de la mochila
    // Atributos para el equipo del personaje
    private var arma: Articulo? = null
    private var proteccion: Articulo? = null
    //Creamos este nuevo atributo para no tener que pasar la id de firebase por intent
    //y usaremos los getters como los setters
    private var uid: String? = null

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        Raza.valueOf(parcel.readString()!!),
        Clase.valueOf(parcel.readString()!!),
        EstadoVital.valueOf(parcel.readString()!!)
    ) {
        parcel.readInt()
        parcel.readInt()
        parcel.readInt()
        parcel.readInt()
        parcel.readInt()
        parcel.readInt()
        //dato curioso: Si no asignas cuando es un objeto lo lee, pero no sabe donde guardarlo ("creo"), porque no deja si no lo asigno, y los atributos anteriores asignar es opcional)
        mochila = parcel.readParcelable(Mochila::class.java.classLoader)!!
        arma = parcel.readParcelable(Articulo::class.java.classLoader)
        proteccion = parcel.readParcelable(Articulo::class.java.classLoader)
        // Vale el dato curioso anterior puede que no sea cierto por que ahora me ha obligado a asignarle uid, puede que
        //podamos asignar las variables con los readInt() de arriba pero por ahora no lo hemos probado porque funciona correctamente
        // y como dijo Maradona: La vida es una lenteja o la tomas o la dejas
        uid = parcel.readString()
    }

    // Inicialización de los atributos tras la construcción del objeto Personaje
    init {
        calcularSalud()
        calcularAtaque()
        calcularDefensa()
        experiencia = 0
        nivel = 1
        suerte = (0..10).random() // Asigna un valor de suerte aleatorio entre 0 y 10
    }

    fun getUid(): String? = uid
    fun setUid(uid: String) {
        this.uid = uid
    }
    fun getNombre(): String {
        return nombre
    }
    fun setNombre(nuevoNombre: String) {
        nombre = nuevoNombre
    }
    fun getRaza(): Raza {
        return raza
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
    fun getClase(): Clase {
        return clase
    }
    fun setClase(nuevaClase: Clase) {
        clase = nuevaClase
    }
    fun getEstadoVital(): EstadoVital {
        return estadoVital
    }
    fun setEstadoVital(nuevoEstadoVital: EstadoVital) {
        estadoVital = nuevoEstadoVital
    }
    fun getExperiencia(): Int {
        return experiencia
    }
    fun setExperiencia(experienciaGanada: Int) {
        experiencia += experienciaGanada
        while (experiencia >= 1000) {
            subirNivel()
            experiencia -= 1000 // Reducir la experiencia en 1000 al subir de nivel
        }
    }
    fun getNivel(): Int {
        return nivel
    }
    fun subirNivel() {
        if (nivel < 10) { // Limitar el nivel a 10
            nivel++
            calcularSalud() // Calcular el nuevo valor de salud al subir de nivel
            calcularAtaque() // Calcular el nuevo valor de ataque al subir de nivel
            calcularDefensa()
        }
    }
    private fun calcularSalud() {
        salud = when (nivel) {
            1 -> 100
            2 -> 200
            3 -> 300
            4 -> 450
            5 -> 600
            6 -> 800
            7 -> 1000
            8 -> 1250
            9 -> 1500
            10 -> 2000
            else -> 100 // Valor por defecto si el nivel está fuera del rango especificado
        }
    }

    private fun calcularAtaque() {
        ataque = when (nivel) {
            1 -> 10
            2 -> 20
            3 -> 25
            4 -> 30
            5 -> 40
            6 -> 100
            7 -> 200
            8 -> 350
            9 -> 400
            10 -> 450
            else -> 10 // Valor por defecto si el nivel está fuera del rango especificado
        }
    }
    private fun calcularDefensa() {
        defensa = when (nivel) {
            1 -> 4
            2 -> 9
            3 -> 14
            4 -> 19
            5 -> 49
            6 -> 59
            7 -> 119
            8 -> 199
            9 -> 349
            10 -> 399
            else -> 4 // Valor por defecto si el nivel está fuera del rango especificado
        }
    }
    fun getSuerte() = suerte
    fun getDefensa() = defensa
    fun getArma() = arma
    fun getProteccion() = proteccion
    fun pelea(monstruo: Monstruo) {
        var vidaMonstruo = monstruo.getSalud()
        val expGanada = monstruo.getSalud() // La experiencia ganada es igual a la salud inicial del monstruo
        var vidaPersonaje = salud
        var contador = false
        println("¡Un ${monstruo.getNombre()} se acerca!")



        while (vidaMonstruo > 0 && vidaPersonaje > 0) {
            // Preguntar al usuario si desea activar la habilidad
            println("¿Deseas activar la habilidad del personaje? (Sí/No)")
            val respuesta = readLine()?.toLowerCase()

            if ((respuesta == "si" || respuesta == "sí")&&contador==false) {
                habilidad() // Activa la habilidad del personaje
                contador = true
            }
            val evasion = suerte >= 10
            val ataqueMonstruo = if (evasion) 0 else monstruo.getAtaque()

            // Aplicar la defensa del personaje
            val defensaPersonaje = defensa * suerte / 100
            val danoMonstruo = if (evasion) 0 else ataqueMonstruo - defensaPersonaje

            if (!evasion) {
                vidaPersonaje -= danoMonstruo
            }

            println("${nombre} tiene una suerte de ${suerte}% y una defensa de ${defensaPersonaje}.")
            println("${nombre} ha recibido ${danoMonstruo} de daño. Salud de ${nombre}: ${vidaPersonaje}")

            if (vidaMonstruo > 0) {
                // Personaje ataca al monstruo
                vidaMonstruo -= ataque
                println("${nombre} ataca al ${monstruo.getNombre()}. Salud del ${monstruo.getNombre()}: ${vidaMonstruo}")
                if (vidaMonstruo <= 0) {
                    setExperiencia(expGanada)  // El personaje gana experiencia igual a la salud inicial del monstruo
                    println("${nombre} ha derrotado al ${monstruo.getNombre()} y gana ${expGanada} de experiencia.")
                    break
                }

                // Monstruo ataca al personaje
                vidaPersonaje -= ataqueMonstruo
                println("${monstruo.getNombre()} ataca a ${nombre}. Salud de ${nombre}: ${vidaPersonaje}")
            }
        }
    }

    fun habilidad() {
        when (clase) {
            Clase.Mago -> {
                calcularSalud() // Subir la salud al límite del nivel
                println("$nombre utiliza su habilidad de Mago para restaurar su salud.")
            }
            Clase.Brujo -> {
                ataque *= 2 // Duplicar el ataque
                println("$nombre utiliza su habilidad de Brujo para duplicar su ataque.")
            }
            Clase.Guerrero -> {
                suerte *= 2 // Duplicar la suerte
                println("$nombre utiliza su habilidad de Guerrero para duplicar su suerte.")
            }
        }
    }
    fun entrenar(tiempoDeEntrenamiento: Int) {
        val factorExperienciaPorHora = 5
        val experienciaGanada = tiempoDeEntrenamiento * factorExperienciaPorHora

        setExperiencia(experienciaGanada)

        println("$nombre ha entrenado durante $tiempoDeEntrenamiento horas y ha ganado $experienciaGanada de experiencia.")
    }
    fun realizarMision(tipoMision: String, dificultad: String) {
        val probabilidadExito = when (dificultad) {
            "Fácil" -> if (nivel >= 5) 8 else 6
            "Normal" -> if (nivel >= 3) 6 else 4
            "Difícil" -> if (nivel >= 7) 4 else 2
            else -> 0 // En caso de dificultad no reconocida
        }

        val resultado = (1..10).random() // Valor aleatorio entre 1 y 10

        if (resultado <= probabilidadExito) {
            val experienciaGanada = when (tipoMision) {
                "Caza" -> 1000
                "Búsqueda" -> 500
                "Asedio" -> 2000
                "Destrucción" -> 5000
                else -> 0 // En caso de tipo de misión no reconocido
            }

            val multiplicadorExperiencia = when (dificultad) {
                "Fácil" -> 0.5
                "Normal" -> 1.0
                "Difícil" -> 2.0
                else -> 0.0 // En caso de dificultad no reconocida
            }

            val experienciaFinal = (experienciaGanada * multiplicadorExperiencia).toInt()
            setExperiencia(experienciaFinal)
            println("$nombre ha completado la misión de $tipoMision ($dificultad) con éxito y gana $experienciaFinal de experiencia.")
        } else {
            println("$nombre ha fracasado en la misión de $tipoMision ($dificultad) y no recibe ninguna recompensa.")
        }
    }
    fun cifrado(mensaje : String, ROT : Int) : String{
        val abecedario : ArrayList<Char> = "abcdefghijklmnñopqrstuvwxyz".toList() as ArrayList<Char>
        var stringInv = ""
        var indice = 0

        for(i in mensaje.lowercase().toList() as ArrayList<Char>){
            if(!i.isLetter() || i.isWhitespace()){
                stringInv += i
            } else{
                indice = abecedario.indexOf(i) + ROT
                if (indice >= abecedario.size) {
                    indice -= abecedario.size
                    stringInv += abecedario[indice]
                } else
                    stringInv += abecedario[indice]
            }
        }
        return stringInv.filter { !it.isWhitespace() && it.isLetter() }
    }
    fun comunicacion(mensaje:String): String{
        var respuesta = ""
         respuesta = when(estadoVital) {

             EstadoVital.Adulto -> {
                 when {
                     mensaje == "¿Como estas" -> "En la flor de la vida, pero me empieza a doler la espalda"
                     (mensaje.contains('?') || mensaje.contains('¿')) && mensaje == mensaje.uppercase() ->
                         "Estoy buscando la mejor solución"

                     mensaje == mensaje.uppercase() -> "No me levantes la voz mequetrefe"
                     mensaje == nombre -> "¿Necesitas algo?"
                     mensaje == "¿Tienes algo equipado?" -> {
                         if (arma != null || proteccion != null) {
                             val equipamiento = mutableListOf<String>()
                             if (arma != null) {
                                 equipamiento.add(arma!!.getNombre().name)
                             }
                             if (proteccion != null) {
                                 equipamiento.add(proteccion!!.getNombre().name)
                             }
                             "Tengo equipado: ${equipamiento.joinToString(", ")}"
                         } else {
                             "Ligero como una pluma."
                         }
                     }
                     else -> "No sé de qué me estás hablando"
                 }
             }

             EstadoVital.Joven->{
                 when {
                     mensaje == "¿Como estas" -> "De lujo"
                     (mensaje.contains('?') || mensaje.contains('¿')) && mensaje == mensaje.uppercase() ->
                         "Tranqui se lo que hago"

                     mensaje == mensaje.uppercase() -> "Eh relájate"
                     mensaje == nombre -> "¿Qué pasa?"
                     mensaje == "¿Tienes algo equipado?" -> {
                         if (arma != null || proteccion != null) {
                             val equipamiento = mutableListOf<String>()
                             if (arma != null) {
                                 equipamiento.add(arma!!.getNombre().name)
                             }
                             if (proteccion != null) {
                                 equipamiento.add(proteccion!!.getNombre().name)
                             }
                             "No llevo nada, agente, se lo juro."
                         } else {
                             "Regístrame si quieres."
                         }
                     }
                     else -> "Yo que se"
                 }
             }

             EstadoVital.Anciano->
                 when {
                     mensaje == "¿Como estas" -> "No me puedo mover"
                     (mensaje.contains('?') || mensaje.contains('¿')) && mensaje == mensaje.uppercase() ->
                         "Que no te escucho!"

                     mensaje == mensaje.uppercase() -> "Háblame más alto que no te escucho"
                     mensaje == nombre -> "Las 5 de la tarde"
                     mensaje == "¿Tienes algo equipado?" ->
                             "A ti que te importa nini!"
                     else -> "En mis tiempos esto no pasaba"
                 }
         }

        return when(raza){
            Raza.Elfo-> cifrado(respuesta, 1)
            Raza.Enano-> respuesta.uppercase()
            Raza.Maldito-> cifrado(respuesta, 1)
            Raza.Humano -> respuesta
        }
    }

    /*
    Devuelve
    0 si se añade correctamente a la mochila
    1 si el articulo no cabe en la mochila
    -1 si el Articulo.tipoArticulo no coincide con el Articulo.nombre
    * */
    fun addArticulo(articulo:Articulo):Int{
        return mochila.addArticulo(articulo)
    }

    fun delArticulo(articulo: Articulo){
        mochila.delArticulo(articulo)
    }

    fun equipar(articulo: Articulo) {
        when (articulo.getTipoArticulo()) {
            Articulo.TipoArticulo.ARMA -> {
                if (articulo.getNombre() in Articulo.Nombre.BASTON..Articulo.Nombre.GARRAS) {
                    arma = articulo
                    // Aumentar el ataque del personaje según el nombre del arma
                    ataque += articulo.getAumentoAtaque()
                    println("Has equipado el arma: $articulo")
                    mochila.getContenido().remove(articulo)
                } else {
                    println("No se puede equipar el artículo. Tipo de arma no válido.")
                }
            }
            Articulo.TipoArticulo.PROTECCION -> {
                when (articulo.getNombre()) {
                    Articulo.Nombre.ESCUDO, Articulo.Nombre.ARMADURA -> {
                        proteccion = articulo
                        // Aumentar la defensa del personaje solo si la protección es un escudo o una armadura
                        defensa += articulo.getAumentoDefensa()
                        println("Has equipado la protección: $articulo")
                        mochila.getContenido().remove(articulo)
                    }
                    else -> {
                        println("No se puede equipar el artículo. Tipo de protección no válido.")
                    }
                }
            }
            else -> {
                println("No se puede equipar el artículo. Tipo de artículo no válido.")
            }
        }
    }
    fun usarObjeto(articulo: Articulo) {
        when (articulo.getTipoArticulo()) {
            Articulo.TipoArticulo.OBJETO -> {
                when (articulo.getNombre()) {
                    Articulo.Nombre.POCION -> {
                        // Aumentar la vida del personaje al usar una poción
                        salud += articulo.getAumentoVida()
                        println("Has usado la poción y aumentado tu vida. Vida actual: $salud")
                        mochila.getContenido().remove(articulo)
                    }
                    Articulo.Nombre.IRA -> {
                        // Aumentar el ataque del personaje al usar un objeto de ira
                        ataque += articulo.getAumentoAtaque()
                        println("Has canalizado tu ira y aumentado tu ataque. Ataque actual: $ataque")
                        mochila.getContenido().remove(articulo)
                    }
                    else -> {
                        println("No se puede usar el objeto. Tipo de objeto no válido.")
                    }
                }
            }
            else -> {
                println("No se puede usar el artículo. Tipo de artículo no válido.")
            }
        }
    }
    fun getMochila(): Mochila {
        return this.mochila
    }

    fun setMochila(mochila: Mochila) {
        this.mochila = mochila
    }

    override fun toString(): String {
//      return "Personaje: Nombre: $nombre, Nivel: $nivel, Salud: $salud, Ataque: $ataque, Defensa: $defensa, Suerte: $suerte, Raza: $raza, Clase: $clase, Estado Vital: $estadoVital Mochila: $mochila"
        return "Nombre: $nombre, Raza: $raza, Clase: $clase, Estado Vital: $estadoVital"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        //Dato curioso: Hay que ponerlo en orden
        parcel.writeString(nombre)
        //Escribimos los atributos "enums" como String para leerlo con su respectivo valueOf() en el constructor Parcel
        parcel.writeString(raza.toString())
        parcel.writeString(clase.toString())
        parcel.writeString(estadoVital.toString())
        parcel.writeInt(salud)
        parcel.writeInt(ataque)
        parcel.writeInt(experiencia)
        parcel.writeInt(nivel)
        parcel.writeInt(suerte)
        parcel.writeInt(defensa)
        parcel.writeParcelable(mochila, flags)
        parcel.writeParcelable(arma, flags)
        parcel.writeParcelable(proteccion, flags)
        parcel.writeString(uid)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Personaje> {
        override fun createFromParcel(parcel: Parcel): Personaje {
            return Personaje(parcel)
        }

        override fun newArray(size: Int): Array<Personaje?> {
            return arrayOfNulls(size)
        }
    }

}