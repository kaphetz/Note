package com.example.kienpt.note;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayShowHomeEnabled(true);
        getActionBar().setIcon(R.drawable.ic_note);
        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#d6a2cedc")));
        setContentView(R.layout.activity_main);
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
        Intent intentAdd = new Intent(this, AddUpdateActivity.class );
        startActivity(intentAdd);
    }
}
