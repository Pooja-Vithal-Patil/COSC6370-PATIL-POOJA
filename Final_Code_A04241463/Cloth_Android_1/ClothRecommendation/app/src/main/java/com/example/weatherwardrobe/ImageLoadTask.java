package com.example.weatherwardrobe;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

    private String url;
    private ImageView imageView;

    public static final String PRODUCT_IMAGES_FILEPATH = "/cloth.app/images";

    File myDir = PropertyFileUtils.getDir(PRODUCT_IMAGES_FILEPATH);

    public ImageLoadTask(String url, ImageView imageView) {
        this.url = url;
        this.imageView = imageView;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        try {

            url = url.replace(" ", "%20");
            File file = new File(myDir, url.replace("http", "").replace("/", "_") + ".jpeg");
            if (file.exists()) {
//                Log.e("Exists", file.getAbsolutePath());
                return loadImageFromStorage(file.getAbsolutePath());
            } else {
                return downLoadImageFromUrl(url);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        imageView.setImageBitmap(result);
    }

    public boolean saveImageToInternalStorage(Bitmap image, ImageView imageView, String url) {

        try {
            myDir.mkdirs();
            File file = new File(myDir, url.replace("http", "").replace("/", "_") + ".jpeg");
            //Log.e("PAth", file.getAbsolutePath());
            FileOutputStream out = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    private Bitmap loadImageFromStorage(String filePath) {
        try {
            File f = new File(filePath);
//            Log.e("Succ Loaded", f.getAbsolutePath());
            return BitmapFactory.decodeStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
//            Log.e("Failed Loaded", e.getMessage());
            return null;
        }

    }

    private Bitmap downLoadImageFromUrl(String url) {
        try {
            //Log.e("Not Exists Downloading", file.getAbsolutePath());
            Log.e("URL", url);
            URL urlConnection = new URL(url);
            HttpURLConnection connection = null;
            connection = (HttpURLConnection) urlConnection
                    .openConnection();

            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            saveImageToInternalStorage(myBitmap, imageView, url);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


}