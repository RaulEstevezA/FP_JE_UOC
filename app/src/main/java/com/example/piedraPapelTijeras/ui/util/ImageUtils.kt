package com.example.piedraPapelTijeras.ui.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
fun salvarFotoAGaleria(
    context: Context,
    bitmap: ImageBitmap,
    displayName: String
): Boolean {
    // Define dónde se guardará la imagen (en la carpeta pública de Imágenes)
    val imageCollection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
    } else {
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    }

    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "$displayName.jpg")
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        // IS_PENDING le dice al sistema que estamos trabajando en este archivo
        put(MediaStore.Images.Media.IS_PENDING, 1)
    }

    val resolver = context.contentResolver
    val uri = resolver.insert(imageCollection, contentValues)

    uri?.let {
        try {
            // Abrimos un "canal" para escribir los datos de la imagen
            resolver.openOutputStream(it)?.use { outputStream ->
                // Comprimimos el bitmap en formato JPEG y lo escribimos
                bitmap.asAndroidBitmap().compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            }
            // Una vez guardado, quitamos el estado "pendiente"
            contentValues.clear()
            contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(it, contentValues, null, null)

            Toast.makeText(context, "¡Victoria guardada en la galería!", Toast.LENGTH_SHORT).show()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error al guardar la imagen.", Toast.LENGTH_SHORT).show()
            return false
        }
    }
    return false
}


