package com.advantech.utk3000_glasshouse_checkout.ui;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;

public class ImageUtil {
    public static Bitmap loadImageFromAssets(Context context, String fileName) {
        try {
            AssetManager assetManager = context.getAssets();
            InputStream is = assetManager.open(fileName);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            is.close();
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
