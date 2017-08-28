package com.example.kienpt.note.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.example.kienpt.note.R;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

class BitmapWorkerTask extends AsyncTask<Void, Void, Bitmap> {
    private WeakReference<ImageView> weakReference;
    private String imagePath;
    private Context mContext;

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public BitmapWorkerTask( Context context, ImageView img, String ip) {
        weakReference = new WeakReference<>(img);
        imagePath = ip;
        mContext = context;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        try {
            return getThumbNail(Uri.parse(imagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        if (weakReference != null && bitmap != null) {
            ImageView img = weakReference.get();
            if (img != null) {
                img.setImageBitmap(bitmap);
            }
        }
    }

    private Bitmap getThumbNail(Uri uriFile) throws IOException, NullPointerException {
        FileInputStream input = new FileInputStream(uriFile.toString());
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
        int imageSize = (int)mContext.getResources().getDimension(R.dimen.thumbnail_size);
        if (imageSize == 0 || originalSize <= imageSize) {
            ratio = 1.0;
        } else if (originalSize > imageSize) {
            ratio = originalSize / imageSize;
        }

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        bitmapOptions.inDither = true; //optional
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        input = new FileInputStream(uriFile.toString());
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        return bitmap;
    }

    private static int getPowerOfTwoForSampleRatio(double ratio) {
        int k = Integer.highestOneBit((int) Math.floor(ratio));
        if (k == 0)
            return 1;
        else
            return k;
    }
}