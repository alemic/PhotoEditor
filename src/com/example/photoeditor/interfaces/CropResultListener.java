package com.example.photoeditor.interfaces;

import android.content.Intent;
import com.example.photoeditor.helpers.CropResults;

/**
 * Created by Андрей on 12.12.2014.
 */
public interface CropResultListener {
     void onCropResult(CropResults result,Intent data);
}
