package com.example.android_download_data_from_api.adapters

import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.android_download_data_from_api.R
import com.example.android_download_data_from_api.general.ItemStatus
import com.example.android_download_data_from_api.interfaces.OnBindInterface
import com.example.android_download_data_from_api.interfaces.OnDownloadImageInterface
import com.example.android_download_data_from_api.models.Photo

class UserListAdapter(var list: MutableList<Photo>): RecyclerView.Adapter<UserListAdapter.ViewHolder>() {
    private lateinit var cardViewCallback: OnBindInterface
    private lateinit var downloadImg: OnDownloadImageInterface

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

    fun downloadImgCallback(callback: OnDownloadImageInterface) {
        downloadImg = callback
    }

    fun updateItemState(position: Int, state: ItemStatus) {
        list[position].state = state
        val handler = Handler(Looper.getMainLooper())
        handler.post {
            notifyItemChanged(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.userdata_cell_layout,parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dataPosition = list[position]

        holder.name.text = dataPosition.photographer
        holder.name.setTextColor(Color.parseColor(dataPosition.avgColor))
//        holder.setIsRecyclable(false)

        when(dataPosition.state) {
            ItemStatus.DEFAULT -> {
                holder.button.text = "Download"
                holder.button.setOnClickListener {
                    downloadImg.onDownloadImage(list[position], position)
                }
                Log.d("USER STATUS", "${dataPosition.photographer}: DEFAULT")
            }
            ItemStatus.IN_QUEUE -> {
                holder.button.text = "In Queue"
                Log.d("USER STATUS", "${dataPosition.photographer}: IN_QUEUE")
            }
            ItemStatus.IN_PROGRESS -> {
                holder.button.text = "In Progress"
                Log.d("USER STATUS", "${dataPosition.photographer}: IN_PROGRESS")
            }
            ItemStatus.DOWNLOADED -> {
                holder.button.text = "Downloaded"
                holder.button.isEnabled = false
                Log.d("USER STATUS", "${dataPosition.photographer}: DOWNLOADED")
                holder.cardView.setOnClickListener {
                    cardViewCallback.onBinding(list[position])
                }
            }
        }
        Log.d("UserListAdapter", "Holder Name: ${holder.name.text}")
        Log.d("UserListAdapter", "List[Position] Name: ${dataPosition.photographer}")
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}