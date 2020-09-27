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
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class History_Log_1_1 extends AppCompatActivity { //Present 2 option for set ID of device (QR scan or Manual)

    private Button pitBtn,wallBtn, qrScanBtn;
    private IntentIntegrator qr_scan_intent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        qr_scan_intent = new IntentIntegrator(this); //for activate the QR code
        pitBtn = (Button) findViewById(R.id.button3);
        wallBtn = (Button) findViewById(R.id.button4);
        qrScanBtn = (Button) findViewById(R.id.buttonScan);

        pitBtn.setOnClickListener(new View.OnClickListener() {
            // The option to set unitID manually for pit device
            @Override
            public void onClick(View v) {
                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentProgrammType(22);

                LayoutInflater layoutInflater = LayoutInflater.from(History_Log_1_1.this);
                View promptView = layoutInflater.inflate(R.layout.enter_id, null);
                final EditText input = (EditText) promptView.findViewById(R.id.edittext); // Set an EditText view to get the user input
                input.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);

                AlertDialog dialog = new AlertDialog.Builder(History_Log_1_1.this) //Alert dialog for inserting unitID of pit
                        .setView(promptView)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String id = input.getText().toString();
                                MeganetInstances.getInstance().GetMeganetEngine().SetQrAddress(id);
                                Intent intent = new Intent(History_Log_1_1.this, History_Log_2.class);
                                startActivity(intent);
                                finish();
                            }
                        }).show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
            }
        });
        wallBtn.setOnClickListener(new View.OnClickListener() {
            // The option to set unitID manually for wall device
            @Override
            public void onClick(View v) {
                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentProgrammType(44);

                LayoutInflater layoutInflater = LayoutInflater.from(History_Log_1_1.this);
                View promptView = layoutInflater.inflate(R.layout.enter_id, null);
                final EditText input = (EditText) promptView.findViewById(R.id.edittext); // Set an EditText view to get user input
                input.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);

                AlertDialog dialog = new AlertDialog.Builder(History_Log_1_1.this) //Alert dialog for inserting unitID of wall
                        .setView(promptView)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String id = input.getText().toString();
                                MeganetInstances.getInstance().GetMeganetEngine().SetQrAddress(id);
                                Intent intent = new Intent(History_Log_1_1.this, History_Log_2.class);
                                startActivity(intent);
                                finish();
                            }
                        }).show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
            }
        });

        qrScanBtn.setOnClickListener(new View.OnClickListener() {
            // The option to get unitID by scan QR code
            @Override
            public void onClick(View v) {
                qr_scan_intent.initiateScan();
            }
        });

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) { //Decode the QR code and update details in MeganetInstances
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        String rslt;
        String rslt1;
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(History_Log_1_1.this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                //if qr contains data
                try {
                    //converting the data to json
                    //JSONObject obj = new JSONObject(result.getContents());
                    //setting values to textviews
                    //textViewName.setText(obj.getString("name"));
                    //textViewAddress.setText(obj.getString("address"));


                    //super.onBackPressed();
                    //Toast.makeText(getApplicationContext(), "QR Code", Toast.LENGTH_LONG).show();

                    MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.NONE);

                    MeganetInstances.getInstance().GetMeganetEngine().SetQrAddress("");
                    rslt = result.getContents();
                    MeganetInstances.getInstance().GetMeganetEngine().SetQrAddress(rslt.substring(rslt.length() - 7));

                    if (rslt.substring(0, 5).equals("MN-W1") && rslt.substring(10, 11).equals("E")) {
                        // MN-W1x-xxxE-xx	W

                        MeganetInstances.getInstance().GetMeganetEngine().SetCurrentProgrammType(3);
                    } else if (rslt.substring(0, 5).equals("MN-W2")) // MN-W2x-xxxx-xx	M
                        MeganetInstances.getInstance().GetMeganetEngine().SetCurrentProgrammType(4);
                    else if (rslt.substring(0, 5).equals("MN-W3")) // MN-W3x-xxxx-xx	E
                        MeganetInstances.getInstance().GetMeganetEngine().SetCurrentProgrammType(44);
                    else if (rslt.substring(0, 5).equals("MN-W1") && rslt.substring(10, 11).equals("D")) {
                        // MN-W1x-xxxD-xx	MTWP

                        MeganetInstances.getInstance().GetMeganetEngine().SetCurrentProgrammType(5);
                    } else if (rslt.substring(0, 5).equals("MN-P1")) // MN-P1x-xxxx-xx	P
                        MeganetInstances.getInstance().GetMeganetEngine().SetCurrentProgrammType(1);
                    else if (rslt.substring(0, 5).equals("MN-P2")) // MN-P2x-xxxx-xx	M
                        MeganetInstances.getInstance().GetMeganetEngine().SetCurrentProgrammType(2);
                    else if (rslt.substring(0, 5).equals("MN-P3")) // MN-P3x-xxxx-xx	E
                        MeganetInstances.getInstance().GetMeganetEngine().SetCurrentProgrammType(22);
                    Intent intent = new Intent(History_Log_1_1.this, History_Log_2.class);
                    startActivity(intent);
                    finish();

                    //JSONObject obj = new JSONObject(result.getContents());


                } catch (NumberFormatException nfex) {
                    // ex.printStackTrace();
                    //if control comes here
                    //that means the encoded format not matches
                    //in this case you can display whatever data is available on the qrcode
                    //to a toast
                    Toast.makeText(History_Log_1_1.this, result.getContents(), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
