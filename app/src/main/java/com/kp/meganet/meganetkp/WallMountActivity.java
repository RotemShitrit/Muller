package com.kp.meganet.meganetkp;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class WallMountActivity extends AppCompatActivity {

    private Button buttonScan1;
    private Button buttonScan2;
    private Button buttonScan3;
    private Button buttonScan4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wall_mount);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        buttonScan1 = (Button) findViewById(R.id.buttonScan4);
        buttonScan2 = (Button) findViewById(R.id.buttonScan);
        buttonScan3 = (Button) findViewById(R.id.buttonScan6);
        buttonScan4 = (Button) findViewById(R.id.buttonScan2);

        buttonScan1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent;
                //super.onBackPressed();

                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.NONE);

                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentProgrammType(3);

                intent = new Intent(WallMountActivity.this, ProgrammActivity.class);
                startActivity(intent);
            }
        });

        buttonScan2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent;
                //super.onBackPressed();

                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.NONE);

                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentProgrammType(4);

                intent = new Intent(WallMountActivity.this, ProgrammActivity.class);
                startActivity(intent);
            }
        });

        buttonScan3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent;
                //super.onBackPressed();

                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.NONE);

                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentProgrammType(44);

                intent = new Intent(WallMountActivity.this, ProgrammActivity.class);
                startActivity(intent);
            }
        });

        buttonScan4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent;
                //super.onBackPressed();

                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.NONE);

                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentProgrammType(5);

                intent = new Intent(WallMountActivity.this, ProgrammActivity.class);
                startActivity(intent);
            }
        });
    }
}
