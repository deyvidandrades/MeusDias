package com.deyvidandrades.meusdias.data.helpers

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

object BitmapHelper {
    fun saveBitmapToFile(context: Context, bitmap: ImageBitmap): File {
        val file = File(context.cacheDir, "shared_image.png")
        FileOutputStream(file).use {
            bitmap.asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 100, it)
        }
        return file
    }

    fun getImageUri(context: Context, file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    fun shareImage(context: Context, imageUri: Uri) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, imageUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Image"))
    }
}