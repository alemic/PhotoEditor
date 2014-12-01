package com.example.photoeditor.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.capricorn.ArcMenu;
import com.capricorn.RayMenu;

/**
 * Created by Андрей on 18.11.2014.
 */
public class Drawing {
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
}
