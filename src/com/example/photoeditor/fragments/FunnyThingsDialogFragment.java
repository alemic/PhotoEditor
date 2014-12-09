package com.example.photoeditor.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.example.photoeditor.R;

import java.util.ArrayList;

/**
 * Created by Андрей on 04.12.2014.
 */
public class FunnyThingsDialogFragment extends DialogFragment {
    private GridView mGridView;
    private ArrayList<Bitmap>mPictures = new ArrayList<Bitmap>();
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mPictures.add(BitmapFactory.decodeResource(getResources(),R.drawable.mouthshate));
        mPictures.add(BitmapFactory.decodeResource(getResources(),R.drawable.clownose));
        mPictures.add(BitmapFactory.decodeResource(getResources(),R.drawable.black_hat));
        mPictures.add(BitmapFactory.decodeResource(getResources(),R.drawable.santa_hat_with_eyes));
        mPictures.add(BitmapFactory.decodeResource(getResources(),R.drawable.classic_balck_hat));
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
}
