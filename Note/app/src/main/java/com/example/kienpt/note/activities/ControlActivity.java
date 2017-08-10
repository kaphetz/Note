package com.example.kienpt.note.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.kienpt.note.R;
import com.example.kienpt.note.adapters.CustomGridViewImageAdapter;
import com.example.kienpt.note.adapters.CustomListViewAdapter;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


@RequiresApi(api = Build.VERSION_CODES.N)
public class ControlActivity extends Activity {
    protected ListView mLvCamera;
    protected GridView mGvImage;
    protected Spinner mSpnDate;
    protected Spinner mSpnTime;
    protected LinearLayout mLlDateTime;
    protected TextView mTvAlarm;
    protected TextView mTvDateTime;
    protected EditText mEtTitle;
    protected EditText mEtContent;
    protected LinearLayout mLlNote;
    protected LinearLayout mLlImageContainer;
    protected Calendar mCalendar = Calendar.getInstance();
    protected String mSelectedDate = "";
    protected String mSelectedTime = "";
    protected String mColor = "";
    protected AlertDialog dialog;
    protected Context mContext;
    protected String[] parts = mCalendar.getTime().toString().split(" ");
    protected ArrayAdapter dateAdapter;
    protected ArrayAdapter timeAdapter;
    public List<String> listDate = new ArrayList<>();
    public List<String> listTime = new ArrayList<>();
    public Integer[] mSourceImageList = {R.drawable.ic_take_photo, R.drawable.ic_choose_photo};
    public String[] mSourceImageNameList = {"Take photo", "Choose photo"};
    public CustomGridViewImageAdapter mAdapter;
    public ArrayList<Bitmap> mImageList = new ArrayList<>();

    // Constant value
    private static final int REQUEST_ID_IMAGE_CAPTURE = 1;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 2;
    private static final int PERMISSION_REQUEST_CODE = 3;
    private static final int SELECT_IMAGE = 4;

