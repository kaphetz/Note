package com.example.kienpt.note;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.example.kienpt.note.bean.Note;

import java.util.List;

public class MainActivity extends Activity {
    private List<Note> mListNote;
    private CustomGridViewAdapter adapter;
    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayShowHomeEnabled(true);
        getActionBar().setIcon(R.mipmap.ic_launcher);
        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#7e1b7eff")));
        setContentView(R.layout.activity_main);
        final MyDatabaseHelper db = new MyDatabaseHelper(this);
        final GridView gvListNote = (GridView) findViewById(R.id.gv_listNote);
        TextView tvNoNotes = (TextView) findViewById(R.id.tv_noNotes);
        mListNote = db.getAllNotes();
        adapter = new CustomGridViewAdapter(MainActivity.this, mListNote);
        if (mListNote.size() > 0) {
            gvListNote.setVisibility(View.VISIBLE);
            tvNoNotes.setVisibility(View.GONE);
            gvListNote.setAdapter(adapter);
        } else {
            gvListNote.setVisibility(View.GONE);
            tvNoNotes.setVisibility(View.VISIBLE);
        }

        gvListNote.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                String key = "EDIT";
                intent.putExtra(key, (Note)adapter.getItem(position));
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mn_plus:
                addNote();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void addNote() {
        Intent intentAdd = new Intent(this, AddActivity.class);
        startActivity(intentAdd);
    }
}
