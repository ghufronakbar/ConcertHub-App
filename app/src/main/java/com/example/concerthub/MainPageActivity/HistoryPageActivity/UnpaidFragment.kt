package com.example.concerthub.MainPageActivity.HistoryPageActivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.concerthub.Adapters.HistoryAdapter
import com.example.concerthub.ApiService.TokenManager
import com.example.concerthub.Models.RecycleHistoryModel
import com.example.concerthub.R
import com.example.concerthub.api.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class UnpaidFragment : Fragment() {
    private lateinit var recyclerViewUnpaidOrders: RecyclerView
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var noItem: TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_unpaid, container, false)
        noItem = view.findViewById(R.id.noItem)
        // Initialize RecyclerView and Adapter
        recyclerViewUnpaidOrders = view.findViewById(R.id.recyclerView)
        recyclerViewUnpaidOrders.layoutManager = LinearLayoutManager(context)

        // Initialize SwipeRefreshLayout
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {
            fetchUnpaid()
        }

        fetchUnpaid()

        return view
    }

    private fun fetchUnpaid() {
        swipeRefreshLayout.isRefreshing = true
        val token = context?.let { TokenManager.getToken(it) }
        if (token != null) {
            RetrofitInstance.api.getAllHistory(token, 0)
                .enqueue(object : Callback<ArrayList<RecycleHistoryModel>> {
                    override fun onResponse(
                        call: Call<ArrayList<RecycleHistoryModel>>,
                        response: Response<ArrayList<RecycleHistoryModel>>
                    ) {
                        swipeRefreshLayout.isRefreshing = false
                        if (response.isSuccessful) {
                            response.body()?.let {

                                if (it.size == 0) {
                                    noItem.visibility = View.VISIBLE
                                    recyclerViewUnpaidOrders.visibility = View.GONE
                                } else {
                                    recyclerViewUnpaidOrders.visibility = View.VISIBLE
                                    historyAdapter = HistoryAdapter(context, it)
                                    recyclerViewUnpaidOrders.adapter = historyAdapter
                                }
                            }
                        } else {
                            Toast.makeText(
                                context,
                                "gagal mengambil data",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(
                        call: Call<ArrayList<RecycleHistoryModel>>,
                        t: Throwable
                    ) {
                        swipeRefreshLayout.isRefreshing = false

                        Toast.makeText(context, "$t", Toast.LENGTH_SHORT).show()
                    }
                })
        } else {
            swipeRefreshLayout.isRefreshing = false
            Toast.makeText(context, "Sesi telah habis", Toast.LENGTH_SHORT).show()
        }
    }
}