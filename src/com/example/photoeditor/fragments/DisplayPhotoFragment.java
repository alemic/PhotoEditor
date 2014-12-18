package com.example.photoeditor.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import com.example.photoeditor.R;
import com.example.photoeditor.StartActivity;
import com.example.photoeditor.helpers.ImageDowloader;
import com.example.photoeditor.helpers.ImagesRepository;
import com.example.photoeditor.interfaces.LogOutListener;

import java.net.URL;
import java.util.ArrayList;


/**
 * Created by Андрей on 16.11.2014.
 */
public class DisplayPhotoFragment extends Fragment {
    private GridView mGridView;
    private ProgressDialog mDialog;
    private ArrayList<URL> mThumbnailPhotos;
    private String mAcessToken;
    public static final String CHOOSEN_PHOTO="choosen_photo";
    private DisplayPhotoFragment(String accessToken){
        this.mAcessToken = accessToken;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);
        View view = inflater.inflate(R.layout.view_photo_fragment,container,false);
        mThumbnailPhotos = ImagesRepository.getPhotos(mAcessToken,"standard_resolution").getTumbnailBitmaps();
        mGridView = (GridView)view.findViewById(R.id.gridView);
        mGridView.setAdapter(new DisplayPhotoListAdapter());
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mDialog = ProgressDialog.show(getActivity(),"Photo is loading","Please,waiting while photo is loading");
                Intent intent = new Intent();
                intent.putExtra(CHOOSEN_PHOTO,i);
                getActivity().setResult(Activity.RESULT_OK,intent);
                mDialog.dismiss();
                getActivity().finish();
            }
        });
        return view;
    }


    private class DisplayPhotoListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mThumbnailPhotos.size();
        }
        @Override
        public Object getItem(int i) {return null;}
        @Override
        public long getItemId(int i) {return 0;}
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = new ImageView(getActivity());
                ImageDowloader dowloader =  new ImageDowloader();
                 dowloader.execute(mThumbnailPhotos.get(position));
                while (!(dowloader.isSuccess()));
                ((ImageView)convertView).setImageBitmap(dowloader.getmPhoto());
            }
            return convertView;
        }
    }
    public static DisplayPhotoFragment newInstance(String accessToken)
    {
        return  new DisplayPhotoFragment(accessToken);
    }
}