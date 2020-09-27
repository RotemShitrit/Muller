package com.kp.meganet.meganetkp;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class PitActivity extends AppCompatActivity {

    private Button buttonScan1;
    private Button buttonScan2;
    private Button buttonScan3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pit);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        buttonScan1 = (Button) findViewById(R.id.buttonScan);
        buttonScan2 = (Button) findViewById(R.id.buttonScan3);
        buttonScan3 = (Button) findViewById(R.id.buttonScan5);

        buttonScan1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent;
                //super.onBackPressed();
                //Toast.makeText(getApplicationContext(), "QR Code", Toast.LENGTH_LONG).show();

                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.NONE);

                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentProgrammType(1);

                intent = new Intent(PitActivity.this, ProgrammActivity.class);
                startActivity(intent);
            }
        });

        buttonScan2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent;
                //super.onBackPressed();
               // Toast.makeText(getApplicationContext(), "QR Code", Toast.LENGTH_LONG).show();

                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.NONE);

                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentProgrammType(2);

                intent = new Intent(PitActivity.this, ProgrammActivity.class);
                startActivity(intent);
            }
        });

        buttonScan3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent;
                //super.onBackPressed();
                //Toast.makeText(getApplicationContext(), "QR Code", Toast.LENGTH_LONG).show();

                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.NONE);

                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentProgrammType(22);

                intent = new Intent(PitActivity.this, ProgrammActivity.class);
                startActivity(intent);
            }
        });
    }
}
