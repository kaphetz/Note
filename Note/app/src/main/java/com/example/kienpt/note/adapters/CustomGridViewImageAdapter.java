package com.example.kienpt.note.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;

import com.example.kienpt.note.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class CustomGridViewImageAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<String> mListData;
    private Context mContext;
    private Activity mActivity;
    private LayoutInflater layoutInflater;
    private Bitmap mPlaceHolderBitmap;
    public ImageLoader imageLoader;

    public CustomGridViewImageAdapter(Activity activity, ArrayList<String> listData) {
        mActivity = activity;
        mListData = listData;
        layoutInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        mPlaceHolderBitmap = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.default_image);
        imageLoader = new ImageLoader(mActivity.getApplicationContext(),R.drawable.default_image);
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
        /*if (cancelPotentialWork(imagePath, holder.imageView)) {
            final BitmapWorkerTask task = new BitmapWorkerTask(mContext, holder.imageView,
                    imagePath);
            final AsyncDrawable asyncDrawable =
                    new AsyncDrawable(mContext.getResources(), mPlaceHolderBitmap, task);
            holder.imageView.setImageDrawable(asyncDrawable);
            task.execute();
        }*/

        //DisplayImage function from ImageLoader Class
        imageLoader.DisplayImage(mListData.get(position), holder.imageView);

        holder.delView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListData.remove(position);
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    private static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        AsyncDrawable(Resources res, Bitmap bitmap, BitmapWorkerTask bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference = new WeakReference<>(bitmapWorkerTask);
        }

        BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }

    private static boolean cancelPotentialWork(String data, ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final String bitmapData = bitmapWorkerTask.getImagePath();
            // If bitmapData is not yet set or it differs from the new data
            if (data == null || !bitmapData.equals(data)) {
                // Cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }

    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    private class ImageViewHolder {
        ImageView imageView;
        ImageButton delView;
    }

}
