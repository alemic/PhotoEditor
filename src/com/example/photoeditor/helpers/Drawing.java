package com.example.photoeditor.helpers;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.capricorn.ArcMenu;
import com.capricorn.RayMenu;

import java.awt.font.TextAttribute;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

/**
 * Created by Андрей on 18.11.2014.
 */
public class Drawing {
    public static final int MAX_PHOTO_WIDTH = 600,MAX_PHOTO_HEIGHT = 800;
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
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
    private static Bitmap decodeSampledBitmapFromStream(InputStream stream, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        stream.mark(0);
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(stream,null,options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        try {
            stream.reset();
        }catch (IOException ex)
        {
            ex.printStackTrace();
        }
        return BitmapFactory.decodeStream(stream,null,options);
    }
    public static Bitmap getPermissibleBitmap(InputStream bitmapStream)
    {
        Bitmap bitmap = BitmapFactory.decodeStream(bitmapStream);
        return getPermissibleBitmap(bitmap);
    }
    public static Bitmap getPermissibleBitmap(Bitmap bitmap)
    {
        if(bitmap.getHeight()>MAX_PHOTO_HEIGHT&&bitmap.getWidth()>MAX_PHOTO_WIDTH)
        {
            bitmap = Bitmap.createScaledBitmap(bitmap,MAX_PHOTO_WIDTH,MAX_PHOTO_HEIGHT,false);
        }
        else
        if(bitmap.getWidth()>MAX_PHOTO_WIDTH)
        {
            bitmap = Bitmap.createScaledBitmap(bitmap,MAX_PHOTO_WIDTH,bitmap.getHeight(),false);
        }
        else
        if(bitmap.getHeight()>MAX_PHOTO_HEIGHT)
        {
            bitmap = Bitmap.createScaledBitmap(bitmap,bitmap.getWidth(),MAX_PHOTO_HEIGHT,false);
        }
        return bitmap;
    }
    public static Bitmap sizeToBehindBitmap(Bitmap targetBitmap,int targetSize,boolean isVertical)
    {
        float different,percent;
        if(isVertical) {
            if(targetBitmap.getHeight()== targetSize)
            {
               return targetBitmap;
            }
                 different = targetSize - targetBitmap.getHeight();
                 percent = different / targetBitmap.getHeight();
                float targetWidth = targetBitmap.getWidth() * percent;
                return Bitmap.createScaledBitmap(targetBitmap, (int) (targetBitmap.getWidth() + targetWidth),targetSize, false);
        }
        else
        {
            if(targetBitmap.getWidth()==targetSize)
            {
                return targetBitmap;
            }
                different = targetSize - targetBitmap.getWidth();
                percent = different/targetBitmap.getHeight();
                float targetHeight = targetBitmap.getHeight()*percent;
                return Bitmap.createScaledBitmap(targetBitmap,targetSize,(int)(targetBitmap.getHeight()+targetHeight),false);
        }

    }
}
