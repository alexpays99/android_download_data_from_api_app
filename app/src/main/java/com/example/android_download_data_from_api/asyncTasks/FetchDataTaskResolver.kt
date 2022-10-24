package com.example.android_download_data_from_api.asyncTasks

import android.os.Handler
import android.os.Looper

class ThreadSpawner{
    private lateinit var thread: Thread

    fun execute(task : () -> Unit): ThreadSpawner {
        thread = object : Thread() {
            override fun run() {
                task.invoke()
            }
        }
        thread.start()
        return this
    }

    fun cancel(){
        thread.interrupt()
        thread.stop()
    }
}

abstract class FetchDataTaskResolver<Params, Progress, Result> {
    private val customAsyncTask = ThreadSpawner()

    abstract fun doInBackground(vararg many : Params ) : Result
    abstract fun onPostExecute(data : Result)
    abstract fun onPreExecute()
    abstract fun onProgressUpdate(progress: Progress)

    fun execute(vararg counter : Params) {
        val handler = Handler(Looper.getMainLooper())

        var progress : Progress
        handler.post{
            progress = true as Progress
            onPreExecute()
            onProgressUpdate(progress)
        }
        customAsyncTask.execute{
            val result =  runCatching { doInBackground(*counter) }.getOrNull()

            handler.post{
                progress = false as Progress
                onProgressUpdate(progress)
                result?.let { onPostExecute(it) }
            }
        }
    }

    fun cancel(){
        customAsyncTask.cancel()
    }

}

