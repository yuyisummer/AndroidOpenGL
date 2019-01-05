package com.knight.alphavideoplayer.giftvideo.view

import android.graphics.SurfaceTexture
import android.view.View

interface IAlphaView {
    val mView: View
    var onSurfaceCreated: ((SurfaceTexture) -> Unit)?

    fun clearTexture()
}