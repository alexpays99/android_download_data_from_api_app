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
import android.widget.ProgressBar
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android_download_data_from_api.R
import com.example.android_download_data_from_api.asyncTasks.FetchDataTaskResolver
import com.example.android_download_data_from_api.common.adapters.Common
import com.example.android_download_data_from_api.common.adapters.UserListAdapter
import com.example.android_download_data_from_api.databinding.ActivityMainBinding
import com.example.android_download_data_from_api.models.Photo
import com.example.android_download_data_from_api.models.User
import com.example.android_download_data_from_api.services.DownloadService
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    private var userList = mutableListOf<Photo>()
    private lateinit var adapter: UserListAdapter
    private lateinit var binding: ActivityMainBinding
    private var downloadService: DownloadService? = null
    private lateinit var thread: Thread
    private lateinit var task: Runnable
    private var isRunning: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fetchDataTaskResolver = object : FetchDataTaskResolver<String, Boolean, MutableList<Photo>>() {
            override fun doInBackground(vararg many: String): MutableList<Photo> {
                Log.d("***doInBackground: ", "method is called,  ${Thread.currentThread().name}")

                val retrofitService = Common.retrofitService
                Log.d("***doInBackground: ", "retrofitService init")

                val response = retrofitService.getUsers("search?query=nature&per_page=10").enqueue(object : Callback<MutableList<Photo>> {
                    override fun onResponse(call: Call<MutableList<Photo>>, response: Response<MutableList<Photo>>) {
                        if (response.isSuccessful) {
                            userList = response.body()!!
                            Log.e("RETROFIT RESUL: ", userList.toString())

                            val handler = Handler(Looper.getMainLooper())
                            handler.post {
                                setupAdapter()
                            }
                        }
                        Log.d("****onResponse()", userList.toString())
                    }

                    override fun onFailure(call: Call<MutableList<Photo>>, t: Throwable) {
                        Log.e("RETROFIT RESULT ERROR: ", "ERROR RESPONCE")
                    }
                })
                Log.d("***doInBackground: ", "response: ,  $response")

//                val retrofitService = Common.retrofitService
//                Log.d("***doInBackground: ", "retrofitService init")
//                val response = retrofitService.getUsers("5").execute()
//                Log.d("***doInBackground: ", "response: ,  $response")
//
//                try {
//                    userList.addAll(response.body()!!)
//                    Log.e("RETROFIT RESUL: ", userList.toString())
//
//                    val handler = Handler(Looper.getMainLooper())
//                    handler.post {
//                        setupAdapter()
//                    }
//                } catch (e: Error) {
//                    Log.e("RETROFIT RESULT ERROR: ", e.toString())
//                }
                return userList
            }

            override fun onPostExecute(data: MutableList<Photo>) {
                setupAdapter()
                Log.d("**** onPost Execute = ", ", Current Thread: ${Thread.currentThread().name}, res=" + data)
            }

            override fun onPreExecute() {
                setupAdapter()
                Log.d("**** onPre Execute = ", "onPre Execute, Current Thread: ${Thread.currentThread().name}")
            }

            @SuppressLint("LongLogTag")
            override fun onProgressUpdate(progress: Boolean) {
                progress_two.visibility(progress)
                Log.d("**** onProgressUpdate = ", ", Current Thread: ${Thread.currentThread().name}, " + progress)
            }
        }
        fetchDataTaskResolver.execute()
    }

    private fun setupAdapter() {
        adapter = UserListAdapter(userList)
        binding.recycleView.layoutManager = LinearLayoutManager(this@MainActivity)
        binding.recycleView.adapter = adapter
        binding.recycleView.setItemViewCacheSize(userList.size)
    }

    private fun setupUserList() {
        for (i in 1..30) {
            Thread.sleep(1000)
            userList.add(Photo("Name + $i"))
        }
    }

    //visibility of progress bar
    fun ProgressBar.visibility(progress: Boolean) {
        if (progress) {
            progress_two.visibility = View.VISIBLE
        } else {
            progress_two.visibility = View.GONE
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
        stopService(Intent(this@MainActivity, DownloadService::class.java))
        super.onDestroy()
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            downloadService = (service as DownloadService.CustomBinder).getService()
            if (downloadService != null) {
                setupAdapter()
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



