package com.example.proyecto_ppg

import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import kotlin.math.atan2

class Ruleta(context: Context, private val imagen: ImageView) : View(context) {
    private lateinit var art: Articulo

    fun setArticulo(articulo: Articulo) {
        this.art = articulo
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        val gradosTocados = calcularGradosTocados(x, y)


        if (art.getNivel() < 1) {
            val resultado = opcion(gradosTocados)

            if (resultado) {
                Toast.makeText(context, "Se mejora el objeto", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "No se mejora el objeto", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "No se puede mejorar más el objeto", Toast.LENGTH_SHORT).show()
        }
        return true
    }

    private fun calcularGradosTocados(x: Float, y: Float): Int {
        val centerX = imagen.x + imagen.width / 2f
        val centerY = imagen.y + imagen.height / 2f

        val diffX = x - centerX
        val diffY = y - centerY

        val anguloRad = atan2(diffY, diffX)
        var anguloDeg = Math.toDegrees(anguloRad.toDouble())

        if (anguloDeg < 0) {
            anguloDeg += 360
        }

        return anguloDeg.toInt()
    }

    internal fun opcion(grados: Int): Boolean {
        val opcion1: Pair<Int, Int>
        val opcion2: Pair<Int, Int>

        if (art.getNivel() == 0) {
            opcion1 = Pair(0, (360 * 0.4).toInt())
            opcion2 = Pair(opcion1.second, 360)
            imagen.setImageResource(R.drawable.wheel_forty)
        } else {
            opcion1 = Pair(0, (360 * 0.2).toInt())
            opcion2 = Pair(opcion1.second, 360)
            imagen.setImageResource(R.drawable.wheel_twenty)
        }


        return when (grados) {
            in opcion1.first until opcion1.second -> true
            in opcion2.first until opcion2.second -> false
            else -> false
        }
    }
}
