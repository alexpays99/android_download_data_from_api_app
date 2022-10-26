package com.example.android_download_data_from_api.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.example.android_download_data_from_api.BuildConfig
import com.example.android_download_data_from_api.common.adapters.OnBindInterface
import com.example.android_download_data_from_api.interfaces.RetrofitApiCallInterface
import com.example.android_download_data_from_api.models.Photo
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DownloadService: Service() {
    private val binder = CustomBinder()
    private lateinit var thread: Thread
    private lateinit var task: Runnable

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
        //            return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        println("onDestroy() method is called")
        stopSelf()
        super.onDestroy()
        println("SERVICE HAS BEEN DESTROYED")
    }

//    fun setupTask(task: Runnable) {
//        println("setupTask() method is called")
//        thread = Thread(task)
//        println("onDestroy() method is called")
//        thread.start()
//        println(thread.name + ", " + thread.state + "has started")
//    }
//
//    fun dispose() {
//        println("dispose() method is called")
//        thread.stop()
//        println(thread.name + ", " + thread.state)
//        stopSelf()
//        println("service has been destroyed")
//    }
}