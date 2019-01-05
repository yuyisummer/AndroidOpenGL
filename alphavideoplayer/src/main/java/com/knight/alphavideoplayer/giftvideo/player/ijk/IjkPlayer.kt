package com.knight.alphavideoplayer.giftvideo.player.ijk

import android.content.Context
import android.graphics.SurfaceTexture
import android.net.Uri
import android.util.Log
import android.view.Surface
import com.knight.alphavideoplayer.giftvideo.player.IPlayer
import com.knight.alphavideoplayer.giftvideo.player.VideoPlayerListener
import tv.danmaku.ijk.media.player.IjkMediaPlayer

class IjkPlayer(override val listener: VideoPlayerListener, override val context: Context, override var isLoop: Boolean = false) : IPlayer {


    private val mMediaPlayer: IjkMediaPlayer by lazy(LazyThreadSafetyMode.NONE) {
        IjkMediaPlayer().apply {
            setOnPreparedListener {
                listener.onPrepared(this@IjkPlayer)
            }

            setOnCompletionListener {
                listener.onCompletion(this@IjkPlayer)
            }

            setOnVideoSizeChangedListener { _, width, height, _, _ ->
                listener.onVideoSizeChanged(this@IjkPlayer, width, height)
            }
            setOnErrorListener { _, what, extra ->

                listener.onError(this@IjkPlayer, "what:$what , extra:$extra")
                return@setOnErrorListener false
            }
            setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1)
            isLooping = isLoop
        }
    }

    override fun start() {
        mMediaPlayer.start()
        Log.i("liyachao222", "start")
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

    override fun getDuration(): Long = mMediaPlayer.duration
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

    override fun prepareVideo(mp4Res: Any, surfaceTexture: SurfaceTexture) {
        mMediaPlayer.setSurface(Surface(surfaceTexture))
        mMediaPlayer.setDataSource(RawDataSourceProvider.create(context, buildUri(mp4Res)))
        mMediaPlayer.prepareAsync()
    }
}

