package com.example.concerthub.MainPageActivity.AccountPageActivity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.concerthub.ApiService.TokenManager
import com.example.concerthub.R
import com.example.concerthub.api.RetrofitInstance
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditProfilePage : AppCompatActivity() {

    private lateinit var editTextFullName: TextInputEditText
    private lateinit var editTextPhone: TextInputEditText
    private lateinit var editTextEmail: TextInputEditText
    private lateinit var buttonUpdateProfile: Button
    private lateinit var btnBack: ImageButton

    companion object {
        const val RESULT_UPDATED_PROFILE = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_profile_page)

        // Inisialisasi view
        editTextFullName = findViewById(R.id.editTextFullName)
        editTextPhone = findViewById(R.id.editTextPhone)
        editTextEmail = findViewById(R.id.editTextEmail)
        buttonUpdateProfile = findViewById(R.id.buttonUpdateProfile)
        btnBack = findViewById(R.id.btn_back)

        btnBack.setOnClickListener {
            onBackPressed()
        }

        editTextFullName.setText(intent.getStringExtra("fullname"))
        editTextPhone.setText(intent.getStringExtra("phone"))
        editTextEmail.setText(intent.getStringExtra("email"))

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        buttonUpdateProfile.setOnClickListener {
            val name = editTextFullName.text.toString()
            val email = editTextEmail.text.toString()
            val phoneNumber = editTextPhone.text.toString()


            val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

            if (name.isEmpty() || email.isEmpty() || phoneNumber.isEmpty()) {
                if (name.isEmpty()) {
                    editTextFullName.error = "Masukan nama lengkap"
                }
                if (email.isEmpty()) {
                    editTextEmail.error = "Masukan Email"
                }
                if (phoneNumber.isEmpty()) {
                    editTextPhone.error = "Masukan nomor telepon"
                }
                Toast.makeText(this, "lengkapi form", Toast.LENGTH_SHORT).show()
            } else if (!email.matches(emailPattern.toRegex())) {
                editTextEmail.error = "Masukan alamat email yang valid"
            } else if (phoneNumber.length < 7) {
                editTextPhone.error = "Masukan nomor telepon yang valid"
            } else {
                updateUserProfile()
            }
        }
    }

    private fun updateUserProfile() {
        val token = TokenManager.getToken(this)

        // Ambil data dari input fields
        val fullName = editTextFullName.text.toString()
        val phone = editTextPhone.text.toString()
        val email = editTextEmail.text.toString()

        if (token != null) {
            val apiService = RetrofitInstance.create(token)

            val requestBody = mapOf(
                "fullname" to fullName,
                "phone" to phone,
                "email" to email
            )

            val editProfile: Call<Void?>? = apiService.updateUserProfile(token, requestBody)

            if (editProfile != null) {
                editProfile.enqueue(object : Callback<Void?> {
                    override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                        if (response.isSuccessful) {
                            Toast.makeText(
                                this@EditProfilePage,
                                "Berhasil Memperbarui Profile",
                                Toast.LENGTH_SHORT
                            ).show()
                            // Setelah profil berhasil diperbarui, kirimkan kode RESULT_UPDATED_PROFILE
                            val resultCode = 123
                            val intent = Intent()
                            setResult(resultCode, intent)
                            finish()

                        } else {
                            Toast.makeText(
                                this@EditProfilePage,
                                "Gagal Memperbarui Profile",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<Void?>, t: Throwable) {
                        Toast.makeText(
                            this@EditProfilePage,
                            "Error: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            }
        }
    }
}