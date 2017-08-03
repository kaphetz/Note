package com.example.kienpt.note;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;

import com.example.kienpt.note.Bean.Note;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayShowHomeEnabled(true);
        getActionBar().setIcon(R.drawable.ic_note);
        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#7eafc12b")));
        setContentView(R.layout.activity_main);
        MyDatabaseHelper db = new MyDatabaseHelper(this);
        GridView gvListNote = (GridView) findViewById(R.id.gv_listNote);
        List<Note> details = db.getAllNotes();
        if (details.size() > 0) {
            gvListNote.setAdapter(new CustomGridViewAdapter(MainActivity.this, details));
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
