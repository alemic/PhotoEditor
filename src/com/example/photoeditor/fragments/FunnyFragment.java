package com.example.photoeditor.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.capricorn.ArcMenu;
import com.capricorn.RayMenu;
import com.example.photoeditor.FunnyActivity;
import com.example.photoeditor.R;
import com.example.photoeditor.StartActivity;
import com.example.photoeditor.helpers.Drawing;
import com.example.photoeditor.helpers.ImageSaver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.SelectionKey;

/**
 * Created by Андрей on 03.12.2014.
 */
public class FunnyFragment extends Fragment {
    private FunnyFragment(){}
    private Bitmap mPhoto,mBufferedPhoto,mOverlayPhoto,mCustomFunnyThing;
    private ImageView mPhotoImageView,mOverlayImageView,mDrawThingImageView;
    private RayMenu mOpenThingsArcMenu;
    private FrameLayout.LayoutParams mParams;
    public static final String PHOTO = "photo",DIALOG_RESULT = "dialog_result",TAG="dialog_fragment";
    public static final int DIALOG = 0;
    private static final int DEFAULT_SEEK_BAR_PROGRESS=10,PADDING_VALUE=50;
    private SeekBar mSeekBarSizeChange;
    private float mScaleValue = 0,mDisplayedWidth=0,mDisplayedHeight = 0;
    public static final String mFolderWithCustomImages = "Customs";
    private FrameLayout.LayoutParams mImageViewParams;
    private FrameLayout mContainer;
    private Animation.AnimationListener mListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            mContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.funny_animation_second));
            FrameLayout.LayoutParams mOverlayParams = (FrameLayout.LayoutParams)mOverlayImageView.getLayoutParams();
            int left = Drawing.getDifferent(mImageViewParams.leftMargin,mOverlayParams.leftMargin);
            int top = Drawing.getDifferent(mImageViewParams.topMargin,mOverlayParams.topMargin);
            mOverlayPhoto = Bitmap.createScaledBitmap(mOverlayPhoto,(int)(mOverlayPhoto.getWidth()*mScaleValue),
                    (int)(mOverlayPhoto.getHeight()*mScaleValue),false);
            mBufferedPhoto = new Drawing().putOverlay(mBufferedPhoto, mOverlayPhoto, top,left);
            mPhotoImageView.setImageBitmap(mBufferedPhoto);
            mParams.leftMargin = 0;
            mParams.topMargin = 0;
            mOverlayImageView.setLayoutParams(mParams);
            mDrawThingImageView.setVisibility(View.INVISIBLE);
            mOverlayImageView.setImageBitmap(null);
            mSeekBarSizeChange.setEnabled(false);
            mOverlayImageView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mPhoto = getArguments().getParcelable(PHOTO);
        mBufferedPhoto = mPhoto.copy(Bitmap.Config.ARGB_8888,true);
        View view = inflater.inflate(R.layout.fragment_funny,container,false);
        mPhotoImageView = (ImageView)view.findViewById(R.id.photoFunnyImageView);
        mPhotoImageView.setImageBitmap(mPhoto);
        mOverlayImageView = (ImageView)view.findViewById(R.id.overlayFunnyImageView);
        mDrawThingImageView = (ImageView)view.findViewById(R.id.imageViewDrawThing);
        mParams = (FrameLayout.LayoutParams)mOverlayImageView.getLayoutParams();
        mOpenThingsArcMenu = (RayMenu)view.findViewById(R.id.openThingsArcMenu);
        mSeekBarSizeChange = (SeekBar)view.findViewById(R.id.seekBarSizeChange);
        mSeekBarSizeChange.setEnabled(false);
        mContainer = (FrameLayout)view.findViewById(R.id.container);
        mImageViewParams = (FrameLayout.LayoutParams) mPhotoImageView.getLayoutParams();
        bindHandlers();
        arcMenuInit();
        resizeToContent();
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
                mOverlayImageView.setVisibility(View.INVISIBLE);
                Animation funnyAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.funny_animation);
                funnyAnimation.setAnimationListener(mListener);
                mContainer.startAnimation(funnyAnimation);
                mDrawThingImageView.startAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.button_animation));
            }
        });
        mSeekBarSizeChange.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                mScaleValue = progress/10f;
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
        mPhotoImageView.setOnTouchListener(new View.OnTouchListener() {
        private float startX = 0,startY = 0;
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mDisplayedWidth = mDisplayedWidth==0?mPhotoImageView.getWidth():mDisplayedWidth;
            mDisplayedHeight = mDisplayedHeight==0?mPhotoImageView.getHeight():mDisplayedHeight;
            switch (motionEvent.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    startX = motionEvent.getX();
                    startY = motionEvent.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float left = (motionEvent.getX()-startX) * 0.3f;
                    float top = (motionEvent.getY()-startY) * 0.3f;
                    float summaryWidth = (Math.abs(mImageViewParams.leftMargin)-left+mDisplayedWidth);
                    float summaryHeight = (Math.abs(mImageViewParams.topMargin)-top+mDisplayedHeight);
                    if(mImageViewParams.leftMargin+left<=0&&summaryWidth<mPhoto.getWidth()) {
                        mImageViewParams.leftMargin += left;
                    }
                    if((mImageViewParams.topMargin+top)<=0&&summaryHeight<mPhoto.getHeight()) {
                        mImageViewParams.topMargin += top;
                    }
                    mPhotoImageView.setLayoutParams(mImageViewParams);
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
            return true;
        }});
    }
    public static FunnyFragment newInstance(Bitmap photo)
    {
        Bundle args = new Bundle();
        args.putParcelable(PHOTO,photo);
        FunnyFragment fragment = new FunnyFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode!= Activity.RESULT_OK) {
            return;
        }
       switch (requestCode) {
           case DIALOG:
               mOverlayPhoto = (Bitmap) data.getParcelableExtra(DIALOG_RESULT);
               mOverlayImageView.setImageBitmap(mOverlayPhoto);
               mSeekBarSizeChange.setProgress(DEFAULT_SEEK_BAR_PROGRESS);
               mSeekBarSizeChange.setEnabled(true);
               break;
           case StartActivity.GALLERY:
               try {
                   mCustomFunnyThing = BitmapFactory.decodeStream(getActivity()
                           .getContentResolver().openInputStream(data.getData()));
                   mCustomFunnyThing = Drawing.getPermissibleBitmap(mCustomFunnyThing,Drawing.MAX_FUNNY_WIDTH,Drawing.MAX_FUNNY_HEIGHT);
                   ImageSaver.saveCustomFunnyThings(mCustomFunnyThing,getActivity(),mFolderWithCustomImages);
               } catch (FileNotFoundException ex)
               {
                   ex.printStackTrace();
               }
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
        ImageView downloadImageView = new ImageView(getActivity());
        downloadImageView.setImageResource(R.drawable.download);
        ImageView agreeImageView = new ImageView(getActivity());
        agreeImageView.setImageResource(R.drawable.agree_funny);
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
        mOpenThingsArcMenu.addItem(downloadImageView, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galaryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galaryIntent.setType("image/*");
                startActivityForResult(galaryIntent, StartActivity.GALLERY);
            }
        });
        mOpenThingsArcMenu.addItem(agreeImageView, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                ImageSaver.saveImageInPrivateRepository(getActivity(),mBufferedPhoto,StartActivity.IMAGE_FILE_NAME);
                intent.putExtra(FunnyActivity.PHOTO_WITH_FUNNY,StartActivity.IMAGE_FILE_NAME);
                getActivity().setResult(Activity.RESULT_OK,intent);
                getActivity().finish();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        File file = new File(getActivity().getFilesDir(),mFolderWithCustomImages);
        if(file.exists()) {
            File[] images = file.listFiles();
            for (File image : images)
            {
                image.delete();
            }
            file.delete();
        }
    }
    private void resizeToContent()
    {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)mContainer.getLayoutParams();
        params.height = (mPhoto.getHeight()+PADDING_VALUE);
        params.width = (mPhoto.getWidth()+PADDING_VALUE);
        mContainer.setLayoutParams(params);
    }
}
