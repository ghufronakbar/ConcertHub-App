package com.example.concerthub

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import android.widget.Button
import android.widget.TextView
import com.example.concerthub.api.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterPage : AppCompatActivity() {

    private lateinit var fullNameEditText: TextInputEditText
    private lateinit var emailEditText: TextInputEditText
    private lateinit var phoneNumberEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var confirmPasswordEditText: TextInputEditText
    private lateinit var backLogin: TextView
    private lateinit var registerButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_page)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        fullNameEditText = findViewById(R.id.fullName)
        emailEditText = findViewById(R.id.email)
        phoneNumberEditText = findViewById(R.id.phoneNumber)
        passwordEditText = findViewById(R.id.password)
        confirmPasswordEditText = findViewById(R.id.confirmPassword)
        registerButton = findViewById(R.id.registerBtn)
        backLogin = findViewById(R.id.backLogin)

        backLogin.setOnClickListener {
            onBackPressed()
        }


        registerButton.setOnClickListener {
            val name = fullNameEditText.text.toString()
            val email = emailEditText.text.toString()
            val phoneNumber = phoneNumberEditText.text.toString()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

            if (name.isEmpty() || email.isEmpty() || phoneNumber.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                if (name.isEmpty()) {
                    fullNameEditText.error = "Masukan nama lengkap"
                }
                if (email.isEmpty()) {
                    emailEditText.error = "Masukan Email"
                }
                if (phoneNumber.isEmpty()) {
                    phoneNumberEditText.error = "Masukan nomor telepon"
                }
                if (password.isEmpty()) {
                    Toast.makeText(this, "Masukan password", Toast.LENGTH_SHORT).show()
                }
                if (confirmPassword.isEmpty()) {
                    Toast.makeText(this, "Masukan konfirmasi password", Toast.LENGTH_SHORT).show()
                }
                Toast.makeText(this, "lengkapi form", Toast.LENGTH_SHORT).show()
            } else if (!email.matches(emailPattern.toRegex())) {
                emailEditText.error = "Masukan alamat email yang valid"
            } else if (phoneNumber.length < 7) {
                phoneNumberEditText.error = "Masukan nomor telepon yang valid"
            } else if (password.length < 4) {
                Toast.makeText(this, "Password minimal 4 karakter", Toast.LENGTH_SHORT).show()
            } else if (password != confirmPassword) {
                Toast.makeText(this, "Passwort tidak sama", Toast.LENGTH_SHORT).show()
            } else {
                val apiService = RetrofitInstance.api

                val requestBody = mapOf(
                    "fullname" to name,
                    "phone" to phoneNumber,
                    "email" to email,
                    "password" to password,
                    "confirmation_password" to confirmPassword
                )


                val register: Call<Void?>? = apiService!!.register(requestBody)

                if (register != null) {
                    register.enqueue(object : Callback<Void?> {
                        override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                            if (response.isSuccessful) {
                                val statusCode = response.code()
                                if (statusCode == 200) {
                                    Toast.makeText(
                                        this@RegisterPage,
                                        "Registrasi berhasil",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    onBackPressed()
                                } else {
                                    Toast.makeText(
                                        this@RegisterPage,
                                        "Gagal Membuat akun",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                Log.d("ErrorCuy", "onResponse: ${response.errorBody().toString()}")
                                Toast.makeText(
                                    this@RegisterPage,
                                    "Gagal Membuat akun",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onFailure(call: Call<Void?>, t: Throwable) {

                            Toast.makeText(
                                this@RegisterPage,
                                "Layanan tidak tersedia",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
                }
            }
        }
    }
}