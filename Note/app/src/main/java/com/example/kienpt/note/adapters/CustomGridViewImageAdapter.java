package com.example.kienpt.note.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;

import com.example.kienpt.note.R;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;


public class CustomGridViewImageAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<String> mListData;
    private Context mContext;
    private LayoutInflater layoutInflater;

    public CustomGridViewImageAdapter(Context aContext, ArrayList<String> listData) {
        mContext = aContext;
        mListData = listData;
        layoutInflater = LayoutInflater.from(aContext);
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
        ImageViewHolder holder;
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
        if (BitmapFactory.decodeFile(imagePath) != null) {
            Picasso.with(mContext).load(new File(imagePath)).centerCrop().fit()
                    .placeholder(R.drawable.default_image).error(R.drawable.not_defound)
                    .into(holder.imageView);
        } else {
            Picasso.with(mContext).load(R.drawable.not_defound).centerCrop().fit()
                    .placeholder(R.drawable.default_image).into(holder.imageView);
        }
        holder.delView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mListData.remove(position);
                notifyDataSetChanged();
            }
        });
        /*Animation animation;
        animation = AnimationUtils.loadAnimation(mContext, R.anim.fade_in);
        animation.setDuration(300);
        convertView.startAnimation(animation);*/
        return convertView;
    }

    private class ImageViewHolder {
        ImageView imageView;
        ImageButton delView;
    }
}
