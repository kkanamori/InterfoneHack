package com.example.kanamori.intercom3;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.hardware.Camera;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.Window;

import java.io.FileOutputStream;

public class MainActivity extends Activity {
    private Camera myCamera;
    private Uri.Builder builder;
    private AsyncHttpRequest task;
    private boolean nowShuttering = false;

    private SurfaceHolder.Callback mSurfaceListener =
            new SurfaceHolder.Callback() {
                public void surfaceCreated(SurfaceHolder holder) {
                    // TODO Auto-generated method stub
                    myCamera = Camera.open(1);
                    try {
//                        myCamera.setDisplayOrientation(90);
                        myCamera.setPreviewDisplay(holder);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                public void surfaceDestroyed(SurfaceHolder holder) {
                    // TODO Auto-generated method stub
                    myCamera.release();
                    myCamera = null;
                }

                public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                           int height) {
                    // TODO Auto-generated method stub
                    Camera.Parameters parameters = myCamera.getParameters();
                    parameters.setRotation(270);
                    //Log.d("debug",parameters.flatten());
                    myCamera.setParameters(parameters);
                    myCamera.startPreview();

                }
            };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SurfaceView mySurfaceView = (SurfaceView)findViewById(R.id.surface_view);
        SurfaceHolder holder = mySurfaceView.getHolder();
        holder.addCallback(mSurfaceListener);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        /*
        Window window = getWindow();
        View view = window.getDecorView();
        view.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_FULLSCREEN);
                        */
    }

    // シャッターが押されたときに呼ばれるコールバック
    private Camera.ShutterCallback mShutterListener =
            new Camera.ShutterCallback() {
                public void onShutter() {
                    // TODO Auto-generated method stub
                }
            };

    // JPEGイメージ生成後に呼ばれるコールバック
    private Camera.PictureCallback mPictureListener =
            new Camera.PictureCallback() {
                public void onPictureTaken(byte[] data, Camera camera) {
                    if (data != null) {
                        try {
                            task.execute(data);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        myCamera.startPreview();
                        nowShuttering = false;
                    }
                }
            };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Log.d("debug","onTouchEvent called.");
            if (myCamera != null && nowShuttering != true) {
                nowShuttering = true;
                // 写真アップロードの準備
                builder = new Uri.Builder();
                task = new AsyncHttpRequest(this);

                myCamera.takePicture(null, null, mPictureListener);
            }
        }
        return true;
    }

}