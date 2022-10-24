package com.example.mystoryapp.utlis

import android.animation.ObjectAnimator
import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.opengl.Visibility
import android.os.Environment
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import com.example.mystoryapp.R
import com.example.mystoryapp.utlis.Statics.FILENAME_FORMAT
import java.io.*
import java.nio.ByteBuffer
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object Statics {
    const val FILENAME_FORMAT = "dd-MMM-yyyy"
}
/**
 * TimeStamp
 */
val timeStamp:String = SimpleDateFormat(
    FILENAME_FORMAT,
    Locale.US
).format(System.currentTimeMillis())

/**
 * tempporary for intent Camera
 */
fun createCustomTempFile(context: Context): File{
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(timeStamp, ".jpg", storageDir)
}

/**
 * function untuk mengubah uri menjadi sebuah file
 */
fun uriToFile(selectedImage: Uri, context: Context): File{
    val contentResolver: ContentResolver = context.contentResolver
    val myFile = createCustomTempFile(context)

    val inputStream = contentResolver.openInputStream(selectedImage) as InputStream
    val outputStream:OutputStream = FileOutputStream(myFile)
    val buf = ByteArray(1024)
    var lent: Int
    while (inputStream.read(buf).also { lent = it } > 0) outputStream.write(buf, 0, lent)
    outputStream.close()
    inputStream.close()

    return myFile
}

/**
 * function to compress image if image size more than 1MB
 */
fun reduceFile(file: File): File{
    val bitmap = BitmapFactory.decodeFile(file.path)
    var compressQuality = 100
    var streamLength: Int
    do {
        val bmpStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
        val bmpPicByteArray = bmpStream.toByteArray()
        streamLength = bmpPicByteArray.size
        compressQuality-=5
    } while (streamLength > 1000000)
    bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
    return file
}


/**
 * extension for set visibility an a View like customView
 */
fun View.animateVisibility(visibility: Boolean){
//    this.isVisible = visibility
    ObjectAnimator.ofFloat(this,View.ALPHA, if (visibility) 1f else 0f)
        .setDuration(500)
        .start()
}

/**
 * extension to generate bearer for authorization token
 */
fun generateBearerToken(token:String): String{
    return "Bearer $token"
}

/**
 * extension for convert dataFormat from api to TextView
 */
fun TextView.generateDateFormat(time: String){
    val currentFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    val simpleDateFormat = SimpleDateFormat(currentFormat, Locale.getDefault())
    val dateParse = simpleDateFormat.parse(time) as Date

    val formattedDate = DateFormat.getDateInstance(DateFormat.FULL).format(dateParse)
    this.text = formattedDate
}