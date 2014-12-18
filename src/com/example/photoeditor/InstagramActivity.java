package com.example.photoeditor;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.example.photoeditor.fragments.InstagramLoginFragment;
import com.example.photoeditor.R;
import com.example.photoeditor.interfaces.LogOutListener;

/**
 * Created by Андрей on 16.11.2014.
 */
public class InstagramActivity extends FragmentActivity implements LogOutListener {
    public static final String EXTRA_ACCESS_TOKEN="token";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instagram);
        FragmentManager manager = getSupportFragmentManager();
        Fragment loginFragment = manager.findFragmentById(R.id.instagram_frame_container);
        if(loginFragment==null)
        {
            loginFragment = InstagramLoginFragment.newInstance(false);
            manager.beginTransaction().add(R.id.instagram_frame_container,loginFragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.instagram_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.log_out:
                onLogOut();
                break;
        }
        return true;
    }

    @Override
    public void onLogOut() {
        FragmentManager manager = getSupportFragmentManager();
        Fragment logOutFragment = InstagramLoginFragment.newInstance(true);
        manager.beginTransaction().replace(R.id.instagram_frame_container,logOutFragment).commit();
    }
}
