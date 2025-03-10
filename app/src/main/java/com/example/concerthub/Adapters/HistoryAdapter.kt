package com.example.concerthub.Adapters

import android.content.Context
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.concerthub.MainPageActivity.HistoryPageActivity.DetailHistoryPage
import com.example.concerthub.Models.RecycleHistoryModel
import com.example.concerthub.R
import com.example.concerthub.Utils.DateTimeFormatter

class HistoryAdapter(
    private val context: Context?,
    private val historyList: List<RecycleHistoryModel>
) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val history = historyList[position]

        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailHistoryPage::class.java).apply {
                putExtra("id_history", history.idHistory.toString())
            }
            holder.itemView.context.startActivity(intent)
        }

        holder.eventNameTextView.text = history.eventName
        holder.typeTicketTextView.text = history.typeTicket
        holder.orderedAtTextView.text = DateTimeFormatter.formatDateWithTime(history.datetime)
        val statusBackground = holder.status.background as GradientDrawable
        when (history.paid) {
            0 -> {
                statusBackground.setColor(holder.itemView.context.getColor(R.color.pending))
                holder.status.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.white
                    )
                )
                holder.status.text = "Belum Bayar"
            }

            1 -> {
                statusBackground.setColor(holder.itemView.context.getColor(R.color.cbu))
                holder.status.text = "Dibatalkan User"
            }

            2 -> {
                statusBackground.setColor(holder.itemView.context.getColor(R.color.annomaly))
                holder.status.text = "Transaksi Anomali"
            }

            3 -> {
                statusBackground.setColor(holder.itemView.context.getColor(R.color.paid))
                holder.status.text = "Telah Dibayar"
            }

            4 -> {
                statusBackground.setColor(holder.itemView.context.getColor(R.color.confirm))
                holder.status.text = "Terkonfirmasi"
            }

            5 -> {
                statusBackground.setColor(holder.itemView.context.getColor(R.color.timeout))
                holder.status.text = "Kadaluarsa"
            }

            else -> statusBackground.setColor(holder.itemView.context.getColor(R.color.black)) // Default
        }
    }

    override fun getItemCount(): Int {
        return historyList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val eventNameTextView: TextView = itemView.findViewById(R.id.eventName)
        val typeTicketTextView: TextView = itemView.findViewById(R.id.type_ticket)
        val orderedAtTextView: TextView = itemView.findViewById(R.id.ordered_at)
        val status: TextView = itemView.findViewById(R.id.status)

    }
}
