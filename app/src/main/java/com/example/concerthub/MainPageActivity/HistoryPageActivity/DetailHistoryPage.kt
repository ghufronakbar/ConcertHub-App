package com.example.concerthub.MainPageActivity.HistoryPageActivity

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.concerthub.ApiService.ApiService
import com.example.concerthub.ApiService.TokenManager
import com.example.concerthub.Models.HistoryResponse
import com.example.concerthub.PaymentPage
import com.example.concerthub.R
import com.example.concerthub.Utils.GenerateQrCode
import com.example.concerthub.Utils.Middleware
import com.example.concerthub.api.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailHistoryPage : AppCompatActivity() {
    private lateinit var btnBack: ImageButton
    private lateinit var informationPayment: Button
    private lateinit var cancelOrderButton: Button
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var id_history: String
    private lateinit var snapToken: String

    companion object {
        const val PAYMENT_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail_history_page)
        id_history = intent.getStringExtra("id_history")!!
        snapToken = intent.getStringExtra("snapToken")!!
        cancelOrderButton = findViewById(R.id.cancel_order)
        btnBack = findViewById(R.id.btn_back)
        informationPayment = findViewById(R.id.information_payment)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btnBack.setOnClickListener {
            finish()
        }

        if (id_history != null) {
            swipeRefreshLayout.setOnRefreshListener {
                fetchEventDetails(id_history.toInt())
                swipeRefreshLayout.isRefreshing = false
            }
            fetchEventDetails(id_history.toInt())

            informationPayment.setOnClickListener {
                val intent = Intent(this, PaymentPage::class.java).apply {
                    putExtra("id_history", id_history)
                    putExtra("snapToken", snapToken)
                }
                startActivityForResult(intent, PAYMENT_REQUEST_CODE)
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
                                            this@DetailHistoryPage,
                                            "Pembelian dibatalkan",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        fetchEventDetails(id_history.toInt())
                                    } else {
                                        Toast.makeText(
                                            this@DetailHistoryPage,
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
                                        this@DetailHistoryPage,
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
                                findViewById<TextView>(R.id.eventName).text = payment.eventName
                                findViewById<TextView>(R.id.typeTicket).text = payment.typeTicket
                                findViewById<TextView>(R.id.amount).text = payment.amount.toString()
                                findViewById<TextView>(R.id.price).text = payment.price.toString()
                                findViewById<TextView>(R.id.totalPayment).text =
                                    payment.total.toString()
                                findViewById<TextView>(R.id.datetime).text = payment.datetime
                                val qrCode = findViewById<ImageView>(R.id.qrCode)
                                val containerUniqueCode =
                                    findViewById<LinearLayout>(R.id.containerUniqueCode)
                                val used = findViewById<TextView>(R.id.used)
                                val usedBackground = used.background as GradientDrawable
                                val status = findViewById<TextView>(R.id.status)
                                val statusBackground = status.background as GradientDrawable



                                when (payment.paid) {
                                    0 -> {
                                        statusBackground.setColor(this@DetailHistoryPage.getColor(R.color.pending))
                                        status.setTextColor(
                                            ContextCompat.getColor(
                                                this@DetailHistoryPage,
                                                R.color.white
                                            )
                                        )
                                        status.text = "Belum Bayar"
                                        cancelOrderButton.visibility = View.VISIBLE
                                        informationPayment.visibility = View.VISIBLE
                                    }

                                    1 -> {
                                        statusBackground.setColor(this@DetailHistoryPage.getColor(R.color.cbu))
                                        status.text = "Dibatalkan User"
                                        cancelOrderButton.visibility = View.GONE
                                        informationPayment.visibility = View.GONE
                                    }

                                    2 -> {
                                        statusBackground.setColor(this@DetailHistoryPage.getColor(R.color.annomaly))
                                        status.text = "Transaksi Anomali"
                                        cancelOrderButton.visibility = View.GONE
                                        informationPayment.visibility = View.GONE
                                    }

                                    3 -> {
                                        statusBackground.setColor(this@DetailHistoryPage.getColor(R.color.paid))
                                        status.text = "Telah Dibayar"
                                        cancelOrderButton.visibility = View.GONE
                                        informationPayment.visibility = View.GONE
                                    }

                                    4 -> {
                                        statusBackground.setColor(this@DetailHistoryPage.getColor(R.color.confirm))
                                        status.text = "Terkonfirmasi"
                                        when (payment.uniqueCode) {
                                            null -> containerUniqueCode.visibility = View.GONE
                                            else -> {
                                                containerUniqueCode.visibility = View.VISIBLE
                                                val bitmap = GenerateQrCode(
                                                    payment.uniqueCode.toString(),
                                                    700,
                                                    700
                                                )
                                                qrCode.setImageBitmap(bitmap)
                                            }
                                        }
                                        cancelOrderButton.visibility = View.GONE
                                        informationPayment.visibility = View.GONE
                                        used.visibility = View.VISIBLE
                                        when (payment.used) {
                                            0 -> {
                                                used.text = "Belum Digunakan"
                                                usedBackground.setColor(
                                                    this@DetailHistoryPage.getColor(
                                                        R.color.paid
                                                    )
                                                )
                                            }

                                            else -> {
                                                used.text = "Sudah Digunakan"
                                                usedBackground.setColor(
                                                    this@DetailHistoryPage.getColor(
                                                        R.color.pending
                                                    )
                                                )
                                            }
                                        }
                                    }

                                    5 -> {
                                        statusBackground.setColor(this@DetailHistoryPage.getColor(R.color.timeout))
                                        status.text = "Kadaluarsa"
                                        cancelOrderButton.visibility = View.GONE
                                        informationPayment.visibility = View.GONE
                                    }

                                    else -> statusBackground.setColor(
                                        this@DetailHistoryPage.getColor(
                                            R.color.black
                                        )
                                    ) // Default
                                }
                            }
                        } else if (response.code() == 401) {
                            Middleware.handleSessionExpired(
                                this@DetailHistoryPage,
                                this@DetailHistoryPage
                            )
                        } else {
                            Toast.makeText(
                                this@DetailHistoryPage,
                                "Gagal mengambil data",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<HistoryResponse>, t: Throwable) {
                        Toast.makeText(
                            this@DetailHistoryPage,
                            "Layanan Tidak Tersedia",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PAYMENT_REQUEST_CODE && resultCode == RESULT_OK) {
            fetchEventDetails(id_history.toInt())
        }
    }
}
