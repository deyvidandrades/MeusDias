package com.deyvidandrades.meusdias.assistentes

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import java.io.IOException

class AssistenteViewToBitmap {

    companion object {
        fun getViewToBitmapURI(context: Context, printView: View): Uri? {
            return saveScreenshot(context, printView)
        }
        private fun saveScreenshot(context: Context, printView: View): Uri? {
            // Save the screenshot using MediaStore
            val bitmap = getBitmapFromView(printView)
            val displayName = "screenshot_meus_dias.png"
            val mimeType = "image/png"

            val values = ContentValues()
            values.put(MediaStore.Images.Media.DISPLAY_NAME, displayName)
            values.put(MediaStore.Images.Media.MIME_TYPE, mimeType)

            val contentResolver = context.contentResolver
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.put(
                    MediaStore.Images.Media.RELATIVE_PATH,
                    Environment.DIRECTORY_PICTURES + "/MeusDias"
                )
            }

            // Save the image
            val imageUri =
                contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            try {
                val outputStream = contentResolver.openOutputStream(imageUri!!)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream?.close()
                return imageUri
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
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
}