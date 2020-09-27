package com.kp.meganet.meganetkp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class WorkOrderSelect_5  extends AppCompatActivity implements iPulseCallback{

    private Button syncButton;
    private Button saveButton;

    private TextView portId;
    private TextView portSNeditText;
    private TextView portReadeditText;

    private RadioButton Port1rbo;
    private RadioButton Port2rbo;

    private String _toastMessageToDisplay;
    private boolean _pairDialogIsON;

    int input_number;
    int unitID;

    String fileName;
    String dataFile;

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(WorkOrderSelect_5.this, WorkOrderSelect_8.class);
        intent.putExtra("file name", fileName);
        intent.putExtra("data file", dataFile);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_order_select_5);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        _pairDialogIsON = false;
        syncButton  = (Button) findViewById(R.id.buttonSync);
        saveButton  = (Button) findViewById(R.id.buttonSave);

        portId  = (TextView) findViewById(R.id.textViewID);
        portSNeditText  = (TextView) findViewById(R.id.textViewSN);
        portReadeditText  = (TextView) findViewById(R.id.textViewRead);

        Port1rbo  = (RadioButton) findViewById(R.id.radioButton1);
        Port2rbo  = (RadioButton) findViewById(R.id.radioButton2);

        fileName = getIntent().getStringExtra("file name");
        dataFile = getIntent().getStringExtra("data file");
        input_number = 1;

        unitID = Integer.parseInt(MeganetInstances.getInstance().GetMeganetEngine().GetQrAddress());
        MeganetInstances.getInstance().GetMeganetEngine().SetUnitAddress(String.valueOf(unitID));
        portId.setText(Utilities.StringCompleter(String.valueOf(unitID), 7, "0", true));

        MeganetInstances.getInstance().GetMeganetEngine().InitPulse(this);

        if (MeganetInstances.getInstance().GetMeganetEngine().GetCurrentProgrammType() == 1)
        {
            //MeganetInstances.getInstance().GetMeganetEngine().PromptSync(MeganetEngine.ePromptType.PAIRING, PromptConvert("MTPIT"),1);
            Port1rbo.setVisibility(View.INVISIBLE);
            Port2rbo.setVisibility(View.INVISIBLE);
            OnMessageCb("Set serial number and reading");

            LayoutInflater layoutInflater = LayoutInflater.from(WorkOrderSelect_5.this);
            View promptView = layoutInflater.inflate(R.layout.enter_details, null);
            final EditText input_serialNum = (EditText) promptView.findViewById(R.id.edittext);
            final EditText input_reading = (EditText) promptView.findViewById(R.id.edittext2);
            input_serialNum.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
            input_reading.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);

            AlertDialog dialog = new AlertDialog.Builder(WorkOrderSelect_5.this)
                    .setView(promptView)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String metreSN = input_serialNum.getText().toString();
                            String reading = input_reading.getText().toString();
                            portSNeditText.setText(metreSN);
                            portReadeditText.setText(reading);
                            MeganetInstances.getInstance().GetMeganetEngine().SetLastAccSN(metreSN);
                            MeganetInstances.getInstance().GetMeganetEngine().SetLastAccRead(reading);
                        }
                    }).show();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);

        }
        else if(MeganetInstances.getInstance().GetMeganetEngine().GetCurrentProgrammType() == 2)
        {
            //MeganetInstances.getInstance().GetMeganetEngine().PromptSync(MeganetEngine.ePromptType.PAIRING, PromptConvert("MT2W\\MT2PIT\\MC"),input_number);
            OnMessageCb("Set serial number, reading and choose input number");

            LayoutInflater layoutInflater = LayoutInflater.from(WorkOrderSelect_5.this);
            View promptView = layoutInflater.inflate(R.layout.enter_details, null);
            final EditText input_serialNum = (EditText) promptView.findViewById(R.id.edittext);
            final EditText input_reading = (EditText) promptView.findViewById(R.id.edittext2);
            input_serialNum.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
            input_reading.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);

            AlertDialog dialog = new AlertDialog.Builder(WorkOrderSelect_5.this)
                    .setView(promptView)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String metreSN = input_serialNum.getText().toString();
                            String reading = input_reading.getText().toString();
                            portSNeditText.setText(metreSN);
                            portReadeditText.setText(reading);
                            MeganetInstances.getInstance().GetMeganetEngine().SetLastAccSN(metreSN);
                            MeganetInstances.getInstance().GetMeganetEngine().SetLastAccRead(reading);
                        }
                    }).show();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
        }
        else if(MeganetInstances.getInstance().GetMeganetEngine().GetCurrentProgrammType() == 22)
        {
            MeganetInstances.getInstance().GetMeganetEngine().PromptSync(MeganetEngine.ePromptType.TEN_CHR_PAIRING, PromptConvert("E"),input_number);
        }
        else if(MeganetInstances.getInstance().GetMeganetEngine().GetCurrentProgrammType() == 3)
        {
            //MeganetInstances.getInstance().GetMeganetEngine().PromptSync(MeganetEngine.ePromptType.PAIRING, PromptConvert("MT1W"),input_number);
            OnMessageCb("Set serial number, reading and choose input number");

            LayoutInflater layoutInflater = LayoutInflater.from(WorkOrderSelect_5.this);
            View promptView = layoutInflater.inflate(R.layout.enter_details, null);
            final EditText input_serialNum = (EditText) promptView.findViewById(R.id.edittext);
            final EditText input_reading = (EditText) promptView.findViewById(R.id.edittext2);
            input_serialNum.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
            input_reading.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);

            AlertDialog dialog = new AlertDialog.Builder(WorkOrderSelect_5.this)
                    .setView(promptView)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String metreSN = input_serialNum.getText().toString();
                            String reading = input_reading.getText().toString();
                            portSNeditText.setText(metreSN);
                            portReadeditText.setText(reading);
                            MeganetInstances.getInstance().GetMeganetEngine().SetLastAccSN(metreSN);
                            MeganetInstances.getInstance().GetMeganetEngine().SetLastAccRead(reading);
                        }
                    }).show();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
        }
        else if(MeganetInstances.getInstance().GetMeganetEngine().GetCurrentProgrammType() == 4)
        {
            //MeganetInstances.getInstance().GetMeganetEngine().PromptSync(MeganetEngine.ePromptType.PAIRING, PromptConvert("MT1W"),input_number);
            OnMessageCb("Set serial number, reading and choose input number");

            LayoutInflater layoutInflater = LayoutInflater.from(WorkOrderSelect_5.this);
            View promptView = layoutInflater.inflate(R.layout.enter_details, null);
            final EditText input_serialNum = (EditText) promptView.findViewById(R.id.edittext);
            final EditText input_reading = (EditText) promptView.findViewById(R.id.edittext2);
            input_serialNum.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
            input_reading.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);

            AlertDialog dialog = new AlertDialog.Builder(WorkOrderSelect_5.this)
                    .setView(promptView)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String metreSN = input_serialNum.getText().toString();
                            String reading = input_reading.getText().toString();
                            portSNeditText.setText(metreSN);
                            portReadeditText.setText(reading);
                            MeganetInstances.getInstance().GetMeganetEngine().SetLastAccSN(metreSN);
                            MeganetInstances.getInstance().GetMeganetEngine().SetLastAccRead(reading);
                        }
                    }).show();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
        }
        else if(MeganetInstances.getInstance().GetMeganetEngine().GetCurrentProgrammType() == 44)
        {
            MeganetInstances.getInstance().GetMeganetEngine().PromptSync(MeganetEngine.ePromptType.TEN_CHR_PAIRING, PromptConvert("E"),input_number);
        }
        else if(MeganetInstances.getInstance().GetMeganetEngine().GetCurrentProgrammType() == 5)
        {
            //MeganetInstances.getInstance().GetMeganetEngine().PromptSync(MeganetEngine.ePromptType.PAIRING, PromptConvert("MT1W"),1);
            //inputRG.setVisibility(View.INVISIBLE);
            Port1rbo.setVisibility(View.INVISIBLE);
            Port2rbo.setVisibility(View.INVISIBLE);
            OnMessageCb("Set serial number and reading");

            LayoutInflater layoutInflater = LayoutInflater.from(WorkOrderSelect_5.this);
            View promptView = layoutInflater.inflate(R.layout.enter_details, null);
            final EditText input_serialNum = (EditText) promptView.findViewById(R.id.edittext);
            final EditText input_reading = (EditText) promptView.findViewById(R.id.edittext2);
            input_serialNum.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
            input_reading.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);

            AlertDialog dialog = new AlertDialog.Builder(WorkOrderSelect_5.this)
                    .setView(promptView)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String metreSN = input_serialNum.getText().toString();
                            String reading = input_reading.getText().toString();
                            portSNeditText.setText(metreSN);
                            portReadeditText.setText(reading);
                            MeganetInstances.getInstance().GetMeganetEngine().SetLastAccSN(metreSN);
                            MeganetInstances.getInstance().GetMeganetEngine().SetLastAccRead(reading);
                        }
                    }).show();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
        }

        //MeganetInstances.getInstance().GetMeganetEngine().SetCurrentProgrammType(44);
        //MeganetInstances.getInstance().GetMeganetEngine().PromptSync(MeganetInstances.getInstance().GetMeganetEngine().get_promtType(), PromptConvert("E"), 1);


        syncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MeganetInstances.getInstance().GetMeganetEngine().GetCurrentProgrammType() == 1)
                {
                    //MeganetInstances.getInstance().GetMeganetEngine().PromptSync(MeganetEngine.ePromptType.PAIRING, PromptConvert("MTPIT"),1);
                    Port1rbo.setVisibility(View.INVISIBLE);
                    Port2rbo.setVisibility(View.INVISIBLE);
                    OnMessageCb("Set serial number and reading");

                    LayoutInflater layoutInflater = LayoutInflater.from(WorkOrderSelect_5.this);
                    View promptView = layoutInflater.inflate(R.layout.enter_details, null);
                    final EditText input_serialNum = (EditText) promptView.findViewById(R.id.edittext);
                    final EditText input_reading = (EditText) promptView.findViewById(R.id.edittext2);
                    input_serialNum.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
                    input_reading.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);

                    AlertDialog dialog = new AlertDialog.Builder(WorkOrderSelect_5.this)
                            .setView(promptView)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    String metreSN = input_serialNum.getText().toString();
                                    String reading = input_reading.getText().toString();
                                    portSNeditText.setText(metreSN);
                                    portReadeditText.setText(reading);
                                    MeganetInstances.getInstance().GetMeganetEngine().SetLastAccSN(metreSN);
                                    MeganetInstances.getInstance().GetMeganetEngine().SetLastAccRead(reading);
                                }
                            }).show();
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);

                }
                else if(MeganetInstances.getInstance().GetMeganetEngine().GetCurrentProgrammType() == 2)
                {
                    //MeganetInstances.getInstance().GetMeganetEngine().PromptSync(MeganetEngine.ePromptType.PAIRING, PromptConvert("MT2W\\MT2PIT\\MC"),input_number);
                    OnMessageCb("Set serial number, reading and choose input number");

                    LayoutInflater layoutInflater = LayoutInflater.from(WorkOrderSelect_5.this);
                    View promptView = layoutInflater.inflate(R.layout.enter_details, null);
                    final EditText input_serialNum = (EditText) promptView.findViewById(R.id.edittext);
                    final EditText input_reading = (EditText) promptView.findViewById(R.id.edittext2);
                    input_serialNum.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
                    input_reading.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);

                    AlertDialog dialog = new AlertDialog.Builder(WorkOrderSelect_5.this)
                            .setView(promptView)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    String metreSN = input_serialNum.getText().toString();
                                    String reading = input_reading.getText().toString();
                                    portSNeditText.setText(metreSN);
                                    portReadeditText.setText(reading);
                                    MeganetInstances.getInstance().GetMeganetEngine().SetLastAccSN(metreSN);
                                    MeganetInstances.getInstance().GetMeganetEngine().SetLastAccRead(reading);
                                }
                            }).show();
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
                }
                else if(MeganetInstances.getInstance().GetMeganetEngine().GetCurrentProgrammType() == 22)
                {
                    MeganetInstances.getInstance().GetMeganetEngine().PromptSync(MeganetEngine.ePromptType.TEN_CHR_PAIRING, PromptConvert("E"),input_number);
                }
                else if(MeganetInstances.getInstance().GetMeganetEngine().GetCurrentProgrammType() == 3)
                {
                    //MeganetInstances.getInstance().GetMeganetEngine().PromptSync(MeganetEngine.ePromptType.PAIRING, PromptConvert("MT1W"),input_number);
                    OnMessageCb("Set serial number, reading and choose input number");

                    LayoutInflater layoutInflater = LayoutInflater.from(WorkOrderSelect_5.this);
                    View promptView = layoutInflater.inflate(R.layout.enter_details, null);
                    final EditText input_serialNum = (EditText) promptView.findViewById(R.id.edittext);
                    final EditText input_reading = (EditText) promptView.findViewById(R.id.edittext2);
                    input_serialNum.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
                    input_reading.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);

                    AlertDialog dialog = new AlertDialog.Builder(WorkOrderSelect_5.this)
                            .setView(promptView)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    String metreSN = input_serialNum.getText().toString();
                                    String reading = input_reading.getText().toString();
                                    portSNeditText.setText(metreSN);
                                    portReadeditText.setText(reading);
                                    MeganetInstances.getInstance().GetMeganetEngine().SetLastAccSN(metreSN);
                                    MeganetInstances.getInstance().GetMeganetEngine().SetLastAccRead(reading);
                                }
                            }).show();
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
                }
                else if(MeganetInstances.getInstance().GetMeganetEngine().GetCurrentProgrammType() == 4)
                {
                    //MeganetInstances.getInstance().GetMeganetEngine().PromptSync(MeganetEngine.ePromptType.PAIRING, PromptConvert("MT1W"),input_number);
                    OnMessageCb("Set serial number, reading and choose input number");

                    LayoutInflater layoutInflater = LayoutInflater.from(WorkOrderSelect_5.this);
                    View promptView = layoutInflater.inflate(R.layout.enter_details, null);
                    final EditText input_serialNum = (EditText) promptView.findViewById(R.id.edittext);
                    final EditText input_reading = (EditText) promptView.findViewById(R.id.edittext2);
                    input_serialNum.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
                    input_reading.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);

                    AlertDialog dialog = new AlertDialog.Builder(WorkOrderSelect_5.this)
                            .setView(promptView)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    String metreSN = input_serialNum.getText().toString();
                                    String reading = input_reading.getText().toString();
                                    portSNeditText.setText(metreSN);
                                    portReadeditText.setText(reading);
                                    MeganetInstances.getInstance().GetMeganetEngine().SetLastAccSN(metreSN);
                                    MeganetInstances.getInstance().GetMeganetEngine().SetLastAccRead(reading);
                                }
                            }).show();
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
                }
                else if(MeganetInstances.getInstance().GetMeganetEngine().GetCurrentProgrammType() == 44)
                {
                    MeganetInstances.getInstance().GetMeganetEngine().PromptSync(MeganetEngine.ePromptType.TEN_CHR_PAIRING, PromptConvert("E"),input_number);
                }
                else if(MeganetInstances.getInstance().GetMeganetEngine().GetCurrentProgrammType() == 5)
                {
                    //MeganetInstances.getInstance().GetMeganetEngine().PromptSync(MeganetEngine.ePromptType.PAIRING, PromptConvert("MT1W"),1);
                    //inputRG.setVisibility(View.INVISIBLE);
                    Port1rbo.setVisibility(View.INVISIBLE);
                    Port2rbo.setVisibility(View.INVISIBLE);
                    OnMessageCb("Set serial number and reading");

                    LayoutInflater layoutInflater = LayoutInflater.from(WorkOrderSelect_5.this);
                    View promptView = layoutInflater.inflate(R.layout.enter_details, null);
                    final EditText input_serialNum = (EditText) promptView.findViewById(R.id.edittext);
                    final EditText input_reading = (EditText) promptView.findViewById(R.id.edittext2);
                    input_serialNum.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
                    input_reading.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);

                    AlertDialog dialog = new AlertDialog.Builder(WorkOrderSelect_5.this)
                            .setView(promptView)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    String metreSN = input_serialNum.getText().toString();
                                    String reading = input_reading.getText().toString();
                                    portSNeditText.setText(metreSN);
                                    portReadeditText.setText(reading);
                                    MeganetInstances.getInstance().GetMeganetEngine().SetLastAccSN(metreSN);
                                    MeganetInstances.getInstance().GetMeganetEngine().SetLastAccRead(reading);
                                }
                            }).show();
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
                }


            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MeganetInstances.getInstance().GetMeganetEngine().StartCollectData(false, false);
                Intent intent = new Intent(WorkOrderSelect_5.this, WorkOrderSelect_6.class);
                intent.putExtra("file name", fileName);
                intent.putExtra("data file", dataFile);
                startActivity(intent);
            }
        });


    }
    private String PromptConvert(String displayPrompt)
    {
        String convertedPrompt = "";

        if(displayPrompt.equals("MTWE"))
            return "KPMTWEN";

        if(displayPrompt.equals("MTWP"))
            return "KPMTWPN";

        if(displayPrompt.equals("MT1W"))
            return "W";

        if(displayPrompt.equals("MTPIT"))
            return "P";

        if(displayPrompt.equals("MT2W\\MT2PIT\\MC"))
            return "M";
////////////////////////////////////////
        if(displayPrompt.equals( "KPMTWEN"))
            return "MTWE";

        if(displayPrompt.equals("KPMTWPN"))
            return "MTWP";

        if(displayPrompt.equals("W"))
            return "MT1W";

        if(displayPrompt.equals("P"))
            return "MTPIT";

        if(displayPrompt.equals("M"))
            return "MT2W\\MT2PIT\\MC";

        if(displayPrompt.equals( "E"))
            return "E";

        return convertedPrompt;
    }


    private void PairingDialot()
    {
        String addr_pair = MeganetInstances.getInstance().GetMeganetEngine().GetQrAddress();
        if (addr_pair.equalsIgnoreCase(MeganetInstances.getInstance().GetMeganetEngine().GetUnitAddress()))
        {
            MeganetInstances.getInstance().GetMeganetEngine().NewPulsePairingDevice(true, false);
            //MeganetInstances.getInstance().GetMeganetEngine().SetAccountNumber(MeganetInstances.getInstance().GetMeganetEngine().GetUnitAddress());

            if(input_number == 2)
                portId.setText(Utilities.StringCompleter(String.valueOf(unitID+1), 7, "0", true));
            else
                portId.setText(Utilities.StringCompleter(String.valueOf(unitID), 7, "0", true));

            _pairDialogIsON = false;
        }
        else {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final int unitAddress = Integer.parseInt(MeganetInstances.getInstance().GetMeganetEngine().GetUnitAddress());
            if(input_number == 2) {
                MeganetInstances.getInstance().GetMeganetEngine().SetUnitAddress(Utilities.StringCompleter(String.valueOf(unitAddress+1), 7, "0", true));
            }
            builder.setMessage("Connect to MTU ID: " + unitAddress + " ?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Toast.makeText(getApplicationContext(), "Unit Paired",
                                    Toast.LENGTH_SHORT).show();

                            unitID = unitAddress;
                            MeganetInstances.getInstance().GetMeganetEngine().SetQrAddress(Utilities.StringCompleter(String.valueOf(unitID), 7, "0", true));

                            if(!MeganetInstances.getInstance().GetMeganetEngine().get_deviceVersion().startsWith("TOUAREG")) {
                                LayoutInflater layoutInflater = LayoutInflater.from(WorkOrderSelect_5.this);
                                View promptView = layoutInflater.inflate(R.layout.enter_details, null);
                                final EditText input_serialNum = (EditText) promptView.findViewById(R.id.edittext);
                                final EditText input_reading = (EditText) promptView.findViewById(R.id.edittext2);
                                input_serialNum.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
                                input_reading.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);

                                AlertDialog alertDialog = new AlertDialog.Builder(WorkOrderSelect_5.this)
                                        .setView(promptView)
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                String metreSN = input_serialNum.getText().toString();
                                                String reading = input_reading.getText().toString();
                                                portSNeditText.setText(metreSN);
                                                portReadeditText.setText(reading);
                                                MeganetInstances.getInstance().GetMeganetEngine().SetLastAccSN(metreSN);
                                                MeganetInstances.getInstance().GetMeganetEngine().SetLastAccRead(reading);
                                            }
                                        }).show();
                                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
                            }

                            MeganetInstances.getInstance().GetMeganetEngine().NewPulsePairingDevice(true, false);
                            //MeganetInstances.getInstance().GetMeganetEngine().SetAccountNumber(MeganetInstances.getInstance().GetMeganetEngine().GetUnitAddress());
                            dialog.dismiss();
                            _pairDialogIsON = false;
                            // MeganetInstances.getInstance().GetMeganetEngine().Sync()
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // some code if you want
                            Toast.makeText(getApplicationContext(), "UNPAIR FROM UNIT",
                                    Toast.LENGTH_SHORT).show();
                            MeganetInstances.getInstance().GetMeganetEngine().NewPulsePairingDevice(false, false);
                            dialog.dismiss();
                            portId.setText("0000000");
                            _pairDialogIsON = false;
                            MeganetInstances.getInstance().GetMeganetEngine().Disconnect();
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
            //bq.setBackgroundColor(Color.BLUE);
            portId.setText(MeganetInstances.getInstance().GetMeganetEngine().GetUnitAddress());
        }
    }

    public boolean PairData(String deviceName_prm, String ndevice_pam, boolean titleOnly)
    {
        //deviceTextView.setText(deviceName_prm.substring(0, deviceName_prm.length()-3));

        if (titleOnly)
            return true;

        if(!_pairDialogIsON)
        {
            _pairDialogIsON = true;
            PairingDialot();
        }

        return true;
    }
    @Override
    public void OnRead(String serial_num, String reading)
    {
        portSNeditText.setText(serial_num);
        portReadeditText.setText(reading);
        MeganetInstances.getInstance().GetMeganetEngine().SetLastAccSN(serial_num);
        MeganetInstances.getInstance().GetMeganetEngine().SetLastAccRead(reading);
        MeganetInstances.getInstance().GetMeganetEngine().SetUnitAddress(portId.getText().toString());
        MeganetInstances.getInstance().GetMeganetEngine().Disconnect();
    }
    @Override
    public void OnErrorRead(String error)
    {

    }
    @Override
    public void OnMessageCb(String message_prm)
    {
        if(message_prm.length() > 0)
        {
            _toastMessageToDisplay = message_prm;
            this.runOnUiThread(new Runnable() {
                public void run() {
                    // Access/update UI here
                    Toast.makeText(getApplicationContext(), _toastMessageToDisplay,
                            Toast.LENGTH_LONG).show();
                }
            });
        }
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

    public void radioButtonClick(View view) {
        if(Port1rbo.isChecked()) {
            input_number = 1;
            MeganetInstances.getInstance().GetMeganetEngine().SetUnitAddress(Utilities.StringCompleter(String.valueOf(unitID), 7, "0", true));
            portId.setText(Utilities.StringCompleter(String.valueOf(unitID), 7, "0", true));
        }
        else {
            input_number = 2;
            MeganetInstances.getInstance().GetMeganetEngine().SetUnitAddress(Utilities.StringCompleter(String.valueOf(unitID + 1), 7, "0", true));
            portId.setText(Utilities.StringCompleter(String.valueOf(unitID+1), 7, "0", true));
        }
    }
}
