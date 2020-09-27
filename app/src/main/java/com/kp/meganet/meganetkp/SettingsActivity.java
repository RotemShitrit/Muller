package com.kp.meganet.meganetkp;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    EditText hhidEditText;
    EditText fieldVerifFreq1EditText;
    EditText fieldVerifFreq2EditText;
    EditText readMeterFreqEditText;
    EditText RDMFreqEditText;
    EditText ranmanUrlEditText;
    TextView appVer;
    TextView rsntVer;
    Button applyButton;
    CheckBox kpackCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        hhidEditText = (EditText)findViewById(R.id.editTextHHID);
        fieldVerifFreq1EditText = (EditText)findViewById(R.id.editTextFieldVerifFreq1);
        fieldVerifFreq2EditText = (EditText)findViewById(R.id.editTextFieldVerifFreq2);
        readMeterFreqEditText = (EditText)findViewById(R.id.editTextReadMeterFreq);
        RDMFreqEditText = (EditText)findViewById(R.id.editTextReadRDMFreq);
        ranmanUrlEditText = (EditText)findViewById(R.id.editTextRanManURL);
        appVer = (TextView)findViewById(R.id.textViewAppver);
        rsntVer = (TextView)findViewById(R.id.textViewRSNTver);
        applyButton = (Button)findViewById(R.id.buttonApply);
        kpackCheckBox = (CheckBox) findViewById(R.id.checkBoxKPACK);

        ////////////////////////////////////////////////////////////////
        rsntVer.setText(MeganetInstances.getInstance().GetMeganetEngine().GetRSNTVersion());
        appVer.setText(R.string.app_version);

        CommonSettingsData data;

        data = MeganetInstances.getInstance().GetMeganetDb().getSetting(1);
        if(data.GetID() != -1)
            hhidEditText.setText(Utilities.StringCompleter(data.GetKeyValue(), 5, "0", true));
        else
            hhidEditText.setText("00000");

        data = MeganetInstances.getInstance().GetMeganetDb().getSetting(2);
        if(data.GetID() != -1)
            fieldVerifFreq1EditText.setText(data.GetKeyValue());
        else
            fieldVerifFreq1EditText.setText("00000000");

        data = MeganetInstances.getInstance().GetMeganetDb().getSetting(3);
        if(data.GetID() != -1)
            fieldVerifFreq2EditText.setText(data.GetKeyValue());
        else
            fieldVerifFreq2EditText.setText("00000000");

        data = MeganetInstances.getInstance().GetMeganetDb().getSetting(4);
        if(data.GetID() != -1)
            readMeterFreqEditText.setText(data.GetKeyValue());
        else
            readMeterFreqEditText.setText("00000000");

        data = MeganetInstances.getInstance().GetMeganetDb().getSetting(9);
        if(data.GetID() != -1)
            RDMFreqEditText.setText(data.GetKeyValue());
        else
            RDMFreqEditText.setText("00000000");

        data = MeganetInstances.getInstance().GetMeganetDb().getSetting(7);
        if(data.GetID() != -1)
            ranmanUrlEditText.setText(data.GetKeyValue());
        else
            ranmanUrlEditText.setText("http://www.google.com");

        data = MeganetInstances.getInstance().GetMeganetDb().getSetting(8);
        if(data.GetID() != -1) {
            if (data.GetKeyValue().equals("1"))
                kpackCheckBox.setChecked(true);
            else
                kpackCheckBox.setChecked(false);
        }
        else
            kpackCheckBox.setChecked(false);

        ///////////////////////////////////////////////////////////////////
        applyButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                CommonSettingsData data = new CommonSettingsData();

                data.SetID(1);
                data.SetKeyName("hh_id");
                data.SetKeyValue(Utilities.StringCompleter(hhidEditText.getText().toString(), 5, "0", true));
                MeganetInstances.getInstance().GetMeganetDb().updateProperty(data);

                data.SetID(2);
                data.SetKeyName("field_verif_freq_1");
                data.SetKeyValue(fieldVerifFreq1EditText.getText().toString());
                MeganetInstances.getInstance().GetMeganetDb().updateProperty(data);

                data.SetID(3);
                data.SetKeyName("field_verif_freq_2");
                data.SetKeyValue(fieldVerifFreq2EditText.getText().toString());
                MeganetInstances.getInstance().GetMeganetDb().updateProperty(data);

                data.SetID(4);
                data.SetKeyName("read_meter_freq");
                data.SetKeyValue(readMeterFreqEditText.getText().toString());
                MeganetInstances.getInstance().GetMeganetDb().updateProperty(data);

                data.SetID(7);
                data.SetKeyName("ranman_url");
                data.SetKeyValue(ranmanUrlEditText.getText().toString());
                MeganetInstances.getInstance().GetMeganetDb().updateProperty(data);

                String uskp;
                data.SetID(8);
                data.SetKeyName("use_kpack");
                if(kpackCheckBox.isChecked())
                {
                    data.SetKeyValue("1");
                    uskp = "1";
                }
                else
                {
                    data.SetKeyValue("0");
                    uskp = "0";
                }
                MeganetInstances.getInstance().GetMeganetDb().updateProperty(data);

                data.SetID(9);
                data.SetKeyName("rdm_freq");
                data.SetKeyValue(RDMFreqEditText.getText().toString());
                MeganetInstances.getInstance().GetMeganetDb().updateProperty(data);

                MeganetInstances.getInstance().GetMeganetEngine().ReInitProperties(fieldVerifFreq1EditText.getText().toString(), fieldVerifFreq2EditText.getText().toString(), readMeterFreqEditText.getText().toString(), RDMFreqEditText.getText().toString(), Utilities.StringCompleter(hhidEditText.getText().toString(), 5, "0", true), uskp);

                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent intent;
        switch (item.getItemId()) {

            case R.id.menu_settings_field_verif:
                super.onBackPressed();
                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.FIELD_VERIF_1);
                intent = new Intent(SettingsActivity.this, ReadsActivity.class);
                startActivity(intent);
                break;

            case R.id.menu_settings_ranman:
                super.onBackPressed();
                Toast.makeText(getApplicationContext(), "RANMAN RSSI", Toast.LENGTH_LONG).show();
                String url = MeganetInstances.getInstance().GetMeganetDb().getSetting(7).GetKeyValue();//"http://www.google.com";
                if (!url.startsWith("http://") && !url.startsWith("https://"))
                    url = "http://" + url;
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                break;

            case R.id.menu_settings_read_meter:
                super.onBackPressed();
                Toast.makeText(getApplicationContext(), "Read Meter", Toast.LENGTH_LONG).show();

                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.READ_METER);
                intent = new Intent(SettingsActivity.this, ReadsActivity.class);
                startActivity(intent);
                break;

            case R.id.menu_settings_rdm:
                super.onBackPressed();
                Toast.makeText(getApplicationContext(), "RDM Control", Toast.LENGTH_LONG).show();

                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.NONE);
                intent = new Intent(SettingsActivity.this, RDM_Controll.class);
                startActivity(intent);
                break;

            case R.id.menu_settings_ftp:
                super.onBackPressed();
                Toast.makeText(getApplicationContext(), "FTP", Toast.LENGTH_LONG).show();

                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.NONE);
                intent = new Intent(SettingsActivity.this, FTP_Controll.class);
                startActivity(intent);
                // TODO Something
                break;


            case R.id.menu_settings_program:
                Toast.makeText(getApplicationContext(), "Programming", Toast.LENGTH_LONG).show();
                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.NONE);
                intent = new Intent(SettingsActivity.this, QrCodeActivity.class);
                startActivity(intent);
                // TODO Something
                break;

            case R.id.menu_settings_getlog:
                Toast.makeText(getApplicationContext(), "Get Log", Toast.LENGTH_LONG).show();
                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.NONE);
                intent = new Intent(SettingsActivity.this, History_Log_1.class);
                startActivity(intent);
                // TODO Something
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
