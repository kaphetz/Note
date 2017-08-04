package com.example.kienpt.note;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;


public class Popup extends Activity implements View.OnClickListener {
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_window);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        getWindow().setStatusBarColor(Color.GREEN);
        int width = 500;
        int height = 300;
        getWindow().setLayout((int) (width * .8), (int) (height * .6));
        Button btnWhite = (Button) findViewById(R.id.btn_white);
        Button btnYellow = (Button) findViewById(R.id.btn_yellow);
        Button btnGreen = (Button) findViewById(R.id.btn_green);
        Button btnBlue = (Button) findViewById(R.id.btn_blue);

        btnWhite.setOnClickListener(this);
        btnYellow.setOnClickListener(this);
        btnGreen.setOnClickListener(this);
        btnBlue.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        Intent activityIntent = new Intent(this, AddUpdateActivity.class);
        Bundle backgroundColorInfo = new Bundle();
        switch (view.getId()) {
            case R.id.btn_white:
                backgroundColorInfo.putString("backgroundColor", "White");
                activityIntent.putExtras(backgroundColorInfo);
                startActivity(activityIntent);
                break;
            case R.id.btn_yellow:
                backgroundColorInfo.putString("backgroundColor", "Yellow");
                activityIntent.putExtras(backgroundColorInfo);
                startActivity(activityIntent);
                break;
            case R.id.btn_green:
                backgroundColorInfo.putString("backgroundColor", "Green");
                activityIntent.putExtras(backgroundColorInfo);
                startActivity(activityIntent);
                break;
            case R.id.btn_blue:
                backgroundColorInfo.putString("backgroundColor", "Blue");
                activityIntent.putExtras(backgroundColorInfo);
                startActivity(activityIntent);
                break;
        }
    }
}
