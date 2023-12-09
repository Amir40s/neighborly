package com.technogenis.neighborlly.start

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.technogenis.expensior.constant.LoadingBar
import com.technogenis.neighborlly.MainActivity
import com.technogenis.neighborlly.R
import com.technogenis.neighborlly.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"

    private lateinit var email : String; lateinit var password : String
    private lateinit var auth: FirebaseAuth

    val loadingBar = LoadingBar(this)

    private lateinit var firestore : FirebaseFirestore


    override fun onStart() {
        super.onStart()
        val user = auth.currentUser
        if (user!=null)
        {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // connection between database and mobile app
        auth = Firebase.auth
        firestore = FirebaseFirestore.getInstance()

        binding.llRegister.setOnClickListener {
            val intent = Intent(this,CreateAccountActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogin.setOnClickListener{
            loadingBar.ShowDialog("please wait")
            email = binding.edEmail.text.toString()
            password = binding.edPassword.text.toString()

            if (isValid(email,password))
            {
                //function to Sign in account
                signWithEmailAuth(email,password)
            }
        }
    }

    private fun signWithEmailAuth(email: String, password: String)
    {

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    loadingBar.HideDialog()
                   val intent = Intent(this,MainActivity::class.java)
                    startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()

                }
            }

    }

    private fun isValid(email : String, password : String) : Boolean{

        if (email.isEmpty())
        {
            Toast.makeText(this,"Password Required", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password.isEmpty())
        {
            Toast.makeText(this,"Password Required", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!isValidEmail(email))
        {
            binding.edEmail.error = "enter valid email address"
            binding.edEmail.requestFocus()
            loadingBar.HideDialog()
            return false
        }



        return true
    }

    private fun isValidEmail(email: String): Boolean {
        return email.matches(emailRegex.toRegex())
    }
}