package com.example.kienpt.note.adapters;

import android.content.Context;
import android.net.Uri;

import java.io.File;

class FileCache {
    private File cacheDir;

    FileCache(Context context) {
        //Find the dir at SDCARD to save cached images
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            //if SDCARD is mounted (SDCARD is present on device and mounted)
            cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), "LazyList");
        } else {
            // if checking on simulator the create cache dir in your application context
            cacheDir = context.getCacheDir();
        }
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
    }

    File getFile(String uri) {
        File f = new File(cacheDir, Uri.parse(uri).toString());
        return f;
    }
/*

    public void clear() {
        // list all files inside cache directory
        File[] files = cacheDir.listFiles();
        if (files == null)
            return;
        //delete all cache directory files
        for (File f : files)
            f.delete();
    }
*/

}