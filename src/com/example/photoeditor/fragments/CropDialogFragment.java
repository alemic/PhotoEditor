package com.example.photoeditor.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.ToggleButton;
import com.edmodo.cropper.CropImageView;
import com.edmodo.cropper.cropwindow.CropOverlayView;
import com.example.photoeditor.R;
import com.example.photoeditor.StartActivity;
import com.example.photoeditor.helpers.CropResults;
import com.example.photoeditor.helpers.ImageSaver;
import com.example.photoeditor.interfaces.CropResultListener;

/**
 * Created by Андрей on 12.12.2014.
 */
public class CropDialogFragment extends DialogFragment {
    private CropImageView mCropImageView;
    private Bitmap mPhoto;
    private CropOverlayView mCropOverlayView;
    public static final String IMAGE_TO_CROP = "crop_image",CROPPED_IMAGE = "cropped_image";
    private static final int ROTATE_VALUE = 90;
    private ImageView mRotateImageView;
    private CropDialogFragment(){}
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String path = getArguments().getString(IMAGE_TO_CROP);
        mPhoto = ImageSaver.getImageInPrivateRepository(getActivity(),path);
        View view = getActivity().getLayoutInflater().inflate(R.layout.crop_fragment,null);
        mCropImageView = (CropImageView)view.findViewById(R.id.cropPhotoImageView);
        mCropImageView.setImageBitmap(mPhoto);
        mCropOverlayView = mCropImageView.getmCropOverlayView();
        mRotateImageView = (ImageView)view.findViewById(R.id.imageViewRotate);
        bindHandlers();
        return new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.crop_dialog_fragment))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent data = new Intent();
                        ImageSaver.saveImageInPrivateRepository(getActivity(),
                                mCropImageView.getOriginalBitmap(),StartActivity.IMAGE_FILE_NAME);
                        data.putExtra(CROPPED_IMAGE, StartActivity.IMAGE_FILE_NAME);
                        ((CropResultListener)getActivity()).onCropResult(CropResults.Ok,data);
                        dismiss();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ((CropResultListener)getActivity()).onCropResult(CropResults.Cancel,null);
                        dismiss();
                    }
                })
                .setView(view).create();
    }
    private void bindHandlers()
    {
        mRotateImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCropImageView.getCutImageView().setVisibility(View.INVISIBLE);
                mRotateImageView.startAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.rotate_button_animation));
                mCropImageView.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.rotate));
                mCropImageView.rotateImage(ROTATE_VALUE);
            }
        });
    }
    public static CropDialogFragment newInstance(String imagePath) {
        Bundle args = new Bundle();
        args.putString(IMAGE_TO_CROP,imagePath);
        CropDialogFragment fragment = new CropDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
