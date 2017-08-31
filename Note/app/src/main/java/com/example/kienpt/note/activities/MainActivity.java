package com.example.kienpt.note.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
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
import com.example.kienpt.note.utils.ListUtil;

import java.util.List;

public class MainActivity extends Activity {
    private CustomGridViewNotesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayShowHomeEnabled(true);
        getActionBar().setIcon(R.mipmap.ic_launcher);
        getActionBar().setBackgroundDrawable(
                new ColorDrawable(getResources().getColor(R.color.colorCyan)));
        setContentView(R.layout.activity_main);
//        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        DatabaseManager.initializeInstance(new MyDatabaseHelper(this));
        GridView gvListNote = (GridView) findViewById(R.id.gv_listNote);
        TextView tvNoNotes = (TextView) findViewById(R.id.tv_noNotes);
        final FloatingActionButton fabAddNote = (FloatingActionButton) findViewById(R.id.fab_add_note);
        final Animation animAlpha = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);
        List<Note> mListNote = ListUtil.orderByCreatedTime(new NoteRepo().getAllNotes());
        adapter = new CustomGridViewNotesAdapter(MainActivity.this, mListNote);
        //show list of notes if count > 0
        //if count = 0, show "No Notes"
        if (!mListNote.isEmpty()) {
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
                intent.putExtra(EditActivity.sNOTE, (Note) adapter.getItem(position));
                startActivity(intent);
            }
        });
        gvListNote.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                int btnPosY = fabAddNote.getScrollY();
                if (scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    fabAddNote.animate().cancel();
                    fabAddNote.animate().translationYBy(150);
                } else if (scrollState == SCROLL_STATE_FLING) {
                    fabAddNote.animate().cancel();
                    fabAddNote.animate().translationYBy(150);
                } else {
                    fabAddNote.animate().cancel();
                    fabAddNote.animate().translationY(btnPosY);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //Do something
            }
        });

        fabAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAdd = new Intent(MainActivity.this, AddActivity.class);
                startActivity(intentAdd);
                v.startAnimation(animAlpha);
            }
        });
    }
}

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mn_plus:
                Intent intentAdd = new Intent(this, AddActivity.class);
                startActivity(intentAdd);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/
