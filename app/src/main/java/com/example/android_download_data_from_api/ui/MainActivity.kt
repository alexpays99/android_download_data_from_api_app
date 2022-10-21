package com.example.android_download_data_from_api.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android_download_data_from_api.R
import com.example.android_download_data_from_api.common.adapters.UserListAdapter
import com.example.android_download_data_from_api.databinding.ActivityMainBinding
import com.example.android_download_data_from_api.models.Photo
import com.example.android_download_data_from_api.models.User

class MainActivity : AppCompatActivity() {
    private var userList = mutableListOf<Photo>()
    private lateinit var adapter: UserListAdapter
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAdapter()
        setupUserList()
    }

    private fun setupAdapter() {
        adapter = UserListAdapter(userList)
        binding.recycleView.layoutManager = LinearLayoutManager(this@MainActivity)
        binding.recycleView.adapter = adapter
        binding.recycleView.setItemViewCacheSize(userList.size)
    }

    private fun setupUserList() {
        for (i in 1..30) {
            userList.add(Photo("Name + $i"))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.search_menu, menu)

        val searchItem = menu.findItem(R.id.actionSearch)
        val searchView = searchItem.actionView as? SearchView
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
//                filter(newText)
                return false
            }
        })
        return true
    }
}