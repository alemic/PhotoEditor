package com.example.photoeditor.helpers;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import com.example.photoeditor.R;

import java.io.*;
import java.util.Random;

/**
 * Created by Андрей on 17.11.2014.
 */
public class ImageSaver {
    private String mCurrentPhotoPath;

    public static File saveBitmapToSDCard(Bitmap finalBitmap) throws IOException {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/cropped_images");
        myDir.mkdirs();
        int number = new Random().nextInt(100000);
        String name = "Image-" + number + ".jpg";
        File file = new File(myDir, name);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    public static void saveBitmapToGallary(Bitmap bitmap, ContentResolver resolver, String title, String description) {
        MediaStore.Images.Media.insertImage(resolver, bitmap, title + ".jpg", description);
    }

    public static byte[] compressImage(Bitmap bitmap, int compressSize) {
        if(compressSize>100)
        {
            throw new IllegalArgumentException("Compress size must be below then 100");
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,compressSize,stream);
        return stream.toByteArray();
    }
    public static Bitmap getBitmapFromByteArray(byte[]image)
    {
        Bitmap bitmap = BitmapFactory.decodeByteArray(image,0,image.length);
        return bitmap;
    }
    public static Intent getImageInstagramIntent(String type,String mediaPath,String caption)
    {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType(type);
        File photoFile = new File(mediaPath);
        Uri uri = Uri.fromFile(photoFile);
        intent.putExtra(Intent.EXTRA_STREAM,uri);
        intent.putExtra(Intent.EXTRA_TEXT,caption);
        return intent;
    }
    public static void saveImageInPrivateRepository(Context context,Bitmap image,String fileName)
    {
        try {
            OutputStream stream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            image.compress(Bitmap.CompressFormat.PNG,100,stream);
            stream.flush();
            stream.close();
        }
        catch (FileNotFoundException ex)
        {
            ex.printStackTrace();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }
    public static Bitmap getImageInPrivateRepository(Context context,String fileName)
    {
        InputStream stream = null;
        try
        {
            stream =  context.openFileInput(fileName);
        }catch (FileNotFoundException ex)
        {
            ex.printStackTrace();
        }
        return BitmapFactory.decodeStream(stream);
    }
    public static void saveCustomFunnyThings(Bitmap thing,Context context,String folderName)
    {
        File imagesFile = new File(context.getFilesDir(),folderName);
        if(!(imagesFile.exists()))
        {
            imagesFile.mkdir();
        }
        try {
            File anotherFile = new File(imagesFile,Integer.toString(thing.hashCode())+".jpg");
            FileOutputStream stream = new FileOutputStream(anotherFile);
            thing.compress(Bitmap.CompressFormat.PNG,100,stream);
            stream.flush();
            stream.close();
        }catch (FileNotFoundException ex)
        {
            ex.printStackTrace();
        }catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }
}