    //Khoi tao initview
    protected void initView() {
        listDate.addAll(Arrays.asList(getString(R.string.today),
                getString(R.string.tomorrow),
                dayOfNextWeek(parts[0]),
                getString(R.string.other)));
        listTime.addAll(Arrays.asList(getString(R.string.hour_9h),
                getString(R.string.hour_13h),
                getString(R.string.hour_17h),
                getString(R.string.hour_20h),
                getString(R.string.other)));
        mSpnDate = (Spinner) findViewById(R.id.spn_date);
        mSpnTime = (Spinner) findViewById(R.id.spn_time);
        mLlDateTime = (LinearLayout) findViewById(R.id.ll_dateTime);
        mLlNote = (LinearLayout) findViewById(R.id.ll_note);
        mLlImageContainer = (LinearLayout) findViewById(R.id.lL_image_container);
        mGvImage = (GridView) findViewById(R.id.gv_listImage);
        mTvAlarm = (TextView) findViewById(R.id.tv_alarm);
        mTvDateTime = (TextView) findViewById(R.id.tv_dateTime);
        mEtTitle = (EditText) findViewById(R.id.et_title);
        mEtContent = (EditText) findViewById(R.id.et_content);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState){
        outState.putString("LinearLayoutState",String.valueOf(mLlDateTime.isShown()));
        outState.putParcelableArrayList("ImageList",mImageList);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState.getString("LinearLayoutState").equals("true")){
            mLlDateTime.setVisibility(View.VISIBLE);
            mTvAlarm.setVisibility(View.GONE);
        }else{
            mLlDateTime.setVisibility(View.GONE);
            mTvAlarm.setVisibility(View.VISIBLE);
        }
        mImageList = savedInstanceState.getParcelableArrayList("ImageList");
        mAdapter = new CustomGridViewImageAdapter(this, mImageList);
        mGvImage.setAdapter(mAdapter);
    }


    public String dayOfNextWeek(String dayOfWeek) {
        switch (dayOfWeek) {
            case "Mon":
                return "Next Monday";
            case "Tue":
                return "Next Tuesday";
            case "Wed":
                return "Next Wednesday";
            case "Thu":
                return "Next Thursday";
            case "Fri":
                return "Next Friday";
            case "Sat":
                return "Next Saturday";
            default:
                return "Next Sunday";

        }
    }

    // update adapter of date spinner
    public void updateAdapterForDateSpinner(String newDay) {
//        dateAdapter = new ArrayAdapter<>(context,
//                android.R.layout.simple_spinner_item, list);
        dateAdapter.remove(listDate.get(3));
        dateAdapter.insert(newDay, 3);
        dateAdapter.notifyDataSetChanged();
    }

    // update adapter of time spinner
    public void updateAdapterForTimeSpinner(String newTime) {
        timeAdapter.remove(listTime.get(4));
        timeAdapter.insert(newTime, 4);
        timeAdapter.notifyDataSetChanged();
    }

    public void showDateTimePicker(View v) {
        mLlDateTime.setVisibility(View.VISIBLE);
        mTvAlarm.setVisibility(View.GONE);
    }

    public void hideDateTimePicker(View v) {
        mLlDateTime.setVisibility(View.GONE);
        mTvAlarm.setVisibility(View.VISIBLE);
    }

    //get time of tomorrow (day, month, yeah)
    public String getTomorrow() {
        Date dt = new Date();
        mCalendar.setTime(dt);
        mCalendar.add(Calendar.DATE, 1);
        SimpleDateFormat df = new SimpleDateFormat(getString(R.string.ddmmyyyy_format));
        return df.format(mCalendar.getTime());
    }


    //get time of next 7 days (day, month, yeah)
    public String getDayOfNextWeek() {
        Date dt = new Date();
        mCalendar.setTime(dt);
        mCalendar.add(Calendar.DATE, 7);
        SimpleDateFormat df = new SimpleDateFormat(getString(R.string.ddmmyyyy_format));
        return df.format(mCalendar.getTime());
    }

    //convert from date to string type
    public String convert(Calendar calendar, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(calendar.getTime());
    }

    public void changeBackgroundColor() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.change_color_alert, null);
        mBuilder.setView(mView).setTitle(R.string.choose_color).setIcon(R.drawable.ic_change_color);
        Button btnWhite = (Button) mView.findViewById(R.id.btn_white);
        Button btnYellow = (Button) mView.findViewById(R.id.btn_yellow);
        Button btnGreen = (Button) mView.findViewById(R.id.btn_green);
        Button btnBlue = (Button) mView.findViewById(R.id.btn_blue);
        btnWhite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLlNote.setBackgroundColor(
                        ContextCompat.getColor(getApplicationContext(), R.color.colorWhite));
                mColor = "White";
                dialog.dismiss();
            }
        });
        btnYellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLlNote.setBackgroundColor(
                        ContextCompat.getColor(getApplicationContext(), R.color.colorYellow));
                mColor = "Yellow";
                dialog.dismiss();
            }
        });
        btnGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLlNote.setBackgroundColor(
                        ContextCompat.getColor(getApplicationContext(), R.color.colorGreen));
                mColor = "Green";
                dialog.dismiss();
            }
        });
        btnBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLlNote.setBackgroundColor(
                        ContextCompat.getColor(getApplicationContext(), R.color.colorBlue));
                mColor = "Blue";
                dialog.dismiss();
            }
        });

        dialog = mBuilder.create();
        dialog.show();
    }

    // When click option "Other..." in TimeSpinner
    public void chooseOptionOtherTime(Context context, String selectedTime) {
        if (selectedTime.equals(getString(R.string.other))) {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            String time = String.format("%s:%s",
                                    selectedHour, selectedMinute);
                            updateAdapterForTimeSpinner(time);
                            mSelectedTime = time;
                        }
                    }, hour, minute, false);
            timePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == DialogInterface.BUTTON_NEGATIVE) {
                                mSpnTime.setSelection(0);
                            }
                        }
                    });
            timePickerDialog.setTitle(getString(R.string.choose_time));
            timePickerDialog.show();
        }
    }

    // When click option "Other..." in DateSpinner
    public void chooseOptionOtherDate(Context context, final String selectedDate) {
        if (selectedDate.equals(getString(R.string.other))) {
            final int mYear, mMonth, mDay;
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear,
                                              int dayOfMonth) {
                            String date = String.format("%s/%s/%s", dayOfMonth, monthOfYear + 1, year);
                            updateAdapterForDateSpinner(date);
                            mSelectedDate = date;
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == DialogInterface.BUTTON_NEGATIVE) {
                                mSpnDate.setSelection(0);
                            }
                        }
                    });
            datePickerDialog.setTitle(getString(R.string.choose_date));
            datePickerDialog.show();
        }
    }

    public void insertImage() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.select_image_alert, null);
        mBuilder.setView(mView).setTitle(R.string.insert_picture);
        mLvCamera = (ListView) mView.findViewById(R.id.lv_camera);
        CustomListViewAdapter adapter = new CustomListViewAdapter(this,
                mSourceImageNameList, mSourceImageList);
        mLvCamera.setAdapter(adapter);
        mLvCamera.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        takePhoto();
                        dialog.dismiss();
                        break;
                    case 1:
                        if (Build.VERSION.SDK_INT >= 23) {
                            if (checkPermission()) {
                                Intent intent = new Intent(Intent.ACTION_PICK,
                                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                intent.setType("*/*");
                                startActivityForResult(intent, SELECT_IMAGE);
                            } else {
                                requestPermission();
                            }
                        } else {
                            Intent intent = new Intent(Intent.ACTION_PICK,
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            intent.setType("*/*");
                            startActivityForResult(intent, SELECT_IMAGE);

                        }
                        dialog.dismiss();
                        break;
                }
            }
        });
        dialog = mBuilder.create();
        dialog.show();
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(this,
                    "Write External Storage permission allows us to access images. " +
                            "Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    public void takePhoto() {
        if (checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            invokeCamera();
        } else {
            String[] permissionRequest = {android.Manifest.permission.CAMERA};
            requestPermissions(permissionRequest, CAMERA_PERMISSION_REQUEST_CODE);
        }
    }


    public void invokeCamera() {
        Intent callCameraApplicationIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(callCameraApplicationIntent, REQUEST_ID_IMAGE_CAPTURE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                invokeCamera();
            } else {
                Toast.makeText(this, "Cannot take photo without permission", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Accepted", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("*/*");
                startActivityForResult(intent, SELECT_IMAGE);
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mAdapter = new CustomGridViewImageAdapter(this, mImageList);
        switch (requestCode) {
            case REQUEST_ID_IMAGE_CAPTURE:
                if (resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    mImageList.add(imageBitmap);
                    mGvImage.setAdapter(mAdapter);
                }
                break;
            case SELECT_IMAGE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    String[] FILE = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(uri, FILE, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(FILE[0]);
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();
                    mImageList.add(BitmapFactory.decodeFile(filePath));
                    mGvImage.setAdapter(mAdapter);
                }
                break;
        }
    }

    // Convert bitmap to byte array
    public byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }

}