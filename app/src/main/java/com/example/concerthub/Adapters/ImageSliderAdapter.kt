package com.example.concerthub.Adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.concerthub.DetailEventActivity.DetailImagePage
import com.example.concerthub.Models.ImageData
import com.example.concerthub.R

class ImageSliderAdapter(private var image: List<ImageData>) :
    RecyclerView.Adapter<ImageSliderAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image_slide, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val image = image[position]
        holder.bind(image)
    }

    override fun getItemCount(): Int {
        return image.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivSlider: ImageView = itemView.findViewById(R.id.ivSlider)


        fun bind(image: ImageData) {
            ivSlider.setOnClickListener {
                val intent = Intent(itemView.context, DetailImagePage::class.java)
                intent.putExtra("IMAGE_URL", image.imageUrl)
                itemView.context.startActivity(intent)
            }

            Glide.with(itemView)
                .load(image.imageUrl)
                .into(ivSlider)

        }
    }
}