package com.example.android_download_data_from_api.common.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.android_download_data_from_api.R
import com.example.android_download_data_from_api.models.Photo

class GridAdapter(var list: MutableList<Photo>, private var context: Context): BaseAdapter() {
    private var layoutInflater: LayoutInflater? = null
    private lateinit var imageView: ImageView
    private lateinit var imageTitle: TextView

    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): Any {
        TODO("Not yet implemented")
    }

    override fun getItemId(position: Int): Long {
        TODO("Not yet implemented")
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var convertView = convertView

        if (layoutInflater == null) {
            layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?
        }

        if (convertView == null) {
            convertView = layoutInflater?.inflate(R.layout.grid_item_layout, null)
        }
        imageView = convertView!!.findViewById(R.id.imageView)
        imageTitle = convertView.findViewById(R.id.imageTitle)

        imageView.setImageResource(list.get(position).src.medium.length)
        imageTitle.setText(list.get(position).alt)

        return convertView
    }
}