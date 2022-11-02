package com.example.android_download_data_from_api.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.android_download_data_from_api.general.Constants
import com.example.android_download_data_from_api.general.ItemStatus

class DownloadBroadcastReceiver : BroadcastReceiver() {
    var onDownloadCompleteCallback: ((position: Int, state: ItemStatus) -> Unit)? = null

    override fun onReceive(context: Context?, intent: Intent) {
        val position = intent.extras?.getInt(Constants.position)
        Log.d("onReceive() POSITION:", "POSITION: $position")
        val state = intent.extras?.getString(Constants.state)
        Log.d("onReceive() STATE:", "STATE: $state")

        if (state != null) {
            onDownloadCompleteCallback?.invoke(position!!, ItemStatus.valueOf(state))
            Log.d("BROADCAST STATE:", "STATE: $state, ItemStatus.valueOf(state): $state")
        }
    }
}