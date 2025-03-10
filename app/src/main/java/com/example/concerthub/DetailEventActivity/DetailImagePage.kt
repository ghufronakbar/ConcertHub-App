package com.example.concerthub.DetailEventActivity

import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.concerthub.R
import com.github.chrisbanes.photoview.PhotoView

class DetailImagePage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail_image_page)

        val imageUrl = intent.getStringExtra("IMAGE_URL")
        val imageView: PhotoView = findViewById(R.id.imageViewDetail)
        val btn_back: ImageButton = findViewById(R.id.btn_back)

        btn_back.setOnClickListener {
            onBackPressed()
        }

        Glide.with(this)
            .load(imageUrl)
            .into(imageView)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
