package com.example.kienpt.note;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.kienpt.note.bean.Note;

import java.sql.Time;
import java.util.Calendar;

@RequiresApi(api = Build.VERSION_CODES.N)
public class AddActivity extends ActivityParent {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int SELECTED_PICTURE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        getActionBar().setHomeAsUpIndicator(R.drawable.ic_left);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(true);
        getActionBar().setIcon(R.mipmap.ic_launcher);
        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#7e1b7eff")));
        getActionBar().setTitle("Note");
        initView();

        // Set up for date spinner
        dateAdapter = new ArrayAdapter<>(AddActivity.this,
                android.R.layout.simple_spinner_item, listDate);
        dateAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mSpnDate.setAdapter(dateAdapter);
        mSpnDate.setOnItemSelectedListener(new DateSpinnerInfo());

        // Set up for time spinner
        timeAdapter = new ArrayAdapter<>(AddActivity.this,
                android.R.layout.simple_spinner_item, listTime);
        timeAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mSpnTime.setAdapter(timeAdapter);
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

    Integer[] mImageList = {R.drawable.ic_take_photo, R.drawable.ic_choose_photo};
    String[] mSourceNameList = {"Take Photo", "Choose Photo"};

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
                getImage();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private static final int REQUEST_ID_READ_WRITE_PERMISSION = 99;

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //
        switch (requestCode) {
            case REQUEST_ID_READ_WRITE_PERMISSION: {

                // Chú ý: Nếu yêu cầu bị hủy, mảng kết quả trả về là rỗng.
                // Người dùng đã cấp quyền (đọc/ghi).
                if (grantResults.length > 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this, "Permission granted!", Toast.LENGTH_LONG).show();

                    getImage();

                }
                // Hủy bỏ hoặc bị từ chối.
                else {
                    Toast.makeText(this, "Permission denied!", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE:
                if (resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    ImageView img = (ImageView) findViewById(R.id.img_photo);
                    img.setImageBitmap(imageBitmap);
                }
                break;
            case SELECTED_PICTURE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    String[] FILE = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(uri, FILE, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(FILE[0]);
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();
                    ImageView img = (ImageView) findViewById(R.id.img_photo);
                    img.setImageBitmap(BitmapFactory.decodeFile(filePath));
                }
                break;
        }
    }

    private class DateSpinnerInfo implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> spinner, View selectedView,
                                   int selectedIndex, long id) {
            String selectedDate = spinner.getItemAtPosition(selectedIndex).toString();
            switch (selectedIndex) {
                case 0:
                    SimpleDateFormat df = new SimpleDateFormat("dd/MM");
                    mSelectedDate = df.format(mCalendar.getTime());
                    updateAdapterForDateSpinner("Other...");
                    break;
                case 1:
                    mSelectedDate = getTomorrow();
                    updateAdapterForDateSpinner("Other...");
                    break;
                case 2:
                    mSelectedDate = getDayOfNextWeek();
                    updateAdapterForDateSpinner("Other...");
                    break;
                case 3:
                    if (selectedDate.equals("Other...")) {
                        final int mYear, mMonth, mDay;
                        final Calendar c = Calendar.getInstance();
                        mYear = c.get(Calendar.YEAR);
                        mMonth = c.get(Calendar.MONTH);
                        mDay = c.get(Calendar.DAY_OF_MONTH);
                        DatePickerDialog datePickerDialog = new DatePickerDialog(AddActivity.this,
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                                          int dayOfMonth) {
                                        String s = String.format("%s/%s/%s", dayOfMonth, monthOfYear + 1, year);
                                        updateAdapterForDateSpinner(s);
                                    }
                                }, mYear, mMonth, mDay);
                        datePickerDialog.setTitle("Choose Date");
                        datePickerDialog.show();
                    }
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
            switch (selectedIndex) {
                case 0:
                    mSelectedTime = "09:00";
                    updateAdapterForTimeSpinner("Other...");
                    break;
                case 1:
                    mSelectedTime = "13:00";
                    updateAdapterForTimeSpinner("Other...");
                    break;
                case 2:
                    mSelectedTime = "17:00";
                    updateAdapterForTimeSpinner("Other...");
                    break;
                case 3:
                    mSelectedTime = "20:00";
                    updateAdapterForTimeSpinner("Other...");
                    break;
                case 4:
                    if (selectedTime.equals("Other...")) {
                        final Calendar c = Calendar.getInstance();
                        int hour = c.get(Calendar.HOUR_OF_DAY);
                        int minute = c.get(Calendar.MINUTE);

                        TimePickerDialog timePickerDialog = new TimePickerDialog(AddActivity.this,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                        String selectedTime = String.format("%s:%s",
                                                selectedHour, selectedMinute);
                                        updateAdapterForTimeSpinner(selectedTime);
                                    }
                                }, hour, minute, false);
                        timePickerDialog.setTitle("Choose Time");
                        timePickerDialog.show();
                    }
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> spinner) {
            //do something
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void addNewNote() {
        Note note = new Note();
        if (!mEtTitle.getText().toString().equals("")) {
            note.setNoteTitle(mEtTitle.getText().toString());
        } else {
            note.setNoteTitle("Untitled");
        }
        note.setNoteContent(mEtContent.getText().toString());
        if (mLlDateTime.getVisibility() == View.VISIBLE) {
            note.setNoteTime(String.format("%s %s", mSelectedDate, mSelectedTime));
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

    public void getImage() {
        if (ContextCompat.checkSelfPermission(AddActivity.this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?

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
                    if (ActivityCompat.shouldShowRequestPermissionRationale(AddActivity.this,
                            Manifest.permission.READ_CONTACTS)) {
                        switch (position) {
                            case 0:
                                takeImage();
                                dialog.dismiss();
                                break;
                            case 1:
                                askPermissionAndChooseImage();
                                dialog.dismiss();
                                break;
                        }

                    } else {
                        ActivityCompat.requestPermissions(AddActivity.this,
                                new String[]{Manifest.permission.READ_CONTACTS},
                                REQUEST_ID_READ_WRITE_PERMISSION);
                    }
                }
            });
            dialog = mBuilder.create();
            dialog.show();
        }
    }

    public void takeImage() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void askPermissionAndChooseImage() {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            int readPermission = ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE);
            int writePermission = ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (writePermission != PackageManager.PERMISSION_GRANTED ||
                    readPermission != PackageManager.PERMISSION_GRANTED) {

                this.requestPermissions(
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_ID_READ_WRITE_PERMISSION
                );
                return;
            }
        }
        chooseImage();
    }

    public void chooseImage() {
        Intent choosePictureIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(choosePictureIntent, SELECTED_PICTURE);
    }


}
