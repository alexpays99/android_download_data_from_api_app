package com.example.android_download_data_from_api.ui

import android.annotation.SuppressLint
import android.content.*
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.View.GONE
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android_download_data_from_api.R
import com.example.android_download_data_from_api.general.Common
import com.example.android_download_data_from_api.general.ItemStatus
import com.example.android_download_data_from_api.adapters.UserListAdapter
import com.example.android_download_data_from_api.databinding.ActivityMainBinding
import com.example.android_download_data_from_api.general.Constants
import com.example.android_download_data_from_api.interfaces.OnBindInterface
import com.example.android_download_data_from_api.interfaces.OnDownloadImageInterface
import com.example.android_download_data_from_api.models.Photo
import com.example.android_download_data_from_api.models.User
import com.example.android_download_data_from_api.services.DownloadService
import com.example.android_download_data_from_api.ui.fragments.UserImagesFragment
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.lang.Thread.currentThread
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private var userList = mutableListOf<Photo>()
    private lateinit var recyclerAdapter: UserListAdapter
    private lateinit var binding: ActivityMainBinding
    private val statusReceiver = StatusBroadcastReceiver()
    private var downloadService: DownloadService? = null
    private var executorService: ExecutorService = Executors.newFixedThreadPool(3)
    private lateinit var downloadImageTask: Runnable
    private var isRunning: Boolean = false

    inner class StatusBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent) {
            val position = intent.extras!!.getInt(Constants().position)
            val state = intent.extras!!.getString(Constants().state)

            if (intent.getAction().equals(Constants().UPDATE_STATE_ACTION)) {
                if (state != null) {
                    recyclerAdapter.updateItemState(position, ItemStatus.valueOf(state))
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intentFilter = IntentFilter(Constants().UPDATE_STATE_ACTION)
        registerReceiver(statusReceiver, intentFilter)

        setupRecyclerAdapter()
    }

    private fun setupRecyclerAdapter() {
        recyclerAdapter = UserListAdapter(userList)
        binding.recycleView.layoutManager = LinearLayoutManager(this@MainActivity)
        binding.recycleView.adapter = recyclerAdapter
        binding.recycleView.setItemViewCacheSize(userList.size)
        recyclerAdapter.bindCallback(object : OnBindInterface {
            override fun onBinding(photo: Photo) {
                val bundle = Bundle()
                val userImagesFragment = UserImagesFragment()
                bundle.putString("message", photo.photographer.toString())
                bundle.putString("photoID", photo.id.toString())
                userImagesFragment.arguments = bundle
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.recListConstraintLayout, userImagesFragment)
                    .addToBackStack(null)
                    .commit()
            }
        })
        recyclerAdapter.downloadImgCallback(object : OnDownloadImageInterface {
            override fun onDownloadImage(photo: Photo, position: Int) {
                // run service
                val downloadServiceIntent = Intent(this@MainActivity, DownloadService::class.java)
                ContextCompat.startForegroundService(this@MainActivity, intent)
                println("SERVICE HAS STARTED")
                bindService(downloadServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
                println("SERVICE HAS BINDED")

                downloadService?.setState(position, ItemStatus.IN_QUEUE)
                Log.d("IN_QUEUE:", "$position, Thread: ${currentThread().name}")

                downloadImageTask = Runnable {
                    downloadService?.setState(position, ItemStatus.IN_PROGRESS)
                    Log.d("IN_PROGRESS:", "$position, Thread: ${currentThread().name}")

                    Thread.sleep(5000)

                    val usrArr: ArrayList<String> = ArrayList()
                    usrArr.add(photo.src.original)
                    usrArr.add(photo.src.large2X)
                    usrArr.add(photo.src.large)
                    usrArr.add(photo.src.medium)
                    usrArr.add(photo.src.small)
                    usrArr.add(photo.src.portrait)
                    usrArr.add(photo.src.landscape)
                    usrArr.add(photo.src.tiny)

                    for ((index, i) in (0 until usrArr.size).withIndex()) {
                        val url = usrArr[index]
                        downloadService?.startDownloading(
                            photo.photographer.toString() + photo.id,
                            url
                        )
                        Log.d(
                            "DOWNLOADING IN THREAD:",
                            "startDownloading()," +
                                    "${photo.photographer}, " +
                                    "Thread: ${currentThread().name}" +
                                    "Thread: ${currentThread().state}"
                        )
                    }
                    downloadService?.setState(position, ItemStatus.DOWNLOADED)
                    Log.d("DOWNLOADED:", "$position, Thread: ${currentThread().name}")
                }
                executorService.submit(downloadImageTask)
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
                makeRequest(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                makeRequest(newText)
                return false
            }
        })
        return true
    }

    private fun makeRequest(query: String) {
        val retrofitService = Common.retrofitService
        Log.d("***doInBackground: ", "retrofitService init")
        retrofitService.getUsers(query).enqueue(object : Callback<User> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    Log.e("RESPONSE: ", "response.isSuccessful")
                    try {
                        val handler = Handler(Looper.getMainLooper())
                        handler.post {
                            val resp = response.body()
                            resp?.photos?.forEach { photo ->
                                val path =
                                    "/storage/emulated/0/Download/${photo.photographer}${photo.id}"
                                if (File(path).exists()) {
                                    photo.state = ItemStatus.DOWNLOADED
                                } else {
                                    photo.state = ItemStatus.DEFAULT
                                }
                            }
                        }
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
    }

    override fun onStart() {
        println("onStart method has been called")
        super.onStart()
        if (isRunning == true) {
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
            println("SERVICE HAS BINDED")
        }
    }

    override fun onStop() {
        println("onStart method has been called")
        super.onStop()
//        unbindService(serviceConnection)
//        println("SERVICE UNBINDED")
    }

    override fun onDestroy() {
        Log.e("MainActivity", "${currentThread().name}, ${currentThread().state}")
        Log.e("MainActivity", "${currentThread().name}, ${currentThread().state}")
        super.onDestroy()
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            downloadService = (service as DownloadService.CustomBinder).getService()
            if (downloadService != null) {
                isRunning = true
                println("onServiceConnected has been called")
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            downloadService = null
            isRunning = false
            println("downloadService = null")
        }
    }
}



