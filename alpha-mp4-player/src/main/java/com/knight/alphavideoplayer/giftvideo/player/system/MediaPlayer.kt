package com.knight.alphavideoplayer.giftvideo.player.system

import android.content.Context
import android.graphics.SurfaceTexture
import android.net.Uri
import android.view.Surface
import com.knight.alphavideoplayer.giftvideo.player.IPlayer
import com.knight.alphavideoplayer.giftvideo.player.VideoPlayerListener
import tv.danmaku.ijk.media.player.IjkMediaPlayer

class MediaPlayer(override val listener: VideoPlayerListener, override val context: Context, override var isLoop: Boolean = false) : IPlayer {

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
            isLooping = isLoop
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
    override fun buildUri(mp4Res: Any): Uri {
        return when {
            mp4Res is Int -> {
                Uri.parse("android.resource://${context.packageName}/$mp4Res")
            }
            (mp4Res is String) and (mp4Res as String).endsWith(".mp4") -> Uri.parse("file:///$mp4Res")
            mp4Res is Uri -> mp4Res as Uri
            else -> throw RuntimeException("please check your mp4's resources")
        }
    }

    override fun setSurface(surfaceTexture: SurfaceTexture) {
        mMediaPlayer.setSurface(Surface(surfaceTexture))
    }

    override fun prepare(mp4Res: Any) {
        mMediaPlayer.setDataSource(context, buildUri(mp4Res))
        mMediaPlayer.prepareAsync()
    }

}