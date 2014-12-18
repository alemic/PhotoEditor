package com.example.photoeditor.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by Андрей on 16.11.2014.
 */
public class ImageDowloader extends AsyncTask<URL,Void,Void>
{
    private Bitmap mPhoto;
    public Bitmap getmPhoto() {
        return mPhoto;
    }

    public boolean isSuccess() {
        return isSuccess;
    }
    private boolean isSuccess;
    public ImageDowloader()
    {
        isSuccess = false;
    }
    @Override
    protected Void doInBackground(URL... urls) {
        try
        {
            URLConnection connection = urls[0].openConnection();
            if(CacheHelper.alreadyInCache(urls[0].toString()))
            {
                mPhoto = CacheHelper.getBitmap(urls[0].toString());
            }
            else {
                connection.connect();
                Bitmap photo = BitmapFactory.decodeStream(connection.getInputStream());
                mPhoto = photo;
                CacheHelper.addBitmap(photo,urls[0].toString());
            }
            isSuccess = true;
        }catch (IOException ex)
        {
            ex.printStackTrace();
        }
        return null;
    }
}


