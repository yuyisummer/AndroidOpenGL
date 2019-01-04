package com.example.liyachao.giftvideo

import android.arch.lifecycle.LifecycleObserver
import android.util.Log
import android.view.ViewGroup
import com.example.liyachao.R
import com.example.liyachao.giftvideo.player.IPlayer
import com.example.liyachao.giftvideo.player.VideoPlayerListener
import com.example.liyachao.giftvideo.player.ijk.IjkPlayer
import com.example.liyachao.giftvideo.player.system.MediaPlayer
import com.example.liyachao.giftvideo.view.surfaceview.AlphaGLSurfaceView

class VideoController(val parent: ViewGroup) : LifecycleObserver {

    private val context = parent.context


    init {
        val player = IjkPlayer(object : VideoPlayerListener() {
            override fun onCompletion(player: IPlayer) {

            }

            override fun onError(player: IPlayer, message: String) {
                super.onError(player, message)
                Log.i("liyachao222", "message:$message")
            }
        })

        val alphaView = AlphaGLSurfaceView(context)
        Log.i("liyachao222", "init")
        alphaView.onSurfaceCreated = {
            Log.i("liyachao222", "onSurfaceCreated")
            player.prepareVideo(context, "android.resource://${context.packageName}/${R.raw.unicorn}", it)
//            player.start()
        }
        parent.addView(alphaView)
    }

}