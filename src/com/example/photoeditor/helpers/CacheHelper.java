package com.example.photoeditor.helpers;

import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * Created by Андрей on 03.12.2014.
 */
public class CacheHelper {
    private static LruCache<String,Bitmap> mCache;
    private static final int mMaxMemory = (int)(Runtime.getRuntime().maxMemory());
    private static final int mCaheSize = mMaxMemory/20;
    static
    {
        mCache = new LruCache<String, Bitmap>(mCaheSize)
        {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }
    public static void addBitmap(Bitmap bitmap,String key)
    {
        if(getBitmap(key)==null)
        {
            mCache.put(key,bitmap);
        }
    }
    public static Bitmap getBitmap(String key)
    {
        return mCache.get(key);
    }
    public static boolean alreadyInCache(String key)
    {
        return mCache.get(key)!=null;
    }
}
