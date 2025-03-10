package com.example.concerthub

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.concerthub.ApiService.ApiService
import com.example.concerthub.ApiService.TokenManager
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
    companion object {
        const val PAYMENT_REQUEST_CODE = 1001
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_payment_page)
        val id_history = intent.getStringExtra("id_history")
        cancelOrderButton = findViewById(R.id.cancel_order)
        confirmPaymentButton = findViewById(R.id.confirm_payment)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        if (id_history != null) {
            fetchEventDetails(id_history.toInt())
            confirmPaymentButton.setOnClickListener {
                Log.d("ErrorCuy", "onCreate: $id_history")
                val token = TokenManager.getToken(this)
                if (token != null) {
                    RetrofitInstance.api.confPay(token, id_history.toInt())
                        .enqueue(object : Callback<ApiService.Response> {
                            override fun onResponse(
                                call: Call<ApiService.Response>,
                                response: Response<ApiService.Response>
                            ) {
                                if (response.isSuccessful) {
                                    Toast.makeText(
                                        this@PaymentPage,
                                        "Pembayaran akan dikonfirmasi",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    setResult(RESULT_OK)
                                    finish()
                                } else {
                                    Toast.makeText(
                                        this@PaymentPage,
                                        "Tidak bisa mengkonfirmasi pembayaran",
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
        val bankNameTextView = findViewById<TextView>(R.id.bank_name)
        val bankAccountTextView = findViewById<TextView>(R.id.bank_account)
        val countDownTextView = findViewById<TextView>(R.id.countDown)

        totalPaymentTextView.text = "Rp. ${payment.total}"

        val paymentInfo = payment.paymentInformation
        val bankName = paymentInfo.substringBeforeLast(" ")
        val bankAccount = paymentInfo.substringAfterLast(" ")

        bankNameTextView.text = bankName
        bankAccountTextView.text = bankAccount

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
                countDownTextView.text = "00:00"
            }
        } else {
            countDownTextView.text = "00:00"
        }

        // Setup copy buttons
        val copyTotalPaymentButton = findViewById<Button>(R.id.copy_total_payment)
        val copyBankAccountButton = findViewById<Button>(R.id.copy_bank_account)

        copyTotalPaymentButton.setOnClickListener {
            copyToClipboard("Total Payment", payment.total.toString())
            Toast.makeText(this, "Total pembayaran disalin", Toast.LENGTH_SHORT).show()
        }

        copyBankAccountButton.setOnClickListener {
            copyToClipboard("Bank Account", bankAccountTextView.text.toString())
            Toast.makeText(this, "Nomor rekening disalin", Toast.LENGTH_SHORT).show()
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
