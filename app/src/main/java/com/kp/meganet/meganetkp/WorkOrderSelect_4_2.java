package com.kp.meganet.meganetkp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class WorkOrderSelect_4_2 extends AppCompatActivity {

    String fileName;
    String dataFile;

    private Button buttonScan1;
    private Button buttonScan2;
    private Button buttonScan3;
    private Button buttonScan4;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wall_mount);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        fileName = getIntent().getStringExtra("file name");
        dataFile = getIntent().getStringExtra("data file");

        buttonScan1 = (Button) findViewById(R.id.buttonScan4);
        buttonScan2 = (Button) findViewById(R.id.buttonScan);
        buttonScan3 = (Button) findViewById(R.id.buttonScan6);
        buttonScan4 = (Button) findViewById(R.id.buttonScan2);

        buttonScan1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.NONE);
                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentProgrammType(3);

                LayoutInflater layoutInflater = LayoutInflater.from(WorkOrderSelect_4_2.this);
                View promptView = layoutInflater.inflate(R.layout.enter_id, null);
                final EditText input = (EditText) promptView.findViewById(R.id.edittext);
                input.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);

                AlertDialog dialog = new AlertDialog.Builder(WorkOrderSelect_4_2.this)
                        .setView(promptView)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String id = input.getText().toString();
                                MeganetInstances.getInstance().GetMeganetEngine().SetQrAddress(Utilities.StringCompleter(id, 7, "0", true));
                                Intent intent = new Intent(WorkOrderSelect_4_2.this, WorkOrderSelect_5.class);
                                intent.putExtra("file name", fileName);
                                intent.putExtra("data file", dataFile);
                                startActivity(intent);
                                finish();
                            }
                        }).show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
            }
        });

        buttonScan2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.NONE);
                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentProgrammType(4);

                LayoutInflater layoutInflater = LayoutInflater.from(WorkOrderSelect_4_2.this);
                View promptView = layoutInflater.inflate(R.layout.enter_id, null);
                final EditText input = (EditText) promptView.findViewById(R.id.edittext);
                input.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);

                AlertDialog dialog = new AlertDialog.Builder(WorkOrderSelect_4_2.this)
                        .setView(promptView)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String id = input.getText().toString();
                                MeganetInstances.getInstance().GetMeganetEngine().SetQrAddress(Utilities.StringCompleter(id, 7, "0", true));
                                Intent intent = new Intent(WorkOrderSelect_4_2.this, WorkOrderSelect_5.class);
                                intent.putExtra("file name", fileName);
                                intent.putExtra("data file", dataFile);
                                startActivity(intent);
                                finish();
                            }
                        }).show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
            }
        });

        buttonScan3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.NONE);
                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentProgrammType(44);

                LayoutInflater layoutInflater = LayoutInflater.from(WorkOrderSelect_4_2.this);
                View promptView = layoutInflater.inflate(R.layout.enter_id, null);
                final EditText input = (EditText) promptView.findViewById(R.id.edittext);
                input.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);

                AlertDialog dialog = new AlertDialog.Builder(WorkOrderSelect_4_2.this)
                        .setView(promptView)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String id = input.getText().toString();
                                MeganetInstances.getInstance().GetMeganetEngine().SetQrAddress(Utilities.StringCompleter(id, 7, "0", true));
                                Intent intent = new Intent(WorkOrderSelect_4_2.this, WorkOrderSelect_5.class);
                                intent.putExtra("file name", fileName);
                                intent.putExtra("data file", dataFile);
                                startActivity(intent);
                                finish();
                            }
                        }).show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
            }
        });

        buttonScan4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.NONE);
                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentProgrammType(5);

                LayoutInflater layoutInflater = LayoutInflater.from(WorkOrderSelect_4_2.this);
                View promptView = layoutInflater.inflate(R.layout.enter_id, null);
                final EditText input = (EditText) promptView.findViewById(R.id.edittext);
                input.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);

                AlertDialog dialog = new AlertDialog.Builder(WorkOrderSelect_4_2.this)
                        .setView(promptView)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String id = input.getText().toString();
                                MeganetInstances.getInstance().GetMeganetEngine().SetQrAddress(Utilities.StringCompleter(id, 7, "0", true));
                                Intent intent = new Intent(WorkOrderSelect_4_2.this, WorkOrderSelect_5.class);
                                intent.putExtra("file name", fileName);
                                intent.putExtra("data file", dataFile);
                                startActivity(intent);
                                finish();
                            }
                        }).show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(WorkOrderSelect_4_2.this, WorkOrderSelect_4.class);
        intent.putExtra("file name", fileName);
        intent.putExtra("data file", dataFile);
        startActivity(intent);
        finish();
    }
}
