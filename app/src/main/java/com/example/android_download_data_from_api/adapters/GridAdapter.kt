package com.example.android_download_data_from_api.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.android_download_data_from_api.models.ImageFromPath

class GridAdapter(private var list: MutableList<ImageFromPath>, private var context: Context): BaseAdapter() {
    private var layoutInflater: LayoutInflater? = null
    private lateinit var imageView: ImageView
    private lateinit var imageTitle: TextView

    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): Any {
        return list.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("InflateParams", "SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var convertView = convertView

        if (layoutInflater == null) {
            layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?
        }

        if (convertView == null) {
            convertView = layoutInflater?.inflate(com.example.android_download_data_from_api.R.layout.grid_item_layout, null)
        }

        imageView = convertView!!.findViewById(com.example.android_download_data_from_api.R.id.imageView)
        imageTitle = convertView.findViewById(com.example.android_download_data_from_api.R.id.imageTitle)

        imageView.setImageDrawable(list[position].img)
        imageTitle.setText(list[position].title)

        return convertView
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}