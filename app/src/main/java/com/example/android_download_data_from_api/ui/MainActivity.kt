package com.example.android_download_data_from_api.ui

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.*
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.View.GONE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android_download_data_from_api.R
import com.example.android_download_data_from_api.common.adapters.Common
import com.example.android_download_data_from_api.common.adapters.OnBindInterface
import com.example.android_download_data_from_api.common.adapters.OnDownladImageInterface
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
import java.io.File
import java.lang.Thread.currentThread
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


enum class ItemStatus {
    DEFAULT,
    IN_PROGRESS,
    DOWNLOADED,
    IN_QUEUE
}

class MainActivity : AppCompatActivity() {
    private var userList = mutableListOf<Photo>()
    private lateinit var recyclerAdapter: UserListAdapter
    private lateinit var binding: ActivityMainBinding
    private var downloadService: DownloadService? = null
    private var executorService: ExecutorService = Executors.newFixedThreadPool(3)
    private lateinit var downloadImageTask: Runnable
    private var isRunning: Boolean = false
    private var itemStatus: ItemStatus = ItemStatus.DEFAULT

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
        recyclerAdapter.bindCallback(object : OnBindInterface {
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

        recyclerAdapter.downloadImgCallback(object : OnDownladImageInterface {
            override fun onDownladImage(photo: Photo) {
//                when (itemStatus) {
//                    ItemStatus.DEFAULT ->
//                        ItemStatus.IN_PROGRESS ->
//                    ItemStatus.DOWNLOADED ->
//                    ItemStatus.IN_QUEUE ->
//                }


                downloadImageTask = Runnable {
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
                        startDownloading(photo.photographer.toString(), url)
//                        downloadService?.startDownloading(photo.photographer.toString(), url)
                        Log.d("DOWNLOADING IN THREAD: ",
                            "startDownloading()," +
                                "${photo.photographer}, "+
                                "Thread: ${currentThread().name}"+
                                "Thread: ${currentThread().state}")
                    }
                }
                executorService.execute(downloadImageTask)
                Log.d("DOWNLOADED IN THREAD: ", "Thread: ${currentThread().name}")
            }
        })
    }

    fun startDownloading(fileName: String, imageUrl: String) {
        try {
            val direct = File(
                Environment.getExternalStorageDirectory()
                    .toString() + "/dhaval_files/$fileName"
            )

            if (!direct.exists()) {
                direct.mkdirs()
            }

            val downloadManager: DownloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val downloadUrl = Uri.parse(imageUrl)
            val request: DownloadManager.Request = DownloadManager.Request(downloadUrl)

            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI)
                .setAllowedOverRoaming(false)
                .setTitle("Downloading: $fileName")
                .setDescription("Downloading img...")
                .setMimeType("image/jpeg").setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, File.separator + fileName + File.separator+fileName+".jpg")

            downloadManager.enqueue(request)

            val handler = Handler(Looper.getMainLooper())
            handler.post {
                Toast.makeText(this, "Image downloaded", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.d("DOWNLOADING ERROR: ", "Downloading has been stopped, exception: $e")
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
        startService(intent)
        println("SERVICE HAS STARTED")
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        println("SERVICE HAS BINDED")
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



