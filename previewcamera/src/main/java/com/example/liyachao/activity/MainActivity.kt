package com.example.liyachao.activity

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.FrameLayout

import com.example.liyachao.R
import com.example.liyachao.utils.FileUtil
import com.example.liyachao.video.CameraGLSurfaceView
import com.knight.alphavideoplayer.giftvideo.VideoController

import java.io.File

/**
 * @author liyachao 296777513
 * @version 1.0
 * @date 2017/3/1
 */
class MainActivity : Activity(), View.OnClickListener {

    internal var mCameraGLSurfaceView: CameraGLSurfaceView? = null
    internal var mSwitchBtn: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            }
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
            }
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), 1)
            }
        }
        val root = findViewById<FrameLayout>(R.id.root)
        val videoController = VideoController(root,isLoop = false,playerType = VideoController.IJKPLAYER)
        videoController.prepareVideo(FileUtil.initPath() + "Alarms/unicorn.mp4")
        videoController.start()
//        FileUtil.initPath()
//        val file = File(Environment.getExternalStorageDirectory().toString() + "/Alarms/unicorn.mp4")
//
//        Log.i("liyachao333", "file: " + file.absolutePath)
    }

    override fun onResume() {
        // TODO Auto-generated method stub
        super.onResume()
        //        mCameraGLSurfaceView.bringToFront();
    }

    override fun onPause() {
        // TODO Auto-generated method stub
        super.onPause()
        //        mCameraGLSurfaceView.onPause();
    }


    override fun onClick(v: View) {
        mCameraGLSurfaceView!!.switchCamera()
    }
}
