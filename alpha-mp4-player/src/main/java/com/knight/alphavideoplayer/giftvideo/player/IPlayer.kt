package com.knight.alphavideoplayer.giftvideo.player

import android.content.Context
import android.graphics.SurfaceTexture
import android.net.Uri

interface IPlayer {
    fun start()

    fun release()

    fun pause()

    fun stop()

    fun reset()

    fun getDuration(): Long

    fun setSurface(surfaceTexture: SurfaceTexture)

    fun prepare(mp4Res: Any)

    fun buildUri(mp4Res: Any): Uri

    val listener: VideoPlayerListener

    val context: Context

    var isLoop: Boolean
}