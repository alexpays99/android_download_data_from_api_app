package com.example.android_download_data_from_api.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.android_download_data_from_api.common.adapters.ItemStatus

class DownloadStatusReceiver: BroadcastReceiver() {
    var downloadStatusCallback: ((position: Int, state: ItemStatus) -> Unit)? = null
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("BROADCAST RECEIVER:", "RECEIVED SOMETHING")
        val  position = intent.extras!!.getInt("DOWNLOAD_POSITION_BROADCAST")
        val  state = intent.extras!!.getString("DOWNLOAD_STATUS_BROADCAST")
        downloadStatusCallback?.invoke(position, ItemStatus.valueOf(state!!))
    }
}