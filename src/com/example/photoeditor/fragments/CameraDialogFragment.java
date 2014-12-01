package com.example.photoeditor.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.photoeditor.R;

/**
 * Created by Андрей on 25.11.2014.
 */
public class CameraDialogFragment extends DialogFragment {
    private Bitmap mPhoto;
    private static final String PHOTO= "photo";
    private CameraDialogFragment()
    {}
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_photo,null);
        Bitmap bitmap = (Bitmap)(getArguments().getParcelable(PHOTO));
        ImageView imageView = (ImageView)dialogView.findViewById(R.id.dialog_imageView);
        imageView.setImageBitmap(bitmap);
        return new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.dialog_title))
                .setView(dialogView)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getTargetFragment().onActivityResult(CameraFragment.CAMERA_DIALOG_REQUEST, Activity.RESULT_OK,null);
                    }
                }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getTargetFragment().onActivityResult(CameraFragment.CAMERA_DIALOG_REQUEST,Activity.RESULT_CANCELED,null);
                    }
                }).create();
}
    public static CameraDialogFragment newInstance(byte[] photoArray)
    {
        Bitmap photo = BitmapFactory.decodeByteArray(photoArray,0,photoArray.length);
        Bundle args = new Bundle();
        args.putParcelable(PHOTO,photo);
        CameraDialogFragment fragment = new CameraDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
