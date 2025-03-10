package com.example.concerthub.DetailEventActivity

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.concerthub.Adapters.ImageSliderAdapter
import com.example.concerthub.Adapters.TicketDetailAdapter
import com.example.concerthub.ApiService.TokenManager
import com.example.concerthub.Models.EventDetailResponse
import com.example.concerthub.Models.ImageData
import com.example.concerthub.R
import com.example.concerthub.R.color.black
import com.example.concerthub.R.color.grey
import com.example.concerthub.Utils.DateTimeFormatter
import com.example.concerthub.Utils.Middleware
import com.example.concerthub.api.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailEventPage : AppCompatActivity() {
    private lateinit var ticketRecyclerView: RecyclerView
    private lateinit var TicketAdapter: TicketDetailAdapter
    private lateinit var imageAdapter: ImageSliderAdapter
    private lateinit var btn_back: ImageButton
    private lateinit var eventName: TextView
    private lateinit var status: TextView
    private lateinit var eventStart: TextView
    private lateinit var eventEnd: TextView
    private lateinit var location: TextView
    private lateinit var viewInMaps: TextView
    private lateinit var descriptionEvent: TextView
    private lateinit var organizationName: TextView
    private lateinit var dots: ArrayList<TextView>
    private lateinit var dotsIndicator: LinearLayout
    private lateinit var viewPager: ViewPager2
    private var imageList = ArrayList<ImageData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail_event_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val idEvent = intent.getIntExtra("idEvent", -1)

        btn_back = findViewById(R.id.btn_back)
        btn_back.setOnClickListener {
            onBackPressed()
        }
        viewPager = findViewById(R.id.viewPager)
        dotsIndicator = findViewById(R.id.dotsIndicator)
        eventName = findViewById(R.id.eventName)
        organizationName = findViewById(R.id.organizationName)
        status = findViewById(R.id.status)
        eventStart = findViewById(R.id.eventStart)
        eventEnd = findViewById(R.id.eventEnd)
        location = findViewById(R.id.location)
        viewInMaps = findViewById(R.id.viewInMaps)
        descriptionEvent = findViewById(R.id.descriptionEvent)




        ticketRecyclerView = findViewById(R.id.ticketRecyclerView)
        ticketRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)


        fetchEventDetails(idEvent)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                selectedDots(position)
                super.onPageSelected(position)
            }
        })

    }

    private fun fetchEventDetails(idEvent: Int) {
        val token = TokenManager.getToken(this)
        if (token != null) {
            RetrofitInstance.api.getEventDetails(token, idEvent).enqueue(object :
                Callback<EventDetailResponse> {
                override fun onResponse(
                    call: Call<EventDetailResponse>,
                    response: Response<EventDetailResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { event ->
                            if (event.eventImage != null) {
                                imageList.add(
                                    ImageData(
                                        event.eventImage
                                    )
                                )
                            }
                            if (event.sitePlanImage != null) {
                                imageList.add(
                                    ImageData(
                                        event.sitePlanImage
                                    )
                                )
                            }
                            eventName.setText(event.eventName)
                            organizationName.setText(event.organizationName)
                            status.setText(event.status)
                            eventStart.setText(DateTimeFormatter.formatDateTime(event.eventStart))
                            eventEnd.setText(DateTimeFormatter.formatDateTime(event.eventEnd))
                            location.setText(event.location)
                            descriptionEvent.setText(event.description)

                            TicketAdapter = TicketDetailAdapter(event.tickets)
                            ticketRecyclerView.adapter = TicketAdapter

                            viewInMaps.setOnClickListener {
                                val searchLocation =
                                    event.location
                                val uri =
                                    "geo:0,0?q=$searchLocation"

                                val intent = Intent(Intent.ACTION_VIEW)
                                intent.data = Uri.parse(uri)

                                startActivity(intent)

                            }







                            Log.d("ErrorCuy", "onResponse: ${event.urlGoogleMap}")
                            imageAdapter = ImageSliderAdapter(imageList)
                            viewPager.adapter = imageAdapter
                            dots = ArrayList()
                            setIndicator()
                            // Update other UI elements as needed
                        }
                    } else {
                        Middleware.handleSessionExpired(this@DetailEventPage, this@DetailEventPage)
                    }
                }

                override fun onFailure(call: Call<EventDetailResponse>, t: Throwable) {
                    Log.d("ErrorCuy", "onResponse: ${t}")
                    Log.d("ErrorCuy", "onResponse: ${idEvent}")
                    Toast.makeText(this@DetailEventPage, "Error: ${t.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        }
    }

    private fun selectedDots(position: Int) {
        for (i in 0 until imageList.size) {
            if (i == position) {
                dots[i].setTextColor(getColor(black))
            } else {
                dots[i].setTextColor(getColor(grey))

            }
        }
    }

    private fun setIndicator() {
        for (i in 0 until imageList.size) {
            dots.add(TextView(this))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                dots[i].text = Html.fromHtml("&#9679", Html.FROM_HTML_MODE_LEGACY).toString()
            } else {
                dots[i].text = Html.fromHtml("&#9679")
            }
            dots[i].textSize = 18f
            dotsIndicator.addView(dots[i])
        }
    }
}