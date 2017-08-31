package com.example.kienpt.note.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class ImageLoader {

    // Initialize MemoryCache
    private MemoryCache memoryCache = new MemoryCache();
    private FileCache fileCache;
    private int mPlaceholder;

    private ExecutorService executorService;
    //handler to display images in UI thread
    private Handler handler = new Handler();

    //Create Map (collection) to store image and image url in key value pair
    private Map<ImageView, String> imageViews = Collections.
            synchronizedMap(new WeakHashMap<ImageView, String>());

    ImageLoader(Context context, int placeholder) {
        mPlaceholder = placeholder;
        fileCache = new FileCache(context);
        // Creates a thread pool that reuses a fixed number of 
        // threads operating off a shared unbounded queue.
        executorService = Executors.newFixedThreadPool(10);
    }

    void DisplayImage(String url, ImageView imageView) {
        //Store image and url in Map
        imageViews.put(imageView, url);
        //Check image is stored in MemoryCache Map or not (see MemoryCache.java)
        Bitmap bitmap = memoryCache.get(url);
        if (bitmap != null) {
            // if image is stored in MemoryCache Map then
            // Show image in listview row
            imageView.setImageBitmap(bitmap);
        } else {
            //queue Photo to download from url
            queuePhoto(url, imageView);
            //Before downloading image show default image 
            imageView.setImageResource(mPlaceholder);
        }
    }

    private void queuePhoto(String url, ImageView imageView) {
        // Store image and url in PhotoToLoad object
        PhotoToLoad p = new PhotoToLoad(url, imageView);
        // pass PhotoToLoad object to PhotosLoader runnable class
        // and submit PhotosLoader runnable to executers to run runnable
        // Submits a PhotosLoader runnable task for execution
        executorService.submit(new PhotosLoader(p));
    }

    //Task for the queue
    private class PhotoToLoad {
        String url;
        ImageView imageView;

        PhotoToLoad(String u, ImageView i) {
            url = u;
            imageView = i;
        }
    }

    private class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;

        PhotosLoader(PhotoToLoad photoToLoad) {
            this.photoToLoad = photoToLoad;
        }

        @Override
        public void run() {
            try {
                //Check if image already downloaded
                if (imageViewReused(photoToLoad))
                    return;
                // download image from web url
                Bitmap bmp = getBitmap(photoToLoad.url);
                // set image data in Memory Cache
                memoryCache.put(photoToLoad.url, bmp);
                if (imageViewReused(photoToLoad)) {
                    return;
                }
                // Get bitmap to display
                BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
                // Causes the Runnable bd (BitmapDisplayer) to be added to the message queue. 
                // The runnable will be run on the thread to which this handler is attached.
                // BitmapDisplayer run method will call
                handler.post(bd);
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
    }

    private Bitmap getBitmap(String url) {
        File f = fileCache.getFile(url);
        //from SD cache
        //CHECK : if trying to decode file which not exist in cache return null
        Bitmap b = decodeFile(f);
        if (b != null) {
            return b;
        }
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            FileInputStream stream1 = new FileInputStream(Uri.parse(url).toString());
            BitmapFactory.decodeStream(stream1, null, o);
            stream1.close();
            //Find the correct scale value. It should be the power of 2.
            // Set width/height of recreated image
            final int REQUIRED_SIZE = 150;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
                    break;
                }
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }
            //decode with current scale values
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            FileInputStream stream2 = new FileInputStream(Uri.parse(url).toString());
            Bitmap bitmap = BitmapFactory.decodeStream(stream2, null, o2);
            stream2.close();
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    //Decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(File f) {
        try {
            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            FileInputStream stream1 = new FileInputStream(f);
            BitmapFactory.decodeStream(stream1, null, o);
            stream1.close();
            //Find the correct scale value. It should be the power of 2.
            // Set width/height of recreated image
            final int REQUIRED_SIZE = 150;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
                    break;
                }
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }
            //decode with current scale values
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            FileInputStream stream2 = new FileInputStream(f);
            Bitmap bitmap = BitmapFactory.decodeStream(stream2, null, o2);
            stream2.close();
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private boolean imageViewReused(PhotoToLoad photoToLoad) {
        String tag = imageViews.get(photoToLoad.imageView);
        //Check url is already exist in imageViews MAP
        return tag == null || !tag.equals(photoToLoad.url);
    }

    //Used to display bitmap in the UI thread
    private class BitmapDisplayer implements Runnable {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;

        BitmapDisplayer(Bitmap b, PhotoToLoad p) {
            bitmap = b;
            photoToLoad = p;
        }

        public void run() {
            if (imageViewReused(photoToLoad)) {
                return;
            }
            // Show bitmap on UI
            if (bitmap != null) {
                photoToLoad.imageView.setImageBitmap(bitmap);
            } else {
                photoToLoad.imageView.setImageResource(mPlaceholder);
            }
        }
    }

    /*public void clearCache() {
        //Clear cache directory downloaded images and stored data in maps
        memoryCache.clear();
        fileCache.clear();
    }*/

}
