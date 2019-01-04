package com.example.liyachao.giftvideo.view.surfaceview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.graphics.SurfaceTexture
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.View
import com.example.liyachao.giftvideo.view.IAlphaView
import com.example.liyachao.giftvideo.gles.GiftDrawer
import com.example.liyachao.utils.GlUtil
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class AlphaGLSurfaceView : GLSurfaceView, GLSurfaceView.Renderer,
        SurfaceTexture.OnFrameAvailableListener, IAlphaView {


    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    override var onSurfaceCreated: ((SurfaceTexture) -> Unit)? = null
    override val mView: View = this

    init {
        setEGLContextClientVersion(2) // set OpenGL ES's version number to 2.0

//        mMediaPlayer.setScreenOnWhilePlaying(true)
//        mMediaPlayer.isLooping = true
//        mMediaPlayer.setDataSource(context, Uri.parse("android.resource://${context.packageName}/${R.raw.unicorn}"))
//        mMediaPlayer.prepare()
        setEGLConfigChooser(8, 8, 8, 8, 16, 0)
        holder.setFormat(PixelFormat.TRANSLUCENT)
        setZOrderOnTop(true)
        setRenderer(this) // bind current surfaceview to Renderer
        renderMode = RENDERMODE_WHEN_DIRTY // set render's mode
    }

    var mTextureID: Int = 0
    lateinit var mSurface: SurfaceTexture
    lateinit var mGiftDrawer: GiftDrawer

    @SuppressLint("Recycle")
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        mTextureID = GlUtil.createTextureID()
        mSurface = SurfaceTexture(mTextureID)
        mSurface.setOnFrameAvailableListener(this)

        mGiftDrawer = GiftDrawer(mTextureID, context)
        onSurfaceCreated?.invoke(mSurface)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT) // clear the screen's buffer
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
        GLES20.glClearColor(0f, 0f, 0f, 0f) // set current canvas to transparent
        mSurface.updateTexImage()

        mGiftDrawer.draw(mSurface)
    }


    override fun onFrameAvailable(surfaceTexture: SurfaceTexture?) {
        requestRender()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mSurface.release()
    }
}