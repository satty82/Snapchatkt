package satyam.snap.snapchatktt

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.snap.snapchatkt.R
import com.google.android.gms.tasks.Continuation
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream
import java.util.*
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task

class createSnapActivity : AppCompatActivity() {

    var createSnapImageView: ImageView? = null
    var messageEditText: EditText? = null
    val imageName = UUID.randomUUID().toString() + ".jpg"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_snap)
        setTitle("Create Snap")

        createSnapImageView = findViewById(R.id.createSnapImageView)
        messageEditText = findViewById(R.id.snapEditText)
    }

    fun getPhoto() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 1)
    }

    fun chooseImageClicked(view: View) {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        } else {
            getPhoto()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val selectedImage = data!!.data

        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImage)
                createSnapImageView?.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhoto()
            }
        }
    }

    fun nextClicked(view: View) {

//        createSnapImageView?.setDrawingCacheEnabled(true)
//        createSnapImageView?.buildDrawingCache()
//        val bitmap = createSnapImageView?.getDrawingCache()
//        val baos = ByteArrayOutputStream()
//        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
//        val data = baos.toByteArray()
//
//
//
//        val uploadTask = FirebaseStorage.getInstance().getReference().child("images").child(imageName).putBytes(data)
//        uploadTask.addOnFailureListener(OnFailureListener {
//            // Handle unsuccessful uploads
//            Toast.makeText(this,"UploadFailed",Toast.LENGTH_SHORT).show()
//        }).addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->
//            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
//            val downloadUrl = taskSnapshot.uploadSessionUri // uploadSessionURi is the one which will provide url of the image !! Got it man
//            Log.i("URL", downloadUrl.toString())

        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child("images").child(imageName)

        createSnapImageView?.setDrawingCacheEnabled(true)
        createSnapImageView?.buildDrawingCache()
        val bitmap = createSnapImageView?.getDrawingCache()
        val baos = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 18, baos)
        val data = baos.toByteArray()


        val uploadTask = imageRef.putBytes(data)
        uploadTask.addOnFailureListener(OnFailureListener {
            // Handle unsuccessful uploads
            Toast.makeText(this, "Upload Failed", Toast.LENGTH_SHORT).show()
        }).addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->
            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
            Toast.makeText(this, "Upload success", Toast.LENGTH_SHORT).show()
        })

        val urlTask = uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            return@Continuation imageRef.downloadUrl
        }).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUrl = task.result
                Log.i("URL: ", downloadUrl.toString())

                val intent = Intent(this, chooseUserActivity::class.java)

                intent.putExtra("imageURL", downloadUrl.toString()) // This downloadUrl.toString() bring us the genuine URL
                intent.putExtra("imageName", imageName)
                intent.putExtra("message", messageEditText?.text.toString())
                startActivity(intent)


            } else {
                // Handle failures
                Log.i("URL: ", "Unable to retrieve URL")
            }




        }
    }
}
