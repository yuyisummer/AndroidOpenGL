package com.example.liyachao.giftvideo.gles

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.opengl.Matrix
import com.example.liyachao.R
import com.example.liyachao.utils.GlUtil
import com.example.liyachao.utils.TextResourceReader
import java.lang.RuntimeException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class GiftDrawer(val mTexture: Int, val context: Context) {
    private var mVertexBuffer: FloatBuffer
    private var mProgram: Int = 0
    private var mPositionHandle = 0
    private var mTextureCoordinatorHandle = 0
    private var mMVPMatrixHandle = 0
    private var mUSTMatrixHandle = 0

    private val COORDINATE_PRE_VERTEX = 4 // the number of coordinates at each point
    private val mPerVertexByte = COORDINATE_PRE_VERTEX * 5 // the bytes of each point

    private val mVertices = floatArrayOf(
            // X, Y, Z, U, V
            -1.0f, -1.0f, 0f, 0.5f, 0f,
            1.0f, -1.0f, 0f, 1f, 0f,
            -1.0f, 1.0f, 0f, 0.5f, 1f,
            1.0f, 1.0f, 0f, 1f, 1f
    )

    private val mMVP = FloatArray(16)
    private val sTMatrix = FloatArray(16)

    init {
        val vertexShader = TextResourceReader.readTextFileFromResource(context, R.raw.video_alpha_vertex_shader)
        val fragmentShader = TextResourceReader.readTextFileFromResource(context, R.raw.video_alpha_shader)

        mProgram = GlUtil.createProgram(vertexShader, fragmentShader) // create vertex's shader and fragment's shader, add to shader for build
        if (mProgram == 0) {
            throw  RuntimeException("Unable to create GLES program")
        }
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition")
        GlUtil.checkLocation(mPositionHandle, "aPosition")

        mTextureCoordinatorHandle = GLES20.glGetAttribLocation(mProgram, "aTextureCoord")
        GlUtil.checkLocation(mTextureCoordinatorHandle, "aTextureCoord")

        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix")
        GlUtil.checkLocation(mMVPMatrixHandle, "uMVPMatrix")

        mUSTMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uSTMatrix")
        GlUtil.checkLocation(mUSTMatrixHandle, "uSTMatrix")

        mVertexBuffer = ByteBuffer.allocateDirect(this.mVertices.size * 4).order(ByteOrder.nativeOrder())
                .asFloatBuffer().put(this.mVertices)
        mVertexBuffer.position(0)

        Matrix.setIdentityM(sTMatrix, 0)
    }

    fun draw(surface: SurfaceTexture) {
        surface.getTransformMatrix(sTMatrix)
        GLES20.glUseProgram(mProgram)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mTexture)

        mVertexBuffer.position(0)
        GLES20.glEnableVertexAttribArray(mPositionHandle)// Enable a handle to the triangle vertices
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false,
                mPerVertexByte, mVertexBuffer)

        mVertexBuffer.position(3)
        GLES20.glEnableVertexAttribArray(mTextureCoordinatorHandle)// Enable a handle to the triangle vertices
        GLES20.glVertexAttribPointer(mTextureCoordinatorHandle, 3, GLES20.GL_FLOAT, false,
                mPerVertexByte, mVertexBuffer)

        Matrix.setIdentityM(mMVP, 0)
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVP, 0)
        GLES20.glUniformMatrix4fv(mUSTMatrixHandle, 1, false, sTMatrix, 0)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
        GLES20.glFinish()
    }
}