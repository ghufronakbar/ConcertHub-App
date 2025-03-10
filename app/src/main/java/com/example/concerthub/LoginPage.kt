package com.example.concerthub

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.example.concerthub.ApiService.TokenManager
import com.example.concerthub.Models.LoginRequest
import com.example.concerthub.Models.LoginResponse
import com.example.concerthub.api.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginPage : AppCompatActivity() {
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inisialisasi komponen-komponen
        emailEditText = findViewById(R.id.signInEmail)
        passwordEditText = findViewById(R.id.signInPassword)
        loginButton = findViewById(R.id.signInBtn)
        registerButton = findViewById(R.id.registerBtn)

        emailEditText.setText("")
        passwordEditText.setText("")


        // Contoh penggunaan komponen-komponen
        loginButton.setOnClickListener {
            val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
            if (emailEditText.text.toString().isEmpty() || passwordEditText.text.toString()
                    .isEmpty()
            ) {
                if (emailEditText.text.toString().isEmpty()) {
                    emailEditText.error = "Masukan alamat email"
                } else if (passwordEditText.text.toString().isEmpty()) {
                    Toast.makeText(this, "Masukan password", Toast.LENGTH_SHORT).show()
                }
            } else if (!emailEditText.text.toString().matches(emailPattern.toRegex())) {
                emailEditText.error = "Masukan alamat email yang valid"
            } else {
                Log.d(
                    "ErrorCuy",
                    "onCreate: ${emailEditText.text.toString().matches(emailPattern.toRegex())}"
                )
                login()

            }
        }

        registerButton.setOnClickListener {
            val intent = Intent(this@LoginPage, RegisterPage::class.java)
            startActivity(intent)
        }
    }

    private fun login() {
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email and password must be filled", Toast.LENGTH_SHORT).show()
            return
        }

        val loginRequest = LoginRequest(email, password)
        RetrofitInstance.api.login(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                val loginResponse = response.body()
                val statusCode = response.code()
                if (statusCode == 200 && loginResponse != null && loginResponse.token != null) {
                    TokenManager.saveToken(this@LoginPage, loginResponse.token)
                    Toast.makeText(this@LoginPage, "Login successful", Toast.LENGTH_SHORT).show()
                    val intent = MainPage.getIntent(this@LoginPage, MainPage.FRAGMENT_HOME)
                    startActivity(intent)
                    finish()
                    // Save token and navigate to next screen
                } else {
                    Log.d("ErrorCuy", "onResponse: ${response}")
                    Toast.makeText(this@LoginPage, "Cek Email dan Password!", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.d("ErrorCuy", "onResponse: $t, $call")
                Toast.makeText(this@LoginPage, "Layanan tidak tersedia", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
