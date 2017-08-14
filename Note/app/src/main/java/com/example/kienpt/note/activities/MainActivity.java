package com.example.kienpt.note.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.example.kienpt.note.R;
import com.example.kienpt.note.adapters.CustomGridViewNotesAdapter;
import com.example.kienpt.note.models.DatabaseManager;
import com.example.kienpt.note.models.MyDatabaseHelper;
import com.example.kienpt.note.models.Note;
import com.example.kienpt.note.models.NoteRepo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends Activity {
    private CustomGridViewNotesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayShowHomeEnabled(true);
        getActionBar().setIcon(R.mipmap.ic_launcher);
        getActionBar().setBackgroundDrawable(
                new ColorDrawable(getResources().getColor(R.color.colorSky)));
        setContentView(R.layout.activity_main);
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(this);
        DatabaseManager.initializeInstance(dbHelper);
        GridView gvListNote = (GridView) findViewById(R.id.gv_listNote);
        TextView tvNoNotes = (TextView) findViewById(R.id.tv_noNotes);
        NoteRepo dbNote = new NoteRepo();
        List<Note> mListNote = dbNote.getAllNotes();
        mListNote = orderByCreatedTime(mListNote);
        adapter = new CustomGridViewNotesAdapter(MainActivity.this, mListNote);

         //show list of notes if count > 0
         //if count = 0, show "No Notes"
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
                intent.putExtra(key, (Note) adapter.getItem(position));
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

    public List<Note> orderByCreatedTime(List<Note> listNote) {
        SimpleDateFormat formatter = new SimpleDateFormat(getString(R.string.ddmmyyyy_hhmmss_format));
        for (int i = 0; i < listNote.size() - 1; i++) {
            for (int j = i + 1; j < listNote.size(); j++) {
                try {
                    Date createdTimeOfNoteA = formatter.parse(listNote.get(i).getCreatedTime());
                    Date createdTimeOfNoteB = formatter.parse(listNote.get(j).getCreatedTime());
                    if (createdTimeOfNoteA.before(createdTimeOfNoteB)) {
                        Note mediate = listNote.get(j);
                        listNote.set(j, listNote.get(i));
                        listNote.set(i, mediate);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return listNote;
    }
}
