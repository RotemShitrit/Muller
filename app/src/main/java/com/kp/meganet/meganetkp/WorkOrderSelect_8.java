package com.kp.meganet.meganetkp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class WorkOrderSelect_8 extends AppCompatActivity {

    private Button oldMeterButton;
    private Button newMeterButton;
    private Button submitButton;
    private Button exitButton;
    private TextView accTextView;
    String fileName;
    String dataFile;
    boolean submitFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_order_select_8);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        accTextView = (TextView)findViewById(R.id.textViewAcc);
        oldMeterButton  = (Button) findViewById(R.id.buttonOldMeter);
        newMeterButton  = (Button) findViewById(R.id.buttonNewMeter);
        submitButton  = (Button) findViewById(R.id.buttonSubmit);
        exitButton = (Button) findViewById(R.id.buttonBack);

        fileName = getIntent().getStringExtra("file name");
        dataFile = getIntent().getStringExtra("data file");

        //MeganetInstances.getInstance().GetMeganetEngine().UseOldSnRead(false);
        accTextView.setText( MeganetInstances.getInstance().GetMeganetEngine().GetAddressName() + "  |  " +
                MeganetInstances.getInstance().GetMeganetEngine().GetAccountNumber() );
        accTextView.setTextSize(20);
        accTextView.setGravity(Gravity.CENTER_HORIZONTAL);

        oldMeterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                Toast.makeText(getApplicationContext(), "Work Order", Toast.LENGTH_LONG).show();
                intent = new Intent(WorkOrderSelect_8.this, WorkOrderSelect_9.class);
                intent.putExtra("file name", fileName);
                intent.putExtra("data file", dataFile);
                startActivity(intent);
            }
        });

        newMeterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Work Order", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(WorkOrderSelect_8.this, WorkOrderSelect_4.class);
                intent.putExtra("file name", fileName);
                intent.putExtra("data file", dataFile);
                startActivity(intent);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog();
                submitFlag = true;
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MeganetInstances.getInstance().GetMeganetEngine().SetLastOldAccRead(null);
                MeganetInstances.getInstance().GetMeganetEngine().SetLastOldAccSN(null);
                MeganetInstances.getInstance().GetMeganetEngine().SetLastAccSN(null);
                MeganetInstances.getInstance().GetMeganetEngine().SetLastAccRead(null);
                MeganetInstances.getInstance().GetMeganetEngine().SetUnitAddress(null);
                if(!submitFlag)
                    ExitWithoutSubmit();
                else {
                    Intent intent = new Intent(WorkOrderSelect_8.this, WorkOrderSelect_3.class);
                    intent.putExtra("file name", fileName);
                    intent.putExtra("data file", dataFile);
                    startActivity(intent);
                }
            }
        });

    }
    private void alertDialog() {
        AlertDialog.Builder dialog=new AlertDialog.Builder(WorkOrderSelect_8.this);
        String result_str = MeganetInstances.getInstance().GetMeganetEngine().GetAddressName() + ", " +
                MeganetInstances.getInstance().GetMeganetEngine().GetAccountNumber() + ", " +
                MeganetInstances.getInstance().GetMeganetEngine().GetLastOldAccSN() + ", " +
                MeganetInstances.getInstance().GetMeganetEngine().GetLastOldAccRead() + ", " +
                MeganetInstances.getInstance().GetMeganetEngine().GetUnitAddress() + ", " +
                MeganetInstances.getInstance().GetMeganetEngine().GetLastAccSN() + ", " +
                MeganetInstances.getInstance().GetMeganetEngine().GetLastAccRead() + ", " +
                MeganetInstances.getInstance().GetMeganetEngine().GetLastLatitude() + ", " +
                MeganetInstances.getInstance().GetMeganetEngine().GetLastLongitude();

        String[] splited = dataFile.split("\n");
        boolean find = false;
        int i = 0;
        while (!find)
        {
            if(splited[i].startsWith(MeganetInstances.getInstance().GetMeganetEngine().GetAddressName()))
            {
                find = true;
                splited[i] = result_str;
                dataFile = TextUtils.join("\n", splited);
                break;
            }
            i++;
        }
        dialog.setTitle("Submited! ");
        dialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        //Toast.makeText(getApplicationContext(),"Yes is clicked",Toast.LENGTH_LONG).show();
                    }
                });

        AlertDialog alertDialog=dialog.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(WorkOrderSelect_8.this, WorkOrderSelect_3.class);
        intent.putExtra("file name", fileName);
        intent.putExtra("data file", dataFile);
        startActivity(intent);
    }

    private void ExitWithoutSubmit()
    {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(WorkOrderSelect_8.this);
        builder.setMessage("Are you sure you want to exit without Submit??")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(WorkOrderSelect_8.this, WorkOrderSelect_3.class);
                        intent.putExtra("file name", fileName); // save file name
                        intent.putExtra("data file", dataFile); // save data file
                        startActivity(intent);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // some code if you want
                        dialog.dismiss();
                    }
                });
        android.app.AlertDialog alert = builder.create();
        alert.show();

        Button bq = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        bq.setBackgroundColor(Color.WHITE);
        bq.setTextColor(Color.BLUE);

        bq = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        bq.setBackgroundColor(Color.WHITE);
        bq.setTextColor(Color.BLUE);
    }
}
