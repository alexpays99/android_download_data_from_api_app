package com.example.android_download_data_from_api.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.toDrawable
import com.example.android_download_data_from_api.models.ImageFromPath
import java.io.File

class GridAdapter(private var list: MutableList<ImageFromPath>, path: String, private var context: Context): BaseAdapter() {
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
        val currentItem = list[position]
        var convertView = convertView

        if (layoutInflater == null) {
            layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?
        }

        if (convertView == null) {
            convertView = layoutInflater?.inflate(com.example.android_download_data_from_api.R.layout.grid_item_layout, null)
        }

        imageView = convertView!!.findViewById(com.example.android_download_data_from_api.R.id.imageView)
        imageTitle = convertView.findViewById(com.example.android_download_data_from_api.R.id.imageTitle)

        var imageFile = File(currentItem.imgPath)
        if (imageFile.exists()) {
            var bitmap = BitmapFactory.Options().run {
                inJustDecodeBounds = true
                BitmapFactory.decodeFile(imageFile.absolutePath, this)
                inSampleSize = calculateInSampleSize(this, 400, 400)
                inJustDecodeBounds = false
                BitmapFactory.decodeFile(imageFile.absolutePath, this)
            }
//            val img: Drawable = bitmap.toDrawable(context.resources)
//            imageView.setImageDrawable(img)
            imageView.setImageBitmap(bitmap)
            imageTitle.setText(list[position].title)
        }

        imageView.setImageDrawable(list[position].img)
        imageTitle.setText(list[position].title)

        return convertView
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}