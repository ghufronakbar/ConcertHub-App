package com.example.concerthub

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.concerthub.ApiService.ApiService
import com.example.concerthub.ApiService.TokenManager
import com.example.concerthub.DetailEventActivity.MidtransPayment
import com.example.concerthub.Models.HistoryResponse
import com.example.concerthub.Utils.Middleware
import com.example.concerthub.api.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

class PaymentPage : AppCompatActivity() {
    private lateinit var confirmPaymentButton: Button
    private lateinit var cancelOrderButton: Button
    private lateinit var countDownContainer: LinearLayout

    companion object {
        const val PAYMENT_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_payment_page)
        val snapToken = intent.getStringExtra("snapToken")
        val id_history = intent.getStringExtra("id_history")
        cancelOrderButton = findViewById(R.id.cancel_order)
        confirmPaymentButton = findViewById(R.id.confirm_payment)
        countDownContainer = findViewById(R.id.countDownContainer)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        if (snapToken != null && id_history != null) {
            fetchEventDetails(id_history.toInt())
            confirmPaymentButton.setOnClickListener {
                val intent = Intent(this, MidtransPayment::class.java)
                intent.putExtra("snapToken", snapToken)
                startActivity(intent)
            }


            cancelOrderButton.setOnClickListener {
                val token = TokenManager.getToken(this)
                if (token != null) {
                    if (id_history != null) {
                        RetrofitInstance.api.cancelOrder(token, id_history.toInt())
                            .enqueue(object : Callback<ApiService.Response> {
                                override fun onResponse(
                                    call: Call<ApiService.Response>,
                                    response: Response<ApiService.Response>
                                ) {
                                    if (response.isSuccessful) {
                                        Toast.makeText(
                                            this@PaymentPage,
                                            "Pembelian dibatalkan",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        setResult(RESULT_OK)
                                        finish()
                                    } else {
                                        Toast.makeText(
                                            this@PaymentPage,
                                            "Tidak bisa membatalkan pembelian",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }

                                override fun onFailure(
                                    call: Call<ApiService.Response>,
                                    t: Throwable
                                ) {
                                    Toast.makeText(
                                        this@PaymentPage,
                                        "Layanan tidak tersedia",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                            })
                    }
                }
            }
        }


        val backButton = findViewById<ImageButton>(R.id.btn_back)
        backButton.setOnClickListener {
            finish()
        }
    }

    private fun fetchEventDetails(idHistory: Int) {
        val token = TokenManager.getToken(this)
        if (token != null) {
            RetrofitInstance.api.getHistory(token, idHistory)
                .enqueue(object : Callback<HistoryResponse> {
                    override fun onResponse(
                        call: Call<HistoryResponse>,
                        response: Response<HistoryResponse>
                    ) {
                        if (response.isSuccessful) {
                            Log.d("PaymentPage FUFUFAFA", "Response: ${response.body()}")
                            response.body()?.let { payment ->
                                updateUI(payment)
                            }
                        } else if (response.code() == 401) {
                            Middleware.handleSessionExpired(this@PaymentPage, this@PaymentPage)
                        } else {

                            Toast.makeText(
                                this@PaymentPage,
                                "Gagal mengambil data",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }

                    override fun onFailure(call: Call<HistoryResponse>, t: Throwable) {
                        Toast.makeText(
                            this@PaymentPage,
                            "Layanan Tidak Tersedia",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                })
        }
    }

    private fun updateUI(payment: HistoryResponse) {

        val totalPaymentTextView = findViewById<TextView>(R.id.total_payment)
        val countDownTextView = findViewById<TextView>(R.id.countDown)

        totalPaymentTextView.text = "Rp. ${payment.total}"


        // Convert API datetime to local timezone
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        val eventTime = sdf.parse(payment.datetime)
        val currentTime = Date()

        if (eventTime != null) {
            val tenMinutesInMillis = TimeUnit.MINUTES.toMillis(10)
            val timeDifference = eventTime.time + tenMinutesInMillis - currentTime.time

            if (timeDifference > 0) {
                startCountDown(timeDifference, countDownTextView)
            } else {
                confirmPaymentButton.isEnabled = false
                confirmPaymentButton.isClickable = false
                confirmPaymentButton.text = "Pembayaran Kadaluarsa"
                confirmPaymentButton.setTextColor(Color.WHITE)
                confirmPaymentButton.setBackgroundColor(Color.RED)
                cancelOrderButton.visibility = View.GONE
                countDownContainer.visibility = View.GONE
                countDownTextView.text = "00:00"
            }
        } else {
            countDownTextView.text = "00:00"
        }

        // Setup copy buttons
        val copyTotalPaymentButton = findViewById<Button>(R.id.copy_total_payment)

        copyTotalPaymentButton.setOnClickListener {
            copyToClipboard("Total Payment", payment.total.toString())
            Toast.makeText(this, "Total pembayaran disalin", Toast.LENGTH_SHORT).show()
        }

    }


    private fun startCountDown(timeInMillis: Long, countDownTextView: TextView) {
        object : CountDownTimer(timeInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60
                countDownTextView.text =
                    String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                countDownTextView.text = "00:00"
            }
        }.start()
    }

    private fun copyToClipboard(label: String, text: String) {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
    }
}
