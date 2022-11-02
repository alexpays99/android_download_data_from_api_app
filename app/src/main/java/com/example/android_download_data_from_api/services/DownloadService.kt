package com.example.android_download_data_from_api.services

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.*
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.android_download_data_from_api.general.Constants
import com.example.android_download_data_from_api.general.ItemStatus
import com.example.android_download_data_from_api.models.DownloadStatus
import com.example.android_download_data_from_api.models.Photo
import java.io.File
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.schedule

class DownloadService : Service() {
    private val binder = CustomBinder()
    private var executorService: ExecutorService = Executors.newFixedThreadPool(3)
    private lateinit var downloadImageTask: Runnable
    var downloadCounter = AtomicInteger(0)
    private lateinit var timer: Timer
    private var map = mutableMapOf<Int, DownloadStatus>()

    inner class CustomBinder : Binder() {
        fun getService(): DownloadService = this@DownloadService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onCreate() {
        println("onCreate() method is called")
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        println("onStartCommand() method is called")
        if (intent != null) {
            val photo = intent.getSerializableExtra(Constants.photo)
            val position = intent.getIntExtra(Constants.position, 0)
            Log.d("onStartCommand() DATA:", "PHOTO: $photo, POSITION: $position") //ok
            executionTask(position, photo as Photo)
        }
        Thread {
            timer = Timer()
            timer.schedule(0, 1000) {
                map.forEach { (key, value) ->
                    setState(
                        value.position,
                        value.photo.state!!,
                        value.photo.id.toInt()
                    )
                    // ok
                    Log.d(
                        "map.forEach DATA:",
                        "POSITION: ${value.position}, STATE: ${value.photo.state!!}, ID: ${value.photo.id}"
                    )
                }
                Log.d("timer", timer.toString())
            }
        }.start()
        return START_STICKY
    }

    override fun onDestroy() {
        println("onDestroy() method is called")
        super.onDestroy()
        stopSelf()
        println("SERVICE HAS BEEN DESTROYED")
    }

    private fun setState(position: Int?, state: ItemStatus?, id: Int?) {
        val intent = Intent(Constants.UPDATE_STATE_ACTION)
        val bundle = Bundle()
        if (position != null && state != null && id != null) {
            bundle.putInt(Constants.position, position)
            bundle.putString(Constants.state, state.toString())
            bundle.putInt(Constants.photoId, id)
            intent.putExtras(bundle)
            Log.d("startBroadcast DATA:", "POS: $position, STATE: $state, ID: $id")
            sendBroadcast(intent)
        }
    }

    fun executionTask(position: Int, photo: Photo) {
        downloadImageTask = Runnable {
            Log.d("IN_PROGRESS:", "$position, Thread: ${Thread.currentThread().name}")

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
                startDownloading(photo.photographer.toString() + photo.id, url, photo)
            }

            Thread.sleep(5000)
            downloadCounter.decrementAndGet()
            map.remove(photo.id.toInt())
            setState(position, ItemStatus.DOWNLOADED, photo.id.toInt())
            Log.d("executionTask() DATA:", "POSITION: $position, PHOTO ID: ${photo.id}")
            if (downloadCounter.get() == 0) {
                Handler(Looper.getMainLooper()).post {
                    timer.cancel()
                    stopSelf()
                }
            }
        }
        downloadCounter.incrementAndGet()
        photo.state = ItemStatus.IN_QUEUE
        map[photo.id.toInt()] = DownloadStatus(position, photo)
        executorService.submit(downloadImageTask)
    }

    @SuppressLint("Range")
    fun startDownloading(fileName: String, imageUrl: String, photo: Photo) {
        try {
            val direct = File(
                Environment.getExternalStorageDirectory()
                    .toString() + "/dhaval_files/$fileName"
            )

            if (!direct.exists()) {
                direct.mkdirs()
            }

            val downloadManager: DownloadManager =
                getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val downloadUrl = Uri.parse(imageUrl)
            val request: DownloadManager.Request = DownloadManager.Request(downloadUrl)

            map[photo.id.toInt()]!!.photo.state = ItemStatus.IN_PROGRESS

            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle("Downloading: $fileName")
                .setDescription("Downloading img...")
                .setMimeType("image/jpeg")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    File.separator + fileName + File.separator + fileName + ".jpg"
                )

            downloadManager.enqueue(request)

            stopSelf()
        } catch (e: Exception) {
            Log.d("DOWNLOADING ERROR: ", "Downloading has been stopped, exception: $e")
            map[photo.id.toInt()]!!.photo.state = ItemStatus.DEFAULT
            Handler(Looper.getMainLooper()).post {
                timer.cancel()
                stopSelf()
            }
        }
    }

    fun dispose() {
        println("dispose() method is called")
        stopSelf()
        println("service has been destroyed")
    }
}