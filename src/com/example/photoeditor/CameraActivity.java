package com.example.photoeditor;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.view.WindowManager;
import com.example.photoeditor.fragments.CameraFragment;

/**
 * Created by Андрей on 25.11.2014.
 */
public class CameraActivity extends FragmentActivity {
    public static final String RESULT_PHOTO_FROM_CAMERA = "photo_from_camera";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.camera_activity);
        FragmentManager manager = getSupportFragmentManager();
        Fragment cameraFragment = manager.findFragmentById(R.id.photoContainer);
        if(cameraFragment==null)
        {
            cameraFragment = new CameraFragment();
            manager.beginTransaction().add(R.id.photoContainer,cameraFragment).commit();
        }
    }
}
