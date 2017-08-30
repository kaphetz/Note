package com.example.kienpt.note.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kienpt.note.R;
import com.example.kienpt.note.adapters.CustomGridViewImageAdapter;
import com.example.kienpt.note.adapters.CustomListViewAdapter;
import com.example.kienpt.note.models.Note;
import com.example.kienpt.note.notifications.AlarmReceiver;
import com.example.kienpt.note.utils.DateUtil;
import com.example.kienpt.note.utils.SpinnerUtil;
import com.example.kienpt.note.views.ExpandedGridView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class ControlActivity extends Activity {
    // Constant value
    private static final int REQUEST_ID_IMAGE_CAPTURE = 1;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 2;
    private static final int PERMISSION_REQUEST_CODE = 3;
    private static final int SELECT_IMAGE = 4;
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 5;
    private static final String LINEAR_LAYOUT_STATE = "LinearLayoutState";
//    private static final String IMAGE_LIST = "ImageList";
    private static final String TAKE_PHOTO = "Take photo";
    private static final String CHOOSE_PHOTO = "Choose photo";

    private Uri mCapturedImageURI;
    protected ListView mLvCamera;
    protected ExpandedGridView mGvImage;
    protected Spinner mSpnDate, mSpnTime;
    protected ImageButton mIBtnHide;
    protected LinearLayout mLlDateTime;
    protected TextView mTvAlarm, mTvDateTime;
    protected EditText mEtTitle, mEtContent;
    protected RelativeLayout mRlNote;
    protected Calendar mCalendar = Calendar.getInstance();
    protected String mSelectedDate = "", mSelectedTime = "", mColor = "";
    protected AlertDialog mDialog;
    protected ArrayAdapter<String> dateAdapter, timeAdapter;
    protected AlarmManager mAlarmManager;
    protected PendingIntent mPendingIntent;
    protected List<String> mListDate = new ArrayList<>();
    protected List<String> mListTime = new ArrayList<>();
    protected Integer[] mSourceImageList = {R.drawable.ic_take_photo, R.drawable.ic_choose_photo};
    protected String[] mSourceImageNameList = {TAKE_PHOTO, CHOOSE_PHOTO};
    protected CustomGridViewImageAdapter mAdapter;
    protected ArrayList<String> mImageList = new ArrayList<>();

    protected void initView() {
        int dayOfWeek = mCalendar.get(Calendar.DAY_OF_WEEK);
        mListDate.addAll(Arrays.asList(getString(R.string.today),
                getString(R.string.tomorrow),
                DateUtil.dayOfNextWeek(dayOfWeek),
                getString(R.string.other)));
        mListTime.addAll(Arrays.asList(getString(R.string.hour_9h),
                getString(R.string.hour_13h),
                getString(R.string.hour_17h),
                getString(R.string.hour_20h),
                getString(R.string.other)));
        mSpnDate = (Spinner) findViewById(R.id.spn_date);
        mSpnTime = (Spinner) findViewById(R.id.spn_time);
        mIBtnHide = (ImageButton) findViewById(R.id.iBtn_hide);
        mLlDateTime = (LinearLayout) findViewById(R.id.ll_dateTime);
        mRlNote = (RelativeLayout) findViewById(R.id.rl_note);
        mGvImage = (ExpandedGridView) findViewById(R.id.gv_listImage);
        mTvAlarm = (TextView) findViewById(R.id.tv_alarm);
        mTvDateTime = (TextView) findViewById(R.id.tv_dateTime);
        mEtTitle = (EditText) findViewById(R.id.et_title);
        mEtContent = (EditText) findViewById(R.id.et_content);
        // show datetime picker
        mTvAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpinnerUtil.showSpinner(mLlDateTime, mTvAlarm);
            }
        });
        //hide datetime picker
        mIBtnHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpinnerUtil.hideSpinner(mLlDateTime, mTvAlarm);
            }
        });
    }

    /**
     * restore data
     */
    public void restoreMe() {
        // check last state's mImageList
        if (getLastNonConfigurationInstance() != null) {
            mImageList = (ArrayList<String>) getLastNonConfigurationInstance();
        }
    }

    /**
     * retain image list
     */
    @Override
    @Deprecated
    public Object onRetainNonConfigurationInstance() {
        return mImageList;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(LINEAR_LAYOUT_STATE, String.valueOf(mLlDateTime.isShown()));
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.getString(LINEAR_LAYOUT_STATE).equals("true")) {
            mLlDateTime.setVisibility(View.VISIBLE);
            mTvAlarm.setVisibility(View.GONE);
        } else {
            mLlDateTime.setVisibility(View.GONE);
            mTvAlarm.setVisibility(View.VISIBLE);
        }
        mAdapter = new CustomGridViewImageAdapter(this, mImageList);
        mGvImage.setAdapter(mAdapter);
    }

    public void changeBackgroundColor() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.change_color_alert, null);
        mBuilder.setView(mView).setTitle(R.string.choose_color)
                .setIcon(R.drawable.ic_change_color_dark);
        Button btnWhite = (Button) mView.findViewById(R.id.btn_white);
        Button btnYellow = (Button) mView.findViewById(R.id.btn_yellow);
        Button btnGreen = (Button) mView.findViewById(R.id.btn_green);
        Button btnBlue = (Button) mView.findViewById(R.id.btn_blue);
        btnWhite.setOnClickListener(new ChangeColor(R.color.colorWhite, R.string.white));
        btnYellow.setOnClickListener(new ChangeColor(R.color.colorYellow, R.string.yellow));
        btnGreen.setOnClickListener(new ChangeColor(R.color.colorGreen, R.string.green));
        btnBlue.setOnClickListener(new ChangeColor(R.color.colorBlue, R.string.blue));
        mDialog = mBuilder.create();
        mDialog.show();
    }

    private class ChangeColor implements View.OnClickListener {
        private int mBackgroundColor;
        private int mBackgroundColorName;

        private ChangeColor(int color, int colorName) {
            mBackgroundColor = color;
            mBackgroundColorName = colorName;
        }

        @Override
        public void onClick(View v) {
            mRlNote.setBackgroundColor(
                    ContextCompat.getColor(getApplicationContext(), mBackgroundColor));
            mColor = getString(mBackgroundColorName);
            mDialog.dismiss();
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
            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        if (Build.VERSION.SDK_INT >= 23) {
                            if (hasStoragePermissionGranted()) {
                                if (hasPermissionCamera()) {
                                    captureImage();
                                } else {
                                    requestPermissionCamera();
                                }
                            } else {
                                requestStoragePermissionGranted();
                            }
                        } else {
                            captureImage();
                        }
                        mDialog.dismiss();
                        break;
                    case 1:
                        if (Build.VERSION.SDK_INT >= 23) {
                            if (hasPermission()) {
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
                        mDialog.dismiss();
                        break;
                }
            }
        });
        mDialog = mBuilder.create();
        mDialog.show();
    }

    //check read permission
    private boolean hasPermission() {
        int result = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    //request read permission
    private void requestPermission() {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    //check write permission
    public boolean hasStoragePermissionGranted() {
        int result = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    //request write permission
    private void requestStoragePermissionGranted() {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_REQUEST_CODE);
        }
    }

    //check camera permission
    private boolean hasPermissionCamera() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    // request camera permission
    private void requestPermissionCamera() {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    CAMERA_PERMISSION_REQUEST_CODE);
        }
    }

    public void captureImage() {
        Intent callCameraApplicationIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String fileName = "temp.jpg";
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, fileName);
        mCapturedImageURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        callCameraApplicationIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
        startActivityForResult(callCameraApplicationIntent, REQUEST_ID_IMAGE_CAPTURE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    captureImage();
                } else {
                    Toast.makeText(this, R.string.cannot_take_photo, Toast.LENGTH_SHORT).show();
                }
                break;
            case STORAGE_PERMISSION_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (hasPermissionCamera()) {
                        captureImage();
                    } else {
                        requestPermissionCamera();
                    }
                } else {
                    Toast.makeText(this, R.string.cannot_access_media_folder, Toast.LENGTH_SHORT).show();
                }
                break;
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, R.string.accepted, Toast.LENGTH_SHORT).show();
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
                    String[] projection = {MediaStore.Images.Media.DATA};
                    Cursor cursor = managedQuery(mCapturedImageURI, projection, null, null, null);
                    int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    String imagePath = cursor.getString(column_index_data);
                    mImageList.add(imagePath);
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
                    mImageList.add(filePath);
                    mGvImage.setAdapter(mAdapter);
                }
                break;
        }
    }

    public void createNotification(Note note, Calendar calendar) {
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        intent.putExtra(AlarmReceiver.ID, note.getNoteID());
        intent.putExtra(AlarmReceiver.TITLE, note.getNoteTitle());
        mPendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                note.getNoteID(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmManager.setExact(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(), mPendingIntent);
    }

    public Calendar getSelectedDateTime() {
        mSelectedTime = mSpnTime.getSelectedItem().toString();
        if (mSelectedDate.isEmpty()) {
            mSelectedDate = mSpnDate.getSelectedItem().toString();
        }
        String[] selectDate = mSelectedDate.split("/");
        String[] selectTime = mSelectedTime.split(":");
        int month = Integer.valueOf(selectDate[1]) - 1;
        int year = Integer.valueOf(selectDate[2]);
        int day = Integer.valueOf(selectDate[0]);
        int hour = Integer.valueOf(selectTime[0]);
        int minute = Integer.valueOf(selectTime[1]);
        int second = 0;
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute, second);
        return calendar;
    }

    class DateSpinnerInfo implements AdapterView.OnItemSelectedListener {
        private Context mContext;

        DateSpinnerInfo(Context context) {
            mContext = context;
        }

        @Override
        public void onItemSelected(AdapterView<?> spinner, View selectedView,
                                   int selectedIndex, long id) {
            String selectedDate = spinner.getItemAtPosition(selectedIndex).toString();
            switch (selectedIndex) {
                case 0:
                    mSelectedDate = String.valueOf(DateFormat.format(
                            getString(R.string.ddmmyyyy_format), new Date()));
                    SpinnerUtil.updateAdapterOfDateSpinner(dateAdapter,
                            getString(R.string.other), mListDate);
                    break;
                case 1:
                    mSelectedDate = DateUtil.getTomorrow();
                    SpinnerUtil.updateAdapterOfDateSpinner(dateAdapter,
                            getString(R.string.other), mListDate);
                    break;
                case 2:
                    mSelectedDate = DateUtil.getDayOfNextWeek();
                    SpinnerUtil.updateAdapterOfDateSpinner(dateAdapter,
                            getString(R.string.other), mListDate);
                    break;
                case 3:
                    mSelectedDate = "";
                    SpinnerUtil.chooseOptionOtherDate(mContext, mSpnDate,
                            dateAdapter, mListDate, selectedDate);
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> spinner) {
            //do something
        }
    }

    class TimeSpinnerInfo implements AdapterView.OnItemSelectedListener {
        private Context mContext;

        TimeSpinnerInfo(Context context) {
            mContext = context;
        }

        @Override
        public void onItemSelected(AdapterView<?> spinner, View selectedView,
                                   int selectedIndex, long id) {
            String selectedTime = spinner.getItemAtPosition(selectedIndex).toString();
            switch (selectedIndex) {
                case 0:
                    mSelectedTime = getString(R.string.hour_9h);
                    SpinnerUtil.updateAdapterOfTimeSpinner(timeAdapter,getString(R.string.other), mListTime);
                    break;
                case 1:
                    mSelectedTime = getString(R.string.hour_13h);
                    SpinnerUtil.updateAdapterOfTimeSpinner(timeAdapter,
                            getString(R.string.other), mListTime);
                    break;
                case 2:
                    mSelectedTime = getString(R.string.hour_17h);
                    SpinnerUtil.updateAdapterOfTimeSpinner(timeAdapter,
                            getString(R.string.other), mListTime);
                    break;
                case 3:
                    mSelectedTime = getString(R.string.hour_20h);
                    SpinnerUtil.updateAdapterOfTimeSpinner(timeAdapter,
                            getString(R.string.other), mListTime);
                    break;
                case 4:
                    SpinnerUtil.chooseOptionOtherTime(mContext, mSpnTime,
                            timeAdapter, mListTime, selectedTime);
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> spinner) {
            //do something
        }
    }
}