package com.example.kienpt.note.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.kienpt.note.R;
import com.example.kienpt.note.models.Note;

import java.util.List;
import java.util.Objects;


public class CustomGridViewNotesAdapter extends BaseAdapter {
    private List<Note> mListData;
    private LayoutInflater layoutInflater;
    private Context mContext;

    public CustomGridViewNotesAdapter(Context aContext, List<Note> listData) {
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
            holder.detailNoteView = (LinearLayout) convertView.findViewById(R.id.ll_detailNote);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Note note = mListData.get(position);
        holder.titleView.setText(note.getNoteTitle());
        holder.contentView.setText(note.getNoteContent());
        holder.createdTimeView.setText(convertToShortFormat(note.getCreatedTime()));
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

    private StringBuffer convertToShortFormat(String longDateTime) {
        // long version: dd/MM/YYYY hh:mm:ss
        // -> short version dd/mm hh:mm
        StringBuffer strBuf = new StringBuffer(longDateTime);
        int start = 5;
        int end = 10;
        strBuf.replace(start, end, "");
        start = 11;
        end = 14;
        strBuf.replace(start, end, "");
        return strBuf;
    }

    private static class ViewHolder {
        TextView titleView;
        TextView contentView;
        ImageView alarmView;
        TextView createdTimeView;
        LinearLayout detailNoteView;
    }
}
