package com.example.android_download_data_from_api.services

import android.app.DownloadManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.*
import android.util.Log
import android.widget.Toast
import com.example.android_download_data_from_api.common.adapters.ItemStatus
import java.io.File

class DownloadService : Service() {
    private val binder = CustomBinder()

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
        stopSelf()
        super.onDestroy()
        println("SERVICE HAS BEEN DESTROYED")
    }

    fun postState(position: Int, state: ItemStatus) {
        val intent = Intent()
        val bundle = Bundle()
        bundle.putInt("DOWNLOAD_POSITION_BROADCAST", position)
        bundle.putString("DOWNLOAD_STATUS_BROADCAST", state.toString())
        intent.putExtras(bundle)
        sendBroadcast(intent)
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

            val downloadManager: DownloadManager =
                getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val downloadUrl = Uri.parse(imageUrl)
            val request: DownloadManager.Request = DownloadManager.Request(downloadUrl)

            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI)
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
        }
    }

    fun dispose() {
        println("dispose() method is called")
        stopSelf()
        println("service has been destroyed")
    }
}