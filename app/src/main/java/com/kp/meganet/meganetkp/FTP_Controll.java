package com.kp.meganet.meganetkp;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FTP_Controll extends AppCompatActivity {

    private static final int port = 21;

    EditText ftpIP;
    EditText ftpUser;
    EditText ftpPass;
    EditText ftpPort;

    Button ftpSave;
    Button ftpConnecting;
    Button ftpBack;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ftp);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ftpIP = (EditText) findViewById(R.id.IP_FTP);
        ftpUser = (EditText) findViewById(R.id.USER_FTP);
        ftpPass = (EditText) findViewById(R.id.PASS_FTP);
        ftpPort = (EditText) findViewById(R.id.PORT_FTP);
        ftpSave = (Button) findViewById(R.id.buttonSave);
        ftpBack = (Button) findViewById(R.id.buttonBack);

        //getting shared preferences
        SharedPreferences sp = getSharedPreferences("ftp", MODE_PRIVATE);

        //getting values if exist
        ftpIP.setText(sp.getString("host", "host"));
        ftpUser.setText(sp.getString("username", "username"));
        ftpPass.setText(sp.getString("password", "password"));
        ftpPort.setText(String.valueOf(sp.getInt("port", (int) 2l)));

        ftpSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getting shared preferences
                SharedPreferences sp = getSharedPreferences("ftp", MODE_PRIVATE);

                //initializing editor
                SharedPreferences.Editor editor = sp.edit();

                editor.putString("host", ftpIP.getText().toString());
                editor.putString("username", ftpUser.getText().toString());
                editor.putString("password", ftpPass.getText().toString());
                editor.putInt("port",  Integer.valueOf(ftpPort.getText().toString())) ;
                editor.apply();

                Toast.makeText(getApplicationContext(), "FTP details saved!",
                        Toast.LENGTH_SHORT).show();
            }
        });
        ftpBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
