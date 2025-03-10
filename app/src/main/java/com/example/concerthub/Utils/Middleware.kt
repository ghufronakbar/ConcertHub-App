package com.example.concerthub.Utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.concerthub.ApiService.TokenManager
import com.example.concerthub.LoginPage

class Middleware {
    companion object {
        fun handleSessionExpired(context: Context?, activity: Activity?) {
            context?.let {
                TokenManager.clearToken(it)
                val intent = Intent(context, LoginPage::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(intent)
                activity?.finish()
                Toast.makeText(
                    context,
                    "Sesi telah habis",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
