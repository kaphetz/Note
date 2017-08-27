package com.example.kienpt.note.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.example.kienpt.note.views.SquareImageView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;


public class CustomGridViewImageAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<String> mListData;
    private Context mContext;
    private LayoutInflater layoutInflater;
    private Bitmap mPlaceHolderBitmap;
    private int mNumColumns;
    private int mItemHeight;
    private int mImageSize;

    public int getNumColumns() {
        return mNumColumns;
    }

    public void setNumColumns(int numColumns) {
        mNumColumns = numColumns;
    }

    public void setItemHeight(int height) {
        if (height == mItemHeight) {
            return;
        }
        mItemHeight = height;
        setImageSize(height);
        notifyDataSetChanged();
    }

    public void setImageSize(int mImageSize) {
        this.mImageSize = mImageSize;
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
            holder.imageView = (SquareImageView) convertView.findViewById(R.id.img_photo);
            holder.delView = (ImageButton) convertView.findViewById(R.id.btn_delete_image);
            convertView.setTag(holder);
        } else {
            holder = (ImageViewHolder) convertView.getTag();
        }
        String imagePath = mListData.get(position);
        if (cancelPotentialWork(imagePath, holder.imageView)) {
            final BitmapWorkerTask task = new BitmapWorkerTask(holder.imageView, imagePath, mContext);
            final AsyncDrawable asyncDrawable =
                    new AsyncDrawable(mContext.getResources(), mPlaceHolderBitmap, task);
            holder.imageView.setImageDrawable(asyncDrawable);
            task.execute();
        }
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
        SquareImageView imageView;
        ImageButton delView;
    }

}
