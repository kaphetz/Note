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

    private class ImageViewHolder {
        ImageView imageView;
        ImageButton delView;
    }
}
