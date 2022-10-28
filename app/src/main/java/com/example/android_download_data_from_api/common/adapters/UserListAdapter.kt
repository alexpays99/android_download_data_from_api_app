package com.example.android_download_data_from_api.common.adapters

import android.content.ClipData.Item
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.android_download_data_from_api.R
import com.example.android_download_data_from_api.models.Photo
import com.example.android_download_data_from_api.ui.ItemStatus

interface OnBindInterface {
    fun onBinding(photo: Photo)
}

interface OnDownladImageInterface {
    fun onDownladImage(photo: Photo)
}

class UserListAdapter(var list: MutableList<Photo>): RecyclerView.Adapter<UserListAdapter.ViewHolder>() {
    private lateinit var cardViewCallback: OnBindInterface
    private lateinit var downloadImg: OnDownladImageInterface

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var name: TextView
        var button: Button
        var cardView: CardView

        init {
            name = itemView.findViewById(R.id.userName)
            button = itemView.findViewById(R.id.downloadButton)
            cardView = itemView.findViewById(R.id.cardView)
            Log.d("UserListAdapter", "Init name and button")
        }
    }

    fun bindCallback(callback: OnBindInterface) {
        cardViewCallback = callback
    }

    fun downloadImgCallback(callback: OnDownladImageInterface) {
        downloadImg = callback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.userdata_cell_layout,parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dataPosition = list[position]

        println(list[position])
        holder.name.text = dataPosition.photographer
        holder.name.setTextColor(Color.parseColor(dataPosition.avgColor))
        holder.cardView.setOnClickListener {
            cardViewCallback.onBinding(list[position])
        }
        holder.button.setOnClickListener {
            // download user images
            downloadImg.onDownladImage(list[position])
        }
        Log.d("UserListAdapter", "Holder Name: ${holder.name.text}")
        Log.d("UserListAdapter", "List[Position] Name: ${dataPosition.photographer}")
    }

    override fun getItemCount(): Int {
        return list.size
    }
}