package com.example.concerthub.DetailEventActivity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.concerthub.BaseUrl
import com.example.concerthub.R
import com.example.concerthub.MainPage // <-- sesuaikan package MainPage-mu
import com.midtrans.sdk.uikit.SdkUIFlowBuilder
import com.midtrans.sdk.uikit.api.model.TransactionResult
import com.midtrans.sdk.uikit.external.UiKitApi
import com.midtrans.sdk.uikit.internal.util.UiKitConstants

class MidtransPayment : AppCompatActivity() {
    private lateinit var snapToken: String
    private lateinit var baseUrl: String

    // 1) Launcher disimpan sebagai properti supaya jelas lifecycle-nya
    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d("MidtransPayment", "ResultCode=${result.resultCode}, data=${result.data}")

        val data = result.data
        if (data != null) {
            val trx: TransactionResult? =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    data.getParcelableExtra(
                        UiKitConstants.KEY_TRANSACTION_RESULT,
                        TransactionResult::class.java
                    )
                } else {
                    @Suppress("DEPRECATION")
                    data.getParcelableExtra(UiKitConstants.KEY_TRANSACTION_RESULT)
                }

            trx?.let {
                Toast.makeText(
                    this,
                    "Transaksi: ${it.transactionId} (${it.status})",
                    Toast.LENGTH_LONG
                ).show()
                Log.d("MidtransPayment", "Status: ${it.status}")
            }

            data.getStringExtra(UiKitConstants.KEY_ERROR_MESSAGE)?.let { err ->
                Log.e("MidtransPayment", "Midtrans Error: $err")
            }
        } else {
            Toast.makeText(this, "Payment selesai / dibatalkan.", Toast.LENGTH_SHORT).show()
        }

        // 2) Apapun hasilnya, replace ke MainPage
        openMainPageAndClearStack()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_midtrans_payment)

        baseUrl = BaseUrl.URL

        // --- Midtrans init (pilih salah satu mekanisme callback; di sini kita pakai ActivityResult) ---
        SdkUIFlowBuilder.init()
            .setContext(this)
            .setClientKey("SB-Mid-client-kbFXD4vw7o247KMC") // jangan pernah pakai Server Key di app
             .setMerchantBaseUrl(baseUrl)   // kalau perlu
            // .setTransactionFinishedCallback { /* Tidak dipakai, biar tidak double-callback */ }
            .enableLog(true)
            .buildSDK()

        snapToken = intent.getStringExtra("snapToken") ?: run {
            Toast.makeText(this, "Snap token tidak ditemukan", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // 3) Mulai UI Flow Midtrans
        UiKitApi.getDefaultInstance().startPaymentUiFlow(
            this,
            launcher,
            snapToken
        )

        // (Opsional) Jika user menekan back di halaman ini sebelum/ setelah flow:
//        onBackPressedDispatcher.addCallback(this) {
//            openMainPageAndClearStack()
//        }
    }

    private fun openMainPageAndClearStack() {
        // Opsi A: jadikan MainPage sebagai root task yang baru (bersih total)
        val intent = Intent(this, MainPage::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            // kalau mau kirim info:
            // putExtra("fromPayment", true)
            // putExtra("paymentStatus", "success/pending/failed")
        }
        startActivity(intent)
        // pastikan activity ini selesai
        finish()
    }
}
