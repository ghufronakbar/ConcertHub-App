package com.example.concerthub.MainPageActivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.concerthub.Adapters.EventAdapter
import com.example.concerthub.ApiService.ApiService
import com.example.concerthub.ApiService.TokenManager
import com.example.concerthub.MainPage
import com.example.concerthub.Models.ItemEventModel
import com.example.concerthub.R
import com.example.concerthub.Utils.Middleware
import com.example.concerthub.api.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private lateinit var recommendedEventsRecyclerView: RecyclerView
    private lateinit var newEventsRecyclerView: RecyclerView
    private lateinit var eventAdapter: EventAdapter
    private lateinit var newEventAdapter: EventAdapter
    private lateinit var user_name: TextView
    private lateinit var seeAll: TextView
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var contentLayout: LinearLayout
    private var dataFetchedCount = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        loadingIndicator = view.findViewById(R.id.loadingIndicator)
        contentLayout = view.findViewById(R.id.contentLayout)
        user_name = view.findViewById(R.id.user_name)
        seeAll = view.findViewById(R.id.seeAll)
        seeAll.setOnClickListener {
            val mainPageActivity = activity as MainPage
            mainPageActivity.redirectToFragment(MainPage.FRAGMENT_EVENT)
        }

        recommendedEventsRecyclerView = view.findViewById(R.id.recommendedRecyclerView)
        recommendedEventsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        newEventsRecyclerView = view.findViewById(R.id.newRecyclerView)
        newEventsRecyclerView.layoutManager =
            GridLayoutManager(requireContext(), 2, RecyclerView.VERTICAL, false)

        fetchProfile()
        fetchRecommendedEvents()
        fetchNewEvents()

        return view
    }

    private fun fetchProfile() {
        val token = context?.let { TokenManager.getToken(it) }
        if (token != null) {
            RetrofitInstance.api.profile(token).enqueue(object :
                Callback<ApiService.profileResponse> {
                override fun onResponse(
                    call: Call<ApiService.profileResponse>,
                    response: Response<ApiService.profileResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let {

                            val profile = it.values[0]
                            user_name.setText(profile.fullname)
                            checkAllDataFetched()
                        }
                    } else {
                        Middleware.handleSessionExpired(requireContext(), requireActivity())
                    }
                }

                override fun onFailure(call: Call<ApiService.profileResponse>, t: Throwable) {
                    Toast.makeText(context, "Layanan Tidak Tersedia", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun fetchNewEvents() {
        val token = context?.let { TokenManager.getToken(it) }

        if (token != null) {
            RetrofitInstance.api.fetchEvent(token, limit = 8)
                .enqueue(object : Callback<List<ItemEventModel>> {
                    override fun onResponse(
                        call: Call<List<ItemEventModel>>,
                        response: Response<List<ItemEventModel>>
                    ) {
                        if (response.isSuccessful) {
                            val recommendedEvents = response.body()
                            newEventAdapter =
                                recommendedEvents?.let { EventAdapter(it) }!!
                            newEventsRecyclerView.adapter = newEventAdapter
                            checkAllDataFetched()
                        } else {
                            Middleware.handleSessionExpired(requireContext(), requireActivity())
                        }
                    }

                    override fun onFailure(call: Call<List<ItemEventModel>>, t: Throwable) {
                        Toast.makeText(context, "Layanan tidak tersedia", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    private fun fetchRecommendedEvents() {
        val token = context?.let { TokenManager.getToken(it) }

        if (token != null) {
            RetrofitInstance.api.fetchRecomended(token, limit = 3)
                .enqueue(object : Callback<List<ItemEventModel>> {
                    override fun onResponse(
                        call: Call<List<ItemEventModel>>,
                        response: Response<List<ItemEventModel>>
                    ) {
                        if (response.isSuccessful) {

                            val recommendedEvents = response.body()
                            eventAdapter =
                                recommendedEvents?.let { EventAdapter(it) }!!
                            recommendedEventsRecyclerView.adapter = eventAdapter
                            checkAllDataFetched()

                        } else {
                            Middleware.handleSessionExpired(requireContext(), requireActivity())
                        }
                    }

                    override fun onFailure(call: Call<List<ItemEventModel>>, t: Throwable) {
                        Toast.makeText(context, "Layanan tidak tersedia", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    private fun checkAllDataFetched() {
        dataFetchedCount++
        if (dataFetchedCount >= 3) { // Adjust this number based on how many API calls you have

            loadingIndicator.visibility = View.GONE
            contentLayout.visibility = View.VISIBLE
        }
    }
}
