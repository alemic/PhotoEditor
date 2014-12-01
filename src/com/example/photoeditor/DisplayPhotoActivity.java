package com.example.photoeditor;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import com.example.photoeditor.fragments.InstagramLoginFragment;
import com.example.photoeditor.helpers.ImagesRepository;

import java.util.ArrayList;

/**
 * Created by Андрей on 16.11.2014.
 */
public class DisplayPhotoActivity extends FragmentActivity {
    private String mAcessToken;
    private ImagesRepository mDownloader;
    private ArrayList<Bitmap> mPhotos;
    public DisplayPhotoActivity()
    {
        mDownloader = ImagesRepository.getPhotos("","");
    }
    public String getmAcessToken() {
        return mAcessToken;
    }

    public ArrayList<Bitmap> getmPhotos() {
        return mPhotos;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_photo);
        mAcessToken = getIntent().getStringExtra(InstagramActivity.EXTRA_ACCESS_TOKEN);
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.frame_container);
        if(fragment==null)
        {
              fragment = new InstagramLoginFragment();
           manager.beginTransaction().add(R.id.frame_container,fragment).commit();
       }
    }
}
