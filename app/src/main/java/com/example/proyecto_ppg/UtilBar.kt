package com.example.proyecto_ppg

import Personaje
import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

@SuppressLint("ViewConstructor")
class UtilBar(private val context: Context, private val p: Personaje): Toolbar(context) {
    private lateinit var toolbar: Toolbar
    private lateinit var menuInflater: MenuInflater
    private var isPlaying = false
    private var isStopped = false
    private lateinit var mediaPlayer: MediaPlayer

    fun setupToolbar(activity: AppCompatActivity, toolbarId: Int) {
        toolbar = activity.findViewById(toolbarId)
        activity.setSupportActionBar(toolbar)
        menuInflater = activity.menuInflater
        mediaPlayer = MediaPlayer.create(context,R.raw.song)
    }

    fun onCreateOptionsMenu(menu: Menu){
        menuInflater.inflate(R.menu.menu_bar, menu)
    }

    private fun cambioFondoImagen() :Int{

        val clase: Int = when (p.getClase()) {
            Personaje.Clase.Brujo -> R.drawable.class_brujo_marco
            Personaje.Clase.Mago -> R.drawable.class_mago_marco
            Personaje.Clase.Guerrero -> R.drawable.class_guerrero_marco
        }

        return clase
    }

    private fun cambioImagen(): Int{
        val raza :Int
        when (p.getRaza()) {
            Personaje.Raza.Humano ->
                raza = when (p.getEstadoVital()) {
                    Personaje.EstadoVital.Anciano -> R.drawable.icon_humano_anciano
                    Personaje.EstadoVital.Joven -> R.drawable.icon_humano_joven
                    Personaje.EstadoVital.Adulto -> R.drawable.icon_humano_adulto
                }

            Personaje.Raza.Elfo ->
                raza = when (p.getEstadoVital()) {
                    Personaje.EstadoVital.Anciano -> R.drawable.icon_elfo_anciano
                    Personaje.EstadoVital.Joven -> R.drawable.icon_elfo_joven
                    Personaje.EstadoVital.Adulto -> R.drawable.icon_elfo_adulto
                }

            Personaje.Raza.Enano ->
                raza = when (p.getEstadoVital()) {
                    Personaje.EstadoVital.Anciano -> R.drawable.icon_enano_anciano
                    Personaje.EstadoVital.Joven -> R.drawable.icon_enano_joven
                    Personaje.EstadoVital.Adulto -> R.drawable.icon_elfo_adulto
                }

            Personaje.Raza.Maldito ->
                raza = when (p.getEstadoVital()) {
                    Personaje.EstadoVital.Anciano -> R.drawable.icon_maldito_anciano
                    Personaje.EstadoVital.Joven -> R.drawable.icon_maldito_joven
                    Personaje.EstadoVital.Adulto -> R.drawable.icon_maldito_adulto
                }
        }
        return raza
    }

    @Suppress("DEPRECATION","MissingInflatedId", "UseCompatLoadingForDrawables", "InflateParams")
    fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.play -> {
                //iniciar o pausar musica
                isPlaying = if(!isPlaying){
                    startMusic()
                    true
                }else{
                    pauseMusic()
                    false
                }
                updatePlayButtonIcon()
                true
            }
            R.id.stop -> {
                stopMusic()
                true
            }
            R.id.Pj -> {
                val inf = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val layout = inf.inflate(R.layout.pop_up,null)

                val textView = layout.findViewById<TextView>(R.id.textPopUp)
                textView.text = p.toString()
                val imageView = layout.findViewById<ImageView>(R.id.itemPopUp)
                imageView.setBackgroundResource(cambioFondoImagen())
                imageView.setImageDrawable(context.resources.getDrawable(cambioImagen()))

                val toast = Toast(context)
                toast.setGravity(Gravity.CENTER,0,0)
                toast.duration = Toast.LENGTH_SHORT
                toast.view = layout
                toast.show()

                true
            }

            R.id.save -> {
                val db = DatabaseHelper(context)
                db.insertarPersonaje(p)
                Toast.makeText(context, "Guardado", Toast.LENGTH_SHORT).show()
                true
            }

            R.id.backpack -> {
                Toast.makeText(context, "Mochila: ${p.getMochila().getContenido()}", Toast.LENGTH_LONG).show()
                true
            }

            R.id.goldIcon -> {
                val oroActual = p.getMochila().getContenido().find { Articulo.TipoArticulo.ORO == it.getTipoArticulo() && Articulo.Nombre.MONEDA == it.getNombre() }!!
                Toast.makeText(context, "Mi Oro: ${oroActual.getPrecio()}", Toast.LENGTH_LONG).show()
                true
            }
            R.id.goldIconPlus -> {
                val db = DatabaseHelper(context)
                val oroArticulo = db.recogerOro(100, p.getUid()!!)
                Log.d("OROTOOL", "$oroArticulo")
                oroArticulo?.let {
                        p.addArticulo(it)
                        Toast.makeText(context, "+ ${it.getPrecio()} de oro", Toast.LENGTH_LONG).show()
                        db.insertarPersonaje(p)
                }?: run{
                    Toast.makeText(context, "Ya has obtenido tu recompensa del día", Toast.LENGTH_LONG).show()
                }

                true
            }
            else -> false
        }
    }

    private fun updatePlayButtonIcon() {
        val menuItem = toolbar.menu.findItem(R.id.play)

        if (isPlaying) menuItem.setIcon(R.drawable.icon_pause)
        else menuItem.setIcon(R.drawable.icon_play)
    }

    private fun startMusic() {
        if(isStopped){
            mediaPlayer = MediaPlayer.create(context,R.raw.song)
            isStopped = false
        }
        mediaPlayer.start()
        isPlaying = true
    }

    private fun pauseMusic() {
        mediaPlayer.pause()
        isPlaying = false
    }

    private fun stopMusic() {
        mediaPlayer.stop()
        releaseMusic()
        isPlaying = false
        isStopped = true
    }

    private fun releaseMusic() {
        mediaPlayer.release()
    }
}
