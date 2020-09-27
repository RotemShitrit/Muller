package com.kp.meganet.meganetkp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class RDM_Controll extends AppCompatActivity implements iCallback {

    private String _toastMessageToDisplay;

    Button connectButton;
    TextView connectedMTUTextView;
    TextView downCountTextView;

    private boolean _pairDialogIsON;

    private ImageView openImageView;
    private ImageView closeImageView;

    private Button powerOffButton;
    private CheckBox kpeCheckBox;

    private Button openButton;
    private Button closefButton;

    private Timer _downCountTimer;
    private Integer _timerCount;
    private boolean _timerFlag = false;
    private boolean lastCommandIsOpen = false;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rdm__controll);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        _pairDialogIsON = false;
        setTitle("RDM Control");
        connectButton = (Button) findViewById(R.id.buttonConnect);
        powerOffButton = (Button) findViewById(R.id.buttonPowerOff);

        openButton = (Button) findViewById(R.id.buttonOpen);
        closefButton = (Button) findViewById(R.id.buttonClose);
        kpeCheckBox = (CheckBox) findViewById(R.id.checkBoxKPE) ;

        openImageView = (ImageView) findViewById(R.id.imageViewOpen);
        closeImageView = (ImageView) findViewById(R.id.imageViewClose);

        connectedMTUTextView = (TextView) findViewById(R.id.textViewConnectedMTU);
        downCountTextView = (TextView) findViewById(R.id.textViewDownCount);

        _downCountTimer = new Timer();
        _downCountTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                TimerMethod();
            }

        }, 0, 1000);

        MeganetInstances.getInstance().GetMeganetEngine().SetReadMetersRSNT(true);
        MeganetInstances.getInstance().GetMeganetEngine().InitProgramming(this, MeganetInstances.getInstance().GetMeganetDb().getSetting(1).GetKeyValue());
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                MeganetInstances.getInstance().GetMeganetEngine().SetFrequency();
            }
        }, 2000);

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //MeganetInstances.getInstance().GetMeganetEngine().Prompt(MeganetEngine.ePromptType.PAIRING, PromptConvert("MT2W\\MT2PIT\\MC"));
                if (kpeCheckBox.isChecked())
                    MeganetInstances.getInstance().GetMeganetEngine().Prompt(MeganetEngine.ePromptType.TEN_CHR_PAIRING, "E");
                else
                    MeganetInstances.getInstance().GetMeganetEngine().Prompt(MeganetEngine.ePromptType.PAIRING, PromptConvert("MT2W\\MT2PIT\\MC"));
            }
        });

        powerOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                downCountTextView.setText("");
                if (MeganetInstances.getInstance().GetMeganetEngine().MeterPowerOff()) {
                    powerOffButton.setVisibility(View.INVISIBLE);
                }


            }
        });

        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                _timerCount = 0;
                if (kpeCheckBox.isChecked())
                    MeganetInstances.getInstance().GetMeganetEngine().RDM_Command(true, true);
                else
                    MeganetInstances.getInstance().GetMeganetEngine().RDM_Command(true, false);
                lastCommandIsOpen = true;

                //openImageView.setVisibility(View.VISIBLE);
                //closeImageView.setVisibility(View.INVISIBLE);
                //openButton.setVisibility(View.INVISIBLE);
                //closefButton.setVisibility(View.VISIBLE);

            }
        });

        closefButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                _timerCount = 0;
                if (kpeCheckBox.isChecked())
                    MeganetInstances.getInstance().GetMeganetEngine().RDM_Command(false, true);
                else
                    MeganetInstances.getInstance().GetMeganetEngine().RDM_Command(false, false);
                lastCommandIsOpen = false;

                //openImageView.setVisibility(View.INVISIBLE);
                //closeImageView.setVisibility(View.VISIBLE);
                //openButton.setVisibility(View.VISIBLE);
                //closefButton.setVisibility(View.INVISIBLE);

            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private String PromptConvert(String displayPrompt) {
        String convertedPrompt = "";

        if (displayPrompt.equals("MT2W\\MT2PIT\\MC"))
            return "M";
////////////////////////////////////////

        if (displayPrompt.equals("M"))
            return "MT2W\\MT2PIT\\MC";

        return convertedPrompt;
    }

    public void SetReadData(Map<String, QryParams> data_prm) {

    }

    @Override
    public void ReadLog(byte[] dataArr_prm) {

    }

    @Override
    public void GetTime(byte[] dataArr_prm) {

    }

    private void TimerMethod() {
        try {
            if(_timerFlag)
            {

                this.runOnUiThread(new Runnable() {
                    public void run() {
                        // Access/update UI here
                        Integer val = 15 - _timerCount;
                        downCountTextView.setText(val.toString() + " sec till disconnect");
                        if(_timerCount > 15)
                        {
                            Toast.makeText(getApplicationContext(), _toastMessageToDisplay,
                                    Toast.LENGTH_SHORT).show();
                            connectedMTUTextView.setText("Not Connected");
                            //powerOffButton.setVisibility(View.INVISIBLE);
                            openImageView.setVisibility(View.INVISIBLE);
                            closeImageView.setVisibility(View.INVISIBLE);
                            openButton.setVisibility(View.INVISIBLE);
                            closefButton.setVisibility(View.INVISIBLE);
                            downCountTextView.setText("");
                            _timerFlag = false;
                            _timerCount = 0;
                            MeganetInstances.getInstance().GetMeganetEngine().SetReadMetersRSNT(true);
                        }
                    }
                });
                _timerCount++;
            }
            else
                this.runOnUiThread(new Runnable() {
                    public void run() {
                        // Access/update UI here
                        downCountTextView.setText("");
                    }
                });

        } catch (Exception e) {

        }
    }

    public boolean PairData(String deviceName_prm, String ndevice_pam, boolean titleOnly) {
        if (!_pairDialogIsON) {
            _pairDialogIsON = true;
            PairingDialot();
        }

        return true;
    }

    public void OnParameters(String deviceName_prm, List<QryParams> parameters) {

    }

    public void OnRead(String deviceName_prm, String ndevice_pam) {

    }

    public void OnPowerOff(boolean result_prm, String err_prm) {
        _timerFlag = false;
        if (result_prm) {

            _toastMessageToDisplay = "Disconnect successfully ";
            this.runOnUiThread(new Runnable() {
                public void run() {
                    // Access/update UI here
                    Toast.makeText(getApplicationContext(), _toastMessageToDisplay,
                            Toast.LENGTH_SHORT).show();

                    connectedMTUTextView.setText("Not Connected");
                    //powerOffButton.setVisibility(View.INVISIBLE);
                    openImageView.setVisibility(View.INVISIBLE);
                    closeImageView.setVisibility(View.INVISIBLE);
                    openButton.setVisibility(View.INVISIBLE);
                    closefButton.setVisibility(View.INVISIBLE);

                }
            });
        } else {
            _toastMessageToDisplay = "Disconnect fail ";
            this.runOnUiThread(new Runnable() {
                public void run() {
                    // Access/update UI here
                    Toast.makeText(getApplicationContext(), _toastMessageToDisplay,
                            Toast.LENGTH_SHORT).show();
                    connectedMTUTextView.setText("Not Connected");
                    //powerOffButton.setVisibility(View.INVISIBLE);
                    openImageView.setVisibility(View.INVISIBLE);
                    closeImageView.setVisibility(View.INVISIBLE);
                    openButton.setVisibility(View.INVISIBLE);
                    closefButton.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    public void OnSleep(boolean result_prm, String err_prm) {

    }


    public void OnProgramm(boolean result_prm, final String err_prm) {

        _timerCount = 0;
        if (result_prm)
        {
            _toastMessageToDisplay = err_prm;
            this.runOnUiThread(new Runnable() {
                public void run() {
                    // Access/update UI here
                    Toast.makeText(getApplicationContext(), _toastMessageToDisplay,
                            Toast.LENGTH_SHORT).show();

                    //connectedMTUTextView.setText("Not Connected");
                    //powerOffButton.setVisibility(View.INVISIBLE);
                    openButton.setVisibility(View.INVISIBLE);
                    closefButton.setVisibility(View.INVISIBLE);

                    _timerFlag = false;
                    _timerCount = 0;
                    MeganetInstances.getInstance().GetMeganetEngine().SetReadMetersRSNT(true);

                    if (err_prm.equals("RDM Tamper")) {
                        closeImageView.setVisibility(View.INVISIBLE);
                        openButton.setVisibility(View.INVISIBLE);
                        connectedMTUTextView.setText("RDM Tamper!");

                    } else {

                        if (lastCommandIsOpen) {
                            closeImageView.setVisibility(View.INVISIBLE);
                            openImageView.setVisibility(View.VISIBLE);
                            connectedMTUTextView.setText("RDM Opened!");
                        } else {
                            openImageView.setVisibility(View.INVISIBLE);
                            closeImageView.setVisibility(View.VISIBLE);
                            connectedMTUTextView.setText("RDM Closed!");
                        }
                    }
                }
            });
        }
        else
        {
            _toastMessageToDisplay = err_prm;
            this.runOnUiThread(new Runnable() {
                public void run() {
                    // Access/update UI here
                    downCountTextView.setText("");
                    Toast.makeText(getApplicationContext(), _toastMessageToDisplay,
                            Toast.LENGTH_SHORT).show();
                    //connectedMTUTextView.setText("Not Connected");
                    //powerOffButton.setVisibility(View.INVISIBLE);
                    openButton.setVisibility(View.INVISIBLE);
                    closefButton.setVisibility(View.INVISIBLE);

                    if(lastCommandIsOpen) {
                        closeImageView.setVisibility(View.INVISIBLE);
                        openButton.setVisibility(View.INVISIBLE);
                        connectedMTUTextView.setText("Open failed");

                    } else {
                        openImageView.setVisibility(View.INVISIBLE);
                        closeImageView.setVisibility(View.INVISIBLE);
                        connectedMTUTextView.setText("Close failed");
                    }

                    _timerFlag = false;
                    _timerCount = 0;
                    MeganetInstances.getInstance().GetMeganetEngine().SetReadMetersRSNT(true);
                }
            });
        }
    }

    public void OnErrorCb(String error_prm) {

    }

    public void OnMessageCb(String message_prm) {
        if (message_prm.length() > 0) {
            _toastMessageToDisplay = message_prm;
            this.runOnUiThread(new Runnable() {
                public void run() {
                    // Access/update UI here
                    Toast.makeText(getApplicationContext(), _toastMessageToDisplay,
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void PairingDialot() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Connect to Node ID: " + MeganetInstances.getInstance().GetMeganetEngine().GetUnitAddress() + " ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getApplicationContext(), "Unit Paired",
                                Toast.LENGTH_SHORT).show();
                        //powerOffButton.setVisibility(View.VISIBLE);
                        openImageView.setVisibility(View.INVISIBLE);
                        closeImageView.setVisibility(View.INVISIBLE);
                        openButton.setVisibility(View.VISIBLE);
                        closefButton.setVisibility(View.VISIBLE);
                        MeganetInstances.getInstance().GetMeganetEngine().PairingDevice(true, true);
                        connectedMTUTextView.setText("Connected To Node: " + MeganetInstances.getInstance().GetMeganetEngine().GetUnitAddress());
                        dialog.dismiss();
                        _pairDialogIsON = false;
                        _timerFlag = true;
                        _timerCount = 0;
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // some code if you want
                        Toast.makeText(getApplicationContext(), "UNPAIR FROM UNIT",
                                Toast.LENGTH_SHORT).show();
                        connectedMTUTextView.setText("Not Connected");
                        MeganetInstances.getInstance().GetMeganetEngine().PairingDevice(false, true);
                        dialog.dismiss();
                        _pairDialogIsON = false;

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

        Button bq = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        bq.setBackgroundColor(Color.WHITE);
        bq.setTextColor(Color.BLUE);

        bq = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        bq.setBackgroundColor(Color.WHITE);
        bq.setTextColor(Color.BLUE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rdm, menu);

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

            case R.id.menu_rdm_field_verif:
                super.onBackPressed();
                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.FIELD_VERIF_1);
                intent = new Intent(RDM_Controll.this, ReadsActivity.class);
                startActivity(intent);
                break;

            case R.id.menu_rdm_ranman:
                super.onBackPressed();
                Toast.makeText(getApplicationContext(), "RANMAN RSSI", Toast.LENGTH_LONG).show();
                String url = MeganetInstances.getInstance().GetMeganetDb().getSetting(7).GetKeyValue();//"http://www.google.com";
                if (!url.startsWith("http://") && !url.startsWith("https://"))
                    url = "http://" + url;
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                break;

            case R.id.menu_rdm_read_meter:
                super.onBackPressed();
                Toast.makeText(getApplicationContext(), "Read Meter", Toast.LENGTH_LONG).show();

                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.READ_METER);
                intent = new Intent(RDM_Controll.this, ReadsActivity.class);
                startActivity(intent);
                break;

            case R.id.menu_rdm_ftp:
                super.onBackPressed();
                Toast.makeText(getApplicationContext(), "FTP", Toast.LENGTH_LONG).show();

                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.NONE);
                intent = new Intent(RDM_Controll.this, FTP_Controll.class);
                startActivity(intent);
                // TODO Something
                break;

            case R.id.menu_rdm_getlog:
                Toast.makeText(getApplicationContext(), "Get Log", Toast.LENGTH_LONG).show();
                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.NONE);
                intent = new Intent(RDM_Controll.this, History_Log_1.class);
                startActivity(intent);
                // TODO Something
                break;

            case R.id.menu_rdm_settings:
                super.onBackPressed();
                Toast.makeText(getApplicationContext(), "Settings", Toast.LENGTH_LONG).show();

                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.NONE);
                intent = new Intent(RDM_Controll.this, SettingsActivity.class);
                startActivity(intent);
                // TODO Something
                break;

            case R.id.menu_rdm_program:
                Toast.makeText(getApplicationContext(), "Programming", Toast.LENGTH_LONG).show();

                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.NONE);
                intent = new Intent(RDM_Controll.this, QrCodeActivity.class);
                startActivity(intent);
                // TODO Something
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "RDM_Controll Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.kp.meganet.meganetkp/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "RDM_Controll Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.kp.meganet.meganetkp/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
