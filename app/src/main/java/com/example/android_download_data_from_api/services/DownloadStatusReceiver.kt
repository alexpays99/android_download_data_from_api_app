package com.example.android_download_data_from_api.services

import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log

class DownloadStatusReceiver: BroadcastReceiver() {
    var downloadStatusCallback: ((position: Int, state: ItemStatus) -> Unit)? = null
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("BROADCAST RECEIVER:", "RECEIVED SOMETHING")
//        downloadStatusCallback.invoke()
    }
}