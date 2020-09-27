package com.kp.meganet.meganetkp;

import android.content.pm.ActivityInfo;
//import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class PulseActivity extends AppCompatActivity implements iPulseCallback{

    private Button readPortButton;
    private Button writePortButton;
    private Button closeButton;

    private EditText portSNeditText;
    private EditText portReadeditText;

    private RadioButton Port1rbo;
    private RadioButton Port2rbo;
    private RadioButton Port3rbo;
    private RadioButton Port4rbo;

    private String _toastMessageToDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pulse);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        readPortButton  = (Button) findViewById(R.id.buttonReadPort);
        writePortButton  = (Button) findViewById(R.id.buttonWretePort);
        closeButton  = (Button) findViewById(R.id.buttonClose);

        portSNeditText  = (EditText) findViewById(R.id.editTextPortSN);
        portReadeditText  = (EditText) findViewById(R.id.editTextPortRead);

        Port1rbo  = (RadioButton) findViewById(R.id.rboPort1);
        Port2rbo  = (RadioButton) findViewById(R.id.rboPort2);
        Port3rbo  = (RadioButton) findViewById(R.id.rboPort3);
        Port4rbo  = (RadioButton) findViewById(R.id.rboPort4);


        MeganetInstances.getInstance().GetMeganetEngine().InitPulse(this);

        readPortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int portNumVal = 1;
                if (Port1rbo.isChecked())
                {
                    portNumVal = 1;
                }
                else if(Port2rbo.isChecked())
                {
                    portNumVal = 2;
                }
                else if(Port3rbo.isChecked())
                {
                    portNumVal = 3;
                }
                else
                {
                    portNumVal = 4;
                }


                MeganetInstances.getInstance().GetMeganetEngine().ReadPulsePort(portNumVal);

            }
        });

        writePortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int portNumVal = 1;
                if (Port1rbo.isChecked())
                {
                    portNumVal = 1;
                }
                else if(Port2rbo.isChecked())
                {
                    portNumVal = 2;
                }
                else if(Port3rbo.isChecked())
                {
                    portNumVal = 3;
                }
                else
                {
                    portNumVal = 4;
                }

                MeganetInstances.getInstance().GetMeganetEngine().WritePulsePort(portNumVal, Integer.valueOf(portSNeditText.getText().toString()), Integer.valueOf(portReadeditText.getText().toString()));

            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



            }
        });
    }

    @Override
    public void OnRead(String serial_num, String reading)
    {
        portSNeditText.setText(serial_num);
        portReadeditText.setText(reading);
    }
    @Override
    public void OnErrorRead(String error)
    {

    }
    @Override
    public void OnMessageCb(String message_prm) {
    }

    @Override
    public boolean PairData(String deviceName_prm, String ndevice_pam, boolean titleOnly)
    {
        return true;
    }
    @Override
    public void OnWriteResult(boolean result)
    {
        if (result)
            _toastMessageToDisplay = "Disconnect successfully ";
        else
            _toastMessageToDisplay = "Disconnect successfully ";

        this.runOnUiThread(new Runnable() {
            public void run() {
                // Access/update UI here
                Toast.makeText(getApplicationContext(), _toastMessageToDisplay,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
