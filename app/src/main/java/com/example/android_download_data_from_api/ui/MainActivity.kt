package com.example.android_download_data_from_api.ui

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.View.GONE
import android.widget.GridView
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android_download_data_from_api.R
import com.example.android_download_data_from_api.common.adapters.Common
import com.example.android_download_data_from_api.common.adapters.GridAdapter
import com.example.android_download_data_from_api.common.adapters.OnBindInterface
import com.example.android_download_data_from_api.common.adapters.UserListAdapter
import com.example.android_download_data_from_api.databinding.ActivityMainBinding
import com.example.android_download_data_from_api.models.Photo
import com.example.android_download_data_from_api.models.User
import com.example.android_download_data_from_api.services.DownloadService
import com.example.android_download_data_from_api.ui.fragments.UserImagesFragment
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Thread.currentThread

class MainActivity : AppCompatActivity() {
    private var userList = mutableListOf<Photo>()
    private lateinit var recyclerAdapter: UserListAdapter
    private lateinit var gridAdapter: GridAdapter
    private lateinit var binding: ActivityMainBinding
    private var downloadService: DownloadService? = null
    private lateinit var thread: Thread
    private lateinit var task: Runnable
    private var isRunning: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerAdapter()
    }

    private fun setupRecyclerAdapter() {
        recyclerAdapter = UserListAdapter(userList)
        binding.recycleView.layoutManager = LinearLayoutManager(this@MainActivity)
        binding.recycleView.adapter = recyclerAdapter
        binding.recycleView.setItemViewCacheSize(userList.size)
        recyclerAdapter.bind(object : OnBindInterface {
            override fun onBinding(photo: Photo) {
                val bundle = Bundle()
                val userImagesFragment = UserImagesFragment()
                bundle.putString("message", photo.photographer.toString())
                userImagesFragment.arguments = bundle
                supportFragmentManager
                .beginTransaction()
                .replace(R.id.recListConstraintLayout, userImagesFragment)
                .addToBackStack(null)
                .commit()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.search_menu, menu)

        val searchItem = menu.findItem(R.id.actionSearch)
        val searchView = searchItem.actionView as? SearchView

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                progress_two.visibility = View.VISIBLE
                val retrofitService = Common.retrofitService
                Log.d("***doInBackground: ", "retrofitService init")
                retrofitService.getUsers(query)
                    .enqueue(object : Callback<User> {
                        @SuppressLint("NotifyDataSetChanged")
                        override fun onResponse(
                            call: Call<User>,
                            response: Response<User>
                        ) {
                            if (response.isSuccessful) {
                                Log.e("RESPONSE: ", "response.isSuccessful")
                                try {
                                    userList.addAll(response.body()!!.photos)
                                    Log.e("USERLIST RESUL:", userList.toString())
                                    recyclerAdapter.notifyDataSetChanged()
                                    runOnUiThread { progress_two.visibility = GONE }
                                    Log.e("RETROFIT RESUL: ", userList.toString())

                                } catch (e: Error) {
                                    Log.e("****onResponse", e.toString())
                                }
                                Log.d(
                                    "****onResponse()",
                                    currentThread().name + ", " + userList.toString()
                                )
                            }
                        }

                        override fun onFailure(call: Call<User>, t: Throwable) {
                            progress_two.visibility = GONE
                            Log.e("****onFailure", t.toString())
                        }
                    })
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
//                filter(newText)
                return false
            }
        })
        return true
    }

    override fun onStart() {
        println("onStart method has been called")
        super.onStart()
        val intent = Intent(this@MainActivity, DownloadService::class.java)
        if (isRunning == false) {
            //            ContextCompat.startForegroundService(this, intent)
            startService(intent)
            println("SERVICE HAS STARTED")
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
            println("SERVICE HAS BINDED")
//            setupRunnableApiCallTask()

        } else {
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
            println("SERVICE HAS BINDED")
        }
    }

    override fun onDestroy() {
        thread.interrupt()
        Log.e("MainActivity", "${Thread.currentThread().name}, ${Thread.currentThread().state}")
        Log.e("MainActivity", "${thread.name}, ${thread.state}")
        super.onDestroy()
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            downloadService = (service as DownloadService.CustomBinder).getService()
            if (downloadService != null) {
                setupRecyclerAdapter()
                isRunning = true
                println("serviceConnection has been called")
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            downloadService = null
            isRunning = false
            println("audioService = null")
        }
    }
}



