package com.example.kienpt.note;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class AddUpdateActivity extends AppCompatActivity {
    private Spinner mSpnDate;
    private Spinner mSpnTime;
    private LinearLayout mLlDateTime;
    private TextView mTvAlarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_update); // lấy ActionBar
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_previous);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_note);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#d6a2cedc")));
        getSupportActionBar().setTitle("Note");

        mSpnDate = (Spinner) findViewById(R.id.spn_date);
        mSpnTime = (Spinner) findViewById(R.id.spn_time);
        mLlDateTime = (LinearLayout) findViewById(R.id.ll_dateTime);
        mTvAlarm = (TextView) findViewById(R.id.tv_alarm);


        String[] listDate = {"Today", "Tomorrow", "Next Wednesday", "Other"};
        ArrayAdapter<String> spinnerToAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listDate);
        spinnerToAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mSpnDate.setAdapter(spinnerToAdapter);
        mSpnDate.setOnItemSelectedListener(new DateSpinnerInfo());

        String[] listTime = {"09:00", "13:00", "17:00", "20:00", "Other"};
        ArrayAdapter<String> spinner2Adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listTime);
        spinner2Adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mSpnTime.setAdapter(spinner2Adapter);
        mSpnTime.setOnItemSelectedListener(new TimeSpinnerInfo());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnPlus:
//                addNote();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class DateSpinnerInfo implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> spinner, View selectedView,
                                   int selectedIndex, long id) {
            String selectedColor = spinner.getItemAtPosition(selectedIndex).toString();
            switch (selectedColor) {
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> spinner) {
            //do something
        }
    }

    private class TimeSpinnerInfo implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> spinner, View selectedView,
                                   int selectedIndex, long id) {
            String selectedColor = spinner.getItemAtPosition(selectedIndex).toString();
            switch (selectedColor) {
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> spinner) {
            //do something
        }
    }

    public void showDateTimePicker(View v) {
        mLlDateTime.setVisibility(View.VISIBLE);
        mTvAlarm.setVisibility(View.GONE);
    }

    public void hideDateTimePicker(View v) {
        mLlDateTime.setVisibility(View.GONE);
        mTvAlarm.setVisibility(View.VISIBLE);
    }


}
