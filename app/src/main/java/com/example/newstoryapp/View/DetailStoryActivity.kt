package com.example.newstoryapp.View

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.newstoryapp.R
import com.example.newstoryapp.databinding.ActivityDetailStoryBinding
import com.example.newstoryapp.response.ListStoryItem


class DetailStoryActivity : AppCompatActivity() {


    private lateinit var detailBinding: ActivityDetailStoryBinding
    private lateinit var detailStories: ListStoryItem


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        detailBinding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(detailBinding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setLogo(R.drawable.ic_back)
        supportActionBar?.setDisplayUseLogoEnabled(true)
        detailStories = intent.getParcelableExtra<ListStoryItem>(StoryAdapter.DETAIL_STORIES) as ListStoryItem
        supportActionBar?.title = detailStories.name
        setData()
    }


    private fun setData() {
        detailBinding.apply {
            Glide.with(this@DetailStoryActivity)
                .load(detailStories.photoUrl)
                .into(detailBinding.profileImageView)
            nameTextView.text = detailStories.name
            descTextView.text = detailStories.description
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        finish()
        return super.onSupportNavigateUp()
    }
}
