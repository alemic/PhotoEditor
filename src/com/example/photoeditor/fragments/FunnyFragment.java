package com.example.photoeditor.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.capricorn.ArcMenu;
import com.capricorn.RayMenu;
import com.example.photoeditor.R;
import com.example.photoeditor.StartActivity;
import com.example.photoeditor.helpers.Drawing;
import com.example.photoeditor.helpers.ImageSaver;

import java.nio.channels.SelectionKey;

/**
 * Created by Андрей on 03.12.2014.
 */
public class FunnyFragment extends Fragment {
    private FunnyFragment(){}
    private Bitmap mPhoto,mBufferedPhoto,mOverlayPhoto;
    private ImageView mPhotoImageView,mOverlayImageView,mDrawThingImageView,mAgreeImageView;
    private RayMenu mOpenThingsArcMenu;
    private FrameLayout.LayoutParams mParams;
    public static final String PHOTO = "photo",DIALOG_RESULT = "dialog_result",TAG="dialog_fragment";
    public static final int DIALOG = 0;
    private static final int DEFAULT_SEEK_BAR_PROGRESS=10,PADDING_VALUE=25;
    private SeekBar mSeekBarSizeChange;
    private float mScaleValue = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mPhoto = ImageSaver.getBitmapFromByteArray(getArguments().getByteArray(PHOTO));
        mBufferedPhoto = mPhoto.copy(Bitmap.Config.ARGB_8888,true);
        View view = inflater.inflate(R.layout.fragment_funny,container,false);
        mPhotoImageView = (ImageView)view.findViewById(R.id.photoFunnyImageView);
        mPhotoImageView.setImageBitmap(mPhoto);
        mOverlayImageView = (ImageView)view.findViewById(R.id.overlayFunnyImageView);
        mDrawThingImageView = (ImageView)view.findViewById(R.id.imageViewDrawThing);
        mParams = (FrameLayout.LayoutParams)mOverlayImageView.getLayoutParams();
        mOpenThingsArcMenu = (RayMenu)view.findViewById(R.id.openThingsArcMenu);
        mAgreeImageView = (ImageView)view.findViewById(R.id.agreeImageView);
        mSeekBarSizeChange = (SeekBar)view.findViewById(R.id.seekBarSizeChange);
        mSeekBarSizeChange.setEnabled(false);
        bindHandlers();
        arcMenuInit();
        return view;
    }
    private void bindHandlers()
    {
        mOverlayImageView.setOnTouchListener(new View.OnTouchListener() {
            private float startX = 0,startY = 0;
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        mDrawThingImageView.setVisibility(View.INVISIBLE);
                        mParams = (FrameLayout.LayoutParams) mOverlayImageView.getLayoutParams();
                        startX = motionEvent.getX();
                        startX = motionEvent.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (mParams.leftMargin >= 0 && mParams.leftMargin <
                                (mPhotoImageView.getWidth()-(mOverlayPhoto.getWidth()*mScaleValue))) {
                            mParams.leftMargin += (motionEvent.getX() - startX) * 0.3f;
                        } else {
                            mParams.leftMargin = 0;
                        }
                        if (mParams.topMargin >= -mOverlayPhoto.getWidth() && mParams.topMargin <
                                (mPhotoImageView.getHeight()-(mOverlayPhoto.getHeight()*mScaleValue))) {
                            mParams.topMargin += (motionEvent.getY() - startY) * 0.3f;
                        } else
                        {
                            mParams.topMargin = 0;
                        }
                        mOverlayImageView.setLayoutParams(mParams);
                    break;
                    case MotionEvent.ACTION_UP:
                        mDrawThingImageView.setVisibility(View.VISIBLE);
                        break;
                }
                return true;
            }
        });
        mDrawThingImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawThingImageView.startAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.button_animation));
                mOverlayPhoto = Bitmap.createScaledBitmap(mOverlayPhoto,(int)(mOverlayPhoto.getWidth()*mScaleValue),
                        (int)(mOverlayPhoto.getHeight()*mScaleValue),false);
                mBufferedPhoto = new Drawing().putOverlay(mBufferedPhoto, mOverlayPhoto,
                        (mParams.topMargin-PADDING_VALUE),(mParams.leftMargin-PADDING_VALUE));
                mPhotoImageView.setImageBitmap(mBufferedPhoto);
                mParams.leftMargin = 0;
                mParams.topMargin = 0;
                mOverlayImageView.setLayoutParams(mParams);
                mDrawThingImageView.setVisibility(View.INVISIBLE);
                mOverlayImageView.setImageBitmap(null);
                mSeekBarSizeChange.setEnabled(false);
            }
        });
        mAgreeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAgreeImageView.startAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.button_animation));
                byte [] image = ImageSaver.compressImage(mBufferedPhoto,90);
                Intent intent = new Intent();
                intent.putExtra(StartActivity.CHOOSEN_PHOTO,image);
                getActivity().setResult(Activity.RESULT_OK,intent);
                getActivity().finish();
            }
        });
        mSeekBarSizeChange.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                mScaleValue = (float)(progress/10f);
                Matrix matrix = mOverlayImageView.getImageMatrix();
                matrix.setScale(mScaleValue,mScaleValue);
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)mOverlayImageView.getLayoutParams();
                params.height=(int)(mOverlayPhoto .getHeight()*mScaleValue);
                params.width=(int)(mOverlayPhoto .getWidth()*mScaleValue);
                mOverlayImageView.setImageMatrix(matrix);
                mOverlayImageView.setLayoutParams(params);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
    public static FunnyFragment newInstance(byte[] photo)
    {
        Bundle args = new Bundle();
        args.putByteArray(PHOTO,photo);
        FunnyFragment fragment = new FunnyFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode!= Activity.RESULT_OK) {
            return;
        }
       switch (requestCode)
       {
           case DIALOG:
               mOverlayPhoto = (Bitmap)data.getParcelableExtra(DIALOG_RESULT);
               mOverlayImageView.setImageBitmap(mOverlayPhoto);
               mSeekBarSizeChange.setProgress(DEFAULT_SEEK_BAR_PROGRESS);
               mSeekBarSizeChange.setEnabled(true);
               break;
           default:
               super.onActivityResult(requestCode,resultCode,data);
               break;
       }
    }
    private void arcMenuInit()
    {
        ImageView openMenuImageView = new ImageView(getActivity());
        openMenuImageView.setImageResource(R.drawable.funny);
        ImageView exitImageView = new ImageView(getActivity());
        exitImageView.setImageResource(R.drawable.exit);
        ImageView deleteImageView = new ImageView(getActivity());
        deleteImageView.setImageResource(R.drawable.delete);
        mOpenThingsArcMenu.addItem(openMenuImageView, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FunnyThingsDialogFragment dialogFragment = new FunnyThingsDialogFragment();
                dialogFragment.setTargetFragment(FunnyFragment.this,DIALOG);
                dialogFragment.show(getActivity().getSupportFragmentManager(),TAG);
            }
        });
        mOpenThingsArcMenu.addItem(exitImageView, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().setResult(Activity.RESULT_CANCELED);
                getActivity().finish();
            }
        });
        mOpenThingsArcMenu.addItem(deleteImageView, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPhotoImageView.setImageBitmap(mPhoto);
                mBufferedPhoto = mPhoto.copy(Bitmap.Config.ARGB_8888,true);
            }
        });
    }
}
