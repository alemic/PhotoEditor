package com.example.photoeditor.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.example.photoeditor.FilterActivity;
import com.example.photoeditor.R;
import com.example.photoeditor.StartActivity;
import com.example.photoeditor.helpers.ImageSaver;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Андрей on 27.11.2014.
 */
public class FilterFragment extends Fragment {
    private FilterFragment(){}
    private static final String PHOTO = "photo";
    private ImageView mAgreeImage,mPhoto;
    private Bitmap mPhotoFromEditor;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);
        Bundle args = getArguments();
        mPhotoFromEditor = args.getParcelable(PHOTO);
        View view = inflater.inflate(R.layout.filter_fragmnet,container,false);
        mPhoto = (ImageView)view.findViewById(R.id.photo);
        mPhoto.setImageBitmap(mPhotoFromEditor);
        mAgreeImage = (ImageView)view.findViewById(R.id.imageButtonAgree);
        mAgreeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAgreeImage.startAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.button_animation));
                Intent intent = new Intent();
                ImageSaver.saveImageInPrivateRepository(getActivity(),mPhotoFromEditor,StartActivity.IMAGE_FILE_NAME);
                intent.putExtra(FilterActivity.PHOTO_WITH_EFFECT, StartActivity.IMAGE_FILE_NAME);
                getActivity().setResult(Activity.RESULT_OK,intent);
                getActivity().finish();
            }
        });
        return view;
    }
    public static FilterFragment newInstance(Bitmap photo)
    {
        Bundle args = new Bundle();
        args.putParcelable(PHOTO,photo);
        FilterFragment fragment = new FilterFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
