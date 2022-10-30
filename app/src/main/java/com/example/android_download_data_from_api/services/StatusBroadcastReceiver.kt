package com.example.android_download_data_from_api.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.android_download_data_from_api.common.adapters.ItemStatus

class StatusBroadcastReceiver : BroadcastReceiver() {
    var downloadStatusCallback: ((position: Int, state: ItemStatus) -> Unit)? = null

    override fun onReceive(context: Context?, intent: Intent) {
        val position = intent.extras!!.getInt("position")
        val state = intent.extras!!.getString("state")

        if (state != null) {
            downloadStatusCallback?.invoke(position, ItemStatus.valueOf(state))
        }
    }
}