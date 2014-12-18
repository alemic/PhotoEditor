package com.example.photoeditor.helpers;

import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.capricorn.ArcMenu;
import com.capricorn.RayMenu;

import java.awt.font.TextAttribute;
import java.io.*;

/**
 * Created by Андрей on 18.11.2014.
 */
public class Drawing {
    public static final int MAX_PHOTO_WIDTH = 800,MAX_PHOTO_HEIGHT = 1200,
    MAX_FUNNY_WIDTH = 64,MAX_FUNNY_HEIGHT = 64;
    public Bitmap putOverlay(Bitmap bitmap, Bitmap overlay,int top,int left) {
        Bitmap second,outBitmap;
        outBitmap = bitmap.copy(Bitmap.Config.ARGB_8888,true);
        second = overlay.copy(Bitmap.Config.ARGB_8888,true);
        Canvas canvas = new Canvas(outBitmap);
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(second,left,top,paint);
        return outBitmap;
    }
    public Bitmap setBitmapOpacity(Bitmap bitmap, int opacity)
    {
       int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap transBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(transBitmap);
        canvas.drawARGB(0, 0, 0,0);
        final Paint paint = new Paint();
        paint.setAlpha(opacity);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return transBitmap;
    }
    public boolean arcMenuInit(RayMenu menu, final Context context,View.OnClickListener[]listeners,int[]sources)
    {
      if(listeners.length!=sources.length) {
          return true;
      }
        for(int i =0;i<sources.length;i++)
        {
            ImageView view = new ImageView(context);
            view.setImageResource(sources[i]);
            menu.addItem(view,listeners[i]);
        }
        return true;
    }
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height;
            final int halfWidth = width;
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
    public static Bitmap decodeSampledBitmapFromFileDescriptor(FileDescriptor descriptor) {
        BitmapFactory.Options options = null;
        options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(descriptor, null, options);
        options.inSampleSize = calculateInSampleSize(options, MAX_PHOTO_WIDTH, MAX_PHOTO_HEIGHT);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFileDescriptor(descriptor,null,options);
    }
    public static Bitmap getPermissibleBitmap(InputStream bitmapStream,int reqWidth,int reqHeight)
    {
        Bitmap bitmap = BitmapFactory.decodeStream(bitmapStream);
        return getPermissibleBitmap(bitmap,reqWidth,reqHeight);
    }
    public static Bitmap getPermissibleBitmap(Bitmap bitmap,int reqWidth,int reqHeight)
    {
        if(bitmap.getHeight()>reqHeight&&bitmap.getWidth()>reqWidth)
        {
            bitmap = Bitmap.createScaledBitmap(bitmap,reqWidth,reqHeight,false);
        }
        else
        if(bitmap.getWidth()>reqWidth)
        {
            bitmap = Bitmap.createScaledBitmap(bitmap,reqWidth,bitmap.getHeight(),false);
        }
        else
        if(bitmap.getHeight()>reqHeight)
        {
            bitmap = Bitmap.createScaledBitmap(bitmap,bitmap.getWidth(),reqHeight,false);
        }
        return bitmap;
    }
    public static int getDifferent(int behind, int front)
    {
        return  behind>0?front-behind:Math.abs(behind)+front;
    }
}
