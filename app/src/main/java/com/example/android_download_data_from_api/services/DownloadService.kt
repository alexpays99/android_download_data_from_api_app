package com.example.android_download_data_from_api.services

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.*
import android.util.Log
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
import kotlin.math.roundToInt

interface ReceiveDataInerface {
    fun onReCeiveData(position: Int, state: ItemStatus)
}

class DownloadBroadcastReceiver : BroadcastReceiver() {
    //    var incrementCounterCallback: ((counter: Int) -> Unit)? = null
//    var onDownloadComplete: ((position: Int, state: ItemStatus) -> Unit)? = null

    private lateinit var onDownloadCompleteCallback: ReceiveDataInerface

    fun setButtonStateCallback(callback: ReceiveDataInerface) {
        this.onDownloadCompleteCallback = callback
    }

    override fun onReceive(context: Context?, intent: Intent) {
        val position = intent.extras!!.getInt(Constants.shared.position)
        val state = intent.extras!!.getString(Constants.shared.state)

        if (state != null) {
            onDownloadCompleteCallback.onReCeiveData(position, ItemStatus.valueOf(state))
        }
    }
}

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

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        println("onStartCommand() method is called")
        return START_STICKY
    }

    override fun onDestroy() {
        println("onDestroy() method is called")
        super.onDestroy()
        stopSelf()
        println("SERVICE HAS BEEN DESTROYED")
    }

    fun setState(position: Int, state: ItemStatus) {
        val intent = Intent(Constants.shared.UPDATE_STATE_ACTION)
        val bundle = Bundle()
        bundle.putInt(Constants.shared.position, position)
        bundle.putString(Constants.shared.state, state.toString())
        intent.putExtras(bundle)
        sendBroadcast(intent)
    }

    fun startBroadcast(position: Int, state: ItemStatus?, id: Int) {
        val intent = Intent(Constants.shared.UPDATE_STATE_ACTION)
        intent.putExtra(Constants.shared.state, state)
        intent.putExtra(Constants.shared.position, position)
        intent.putExtra(Constants.shared.photoId, id)
        sendBroadcast(intent)
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
            startBroadcast(position, ItemStatus.DOWNLOADED, photo.id.toInt())
            if(downloadCounter.get() == 0) {
                stopSelf()
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