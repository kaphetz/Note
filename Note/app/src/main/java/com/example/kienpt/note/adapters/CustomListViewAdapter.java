package com.example.kienpt.note.adapters;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kienpt.note.R;

public class CustomListViewAdapter extends BaseAdapter {

    private String[] mSourceName;
    private Integer[] mImageId;
    private LayoutInflater layoutInflater;
    private Activity mContext;

    public CustomListViewAdapter(Activity context, String[] nameList, Integer[] images) {
        mContext = context;
        mSourceName = nameList;
        mImageId = images;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mSourceName.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listViewSingle = layoutInflater.inflate(R.layout.custom_detail_camera, null, true);
        TextView listViewItems = (TextView) listViewSingle.findViewById(R.id.tv_source_name);
        ImageView listViewImage = (ImageView) listViewSingle.findViewById(R.id.img_camera_source);

        listViewItems.setText(mSourceName[position]);
        listViewImage.setImageResource(mImageId[position]);
        return listViewSingle;
    }
}
