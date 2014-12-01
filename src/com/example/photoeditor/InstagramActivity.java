package com.example.photoeditor;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import com.example.photoeditor.fragments.InstagramLoginFragment;

/**
 * Created by Андрей on 16.11.2014.
 */
public class InstagramActivity extends FragmentActivity {
    public static final String EXTRA_ACCESS_TOKEN="token";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instagram);
        FragmentManager manager = getSupportFragmentManager();
        Fragment loginFragment = manager.findFragmentById(R.id.instagram_frame_container);
        if(loginFragment==null)
        {
            loginFragment = new InstagramLoginFragment();
            manager.beginTransaction().add(R.id.instagram_frame_container,loginFragment)
                    .commit();
        }
    }
}
