package com.example.kienpt.note;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;

import java.util.ArrayList;


public class CustomGridViewImageAdapter extends BaseAdapter implements ListAdapter {

    private ArrayList<Bitmap> mListData;
    private Context mContext;
    private LayoutInflater layoutInflater;

    public CustomGridViewImageAdapter(Context aContext, ArrayList<Bitmap> listData) {
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
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.custom_image_view, null);

            ImageView imgView = (ImageView) convertView.findViewById(R.id.img_photo);
            ImageButton iBtnDel = (ImageButton) convertView.findViewById(R.id.btn_delete_image);
            Bitmap image = mListData.get(position);
            imgView.setImageBitmap(image);
            iBtnDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //do something
                    mListData.remove(position);
                    notifyDataSetChanged();
                }
            });
        }
        return convertView;
    }
}
