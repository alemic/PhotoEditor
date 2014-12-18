package com.example.photoeditor.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.example.photoeditor.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Андрей on 04.12.2014.
 */
public class FunnyThingsDialogFragment extends DialogFragment {
    private GridView mGridView;
    private ArrayList<Bitmap>mPictures = new ArrayList<Bitmap>();
    private int [] mFunnyPictures = {R.drawable.lips,R.drawable.mouthshate,R.drawable.eye,
    R.drawable.eric_cartman,R.drawable.santa_hat,R.drawable.glasses};
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.funny_things_dialog_fragment,null);
        mGridView = (GridView)view.findViewById(R.id.gridViewThings);
        mGridView.setAdapter(new FunnyThingsAdapter());
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent();
                intent.putExtra(FunnyFragment.DIALOG_RESULT,mPictures.get(i));
                getTargetFragment().onActivityResult(FunnyFragment.DIALOG,Activity.RESULT_OK,intent);
                dismiss();
            }
        });
        for(int image:mFunnyPictures)
        {
            mPictures.add(BitmapFactory.decodeResource(getResources(),image));
        }
        loadCustomImages();
        return new AlertDialog.Builder(getActivity()).setView(view).create();
    }
    private class FunnyThingsAdapter extends BaseAdapter
    {
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            Bitmap bitmap = mPictures.get(i);
            ImageView imageView = new ImageView(getActivity());
            imageView.setImageBitmap(bitmap);
            return imageView;
        }
        @Override
        public int getCount() {
            return mPictures.size();
        }

        @Override
        public Object getItem(int i) {
            return mPictures.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }
    }
    private void loadCustomImages()
    {
        FileInputStream stream = null;
        File folder = new File(getActivity().getFilesDir(),FunnyFragment.mFolderWithCustomImages);
        if(!folder.exists())
        {
            return;
        }
        File [] images = folder.listFiles();
        for(File image : images) {
            try {
                stream = new FileInputStream(image);
                mPictures.add(BitmapFactory.decodeStream(stream));
                stream.close();
            } catch (FileNotFoundException ex)
            {
                ex.printStackTrace();
            }catch (IOException ex)
            {
                ex.printStackTrace();
            }
        }
    }
}
