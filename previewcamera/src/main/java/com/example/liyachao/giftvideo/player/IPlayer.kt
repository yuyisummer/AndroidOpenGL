package com.example.liyachao.giftvideo.player

import android.content.Context
import android.graphics.SurfaceTexture

interface IPlayer {
    fun start()

    fun release()

    fun pause()

    fun stop()

    fun reset()

    fun getDuration(): Long

    fun prepareVideo(context: Context, path: String, surfaceTexture: SurfaceTexture)

    val listener: VideoPlayerListener
}