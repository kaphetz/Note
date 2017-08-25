package com.example.kienpt.note.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;

import com.example.kienpt.note.R;
import com.example.kienpt.note.models.NoteImage;
import com.example.kienpt.note.models.NoteImageRepo;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;


public class CustomGridViewImageAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<String> mListData;
    private Context mContext;
    private LayoutInflater layoutInflater;
    private Bitmap mPlaceHolderBitmap;
    private int numColumns;
    private int itemHeight;
    private int imageSize;

    public int getNumColumns() {
        return numColumns;
    }

    public void setNumColumns(int numColumns) {
        this.numColumns = numColumns;
    }

    public void setItemHeight(int height) {
        if (height == itemHeight) {
            return;
        }
        itemHeight = height;
        setImageSize(height);
        notifyDataSetChanged();
    }

    public int getImageSize() {
        return imageSize;
    }

    public void setImageSize(int imageSize) {
        this.imageSize = imageSize;
    }


    public CustomGridViewImageAdapter(Context aContext, ArrayList<String> listData) {
        mContext = aContext;
        mListData = listData;
        layoutInflater = LayoutInflater.from(aContext);


        mPlaceHolderBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.default_image);
    }

    @Override
    public int getCount() {
        return mListData.size();
    }

    @Override
    public Object getItem(int position) {
        return mListData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ImageViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.selected_image, null);
            holder = new ImageViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.img_photo);
            holder.delView = (ImageButton) convertView.findViewById(R.id.btn_delete_image);
            convertView.setTag(holder);
        } else {
            holder = (ImageViewHolder) convertView.getTag();
        }
        String imagePath = mListData.get(position);
        final BitmapWorkerTask task = new BitmapWorkerTask(holder.imageView, imagePath);
        final AsyncDrawable asyncDrawable =
                new AsyncDrawable(mContext.getResources(), mPlaceHolderBitmap, task);
        holder.imageView.setImageDrawable(asyncDrawable);
        task.execute();
        holder.delView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListData.remove(position);
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap, BitmapWorkerTask bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference = new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }


    private class ImageViewHolder {
        ImageView imageView;
        ImageButton delView;
    }


    private class BitmapWorkerTask extends AsyncTask<Void, Void, Bitmap> {
        private WeakReference<ImageView> weakReference;
        private String imagePath;

        public BitmapWorkerTask(ImageView img, String ip) {
            weakReference = new WeakReference<ImageView>(img);
            imagePath = ip;
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
        int imageSize = (int) mContext.getResources().getDimension(R.dimen.image_thumbnail_size);
        if (originalSize <= imageSize) {
            ratio = 1.0;
        } else if (originalSize > imageSize) {
            ratio = originalSize / imageSize;
        }

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = MathUtil.getPowerOfTwoForSampleRatio(ratio);
        bitmapOptions.inDither = true; //optional
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        input = new FileInputStream(uriFile.toString());
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        return bitmap;
    }

    private static class MathUtil {
        static int getPowerOfTwoForSampleRatio(double ratio) {
            int k = Integer.highestOneBit((int) Math.floor(ratio));
            if (k == 0) {
                return 1;
            } else {
                return k;
            }
        }
    }

   /* private Bitmap loadImageFromResources(String uri, int reqWidth, int reqHeight) {
        // Remove previous callback
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), resId, options);
        Bitmap myBitmap = BitmapFactory.decodeFile(new File(uri).getAbsolutePath());
        return myBitmap;
    }
*/

    private int calculateInSampleSize(BitmapFactory.Options options,
                                      int reqWidth, int reqHeight) {

        int consumeMemory = options.outWidth * options.outHeight * 4;
        String mimeType = options.outMimeType;

        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
