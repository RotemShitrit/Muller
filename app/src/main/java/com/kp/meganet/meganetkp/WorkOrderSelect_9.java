package com.kp.meganet.meganetkp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class WorkOrderSelect_9 extends AppCompatActivity {

    EditText snEditText;
    EditText readEditText;
    Button btnSave;
    String fileName;
    String dataFile;

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(WorkOrderSelect_9.this, WorkOrderSelect_8.class);
        intent.putExtra("file name", fileName);
        intent.putExtra("data file", dataFile);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_order_select_9);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        snEditText = (EditText)findViewById(R.id.textViewSn);
        readEditText = (EditText)findViewById(R.id.textViewRead);

        snEditText.setText(MeganetInstances.getInstance().GetMeganetEngine().GetLastOldAccSN());
        readEditText.setText(MeganetInstances.getInstance().GetMeganetEngine().GetLastOldAccRead());

        fileName = getIntent().getStringExtra("file name");
        dataFile = getIntent().getStringExtra("data file");
        btnSave  = (Button) findViewById(R.id.buttonSave);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //MeganetInstances.getInstance().GetMeganetEngine().UseOldSnRead(true);
                MeganetInstances.getInstance().GetMeganetEngine().SetLastOldAccRead(readEditText.getText().toString());
                MeganetInstances.getInstance().GetMeganetEngine().SetLastOldAccSN(snEditText.getText().toString());

                Toast.makeText(getApplicationContext(), "Saved!", Toast.LENGTH_LONG).show();
                //MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.NONE);
                Intent intent = new Intent(WorkOrderSelect_9.this, WorkOrderSelect_8.class);
                intent.putExtra("file name", fileName);
                intent.putExtra("data file", dataFile);
                startActivity(intent);
            }
        });
    }


}
