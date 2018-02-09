package com.panzhyiev.fcmexample.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.Menu;

import java.io.IOException;
import java.io.InputStream;


public class Utils {

    public static void setToolbarIconsColor(Context context, Menu menu, int color){
        for(int i = 0; i < menu.size(); i++){
            Drawable drawable = menu.getItem(i).getIcon();
            if(drawable != null) {
                drawable.mutate();
                drawable.setTint(context.getResources().getColor(color));
            }
        }
    }

    public static Bitmap getBitmapFromCryptoIconsAssets(Context context, String fileName) {
        AssetManager assetManager = context.getAssets();

        InputStream istr;
        Bitmap bitmap = null;
        try {
            istr = assetManager.open("cryptoIcons/" + fileName + ".png");
            bitmap = BitmapFactory.decodeStream(istr);
        } catch (IOException e) {
            e.printStackTrace();
            // handle exception
        }

        return bitmap;
    }
}
