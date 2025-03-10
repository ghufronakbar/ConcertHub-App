package com.example.concerthub.MainPageActivity


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.concerthub.Adapters.EventAdapter
import com.example.concerthub.ApiService.TokenManager
import com.example.concerthub.Models.ItemEventModel
import com.example.concerthub.R
import com.example.concerthub.Utils.Middleware
import com.example.concerthub.api.RetrofitInstance
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EventFragment : Fragment() {
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var contentLayout: LinearLayout
    private lateinit var allEventsRecyclerView: RecyclerView
    private lateinit var eventAdapter: EventAdapter
    private lateinit var searchEditText: TextInputEditText
    private lateinit var noItemTextView: TextView
    private var fullEventList: List<ItemEventModel> = listOf()
    private var filteredEventList: List<ItemEventModel> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_event, container, false)
        loadingIndicator = view.findViewById(R.id.loadingIndicator)
        contentLayout = view.findViewById(R.id.contentLayout)
        allEventsRecyclerView = view.findViewById(R.id.allRecyclerView)
        searchEditText = view.findViewById(R.id.search)
        noItemTextView = view.findViewById(R.id.noItem)
        allEventsRecyclerView.layoutManager =
            GridLayoutManager(requireContext(), 2, RecyclerView.VERTICAL, false)

        fetchEvents()

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val searchText = s.toString().trim()
                filterEvents(searchText)
            }
        })

        return view
    }

    private fun filterEvents(searchText: String) {
        filteredEventList = if (searchText.isEmpty()) {
            fullEventList
        } else {
            fullEventList.filter { event ->
                event.eventName.contains(searchText, true) || event.organizationName.contains(searchText, true)
            }
        }

        if (filteredEventList.isNotEmpty()) {
            eventAdapter.updateEvents(filteredEventList)
            allEventsRecyclerView.visibility = View.VISIBLE
            noItemTextView.visibility = View.GONE
        } else {
            allEventsRecyclerView.visibility = View.GONE
            noItemTextView.visibility = View.VISIBLE
        }
    }

    private fun fetchEvents() {
        val token = context?.let { TokenManager.getToken(it) }

        if (token != null) {
            RetrofitInstance.api.fetchEvent(token)
                .enqueue(object : Callback<List<ItemEventModel>> {
                    override fun onResponse(
                        call: Call<List<ItemEventModel>>,
                        response: Response<List<ItemEventModel>>
                    ) {
                        if (response.isSuccessful) {
                            response.body()?.let { events ->
                                fullEventList = events
                                filteredEventList = fullEventList
                                eventAdapter = EventAdapter(filteredEventList)
                                allEventsRecyclerView.adapter = eventAdapter
                                loadingIndicator.visibility = View.GONE
                                contentLayout.visibility = View.VISIBLE
                            }
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
}

