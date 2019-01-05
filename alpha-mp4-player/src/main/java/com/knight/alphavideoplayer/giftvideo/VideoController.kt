package com.knight.alphavideoplayer.giftvideo

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.support.annotation.IntDef
import android.view.ViewGroup
import com.knight.alphavideoplayer.giftvideo.player.IPlayer
import com.knight.alphavideoplayer.giftvideo.player.VideoPlayerListener
import com.knight.alphavideoplayer.giftvideo.player.exo.exoPlayer
import com.knight.alphavideoplayer.giftvideo.player.ijk.IjkPlayer
import com.knight.alphavideoplayer.giftvideo.player.system.MediaPlayer
import com.knight.alphavideoplayer.giftvideo.view.IAlphaView
import com.knight.alphavideoplayer.giftvideo.view.surfaceview.AlphaGLSurfaceView
import com.knight.alphavideoplayer.giftvideo.view.textureview.AlphaTextureView

class VideoController(val parent: ViewGroup, val isLoop: Boolean = false,
                      @PlayerType playerType: Int = IJKPLAYER, @ViewType viewType: Int = GLSURFACEVIEW) : LifecycleObserver {

    companion object {
        const val IJKPLAYER = 1
        const val EXOPLAYER = 2
        const val MEDIAPLAYER = 3

        @IntDef(IJKPLAYER, EXOPLAYER, MEDIAPLAYER)
        @Retention(AnnotationRetention.SOURCE)
        annotation class PlayerType


        const val GLSURFACEVIEW = 4
        const val TEXUTREVIEW = 5

        @IntDef(GLSURFACEVIEW, TEXUTREVIEW)
        @Retention(AnnotationRetention.SOURCE)
        annotation class ViewType
    }


    private val context = parent.context
    val player: IPlayer by lazy(LazyThreadSafetyMode.NONE) {
        when (playerType) {
            IJKPLAYER -> IjkPlayer(listener, context, isLoop)
            EXOPLAYER -> exoPlayer(listener, context, isLoop)
            else -> MediaPlayer(listener, context, isLoop)
        }
    }

    val listener: VideoPlayerListener by lazy(LazyThreadSafetyMode.NONE) {
        object : VideoPlayerListener() {
            override fun onCompletion(player: IPlayer) {
                alphaView.clearTexture()
            }

            override fun onError(player: IPlayer, message: String) {
                super.onError(player, message)
            }

            override fun onPrepared(player: IPlayer) {
            }
        }
    }

    val alphaView: IAlphaView by lazy(LazyThreadSafetyMode.NONE) {
        when (viewType) {
            GLSURFACEVIEW -> AlphaGLSurfaceView(context) as IAlphaView
            else -> AlphaTextureView(context) as IAlphaView
        }
    }

    init {
        alphaView.onSurfaceCreated = {
            player.setSurface(it)

        }
        parent.addView(alphaView.mView)
    }

    fun prepareVideo(mp4Path: String) {
        player.prepare(mp4Path)
    }

    fun start() {
        player.start()
    }

    fun pause() {
        player.pause()
    }

    fun release() {
        player.release()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
//        player.start()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
//        player.pause()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
//        player.stop()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        player.release()
    }

}