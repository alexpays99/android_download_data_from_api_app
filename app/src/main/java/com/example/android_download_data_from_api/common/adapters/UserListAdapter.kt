package com.example.android_download_data_from_api.common.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.android_download_data_from_api.R
import com.example.android_download_data_from_api.models.Photo
import com.example.android_download_data_from_api.models.User
import com.example.android_download_data_from_api.ui.fragments.UserImagesFragment

class UserListAdapter(var list: MutableList<Photo>): RecyclerView.Adapter<UserListAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var name: TextView
        var button: Button

        init {
            name = itemView.findViewById(R.id.userName)
            button = itemView.findViewById(R.id.downloadButton)
            Log.d("UserListAdapter", "Init name and button")
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

        println(list[position])
        holder.name.text = dataPosition.photographer
        holder.name.setOnClickListener { v ->
            // open new fragment
            val activity = v!!.context as AppCompatActivity
            val userInfoFragment = UserImagesFragment()
            activity.supportFragmentManager
                .beginTransaction()
                .add(R.id.recListConstraintLayout, userInfoFragment)
                .addToBackStack(null)
                .commit()
        }
        holder.button.setOnClickListener { v ->
            // download user images
        }
        Log.d("UserListAdapter", "Holder Name: ${holder.name.text}")
        Log.d("UserListAdapter", "List[Position] Name: ${dataPosition.photographer}")
    }

    override fun getItemCount(): Int {
        return list.size
    }
}