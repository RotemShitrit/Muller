package com.kp.meganet.meganetkp;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class WorkOrderSelect_4 extends AppCompatActivity {
    private IntentIntegrator qrScan;
    private Button pit,wall,qr;

    String fileName;
    String dataFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        qrScan = new IntentIntegrator(this);
        fileName = getIntent().getStringExtra("file name");
        dataFile = getIntent().getStringExtra("data file");

        pit = (Button) findViewById(R.id.button3);
        wall = (Button) findViewById(R.id.button4);
        qr = (Button) findViewById(R.id.buttonScan);

        pit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WorkOrderSelect_4.this, WorkOrderSelect_4_1.class);
                intent.putExtra("file name", fileName);
                intent.putExtra("data file", dataFile);
                startActivity(intent);
                finish();
            }
        });

        wall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WorkOrderSelect_4.this, WorkOrderSelect_4_2.class);
                intent.putExtra("file name", fileName);
                intent.putExtra("data file", dataFile);
                startActivity(intent);
                finish();
            }
        });

        qr.setOnClickListener(new View.OnClickListener() { //Open scanner and scan QR code
            @Override
            public void onClick(View v) {
                qrScan.initiateScan(); // activate camera for QR scan
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(WorkOrderSelect_4.this, WorkOrderSelect_8.class);
        intent.putExtra("file name", fileName);
        intent.putExtra("data file", dataFile);
        startActivity(intent);
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { //Decode the scan code and update the details in MeganetInstance
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

                    Intent intent = new Intent(WorkOrderSelect_4.this, WorkOrderSelect_5.class);
                    intent.putExtra("file name", fileName);
                    intent.putExtra("data file", dataFile);
                    startActivity(intent);
                    finish();

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
}
