package com.example.android_download_data_from_api.ui.fragments

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.TextView
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import com.example.android_download_data_from_api.R
import com.example.android_download_data_from_api.adapters.GridAdapter
import com.example.android_download_data_from_api.models.ImageFromPath
import java.io.File

class UserImagesFragment : Fragment() {
    private var photoList = mutableListOf<ImageFromPath>()
    private lateinit var title: TextView
    private lateinit var gridView: GridView
    val path = this.arguments?.getString("message") + this.arguments?.getString("photoID")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_images, container, false)
        title = view.findViewById(R.id.title)
        title.setText(this.arguments?.getString("message") ?: "***")

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().title = "Info"
        setupGridAdapter(view)

        setupPhotoList()
    }

    private fun setupGridAdapter(view: View) {
        gridView = view.findViewById(R.id.grid)
        gridView.adapter = GridAdapter(photoList, requireActivity())
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setupPhotoList() {
        for ((index, i) in (0 until 8).withIndex()) {
            val path = this.arguments?.getString("message") + this.arguments?.getString("photoID")
            val num = if (i == 0) {
                path
            } else {
                "$path-$i"
            }

            val fullPath = "/storage/emulated/0/Download/$path/$num.jpg"
            val imgFile = File(fullPath)
            val fileName = imgFile.name ?: "unknown"

            if (imgFile.exists()) {
                try {
                    val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                    val img: Drawable = myBitmap.toDrawable(resources)
                    photoList.add(ImageFromPath(fullPath, img, fileName))
                    Log.d("LIST OF PHOTOS:", "${photoList[index]}")
                } catch (e: Error) {
                    Log.d("FETCHING FROM GALLERY:", "Error while fetching image, $e")
                }
            } else {
//                val res = getResources().getDrawable(R.drawable.ic_launcher_background)
                val res = context?.getDrawable(R.drawable.ic_launcher_background)
                res?.let { ImageFromPath(fullPath, it, fileName) }?.let { photoList.add(it) }
            }
        }
    }
}