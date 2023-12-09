package com.technogenis.neighborlly.start

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.technogenis.expensior.constant.LoadingBar
import com.technogenis.neighborlly.MainActivity
import com.technogenis.neighborlly.R
import com.technogenis.neighborlly.databinding.ActivityCreateAccountBinding

class CreateAccountActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateAccountBinding

    lateinit var name : String
    lateinit var email : String
    lateinit var phone : String
    lateinit var password : String
    lateinit var conPassword : String

    // email validation pattern
    private val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var loadingBar = LoadingBar(this)

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser!=null)
        {
            Firebase.auth.signOut()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        firestore = FirebaseFirestore.getInstance()

        binding.llLogin.setOnClickListener {
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }

        // button click listener
        binding.btnRegister.setOnClickListener{
            // custom loader
            loadingBar.ShowDialog("please wait")
            // get all input fields in variables
            name = binding.edName.text.toString()
            phone = binding.edPhone.text.toString()
            email = binding.edEmail.text.toString()
            password = binding.edPassword.text.toString()
            conPassword = binding.edConfirmPassword.text.toString()

            if (isValid(name,phone,email,password,conPassword))
            {
                createAccountWithEmailAuth(email,password)
            }
        }

    }

    // function to create account using Email & Password Authentication
    private fun createAccountWithEmailAuth(email: String, password: String)
    {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val userUID = auth.currentUser?.uid.toString()
                    saveUserData(userUID)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT,
                    ).show()
                    loadingBar.HideDialog()

                }
            }
    }

    // function to save data in cloud firestore
    private fun saveUserData(userUID: String?) {
        // use to store data in key for save data in firestore
        val userData = hashMapOf(
            "userUID" to userUID,
            "fullName" to name,
            "phone" to phone,
            "email" to email,
            "password" to password,
        )

        firestore.collection("users").document(userUID.toString())
            .set(userData)
            .addOnCompleteListener{
                val intent  =Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
                loadingBar.HideDialog()
            }.addOnFailureListener{
                loadingBar.HideDialog()
            }

    }

    // function to check empty fields
    private fun isValid(fullName : String, phone : String,
                        email : String, password : String,conPassword : String) : Boolean{

        // condition apply to check empty fields
        if (fullName.isEmpty())
        {
            binding.edName.error = "Full Name Required"
            binding.edName.requestFocus()
            loadingBar.HideDialog()
            return false
        }
        if (phone.isEmpty())
        {
            binding.edPhone.error = "Phone Number Required"
            binding.edPhone.requestFocus()
            loadingBar.HideDialog()
            return false
        }

        if (email.isEmpty())
        {
            binding.edEmail.error = "Email Required"
            binding.edEmail.requestFocus()
            loadingBar.HideDialog()
            return false
        }

        if (!isValidEmail(email))
        {
            binding.edEmail.error = "enter valid email address"
            binding.edEmail.requestFocus()
            loadingBar.HideDialog()
            return false
        }

        if (password.isEmpty())
        {
            binding.edPassword.error = "Password Required"
            binding.edPassword.requestFocus()
            loadingBar.HideDialog()
            return false
        }

        if (conPassword.isEmpty())
        {
            binding.edConfirmPassword.error = "Confirm Password Required"
            binding.edConfirmPassword.requestFocus()
            loadingBar.HideDialog()
            return false
        }

        if (password != conPassword)
        {
            Toast.makeText(this,"Password Not Match", Toast.LENGTH_SHORT).show()
            loadingBar.HideDialog()
            return false
        }

        if (password.length < 8)
        {
            Toast.makeText(this,"Minimum 8 character required", Toast.LENGTH_SHORT).show()
            loadingBar.HideDialog()
            return false
        }

        return true
    }
    private fun isValidEmail(email: String): Boolean {
        return email.matches(emailRegex.toRegex())
    }
}