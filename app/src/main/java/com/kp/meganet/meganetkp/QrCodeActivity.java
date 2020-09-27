package com.kp.meganet.meganetkp;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class QrCodeActivity extends AppCompatActivity   implements View.OnClickListener{

    //View Objects
    private Button buttonScan;
    private Button buttonScan3;
    private Button buttonScan4;
    private TextView textViewName, textViewAddress;

    //qr code scanner object
    private IntentIntegrator qrScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //View objects
        buttonScan = (Button) findViewById(R.id.buttonScan);
        textViewName = (TextView) findViewById(R.id.textViewName);
        textViewAddress = (TextView) findViewById(R.id.textViewAddress);
        buttonScan3 = (Button) findViewById(R.id.button3);
        buttonScan4 = (Button) findViewById(R.id.button4);
        //intializing scan object
        qrScan = new IntentIntegrator(this);

        //attaching onclick listener
        buttonScan.setOnClickListener(this);

        buttonScan3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent;
                //super.onBackPressed();
                //Toast.makeText(getApplicationContext(), "QR Code", Toast.LENGTH_LONG).show();

                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.NONE);

                intent = new Intent(QrCodeActivity.this, PitActivity.class);
                startActivity(intent);
            }
        });

        buttonScan4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent;
                //super.onBackPressed();
                //Toast.makeText(getApplicationContext(), "QR Code", Toast.LENGTH_LONG).show();

                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.NONE);

                intent = new Intent(QrCodeActivity.this, WallMountActivity.class);
                startActivity(intent);
            }
        });
    }

    //Getting the scan results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        String rslt;
        String rslt1;
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                //if qr contains data
                try {
                    //converting the data to json
                    //JSONObject obj = new JSONObject(result.getContents());
                    //setting values to textviews
                    //textViewName.setText(obj.getString("name"));
                    //textViewAddress.setText(obj.getString("address"));

                    Intent intent;
                    //super.onBackPressed();
                    //Toast.makeText(getApplicationContext(), "QR Code", Toast.LENGTH_LONG).show();

                    MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.NONE);


                    rslt = result.getContents();

                    if(rslt.substring(0, 5).equals("MN-W1") && rslt.substring(10, 11).equals("E")){
                        // MN-W1x-xxxE-xx	W

                            MeganetInstances.getInstance().GetMeganetEngine().SetCurrentProgrammType(3);
                    }

                    else if(rslt.substring(0, 5).equals("MN-W2") ) // MN-W2x-xxxx-xx	M
                        MeganetInstances.getInstance().GetMeganetEngine().SetCurrentProgrammType(4);
                    else if(rslt.substring(0, 5).equals("MN-W3") ) // MN-W3x-xxxx-xx	E
                        MeganetInstances.getInstance().GetMeganetEngine().SetCurrentProgrammType(44);
                    else if(rslt.substring(0, 5).equals("MN-W1") && rslt.substring(10, 11).equals("D")){
                        // MN-W1x-xxxD-xx	MTWP

                            MeganetInstances.getInstance().GetMeganetEngine().SetCurrentProgrammType(5);
                    }

                    else if(rslt.substring(0, 5).equals("MN-P1")) // MN-P1x-xxxx-xx	P
                        MeganetInstances.getInstance().GetMeganetEngine().SetCurrentProgrammType(1);
                    else if(rslt.substring(0, 5).equals("MN-P2")) // MN-P2x-xxxx-xx	M
                        MeganetInstances.getInstance().GetMeganetEngine().SetCurrentProgrammType(2);
                    else if(rslt.substring(0, 5).equals("MN-P3")) // MN-P3x-xxxx-xx	E
                        MeganetInstances.getInstance().GetMeganetEngine().SetCurrentProgrammType(22);

                    intent = new Intent(QrCodeActivity.this, ProgrammActivity.class);
                    startActivity(intent);

                    //JSONObject obj = new JSONObject(result.getContents());






                } catch (NumberFormatException nfex) {
                   // ex.printStackTrace();
                    //if control comes here
                    //that means the encoded format not matches
                    //in this case you can display whatever data is available on the qrcode
                    //to a toast
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    @Override
    public void onClick(View view) {
        //initiating the qr code scan
        qrScan.initiateScan();
    }

}
