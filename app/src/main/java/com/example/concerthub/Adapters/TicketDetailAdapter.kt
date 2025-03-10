package com.example.concerthub.Adapters

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.concerthub.ApiService.ApiService
import com.example.concerthub.ApiService.TokenManager
import com.example.concerthub.Models.Ticket
import com.example.concerthub.PaymentPage
import com.example.concerthub.R
import com.example.concerthub.Utils.DateTimeFormatter
import com.example.concerthub.Utils.Middleware
import com.example.concerthub.Utils.PriceFormatter
import com.example.concerthub.api.RetrofitInstance
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class TicketDetailAdapter(private val tickets: List<Ticket>) :
    RecyclerView.Adapter<TicketDetailAdapter.ViewHolder>() {
    private var quantity: Int = 0

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ticketTypeTextView: TextView = itemView.findViewById(R.id.ticketType)
        private val dateStartTextView: TextView = itemView.findViewById(R.id.dateStart)
        private val dateEndTextView: TextView = itemView.findViewById(R.id.dateEnd)
        private val priceTextView: TextView = itemView.findViewById(R.id.price)
        private val stockTextView: TextView = itemView.findViewById(R.id.stock)
        private val orderTicket: Button = itemView.findViewById(R.id.orderTicket)

        fun bind(ticket: Ticket) {
            // Handle klik pada tombol orderTicket
            orderTicket.setOnClickListener {
                // Tampilkan dialog untuk memasukkan jumlah pembelian
                showQuantityDialog(itemView.context, ticket.idTicket, ticket.ticketType)
            }

            ticketTypeTextView.text = ticket.ticketType
            dateStartTextView.text = DateTimeFormatter.formatDate(ticket.dateStart)
            dateEndTextView.text = DateTimeFormatter.formatDate(ticket.dateEnd)
            priceTextView.text = "Rp. " + PriceFormatter.format(ticket.price.toString())
            val stock = ticket.amount - ticket.sold
            stockTextView.text = stock.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ticket_detail, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ticket = tickets[position]
        holder.bind(ticket)
    }

    override fun getItemCount(): Int {
        return tickets.size
    }

    private fun showQuantityDialog(context: Context, idTicket: Int, tipe: String) {
        val builder = AlertDialog.Builder(context)

        // Inflate custom layout
        val customLayout =
            LayoutInflater.from(context).inflate(R.layout.custom_quantity_dialog, null)
        val inputLayout = customLayout.findViewById<TextInputLayout>(R.id.quantityInputLayout)
        val input = customLayout.findViewById<TextInputEditText>(R.id.quantity)

        // Set custom title with outfit_regular font
        val titleTextView = TextView(context).apply {
            text = "Pembelian tiket $tipe"
            textSize = 20f
            typeface = ResourcesCompat.getFont(context, R.font.outfit_regular)
            setTextColor(ContextCompat.getColor(context, R.color.black))
            gravity = Gravity.CENTER
            setPadding(0, 30, 0, 30)
        }
        builder.setCustomTitle(titleTextView)

        builder.setView(customLayout)

        // Set rounded background drawable for the dialog
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(R.drawable.rounded_dialog_background)

        builder.setPositiveButton("Beli") { dialog, which ->
            val inputValue = input.text.toString()
            if (inputValue.isNotEmpty()) {
                val quantity = inputValue.toInt()

                fetchRecommendedEvents(context, idTicket, quantity)
            } else {
                Toast.makeText(context, "Masukkan jumlah pembelian.", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Batal") { dialog, which -> dialog.cancel() }
        builder.show()
    }

    private fun fetchRecommendedEvents(context: Context, idTicket: Int, amount: Int) {
        val token = TokenManager.getToken(context)
        val requestBody = mapOf(
            "id_ticket" to idTicket.toString(),
            "amount" to amount.toString(),
        )
        if (token != null) {
            RetrofitInstance.api.makeOrder(token, requestBody)
                ?.enqueue(object : Callback<ApiService.orderResponse> {
                    override fun onResponse(
                        call: Call<ApiService.orderResponse>,
                        response: Response<ApiService.orderResponse>
                    ) {
                        if (response.isSuccessful) {
                            val id_history = response.body()?.id_histories
                            val intent = Intent(context, PaymentPage::class.java).apply {
                                putExtra("id_history", id_history.toString())
                            }
                            context.startActivity(intent)
                            Toast.makeText(context, "Anda telah memesan $quantity tiket.", Toast.LENGTH_SHORT)
                                .show()

                        } else if (response.code() == 401) {
                            Middleware.handleSessionExpired(context, null)
                        } else {
                            Toast.makeText(
                                context,
                                "Periksa pembelian sebelumnya!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<ApiService.orderResponse>, t: Throwable) {
                        Toast.makeText(context, "Layanan tidak tersedia", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }


}
