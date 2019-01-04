package com.example.liyachao.activity;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.example.liyachao.R;
import com.example.liyachao.giftvideo.VideoController;
import com.example.liyachao.video.CameraGLSurfaceView;

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
