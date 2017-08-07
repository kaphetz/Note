package com.example.kienpt.note;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.kienpt.note.bean.Note;

import java.util.Calendar;

public class AddActivity extends ActivityParent {
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        getActionBar().setHomeAsUpIndicator(R.drawable.ic_previous);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(true);
        getActionBar().setIcon(R.mipmap.ic_launcher);
        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#7e1b7eff")));
        getActionBar().setTitle("Note");
        initView();
        String[] parts = mCalendar.getTime().toString().split(" ");
        String[] listDate = {"Today", "Tomorrow", dayOfNextWeek(parts[0]), "Other..."};
        ArrayAdapter<String> spinnerToAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, listDate);
        spinnerToAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mSpnDate.setAdapter(spinnerToAdapter);
        mSpnDate.setOnItemSelectedListener(new DateSpinnerInfo());

        String[] listTime = {"09:00", "13:00", "17:00", "20:00", "Other..."};
        ArrayAdapter<String> spinner2Adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listTime);
        spinner2Adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mSpnTime.setAdapter(spinner2Adapter);
        mSpnTime.setOnItemSelectedListener(new TimeSpinnerInfo());
        Calendar now = Calendar.getInstance();
        mTvDateTime.setText(convert(now, "dd/MM/YYYY hh:mm"));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_menu, menu);
        return true;
    }

    private static final int SELECTED_PICTURE = 1;
    Integer[] mImageList = {R.drawable.ic_take_photo, R.drawable.ic_choose_photo};
    String[] mSourceNameList = {"Take Photo", "Choose Photo"};

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mn_save:
                addNewNote();
                return true;
            case R.id.mn_change_color:
                changeBackgroundColor();
                return true;
            case R.id.mn_camera:
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
                View mView = getLayoutInflater().inflate(R.layout.take_photo_alert, null);
                mBuilder.setView(mView).setTitle("Insert Picture");
                mLvCamera = (ListView) mView.findViewById(R.id.lv_camera);
                CustomListViewAdapter adapter = new CustomListViewAdapter(AddActivity.this,
                        mSourceNameList, mImageList);
                mLvCamera.setAdapter(adapter);
                mLvCamera.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        switch (position) {
                            case 0:
                                break;
                            case 1:
                                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(i, SELECTED_PICTURE);
                                dialog.dismiss();
                                break;
                        }
                    }
                });
                dialog = mBuilder.create();
                dialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SELECTED_PICTURE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    String[] projection = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(projection[0]);
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();

                    Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath);
                    Drawable d = new BitmapDrawable(yourSelectedImage);

                    ImageView img = (ImageView) findViewById(R.id.img_photo);
                    img.setBackground(d);
                    dialog.dismiss();
                }
        }
    }

    private class DateSpinnerInfo implements AdapterView.OnItemSelectedListener {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onItemSelected(AdapterView<?> spinner, View selectedView,
                                   int selectedIndex, long id) {
            String selectedDate = spinner.getItemAtPosition(selectedIndex).toString();
            switch (selectedDate) {
                case "Today":
                    SimpleDateFormat df = new SimpleDateFormat("dd/MM");
                    mSpinnerDate = df.format(mCalendar.getTime());
                    break;
                case "Tomorrow":
                    mSpinnerDate = getTomorrow();
                    break;
                case "Other...":
                    break;
                default:
                    mSpinnerDate = getDayOfNextWeek();
                    break;
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
            String selectedTime = spinner.getItemAtPosition(selectedIndex).toString();
            switch (selectedTime) {
                case "09:00":
                    mSpinnerTime = "09:00";
                    break;
                case "13:00":
                    mSpinnerTime = "13:00";
                    break;
                case "17:00":
                    mSpinnerTime = "17:00";
                    break;
                case "20:00":
                    mSpinnerTime = "20:00";
                    break;
                case "Other...":
//                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(AddActivity.this);
//                    View mView = getLayoutInflater().inflate(R.layout.clock, null);
//                    mBuilder.setView(mView).setTitle("Choose Time");
//                    mBuilder.setPositiveButton("OK",
//                            new DialogInterface.OnClickListener() {
//
//                                TimePicker timePicker =
//                                        (TimePicker) findViewById(R.id.tp_clock);
//                                @Override
//                                public void onClick(DialogInterface dialog,
//                                                    int which) {
//                                    int hour = 0;
//                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//                                        hour = timePicker.getHour();
//                                    }else{
//                                        hour = timePicker.getCurrentHour();
//                                    }
//                                    int minute = 0;
//                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//                                        minute = timePicker.getMinute();
//                                    }else{
//                                        minute = timePicker.getCurrentMinute();
//                                    }
//                                    dialog.dismiss();
//                                    Toast.makeText(AddActivity.this, String.format("%s", hour), Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                    );
//                    mBuilder.setNegativeButton("Cancel",
//                            new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog,
//                                                    int which) {
//                                    dialog.dismiss();
//                                }
//                            });
//                    dialog = mBuilder.create();
//                    dialog.show();
                    break;
                default:
                    break;
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void addNewNote() {
        Note note = new Note();
        note.setNoteTitle(mEtTitle.getText().toString());
        note.setNoteContent(mEtContent.getText().toString());
        if (mLlDateTime.getVisibility() == View.VISIBLE) {
            note.setNoteTime(String.format("%s %s", mSpinnerDate, mSpinnerTime));
        } else {
            note.setNoteTime("");
        }
        //get create datetime
        mCalendar = Calendar.getInstance();
        note.setCreatedTime(String.format("%s", convert(mCalendar, "dd/MM HH:mm")));
        note.setBackgroundColor(mColor);
        // add new note into db
        MyDatabaseHelper db = new MyDatabaseHelper(this);
        db.addNote(note);
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
    }

    public void changeBackgroundColor() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.change_color_alert, null);
        mBuilder.setView(mView).setTitle("Choose Color").setIcon(R.drawable.ic_change_color);
        Button btnWhite = (Button) mView.findViewById(R.id.btn_white);
        Button btnYellow = (Button) mView.findViewById(R.id.btn_yellow);
        Button btnGreen = (Button) mView.findViewById(R.id.btn_green);
        Button btnBlue = (Button) mView.findViewById(R.id.btn_blue);
        btnWhite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRlNote.setBackgroundColor(
                        ContextCompat.getColor(getApplicationContext(), R.color.colorWhite));
                mColor = "White";
                dialog.dismiss();
            }
        });
        btnYellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRlNote.setBackgroundColor(
                        ContextCompat.getColor(getApplicationContext(), R.color.colorYellow));
                mColor = "Yellow";
                dialog.dismiss();
            }
        });
        btnGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRlNote.setBackgroundColor(
                        ContextCompat.getColor(getApplicationContext(), R.color.colorGreen));
                mColor = "Green";
                dialog.dismiss();
            }
        });
        btnBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRlNote.setBackgroundColor(
                        ContextCompat.getColor(getApplicationContext(), R.color.colorBlue));
                mColor = "Blue";
                dialog.dismiss();
            }
        });

        dialog = mBuilder.create();
        dialog.show();

    }


}
