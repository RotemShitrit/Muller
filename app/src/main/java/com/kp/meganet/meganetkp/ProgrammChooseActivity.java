package com.kp.meganet.meganetkp;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ProgrammChooseActivity extends AppCompatActivity {

    //View Objects
    private Button buttonScan;

    private Button buttonScan3;
    private Button buttonScan4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_programm_choose);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        buttonScan = (Button) findViewById(R.id.buttonScan);


    }



}
