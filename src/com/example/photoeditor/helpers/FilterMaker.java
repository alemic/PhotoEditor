package com.example.photoeditor.helpers;

import Catalano.Imaging.FastBitmap;
import Catalano.Imaging.Filters.*;
import Catalano.Imaging.Filters.Artistic.FilmGrain;
import android.graphics.Bitmap;

/**
 * Created by Андрей on 29.11.2014.
 */
public class FilterMaker {
    private static final int FILTER_COUNT = Filters.values().length;
    public int getFilterCount()
    {
        Filters filter = Filters.Sepia;
        return FILTER_COUNT;
    }
    public Bitmap getFilter(Bitmap bitmap,Filters filter)
    {
        FastBitmap fastBitmap = new FastBitmap(bitmap);
        switch (filter)
        {
            case Sepia:
                new Sepia().applyInPlace(fastBitmap);
                break;
            case HighBoost:
                new HighBoost().applyInPlace(fastBitmap);
                break;
            case Emboss:
                new Emboss().applyInPlace(fastBitmap);
                break;
            case Erosion:
                new Erosion().applyInPlace(fastBitmap);
                break;
            case GammaCorrection:
                new GammaCorrection().applyInPlace(fastBitmap);
                break;
            case Desaturation:
                new Desaturation().applyInPlace(fastBitmap);
                break;
            case Dilatation:
                new Dilatation().applyInPlace(fastBitmap);
                break;
            case Grayscale:
                new Grayscale().applyInPlace(fastBitmap);
                break;
            case Invert:
                new Invert().applyInPlace(fastBitmap);
                break;
            case Mirror:
                new Mirror(true,false).applyInPlace(fastBitmap);
                break;
        }
        return fastBitmap.toBitmap();
    }
}
