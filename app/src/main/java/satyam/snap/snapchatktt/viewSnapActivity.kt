package satyam.snap.snapchatktt

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.snap.snapchatkt.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.net.HttpURLConnection
import java.net.URL

class viewSnapActivity : AppCompatActivity() {

    var messageTextView: TextView? = null
    var snapImageView: ImageView? = null
    val mAuth = FirebaseAuth.getInstance()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_snap)

        messageTextView = findViewById(R.id.messageTextView)
        snapImageView = findViewById(R.id.snapImageView)

        messageTextView?.text = intent.getStringExtra("message")
        setTitle("Snap")


        val imageDownloader = ImageDownloader()
        val myImage: Bitmap

        try {

            Log.i("imageURL", intent.getStringExtra("imageURL"))

            myImage = imageDownloader.execute(intent.getStringExtra("imageURL")).get()

            snapImageView?.setImageBitmap(myImage)

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    inner class ImageDownloader : AsyncTask<String, Void, Bitmap>() {


        override fun doInBackground(vararg urls: String): Bitmap? {

            try {
                val url = URL(urls[0])

                val httpURLConnection = url.openConnection() as HttpURLConnection
                httpURLConnection.connect()

                val `in` = httpURLConnection.inputStream

                return BitmapFactory.decodeStream(`in`)

            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }

        }
    }


    override fun onBackPressed() {
        super.onBackPressed()

        FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.currentUser?.uid.toString()).child("snaps").child(intent.getStringExtra("snapKey")).removeValue()

        FirebaseStorage.getInstance().getReference().child("images").child(intent.getStringExtra("imageName")).delete()

    }
}

