package com.example.photoeditor.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.capricorn.ArcMenu;
import com.example.photoeditor.R;
import com.example.photoeditor.helpers.ImageSaver;

import java.io.IOException;

/**
 * Created by Андрей on 01.12.2014.
 */
public class SaveDialogFragment extends DialogFragment {
    private SaveDialogFragment(){}
    private ArcMenu mArcMenu;
    private Bitmap mPhoto;
    private static final String RESULT_PHOTO_TO_SAVE="result_save_photo";
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mPhoto = getArguments().getParcelable(RESULT_PHOTO_TO_SAVE);
        View view  = getActivity().getLayoutInflater().inflate(R.layout.save_dialog,null);
        mArcMenu = (ArcMenu)view.findViewById(R.id.arc_menu);
        arcMenuInit();
        return new AlertDialog.Builder(getActivity()).setView(view)
                .setTitle(getString(R.string.save_dialog_title))
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismiss();
                    }
                })
                .create();
    }
    private void arcMenuInit()
    {
        ImageView imageInstagram = new ImageView(getActivity());
        imageInstagram.setImageResource(R.drawable.inst);
        ImageView imageGalary = new ImageView(getActivity());
        imageGalary.setImageResource(android.R.drawable.ic_menu_gallery);
        mArcMenu.addItem(imageGalary, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String path = ImageSaver.saveBitmapToSDCard(mPhoto).getPath();
                    Toast.makeText(getActivity(), "Your photo has just been saved into - "
                            + path, Toast.LENGTH_LONG).show();
                }catch (IOException ex)
                {
                    ex.printStackTrace();
                }
            }
        });
        mArcMenu.addItem(imageInstagram, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String path = ImageSaver.saveBitmapToSDCard(mPhoto).getPath();
                    Intent intent = ImageSaver.getImageInstagramIntent("image/*",path,"Adriana Lima");
                    startActivity(intent);
                }catch (IOException ex)
                {
                    ex.printStackTrace();
                }
                dismiss();
            }
        });
    }
    public static SaveDialogFragment newInstance(Bitmap resultPhoto)
    {
        Bundle args = new Bundle();
        args.putParcelable(RESULT_PHOTO_TO_SAVE,resultPhoto);
        SaveDialogFragment fragment = new SaveDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
