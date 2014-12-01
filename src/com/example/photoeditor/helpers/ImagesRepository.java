package com.example.photoeditor.helpers;

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
public class ImagesRepository extends AsyncTask<String,Void,Void> {
       private ArrayList<URL>mThumbnailBitmaps;
       private ArrayList<URL>mBitmaps;
       private boolean mSuccess;
       private static ImagesRepository mRepository;
       public static ImagesRepository getPhotos(String accessToken,String resolution)
       {
           if(mRepository==null)
           {
               mRepository = new ImagesRepository(resolution);
               mRepository.execute(accessToken,resolution);
               while (!(mRepository.ismSuccess()));
           }
           return  mRepository;
       }
       public boolean ismSuccess() {
        return mSuccess;
        }

       public ArrayList<URL> getmBitmaps() {
        return mBitmaps;
        }
       public ArrayList<URL> getTumbnailBitmaps(){return mThumbnailBitmaps;}
       private ImagesRepository(String resolution)
       {
           mBitmaps = new ArrayList<URL>();
           mThumbnailBitmaps = new ArrayList<URL>();
           mSuccess = false;
       }
       @Override
       protected Void doInBackground(String... params) {
           try
            {
            InputStream stream = new URL(Uris.PHOTO_GET+params[0]).openStream();
                String resultHtml = fromStreamToString(stream);
            JSONObject jsonObject = new JSONObject(resultHtml);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            for(int i =0;i<jsonArray.length();i++)
            {
                JSONObject object = jsonArray.getJSONObject(i).getJSONObject("images");
                String imageUrl = object.getJSONObject(params[1]).getString("url");
                mBitmaps.add(new URL(imageUrl));
                imageUrl = object.getJSONObject("thumbnail").getString("url");
                mThumbnailBitmaps.add(new URL(imageUrl));
            }
        }
        catch(MalformedURLException ex)
        {
            ex.printStackTrace();
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
        catch(JSONException ex)
        {
            ex.printStackTrace();
        }

        mSuccess = true;
        return null;
        }
    private String fromStreamToString(InputStream stream) throws IOException
    {
        BufferedReader reader = null;
        String resultHtml = "",readString="";
        try {
            reader = new BufferedReader(new InputStreamReader(stream));
            while ((readString = reader.readLine()) != null) {
                resultHtml += readString;
            }
        }finally {
            reader.close();
            stream.close();
        }
        return resultHtml;
    }
}
