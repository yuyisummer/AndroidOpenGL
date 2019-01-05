package com.example.liyachao.activity;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.example.liyachao.R;
import com.knight.alphavideoplayer.giftvideo.VideoController;
import com.example.liyachao.utils.FileUtil;
import com.example.liyachao.video.CameraGLSurfaceView;

import java.io.File;

/**
 * @author liyachao 296777513
 * @version 1.0
 * @date 2017/3/1
 */
public class MainActivity extends Activity implements View.OnClickListener {

    CameraGLSurfaceView mCameraGLSurfaceView;
    Button mSwitchBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
            }
        }
//        mCameraGLSurfaceView = (CameraGLSurfaceView) findViewById(R.id.camera_gl_surface_view);

//        mSwitchBtn = (Button) findViewById(R.id.switch_camera);
//        mSwitchBtn.setOnClickListener(this);
//        AlphaGLSurfaceView alphaGLSurfaceView = new AlphaGLSurfaceView(this);
//        alphaGLSurfaceView.setOnSurfaceCreated(new Function1<SurfaceTexture, Unit>() {
//            @Override
//            public Unit invoke(SurfaceTexture surfaceTexture) {
//                return null;
//            }
//        });
//        VideoPlayer videoPlayer = new VideoPlayer((FrameLayout) findViewById(R.id.root));
//        videoPlayer.start(Uri.parse("android.resource://" + getBaseContext().getPackageName() + "/" + R.raw.unicorn));
        FrameLayout root = findViewById(R.id.root);
        VideoController videoController = new VideoController(root);
//        File file = new File("/mnt/sdcard/Alarms/");
//        if (file.isDirectory()) {
//            for (File f : file.listFiles()) {
//                Log.i("liyachao333", "path: " + f.getName());
//
//            }
//        }
        FileUtil.initPath();
        File file = new File(Environment.getExternalStorageDirectory() + "/Alarms/unicorn.mp4");

        Log.i("liyachao333", "file: " + file.getAbsolutePath());
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
//        mCameraGLSurfaceView.bringToFront();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
//        mCameraGLSurfaceView.onPause();
    }


    @Override
    public void onClick(View v) {
        mCameraGLSurfaceView.switchCamera();
    }
}
