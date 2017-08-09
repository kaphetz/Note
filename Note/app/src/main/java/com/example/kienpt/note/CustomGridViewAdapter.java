package com.example.kienpt.note;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.kienpt.note.bean.Note;

import java.util.List;
import java.util.Objects;


public class CustomGridViewAdapter extends BaseAdapter {
    private List<Note> mListData;
    private LayoutInflater layoutInflater;
    private Context mContext;

    public CustomGridViewAdapter(Context aContext, List<Note> listData) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.detail_note, null);
            holder = new ViewHolder();
            holder.titleView = (TextView) convertView.findViewById(R.id.tv_noteTitle);
            holder.contentView = (TextView) convertView.findViewById(R.id.tv_noteContent);
            holder.createdTimeView = (TextView) convertView.findViewById(R.id.tv_noteCreatedTime);
            holder.alarmView = (ImageView) convertView.findViewById(R.id.img_noteAlarm);
            holder.alarmView = (ImageView) convertView.findViewById(R.id.img_noteAlarm);
            holder.detailNoteView = (LinearLayout) convertView.findViewById(R.id.ll_detailNote);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Note note = mListData.get(position);
        holder.titleView.setText(note.getNoteTitle());
        holder.contentView.setText(note.getNoteContent());

        String createdTime = note.getCreatedTime();
//        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm");
//            Date date = formatter.parse(createdTime);
        StringBuffer strBuf = new StringBuffer(createdTime);
        int start = 5;
        int end = 10;
        strBuf.replace(start, end, "");
        holder.createdTimeView.setText(strBuf);
        if (!Objects.equals(note.getNoteTime(), "")) {
            holder.alarmView.setVisibility(View.VISIBLE);
        } else {
            holder.alarmView.setVisibility(View.GONE);
        }
        switch (note.getBackgroundColor()) {
            case "Yellow":
                holder.detailNoteView.setBackgroundColor(
                        ContextCompat.getColor(mContext, R.color.colorYellow));
                break;
            case "Green":
                holder.detailNoteView.setBackgroundColor(
                        ContextCompat.getColor(mContext, R.color.colorGreen));
                break;
            case "Blue":
                holder.detailNoteView.setBackgroundColor(
                        ContextCompat.getColor(mContext, R.color.colorBlue));
                break;
            default:
                holder.detailNoteView.setBackgroundColor(
                        ContextCompat.getColor(mContext, R.color.colorWhite));

                break;
        }
        return convertView;
    }

    static class ViewHolder {
        TextView titleView;
        TextView contentView;
        ImageView alarmView;
        TextView createdTimeView;
        LinearLayout detailNoteView;
    }
}
