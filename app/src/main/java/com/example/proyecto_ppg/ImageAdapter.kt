package com.example.proyecto_ppg

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView

class ImageAdapter(
    private val context : Context,
    private val list: IntArray,
    private val tam: Int
) :BaseAdapter(){
    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(p0: Int): Any {
        return list[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val img = if (p1 == null){
            ImageView(context).apply{
                layoutParams = ViewGroup.LayoutParams(tam,tam)
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
        } else{
            p1 as ImageView
        }

        img.setImageResource(list[p0])
        return img
    }

}