package com.deyvidandrades.meusdias.assistentes

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.view.View
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

object AssistenteViewToBitmap {

    fun getViewToBitmapURI(context: Context, printView: View): Uri? {
        return cacheScreenshot(context, printView)
    }

    private fun cacheScreenshot(context: Context, printView: View): Uri? {

        val bitmap = getBitmapFromView(printView)
        val cachePath = File(context.cacheDir, "images")

        cachePath.mkdirs()

        val file = File(cachePath, "recorde_meus_dias.png")
        val fileOutputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
        fileOutputStream.close()

        return FileProvider.getUriForFile(context, "com.deyvidandrades.meusdias.provider", file)
    }

    private fun getBitmapFromView(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(
            view.width, view.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }
}