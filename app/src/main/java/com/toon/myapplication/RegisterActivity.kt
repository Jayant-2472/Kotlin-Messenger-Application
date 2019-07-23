package com.toon.myapplication

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.Toast
//import jdk.nashorn.internal.runtime.ECMAException.getException
import com.google.firebase.auth.FirebaseUser
//import org.junit.experimental.results.ResultMatchers.isSuccessful
import com.google.firebase.auth.AuthResult
import com.google.android.gms.tasks.Task
import android.support.annotation.NonNull
import com.google.android.gms.tasks.OnCompleteListener
import android.R.attr.password
import android.app.Activity
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Parcelable
import android.provider.MediaStore
import android.support.v4.app.FragmentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.parcel.Parcelize
import java.util.*


class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        register_button_register.setOnClickListener {

            performRegister()

        }

        login_textView_register.setOnClickListener {

            Log.d("RegisterActivity", "intent login activity")

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

        }

        profile_photo_button_register.setOnClickListener {

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"

            startActivityForResult(intent, 0)

            Log.d("RegisterActivity", "show photo selector")

        }

    }

    private fun performRegister(){

        val email = email_edittext_register.text.toString()
        val password = password_edittext_register.text.toString()

        Log.d("RegisterActivity", "email is " + email)
        Log.d("RegisterActivity", "password is $password")

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, OnCompleteListener<AuthResult> { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("RegisterActivity", "createUserWithEmail:success")

                    uploadImageToFirebaseStorage()

                    Toast.makeText(
                        this, "Register success",
                        Toast.LENGTH_LONG
                    ).show()

//                        val user = FirebaseAuth.getInstance().getCurrentUser()
//                        updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("RegisterActivity", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        this, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
//                        updateUI(null)
                }

                // ...
            })

    }

    private fun uploadImageToFirebaseStorage(){

        if (selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {

                Log.d("RegisterActivity", "Successfully uploaded image ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {

                    Log.d("RegisterActivity", "File Location: $it")

                    saveUserToFirebaseDatabase(it.toString())

                }

            }
            .addOnFailureListener {
                //
            }

    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {

        val uid = FirebaseAuth.getInstance().uid ?:""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid, username_edittext_register.text.toString(), profileImageUrl)

        ref.setValue(user)
            .addOnSuccessListener {

                Log.d("RegisterActivity", "Finally we saved user to firebase database")

                val intent = Intent(this, LatestMessagesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)

            }
            .addOnFailureListener {

                Log.d("RegisterActivity", "Failed to set value to database ${it.message}")

            }

    }

    var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){

            Log.d("RegisterActivity", "photo was selected")

            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            circle_imageView_select_photo_register.setImageBitmap(bitmap)

            profile_photo_button_register.alpha = 0f

//            val bitmapDrawable = BitmapDrawable(bitmap)
//            profile_photo_button_register.setBackgroundDrawable(bitmapDrawable)

        }

    }

}

@Parcelize
class User(val uid: String, val username: String, val profileImageUrl: String): Parcelable {

    constructor() : this("", "", "")

}
