package com.example.kienpt.note.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageLoader {

    // Initialize MemoryCache
    MemoryCache memoryCache = new MemoryCache();
    FileCache fileCache;
    private int mPlaceholder;

    //Create Map (collection) to store image and image url in key value pair
    private Map<ImageView, String> imageViews = Collections.
            synchronizedMap(new WeakHashMap<ImageView, String>());
    ExecutorService executorService;

    //handler to display images in UI thread
    Handler handler = new Handler();

    public ImageLoader(Context context, int placeholder) {
        mPlaceholder = placeholder;
        fileCache = new FileCache(context);

        // Creates a thread pool that reuses a fixed number of 
        // threads operating off a shared unbounded queue.
        executorService = Executors.newFixedThreadPool(5);

    }

    // default image show in list (Before online image download)

    public void DisplayImage(String url, ImageView imageView) {
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
        public String url;
        public ImageView imageView;

        public PhotoToLoad(String u, ImageView i) {
            url = u;
            imageView = i;
        }
    }

    class PhotosLoader implements Runnable {
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

                if (imageViewReused(photoToLoad))
                    return;

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
        try {
            FileInputStream input = new FileInputStream(Uri.parse(url).toString());
            BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
            onlyBoundsOptions.inJustDecodeBounds = true;
            onlyBoundsOptions.inDither = true;//optional
            onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
            BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
            input.close();
            if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1))
                return null;

            int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ?
                    onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;
            double ratio = 1.0;
            int imageSize = 96;
            if (imageSize == 0 || originalSize <= imageSize) {
                ratio = 1.0;
            } else if (originalSize > imageSize) {
                ratio = originalSize / imageSize;
            }

            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
            bitmapOptions.inDither = true; //optional
            bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
            input = new FileInputStream(Uri.parse(url).toString());
            Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
            input.close();
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static int getPowerOfTwoForSampleRatio(double ratio) {
        int k = Integer.highestOneBit((int) Math.floor(ratio));
        if (k == 0) return 1;
        else return k;
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
            final int REQUIRED_SIZE = 96;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                    break;
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

        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    boolean imageViewReused(PhotoToLoad photoToLoad) {

        String tag = imageViews.get(photoToLoad.imageView);
        //Check url is already exist in imageViews MAP
        if (tag == null || !tag.equals(photoToLoad.url))
            return true;
        return false;
    }

    //Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;

        public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
            bitmap = b;
            photoToLoad = p;
        }

        public void run() {
            if (imageViewReused(photoToLoad))
                return;

            // Show bitmap on UI
            if (bitmap != null) {
                photoToLoad.imageView.setImageBitmap(bitmap);
            } else {
                photoToLoad.imageView.setImageResource(mPlaceholder);
            }
        }
    }

    public void clearCache() {
        //Clear cache directory downloaded images and stored data in maps
        memoryCache.clear();
        fileCache.clear();
    }

}
