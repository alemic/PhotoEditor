package com.example.photoeditor;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import com.example.photoeditor.fragments.FilterFragment;
import com.example.photoeditor.helpers.FilterMaker;
import com.example.photoeditor.helpers.Filters;
import com.example.photoeditor.helpers.ImageSaver;

/**
 * Created by Андрей on 27.11.2014.
 */
public class FilterActivity extends FragmentActivity {
    private ViewPager mViewPager;
    private FilterMaker mFilterMaker;
    private Bitmap mPhoto;
    public static final String PHOTO_WITH_EFFECT = "photo_with_effect";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_activity);
        mFilterMaker = new FilterMaker();
        mPhoto = ImageSaver.getImageInPrivateRepository(this,getIntent().getStringExtra(StartActivity.PHOTO_FROM_EDITOR));
        mViewPager = (ViewPager)findViewById(R.id.viewPager);
        mViewPager.setAdapter(new ViewPagerFragmentAdapter(getSupportFragmentManager()));
    }
    private class ViewPagerFragmentAdapter extends FragmentStatePagerAdapter
    {
        public ViewPagerFragmentAdapter(FragmentManager manager)
        {
            super(manager);
        }
        @Override
        public Fragment getItem(int i) {
            Bitmap photo = mPhoto.copy(Bitmap.Config.ARGB_8888,true);
            mFilterMaker.getFilter(photo, Filters.fromInt(i));
            FilterFragment fragment = FilterFragment.newInstance(mFilterMaker.getFilter(photo,Filters.fromInt(i)));
            return fragment;
        }
        @Override
        public int getCount() {
            return mFilterMaker.getFilterCount();
        }
    }
}

