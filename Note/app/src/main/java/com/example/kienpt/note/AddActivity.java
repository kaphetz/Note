package com.example.kienpt.note;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.kienpt.note.bean.Note;

import java.util.ArrayList;
import java.util.Calendar;

@RequiresApi(api = Build.VERSION_CODES.N)
public class AddActivity extends ActivityParent {
    private static final int REQUEST_ID_IMAGE_CAPTURE = 1;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 2;
    private static final int PERMISSION_REQUEST_CODE = 3;
    private static final int SELECT_IMAGE = 4;
    private Integer[] mImageList = {R.drawable.ic_take_photo, R.drawable.ic_choose_photo};
    private String[] mSourceNameList = {"Take photo", "Choose photo"};
    private CustomGridViewImageAdapter mAdapter;
   private ArrayList<Bitmap> mListImages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        getActionBar().setHomeAsUpIndicator(R.drawable.ic_left);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(true);
        getActionBar().setIcon(R.mipmap.ic_launcher);
        getActionBar().setBackgroundDrawable(
                new ColorDrawable(getResources().getColor(R.color.colorSky)));
        getActionBar().setTitle(getString(R.string.note));
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
        mTvDateTime.setText(convert(now, getString(R.string.ddmmyyyy_format)));
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
            case R.id.mn_save:
                addNewNote();
                return true;
            case R.id.mn_change_color:
                changeBackgroundColor();
                return true;
            case R.id.mn_camera:
                insertImage();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private class DateSpinnerInfo implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> spinner, View selectedView,
                                   int selectedIndex, long id) {
            String selectedDate = spinner.getItemAtPosition(selectedIndex).toString();
            switch (selectedIndex) {
                case 0:
                    SimpleDateFormat df = new SimpleDateFormat(getString(R.string.ddmmyyyy_format));
                    mSelectedDate = df.format(mCalendar.getTime());
                    updateAdapterForDateSpinner(getString(R.string.other));
                    break;
                case 1:
                    mSelectedDate = getTomorrow();
                    updateAdapterForDateSpinner(getString(R.string.other));
                    break;
                case 2:
                    mSelectedDate = getDayOfNextWeek();
                    updateAdapterForDateSpinner(getString(R.string.other));
                    break;
                case 3:
                    chooseOptionOtherDate(AddActivity.this, selectedDate);
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
                    mSelectedTime = getString(R.string.hour_9h);
                    updateAdapterForTimeSpinner(getString(R.string.other));
                    break;
                case 1:
                    mSelectedTime = getString(R.string.hour_13h);
                    updateAdapterForTimeSpinner(getString(R.string.other));
                    break;
                case 2:
                    mSelectedTime = getString(R.string.hour_17h);
                    updateAdapterForTimeSpinner(getString(R.string.other));
                    break;
                case 3:
                    mSelectedTime = getString(R.string.hour_20h);
                    updateAdapterForTimeSpinner(getString(R.string.other));
                    break;
                case 4:
                    chooseOptionOtherTime(AddActivity.this, selectedTime);
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> spinner) {
            //do something
        }
    }

    public void addNewNote() {
        Note note = new Note();
        if (!mEtTitle.getText().toString().equals("")) {
            note.setNoteTitle(mEtTitle.getText().toString());
        } else {
            note.setNoteTitle(getString(R.string.untitled));
        }
        note.setNoteContent(mEtContent.getText().toString());
        if (mLlDateTime.getVisibility() == View.VISIBLE) {
            note.setNoteTime(String.format("%s %s", mSelectedDate, mSelectedTime));
        } else {
            note.setNoteTime("");
        }
        //get create datetime
        mCalendar = Calendar.getInstance();
        note.setCreatedTime(String.format("%s", convert(mCalendar,
                getString(R.string.ddmmyyyy_hhmm_format))));
        note.setBackgroundColor(mColor);
        // add new note into db
        NoteRepo dbNote = new NoteRepo();
        dbNote.addNote(note);
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
    }

    public void insertImage() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.take_photo_alert, null);
        mBuilder.setView(mView).setTitle(R.string.insert_picture);
        mLvCamera = (ListView) mView.findViewById(R.id.lv_camera);
        CustomListViewAdapter adapter = new CustomListViewAdapter(AddActivity.this,
                mSourceNameList, mImageList);
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
        int result = ContextCompat.checkSelfPermission(AddActivity.this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(AddActivity.this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(AddActivity.this,
                    "Write External Storage permission allows us to access images. " +
                            "Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(AddActivity.this, new String[]{
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
        switch (requestCode) {
            case REQUEST_ID_IMAGE_CAPTURE:
                if (resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    mListImages.add(imageBitmap);
                    mAdapter = new CustomGridViewImageAdapter(AddActivity.this, mListImages);
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
                    ImageView img = (ImageView) findViewById(R.id.img_photo);
                    img.setImageBitmap(BitmapFactory.decodeFile(filePath));
                }
                break;
        }
    }


}
