package com.example.photoeditor;

import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import com.example.photoeditor.fragments.FunnyFragment;

/**
 * Created by Андрей on 03.12.2014.
 */
public class FunnyActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_funny);
        byte[] photo = getIntent().getByteArrayExtra(StartActivity.CHOOSEN_PHOTO);
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.funnyContainer);
        if(fragment==null)
        {
            fragment = FunnyFragment.newInstance(photo);
            manager.beginTransaction().add(R.id.funnyContainer,fragment).commit();
        }
        Matrix matrix;
    }
}
