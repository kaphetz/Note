package com.example.kienpt.note;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import com.example.kienpt.note.Bean.Note;

import java.util.List;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayShowHomeEnabled(true);
        getActionBar().setIcon(R.mipmap.ic_launcher);
        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#7e1b7eff")));
        setContentView(R.layout.activity_main);
        MyDatabaseHelper db = new MyDatabaseHelper(this);
        GridView gvListNote = (GridView) findViewById(R.id.gv_listNote);
        TextView tvNoNotes = (TextView) findViewById(R.id.tv_noNotes);
        List<Note> details = db.getAllNotes();
        if (details.size() > 0) {
            gvListNote.setVisibility(View.VISIBLE);
            tvNoNotes.setVisibility(View.GONE);
            gvListNote.setAdapter(new CustomGridViewAdapter(MainActivity.this, details));
        }else{
            gvListNote.setVisibility(View.GONE);
            tvNoNotes.setVisibility(View.VISIBLE);
        }
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
            case R.id.mnPlus:
                addNote();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void addNote() {
        Intent intentAdd = new Intent(this, AddUpdateActivity.class);
        startActivity(intentAdd);
    }
}
