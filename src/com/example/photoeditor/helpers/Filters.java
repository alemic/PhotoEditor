package com.example.photoeditor.helpers;
/**
 * Created by Андрей on 29.11.2014.
 */
public enum Filters {

    Sepia,HighBoost,Emboss,Erosion,GammaCorrection,Desaturation,Dilatation,Grayscale,Invert,Mirror;
    public static Filters fromInt(int value)
    {
        return Filters.values()[value];
    }
}
