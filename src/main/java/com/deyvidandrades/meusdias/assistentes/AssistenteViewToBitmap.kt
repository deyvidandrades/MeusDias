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
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class AssistenteViewToBitmap {

    companion object {
        fun getViewToBitmapURI(context: Context, printView: View): Uri? {
            return cacheScreenshot(context, printView)
        }

        private fun removerPrintAntigo(context: Context, uri: Uri) {
            context.contentResolver.delete(
                uri,
                "${MediaStore.Images.Media.DISPLAY_NAME} = ?",
                arrayOf("screenshot_meus_dias.png")
            )
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