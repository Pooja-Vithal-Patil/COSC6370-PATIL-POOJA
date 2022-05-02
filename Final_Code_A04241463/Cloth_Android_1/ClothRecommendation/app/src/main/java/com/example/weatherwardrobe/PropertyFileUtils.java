package com.example.weatherwardrobe;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyFileUtils {

    public static String getProperty(String key, Context context) {
        try {
            Properties properties = new Properties();
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open("config.properties");
            properties.load(inputStream);
            return properties.getProperty(key);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "PropertiesFileNotFoundError";
    }


    public static File getDir(String FolderName) {
        File dir = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + FolderName);
//            Log.e("ANd 11", dir.getAbsolutePath());
        } else {
            dir = new File(Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DOWNLOADS + "/" + FolderName);
//            Log.e("NT Nd 11", dir.getAbsolutePath());
        }

        // Make sure the path directory exists.
        if (!dir.exists()) {
            // Make it, if it doesn't exit
            boolean success = dir.mkdirs();
//            Log.e("success And 11", String.valueOf(success));
            if (!success) {
                dir = null;
            }
        }
        return dir;
    }

}
