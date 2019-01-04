package com.example.liyachao.giftvideo.player.system

import android.content.Context
import android.graphics.SurfaceTexture
import android.net.Uri
import android.view.Surface
import com.example.liyachao.giftvideo.player.IPlayer
import com.example.liyachao.giftvideo.player.VideoPlayerListener
import tv.danmaku.ijk.media.player.IjkMediaPlayer

class MediaPlayer(override val listener: VideoPlayerListener) : IPlayer {
    private val mMediaPlayer: android.media.MediaPlayer by lazy(LazyThreadSafetyMode.NONE) {
        android.media.MediaPlayer().apply {
            setOnPreparedListener {
                listener.onPrepared(this@MediaPlayer)
            }

            setOnCompletionListener {
                listener.onCompletion(this@MediaPlayer)
            }
            setOnVideoSizeChangedListener { _, width, height ->
                listener.onVideoSizeChanged(this@MediaPlayer, width, height)
            }
            setOnErrorListener { _, what, extra ->

                listener.onError(this@MediaPlayer, "what:$what , extra:$extra")
                return@setOnErrorListener false
            }
        }
    }

    override fun start() {
        mMediaPlayer.start()
    }

    override fun release() {
        mMediaPlayer.reset()
        mMediaPlayer.release()
    }

    override fun pause() {
        mMediaPlayer.pause()
    }

    override fun stop() {
        mMediaPlayer.stop()
    }

    override fun reset() {
        mMediaPlayer.reset()
    }

    override fun getDuration(): Long = mMediaPlayer.duration.toLong()


    override fun prepareVideo(context: Context, path: String, surfaceTexture: SurfaceTexture) {
        mMediaPlayer.setSurface(Surface(surfaceTexture))
        mMediaPlayer.setDataSource(context, Uri.parse(path))
        mMediaPlayer.prepareAsync()
    }

}