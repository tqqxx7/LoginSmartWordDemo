package com.bp.loginsmartworddemo

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

class MapUtils{



    fun getMarkerIconFromDrawable(drawable: Drawable?, scale: Float = 0.07f): BitmapDescriptor {
        val canvas = Canvas()
        val drawableWidth = ((drawable?.intrinsicWidth?:0)*scale).toInt()
        val drawableHeight = ((drawable?.intrinsicHeight?:0)*scale).toInt()
        val bitmap = Bitmap.createBitmap(drawableWidth, drawableHeight, Bitmap.Config.ARGB_8888)
        canvas.setBitmap(bitmap)
        drawable?.setBounds(0, 0, drawableWidth, drawableHeight)
        drawable?.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }




}