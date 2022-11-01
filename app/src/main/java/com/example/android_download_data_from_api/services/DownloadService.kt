package com.example.android_download_data_from_api.services

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Binder
import android.os.Bundle
import android.os.Environment
import android.os.IBinder
import android.util.Log
import com.example.android_download_data_from_api.general.Constants
import com.example.android_download_data_from_api.general.ItemStatus
import com.example.android_download_data_from_api.models.DownloadStatus
import java.io.File
import kotlin.math.roundToInt

class DownloadBroadcastReceiver : BroadcastReceiver() {
    //    var incrementCounterCallback: ((counter: Int) -> Unit)? = null
    var onDownloadComplete: ((position: Int, state: ItemStatus) -> Unit)? = null

    override fun onReceive(context: Context?, intent: Intent) {
        val position = intent.extras!!.getInt(Constants.shared.position)
        val state = intent.extras!!.getString(Constants.shared.state)

        val randomId = intent.extras!!.getString("randomID")
        val downloadId = intent.extras!!.getLong(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
        val counter = intent.extras!!.getInt(Constants.shared.counter)
        val map = mutableMapOf(downloadId to counter)

        if (intent.action.equals(Constants.shared.DOWNLOAD_COMPLETE_ACTION)) {
            map.put(downloadId, counter)
            Log.d("COUNTER MAP:", "$map")

            if (map[downloadId] == 7) {
                onDownloadComplete?.invoke(
                    position,
                    ItemStatus.valueOf(ItemStatus.DOWNLOADED.toString())
                )
            }
        }
    }
}

class DownloadService : Service() {
    private val binder = CustomBinder()
    private val map = mutableMapOf(0 to 0)
    var downloadCounter = 0

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

    fun setDownloadId(randomID: String, id: Long, counter: Int) {
        val intent = Intent(Constants.shared.DOWNLOAD_COMPLETE_ACTION)
        val bundle = Bundle()
        bundle.putString("randomID", randomID)
        bundle.putLong(Constants.shared.id, id)
        bundle.putInt(Constants.shared.counter, counter)
        intent.putExtras(bundle)
        sendBroadcast(intent)
    }

    private fun getRandomString(): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        var string = ""
        for (i in (1..20)) {
            string += allowedChars[(Math.random() * (allowedChars.size - 1)).roundToInt()]
        }
        return string
    }

    @SuppressLint("Range")
    fun startDownloading(fileName: String, imageUrl: String): Boolean {
        var downloadID: Long = 0
//        var downloadCounter = 0
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

            downloadID = downloadManager.enqueue(request)
            Log.d("DOWNLOAD ID", "Checking download status for id: $downloadID")

            //Verify if download is a success
            val c: Cursor = downloadManager.query(DownloadManager.Query().setFilterById(downloadID))

            if (c.moveToFirst()) {
                val status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))
                return if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    downloadCounter++
                    setDownloadId(getRandomString(), downloadID, downloadCounter)
                    if (downloadCounter == 8) {
                        downloadCounter = 0
                    }
                    true //Download is valid, celebrate
                } else {
                    val reason = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_REASON))
                    Log.d(
                        "DOWNLOAD ERROR",
                        "Download not correct, status [$status] reason [$reason]"
                    )
                    false
                }
            }

//            downloadCounter++
//            setDownloadId(downloadID, downloadCounter)
            stopSelf()
        } catch (e: Exception) {
            Log.d("DOWNLOADING ERROR: ", "Downloading has been stopped, exception: $e")
        }
        return false
    }

    fun dispose() {
        println("dispose() method is called")
        stopSelf()
        println("service has been destroyed")
    }
}