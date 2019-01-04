package com.example.liyachao.giftvideo.player.ijk

import android.content.Context
import android.graphics.SurfaceTexture
import android.net.Uri
import android.util.Log
import android.view.Surface
import com.example.liyachao.giftvideo.player.IPlayer
import com.example.liyachao.giftvideo.player.VideoPlayerListener
import tv.danmaku.ijk.media.player.IjkMediaPlayer

class IjkPlayer(override val listener: VideoPlayerListener) : IPlayer {
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
            setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);
            setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 1);

            setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1);
            setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0);

            setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "http-detect-range-support", 1);

            setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);
            setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "min-frames", 100);
            setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1);
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


    override fun prepareVideo(context: Context, path: String, surfaceTexture: SurfaceTexture) {
        mMediaPlayer.setSurface(Surface(surfaceTexture))
        mMediaPlayer.setDataSource(context, Uri.parse(path),null)
        mMediaPlayer.prepareAsync()
    }
}

