package com.example.newstoryapp.View

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newstoryapp.R
import com.example.newstoryapp.View.Map.MapsActivity
import com.example.newstoryapp.data.LoadingStateAdapter
import com.example.newstoryapp.databinding.ActivityListStoryBinding


class ListStoryActivity: AppCompatActivity() {
    private lateinit var binding: ActivityListStoryBinding

    private val listViewmodel: ListStoryViewModel by viewModels {
        ViewModelFactory(this)
    }


    private lateinit var userPreferences: SharedPreferences
    private lateinit var name: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.rvList.layoutManager = LinearLayoutManager(this)
        userPreferences = getSharedPreferences(EnterActivity.PREFS_NAME, Context.MODE_PRIVATE)
        name = userPreferences.getString(EnterActivity.NAME, "").toString()
        binding.salam.text = "Hello, $name"
        binding.photoAdd.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            startActivity(intent)
        }

        // Memanggil fungsi untuk mendapatkan data cerita
        getData()
    }

    private fun getData() {
        val token = userPreferences.getString(EnterActivity.TOKEN, "").toString()
        val adapter = StoryAdapter()
        binding.rvList.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )
        listViewmodel.getStory(token).observe(this, {
            adapter.submitData(lifecycle, it)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.bottom_app_menu, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logoutmenu -> {
                userPreferences.edit().apply {
                    clear()
                    apply()
                }
                val loginIntent = Intent(this, EnterActivity::class.java)
                startActivity(loginIntent)
                finish()
                return true
            }

            R.id.map -> {

                val mapsIntent = Intent(this, MapsActivity::class.java)
                startActivity(mapsIntent)
                return true
            }
            else -> false
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        AlertDialog.Builder(this).apply {
            setMessage(resources.getString(R.string.dialEx))

            setPositiveButton(resources.getString(R.string.respY)) { dialog, which ->
                finishAffinity()
            }
            setNegativeButton(resources.getString(R.string.respT)) { dialog, which ->
                dialog.cancel()
            }
            setCancelable(false)
        }.create().show()
    }
}