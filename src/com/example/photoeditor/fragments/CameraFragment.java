package com.example.photoeditor.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.example.photoeditor.CameraActivity;
import com.example.photoeditor.R;
import com.example.photoeditor.StartActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Андрей on 25.11.2014.
 */
@SuppressWarnings("deprecation")
public class CameraFragment extends Fragment implements SurfaceHolder.Callback {
    private static final String TAG = "camera dialog";
    public static final int CAMERA_DIALOG_REQUEST=0;
    private Camera mCamera;
    private SurfaceView mSurfaceView;
    private Button mTakeButton;
    private FrameLayout mProgressContainer;
    private byte[] mPhotoArray;
    private SurfaceHolder mHolder;
    private Camera.ShutterCallback mShutter = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {
            mProgressContainer.setVisibility(View.VISIBLE);
        }
    };
    private Camera.PictureCallback mJpegCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {
              Intent intent = new Intent();
              intent.putExtra(CameraActivity.RESULT_PHOTO_FROM_CAMERA,bytes);
              getActivity().setResult(Activity.RESULT_OK,intent);
            mPhotoArray = bytes;
            CameraDialogFragment fragment = CameraDialogFragment.newInstance(mPhotoArray);
            fragment.setTargetFragment(CameraFragment.this,CAMERA_DIALOG_REQUEST);
            fragment.show(getActivity().getSupportFragmentManager(),TAG);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.camera_fragment,container,false);
        mSurfaceView = (SurfaceView)view.findViewById(R.id.surfaceView);
        mHolder = mSurfaceView.getHolder();
        mTakeButton = (Button)view.findViewById(R.id.takeButton);
        mProgressContainer = (FrameLayout)view.findViewById(R.id.progress_container);
        mProgressContainer.setVisibility(View.INVISIBLE);
        final SurfaceHolder holder = mSurfaceView.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mTakeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mCamera.takePicture(mShutter, null, mJpegCallback);
                }catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        });
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        try {
            mHolder.addCallback(this);
            mCamera = Camera.open(0);
        }catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        if(mCamera!=null)
        {
            mHolder.removeCallback(this);
            mCamera.release();
            mCamera = null;
        }
    }
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try
        {
            if(mCamera!=null)
            {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();
            }
        }catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        if(mCamera==null)return;

        Camera.Parameters parameters = mCamera.getParameters();
        Camera.Size size = getBestSize(parameters.getSupportedPreviewSizes());
        parameters.setPreviewSize(size.width,size.height);
        mCamera.setParameters(parameters);
        try {
            mCamera.startPreview();
        }catch (Exception ex)
        {
            ex.printStackTrace();
            if(mCamera!=null)
            {
                mCamera.release();
                mCamera = null;
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if(mCamera!=null)
        {
            mCamera.stopPreview();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode)
        {
            case CAMERA_DIALOG_REQUEST:
                if(resultCode==Activity.RESULT_OK)
                {
                    Intent intent = new Intent();
                    intent.putExtra(CameraActivity.RESULT_PHOTO_FROM_CAMERA,mPhotoArray);
                    getActivity().setResult(Activity.RESULT_OK,intent);
                    getActivity().finish();
                }
                else
                {
                    mProgressContainer.setVisibility(View.INVISIBLE);
                    mSurfaceView.refreshDrawableState();
                    mCamera.startPreview();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }
    private Camera.Size getBestSize(List<Camera.Size> sizes)
    {
        Camera.Size bestSize = sizes.get(0);
        int lagestArea = bestSize.width*bestSize.height;
        for(Camera.Size size:sizes)
        {
            int area = size.width*size.height;
            if(area>lagestArea)
            {
                lagestArea = area;
                bestSize = size;
            }
        }
        return bestSize;
    }
}
