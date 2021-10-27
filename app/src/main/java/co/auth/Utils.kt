package co.auth

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import java.io.ByteArrayOutputStream


fun getUser():  User {
    val user = User()
    val currentUser = FirebaseAuth.getInstance().currentUser
    currentUser?.let {
        user.name = it.displayName.toString()
        user.image = it.photoUrl.toString()
    }
    return user
}

fun mssg(mssg: String, context: Context) {
    Toast.makeText(context, mssg, Toast.LENGTH_SHORT).show()
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.GONE
}

fun Uri.toBitmap(context: Context): Bitmap {
    return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        val source = ImageDecoder.createSource(context.contentResolver, this)
        ImageDecoder.decodeBitmap(source)
    }else {
        MediaStore.Images.Media.getBitmap(context.contentResolver, this)
    }
}

fun Bitmap.toByteArray(): ByteArray {
    val baos = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.JPEG, 100, baos)
    return baos.toByteArray()
}

fun restartApp(context: Context, activity: MainActivity){
    val intent = Intent(context, MainActivity::class.java)
    context.startActivity(intent)
    activity.finish()
}