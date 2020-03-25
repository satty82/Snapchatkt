@file:Suppress("NAME_SHADOWING")

package satyam.snap.snapchatktt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast
import android.content.Intent
import android.view.View
import com.google.firebase.database.FirebaseDatabase
import satyam.snap.snapchatkt.R


class MainActivity : AppCompatActivity() {

    var emailEditText : EditText ? = null
    var passwordEditText : EditText ? = null
    val mAuth = FirebaseAuth.getInstance()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        setTitle("Login")


        if(mAuth.currentUser != null)
        {
            login()
        }

    }

    fun goClicked( view: View) {

        mAuth.signInWithEmailAndPassword(emailEditText?.text.toString(), passwordEditText?.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information

                    login()

                } else {
                        // Signup user
                    mAuth.createUserWithEmailAndPassword(emailEditText?.text.toString(), passwordEditText?.text.toString()).addOnCompleteListener(this){
                        task ->

                        if(task.isSuccessful)
                        {
                            // Add to Database
                            FirebaseDatabase.getInstance().getReference().child("users").child(task.result!!.user!!.uid).child("email").setValue(emailEditText?.text.toString())
                            login()
                        }
                        else{

                            Toast.makeText(this,"Login Failed !! Try Again ",Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                // ...
            }
    }

    fun login(){
        // move to next Activity
       val intent =Intent(this, SnapActivity::class.java)
        startActivity(intent)
    }



}
