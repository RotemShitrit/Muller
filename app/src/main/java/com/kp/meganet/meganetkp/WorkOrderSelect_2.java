package com.kp.meganet.meganetkp;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class WorkOrderSelect_2 extends AppCompatActivity {

    private Button refreshButton;
    private Button exitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) { // If there is no network so we can't connect to FTP, then we need to find network first.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_order_select_2);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        refreshButton = (Button)findViewById(R.id.buttonRefresh);
        exitButton = (Button)findViewById(R.id.buttonExit2);

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                ConnectivityManager cm = (ConnectivityManager) getSystemService(WorkOrderSelect_2.CONNECTIVITY_SERVICE); // get current state of network
                if (cm.getActiveNetworkInfo() != null) { // If there is network after clicking refresh button
                    intent = new Intent(WorkOrderSelect_2.this, WorkOrderSelect_1.class);
                }
                else { // Still don't have a network
                    intent = new Intent(WorkOrderSelect_2.this, WorkOrderSelect_2.class);
                }
                Toast.makeText(getApplicationContext(), "Work Order", Toast.LENGTH_LONG).show();
                //MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.NONE);
                startActivity(intent);
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
