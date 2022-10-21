package com.example.android_download_data_from_api.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
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
}