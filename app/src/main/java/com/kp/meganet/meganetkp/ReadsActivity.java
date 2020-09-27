package com.kp.meganet.meganetkp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReadsActivity extends AppCompatActivity implements iReadMeterCallBack {

    boolean RowColorGray = false;

    private Button collectReadsButton;
    private Button scanListeningMCsButton;
    private Button clearHistoryButton;

    private CheckBox f1CheckBox;
    private CheckBox f2CheckBox;

    private TextView _frequencyTextView;
    private TextView _filterTextView;

    List<RssiRow> _rssiRows = new ArrayList<RssiRow>();
    private SimpleDateFormat _dateFormat;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reads);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        //SaveLogToFile("aaaKP.txt", "Test Test Test", true);

        switch (MeganetInstances.getInstance().GetMeganetEngine().GetCurrentReadType()) {
            case FIELD_VERIF_1:
                setTitle("Field Verification");
                break;

            case FIELD_VERIF_2:
                setTitle("Field Verification");
                break;

            case READ_METER:
                setTitle("Read Meter");
                break;

        }

        _dateFormat = new SimpleDateFormat("HH:mm:ss");

        _frequencyTextView = (TextView) findViewById(R.id.textViewFrequency);
        _filterTextView = (TextView) findViewById(R.id.textViewFilter);

        collectReadsButton = (Button) findViewById(R.id.buttonCollectReads);
        scanListeningMCsButton = (Button) findViewById(R.id.buttonScanListeningMCs);
        clearHistoryButton = (Button) findViewById(R.id.buttonClearHistory);

        f1CheckBox = (CheckBox) findViewById(R.id.checkBoxF1);
        f2CheckBox = (CheckBox) findViewById(R.id.checkBoxF2);

        collectReadsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MeganetInstances.getInstance().GetMeganetEngine().CollectReads();
            }
        });

        scanListeningMCsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MeganetInstances.getInstance().GetMeganetEngine().ScanLinteningMCs();
            }
        });

        clearHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MeganetInstances.getInstance().GetMeganetEngine().ClearMcHistory();
            }
        });

        f1CheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    f2CheckBox.setChecked(false);
                    MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.FIELD_VERIF_1);

                    MeganetInstances.getInstance().GetMeganetEngine().SetReadMetersRSNT(false);
                    _frequencyTextView.setText("Frequency: " + MeganetInstances.getInstance().GetMeganetEngine().GetFrequency());
                    _filterTextView.setText(MeganetInstances.getInstance().GetMeganetEngine().GetFilter());
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MeganetInstances.getInstance().GetMeganetEngine().SetFrequency();
                        }
                    }, 2000);

                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MeganetInstances.getInstance().GetMeganetEngine().CollectReads();
                        }
                    }, 2000);
                } else {
                    if (!f2CheckBox.isChecked())
                        f2CheckBox.setChecked(true);
                }
            }
        });

        f2CheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    f1CheckBox.setChecked(false);
                    MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.FIELD_VERIF_2);

                    MeganetInstances.getInstance().GetMeganetEngine().SetReadMetersRSNT(false);
                    _frequencyTextView.setText("Frequency: " + MeganetInstances.getInstance().GetMeganetEngine().GetFrequency());
                    _filterTextView.setText(MeganetInstances.getInstance().GetMeganetEngine().GetFilter());
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MeganetInstances.getInstance().GetMeganetEngine().SetFrequency();
                        }
                    }, 2000);

                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MeganetInstances.getInstance().GetMeganetEngine().CollectReads();
                        }
                    }, 2000);
                } else {
                    if (!f1CheckBox.isChecked())
                        f1CheckBox.setChecked(true);
                }
            }
        });


        MeganetInstances.getInstance().GetMeganetEngine().InitReadMeter(this);

        MeganetInstances.getInstance().GetMeganetEngine().SetReadMetersRSNT(false);
        //_dataFormat.format(new Date());
        InitActivity();

        _frequencyTextView.setText("Frequency: " + MeganetInstances.getInstance().GetMeganetEngine().GetFrequency());
        _filterTextView.setText(MeganetInstances.getInstance().GetMeganetEngine().GetFilter());


        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                MeganetInstances.getInstance().GetMeganetEngine().SetFrequency();
            }
        }, 2000);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                MeganetInstances.getInstance().GetMeganetEngine().CollectReads();
            }
        }, 2000);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    @Override
    public void onBackPressed() {
        MeganetInstances.getInstance().GetMeganetEngine().SetReadMetersRSNT(true);
        MeganetInstances.getInstance().GetMeganetEngine().ReadMeters(false);
        super.onBackPressed();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_receiving, menu);


        switch (MeganetInstances.getInstance().GetMeganetEngine().GetCurrentReadType()) {
            case FIELD_VERIF_1:
                menu.add(0, 0, 0, "Read Meter").setShortcut('3', 'c');
                break;

            case FIELD_VERIF_2:
                menu.add(0, 1, 0, "Read Meter").setShortcut('3', 'c');
                break;

            case READ_METER:
                menu.add(0, 2, 0, "Field Verification").setShortcut('3', 'c');
                break;

        }
        return true;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent intent;
        switch (item.getItemId()) {
            case R.id.menu_receive_clear:
                _rssiRows.clear();
                InitActivity();
                break;

            case R.id.menu_receive_all:
                MeganetInstances.getInstance().GetMeganetEngine().SetFilterAll();
                break;

            case R.id.menu_receive_mtu:
                InputSpecMtuFilterDialog();
                break;

            case R.id.menu_receive_range_mtu:
                InputRangeMtuFilterDialog();
                break;

            case R.id.menu_receive_none:
                MeganetInstances.getInstance().GetMeganetEngine().SetFilterNone();
                break;

            case R.id.menu_receive_ranman:
                super.onBackPressed();
                Toast.makeText(getApplicationContext(), "RANMAN RSSI", Toast.LENGTH_LONG).show();
                String url = MeganetInstances.getInstance().GetMeganetDb().getSetting(7).GetKeyValue();//"http://www.google.com";
                if (!url.startsWith("http://") && !url.startsWith("https://"))
                    url = "http://" + url;
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                break;


            case R.id.menu_receive_rdm:
                super.onBackPressed();
                Toast.makeText(getApplicationContext(), "RDM Control", Toast.LENGTH_LONG).show();

                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.NONE);
                intent = new Intent(ReadsActivity.this, RDM_Controll.class);
                startActivity(intent);
                break;


            case R.id.menu_receive_ftp:
                super.onBackPressed();
                Toast.makeText(getApplicationContext(), "FTP", Toast.LENGTH_LONG).show();

                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.NONE);
                intent = new Intent(ReadsActivity.this, FTP_Controll.class);
                startActivity(intent);
                // TODO Something
                break;


            case R.id.menu_receive_getlog:
                Toast.makeText(getApplicationContext(), "Get Log", Toast.LENGTH_LONG).show();
                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.NONE);
                intent = new Intent(ReadsActivity.this, History_Log_1.class);
                startActivity(intent);
                break;
                // TODO Something

            case R.id.menu_receive_program:
                super.onBackPressed();
                Toast.makeText(getApplicationContext(), "Programming", Toast.LENGTH_LONG).show();

                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.NONE);
                intent = new Intent(ReadsActivity.this, QrCodeActivity.class);
                startActivity(intent);
                // TODO Something
                break;

            case R.id.menu_receive_settings:
                super.onBackPressed();
                Toast.makeText(getApplicationContext(), "Settings", Toast.LENGTH_LONG).show();

                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.NONE);
                intent = new Intent(ReadsActivity.this, SettingsActivity.class);
                startActivity(intent);
                // TODO Something
                break;
        }


        //noinspection SimplifiableIfStatement
        if (item.getItemId() == 0) {
            super.onBackPressed();
            Toast.makeText(getApplicationContext(), "Read Meter", Toast.LENGTH_LONG).show();

            MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.READ_METER);
            intent = new Intent(ReadsActivity.this, ReadsActivity.class);
            startActivity(intent);
            // TODO Something
        } else if (item.getItemId() == 1) {
            super.onBackPressed();
            Toast.makeText(getApplicationContext(), "Read Meter", Toast.LENGTH_LONG).show();

            MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.READ_METER);
            intent = new Intent(ReadsActivity.this, ReadsActivity.class);
            startActivity(intent);
            // TODO Something
        } else if (item.getItemId() == 2) {
            super.onBackPressed();
            Toast.makeText(getApplicationContext(), "Field Verification", Toast.LENGTH_LONG).show();

            MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.FIELD_VERIF_1);
            intent = new Intent(ReadsActivity.this, ReadsActivity.class);
            startActivity(intent);
        }


        return super.onOptionsItemSelected(item);
    }


    public boolean InitActivity() { // Create table with the suit case of Read type
        TableLayout stk = (TableLayout) findViewById(R.id.table_main);

        stk.removeViews(0, stk.getChildCount());


        switch (MeganetInstances.getInstance().GetMeganetEngine().GetCurrentReadType()) {
            case FIELD_VERIF_1: {
                f1CheckBox.setVisibility(View.VISIBLE);
                f2CheckBox.setVisibility(View.VISIBLE);
                TableRow tbrow0 = new TableRow(this);

                TextView textViewTime = new TextView(this);
                textViewTime.setText("     Time     ");
                textViewTime.setTextColor(Color.BLACK);
                textViewTime.setBackgroundColor(Color.WHITE);
                textViewTime.setTextSize(16);
                tbrow0.addView(textViewTime);

                TextView textViewID = new TextView(this);
                textViewID.setText("  ID  ");
                textViewID.setTextColor(Color.BLACK);
                textViewID.setTextSize(16);
                textViewID.setBackgroundColor(Color.WHITE);

                tbrow0.addView(textViewID);

                TextView textViewSystem = new TextView(this);
                textViewSystem.setText(" System ");
                textViewSystem.setTextColor(Color.BLACK);
                textViewSystem.setBackgroundColor(Color.WHITE);
                textViewSystem.setTextSize(16);
                tbrow0.addView(textViewSystem);

                TextView textViewCollector = new TextView(this);
                textViewCollector.setText("  Collector  ");
                textViewCollector.setTextColor(Color.BLACK);
                textViewCollector.setBackgroundColor(Color.WHITE);
                textViewCollector.setTextSize(16);
                tbrow0.addView(textViewCollector);

                TextView textViewLevel = new TextView(this);
                textViewLevel.setText("    Level    ");
                textViewLevel.setTextColor(Color.BLACK);
                textViewLevel.setBackgroundColor(Color.WHITE);
                textViewLevel.setTextSize(16);
                tbrow0.addView(textViewLevel);

                stk.addView(tbrow0);
            }
            break;

            case FIELD_VERIF_2: {
                f1CheckBox.setVisibility(View.VISIBLE);
                f2CheckBox.setVisibility(View.VISIBLE);
                TableRow tbrow0 = new TableRow(this);

                TextView textViewTime = new TextView(this);
                textViewTime.setText("     Time     ");
                textViewTime.setTextColor(Color.BLACK);
                textViewTime.setBackgroundColor(Color.WHITE);
                textViewTime.setTextSize(16);
                tbrow0.addView(textViewTime);

                TextView textViewID = new TextView(this);
                textViewID.setText("  ID  ");
                textViewID.setTextColor(Color.BLACK);
                textViewID.setBackgroundColor(Color.WHITE);
                textViewID.setTextSize(16);
                tbrow0.addView(textViewID);

                TextView textViewSystem = new TextView(this);
                textViewSystem.setText(" System ");
                textViewSystem.setTextColor(Color.BLACK);
                textViewSystem.setBackgroundColor(Color.WHITE);
                textViewSystem.setTextSize(16);
                tbrow0.addView(textViewSystem);

                TextView textViewCollector = new TextView(this);
                textViewCollector.setText("  Collector  ");
                textViewCollector.setTextColor(Color.BLACK);
                textViewCollector.setBackgroundColor(Color.WHITE);
                textViewCollector.setTextSize(16);
                tbrow0.addView(textViewCollector);

                TextView textViewLevel = new TextView(this);
                textViewLevel.setText("    Level    ");
                textViewLevel.setTextColor(Color.BLACK);
                textViewLevel.setBackgroundColor(Color.WHITE);
                textViewLevel.setTextSize(16);
                tbrow0.addView(textViewLevel);

                stk.addView(tbrow0);
            }
            break;

            case READ_METER: { // Create the table with columns: Time, ID, System, Reading
                f1CheckBox.setVisibility(View.INVISIBLE);
                f2CheckBox.setVisibility(View.INVISIBLE);

                TableRow tbrow0 = new TableRow(this);

                TextView textViewTime = new TextView(this);
                textViewTime.setText("     Time     ");
                textViewTime.setTextColor(Color.BLACK);
                textViewTime.setBackgroundColor(Color.WHITE);
                textViewTime.setTextSize(16);
                tbrow0.addView(textViewTime);

                TextView textViewID = new TextView(this);
                textViewID.setText("  ID  ");
                textViewID.setTextColor(Color.BLACK);
                textViewID.setBackgroundColor(Color.WHITE);
                textViewID.setTextSize(16);
                tbrow0.addView(textViewID);

                TextView textViewSystem = new TextView(this);
                textViewSystem.setText(" System ");
                textViewSystem.setTextColor(Color.BLACK);
                textViewSystem.setBackgroundColor(Color.WHITE);
                textViewSystem.setTextSize(16);
                tbrow0.addView(textViewSystem);

                TextView textViewCollector = new TextView(this);
                textViewCollector.setText("        Reading        ");
                textViewCollector.setTextColor(Color.BLACK);
                textViewCollector.setBackgroundColor(Color.WHITE);
                textViewCollector.setTextSize(16);
                tbrow0.addView(textViewCollector);

                stk.addView(tbrow0);
            }
            break;

            case DRIVE_BY: {

                TableRow tbrow0 = new TableRow(this);

                TextView textViewTime = new TextView(this);
                textViewTime.setText("   Time   ");
                textViewTime.setTextColor(Color.BLACK);
                textViewTime.setBackgroundColor(Color.WHITE);
                textViewTime.setTextSize(16);
                tbrow0.addView(textViewTime);

                TextView textViewID = new TextView(this);
                textViewID.setText("  ID  ");
                textViewID.setTextColor(Color.BLACK);
                textViewID.setBackgroundColor(Color.WHITE);
                textViewID.setTextSize(16);
                tbrow0.addView(textViewID);

                TextView textViewSystem = new TextView(this);
                textViewSystem.setText(" System ");
                textViewSystem.setTextColor(Color.BLACK);
                textViewSystem.setBackgroundColor(Color.WHITE);
                textViewSystem.setTextSize(16);
                tbrow0.addView(textViewSystem);

                TextView textViewCollector = new TextView(this);
                textViewCollector.setText("  Collector  ");
                textViewCollector.setTextColor(Color.BLACK);
                textViewCollector.setBackgroundColor(Color.WHITE);
                textViewCollector.setTextSize(16);
                tbrow0.addView(textViewCollector);

                stk.addView(tbrow0);

                stk.addView(tbrow0);
            }
            break;

            default:
                return false; // Error. type not defined
        }

        return true;
    }

    public void OnFilterSet(boolean status) {
        //Utilities.StringCompleter(Integer.toString(MeganetInstances.getInstance().GetMeganetEngine().GetFilter(), 5, "0", true);
        if (status)
        {
            _filterTextView.setText("Filter: " + MeganetInstances.getInstance().GetMeganetEngine().GetFilter());
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    MeganetInstances.getInstance().GetMeganetEngine().CollectReads();
                }
            }, 2000);
        }
        else
            _filterTextView.setText("Filter Fail");
    }

    public void OnReadMeters(byte[] data_prm) { // When we get a message from the receiver
        int[] intDataArr = new int[data_prm.length];

        for (int i = 0; i < data_prm.length; i++)
            intDataArr[i] = (int) (data_prm[i] & 0xff);

        // Rows.Add(Format(Int(tm.Hour), "##00") & ":" & Format(Int(tm.Minute), "##00") & ":" & Format(Int(tm.Second), "##00"), ID, System, Collector, Level)
        if (intDataArr.length == 13) {

            if (intDataArr[0] != 2 || intDataArr[12] != 13) // Check begin / end
                return; // Error. Incorrect data;

            // Show frequence and filter by reading type
            switch (MeganetInstances.getInstance().GetMeganetEngine().GetCurrentReadType()) {
                case FIELD_VERIF_1:
                    AddFieldVerificationsRow(intDataArr, false);
                    break;
                case FIELD_VERIF_2:
                    AddFieldVerificationsRow(intDataArr, false);
                    break;
                case READ_METER:
                    AddReadsRow(intDataArr, false);
                    break;
                case DRIVE_BY:
                    AddDriveByRow(intDataArr);
                    break;
                default:
                    return; // Error. type not defined
            }
        }
        else if (intDataArr.length == 16) {
            if (intDataArr[0] != 2 || intDataArr[15] != 13) // Check begin / end
                return; // Error. Incorrect data;

            // Show frequence and filter by reading type
            switch (MeganetInstances.getInstance().GetMeganetEngine().GetCurrentReadType()) {
                case FIELD_VERIF_1:
                    AddFieldVerificationsRow(intDataArr, true);
                    break;
                case FIELD_VERIF_2:
                    AddFieldVerificationsRow(intDataArr, true);
                    break;
                case READ_METER:
                    AddReadsRow(intDataArr, true);
                    break;
                case DRIVE_BY:
                    AddDriveByRow(intDataArr);
                    break;
                default:
                    return; // Error. type not defined
            }
        }
    }

    private void AddDriveByRow(int[] intDataArr) {


        if (!Integer.toHexString(intDataArr[5]).substring(0, 1).equals("0") && !Integer.toHexString(intDataArr[5]).substring(0, 1).equals("1") && !(Integer.toHexString(intDataArr[5]).substring(0, 1).equals("F") || Integer.toHexString(intDataArr[5]).substring(0, 1).equals("f")))
            return; // Incorrect data types;

        String hexValue = Utilities.StringCompleter(Integer.toHexString(intDataArr[9]), 2, "0", true) + Utilities.StringCompleter(Integer.toHexString(intDataArr[10]), 2, "0", true);
        String id = Utilities.StringCompleter(Integer.toString(Integer.parseInt(hexValue, 16)), 5, "0", true);

        String system = Utilities.StringCompleter(Integer.toHexString(intDataArr[5]), 2, "0", true).substring(1, 2);

        String collector = "";
        String level = "";

        if ((Integer.toHexString(intDataArr[5]).substring(0, 1).equals("F") || Integer.toHexString(intDataArr[5]).substring(0, 1).equals("f")))
            collector = "TEST";
        else if (Integer.toHexString(intDataArr[5]).substring(0, 1).equals("0") || Integer.toHexString(intDataArr[5]).substring(0, 1).equals("1")) {
            collector = Integer.toString(Integer.parseInt(Utilities.StringCompleter(Integer.toHexString(intDataArr[6]), 2, "0", true) + Utilities.StringCompleter(Integer.toHexString(intDataArr[7]), 2, "0", true) + Utilities.StringCompleter(Integer.toHexString(intDataArr[8]), 2, "0", true), 16));
        }

        String tmpLog = _dateFormat.format(new Date()) + " - " + id + " - " + system + " - " + collector;
        Log.d("MyActivity", tmpLog);

        TableLayout stk = (TableLayout) findViewById(R.id.table_main);

        TableRow tbrow = new TableRow(this);
        TextView textViewTime = new TextView(this);
        textViewTime.setText(_dateFormat.format(new Date()) + "  ");
        textViewTime.setTextColor(Color.BLACK);
        textViewTime.setGravity(Gravity.CENTER);
        textViewTime.setTextSize(16);
        tbrow.addView(textViewTime);

        TextView textViewID = new TextView(this);
        textViewID.setText(id);
        textViewID.setTextColor(Color.BLACK);
        textViewID.setGravity(Gravity.CENTER);
        textViewID.setTextSize(16);
        tbrow.addView(textViewID);

        TextView textViewSystem = new TextView(this);
        textViewSystem.setText(system);
        textViewSystem.setTextColor(Color.BLACK);
        textViewSystem.setGravity(Gravity.CENTER);
        textViewSystem.setTextSize(16);
        tbrow.addView(textViewSystem);

        TextView textViewCollector = new TextView(this);
        textViewCollector.setText(collector);
        textViewCollector.setTextColor(Color.BLACK);
        textViewCollector.setGravity(Gravity.CENTER);
        textViewCollector.setTextSize(16);
        tbrow.addView(textViewCollector);

        stk.addView(tbrow, 1);

    }

    private void AddFieldVerificationsRow(int[] intDataArr, boolean isTen) {
        int rowBgColor;

        if (RowColorGray) {
            RowColorGray = false;
            rowBgColor = Color.LTGRAY;
        } else {
            RowColorGray = true;
            rowBgColor = Color.WHITE;
        }

        if (!Integer.toHexString(intDataArr[5]).substring(0, 1).equals("2") && !(Integer.toHexString(intDataArr[5]).substring(0, 1).equals("E") || Integer.toHexString(intDataArr[5]).substring(0, 1).equals("e")))
            return; // Incorrect data types;


        String hexValue;
        String id;
        String system;
        String levelTmp;
        String level;
        String collectorTmp;
        String collector;

        if (!isTen )
        {
             hexValue = Utilities.StringCompleter(Integer.toHexString(intDataArr[9]), 2, "0", true) + Utilities.StringCompleter(Integer.toHexString(intDataArr[10]), 2, "0", true);
             id = Utilities.StringCompleter(Integer.toString(Integer.parseInt(hexValue, 16)), 5, "0", true);

             system = Utilities.StringCompleter(Integer.toHexString(intDataArr[5]), 2, "0", true).substring(1, 2);

             levelTmp = "";
             level = "";
             collectorTmp = "";
             collector = "";

            if (Utilities.StringCompleter(Integer.toHexString(intDataArr[5]), 2, "0", true).substring(0, 1).equals("2")) {
                if (Utilities.StringCompleter(Integer.toHexString(intDataArr[6]), 2, "0", true).substring(0, 1).equals("0")) {
                    levelTmp = Utilities.StringCompleter(Integer.toHexString(intDataArr[8]), 2, "0", true).substring(0, 1);
                    collectorTmp = Utilities.StringCompleter(Integer.toHexString(intDataArr[8]), 2, "0", true).substring(1, 2);
                    collector = String.valueOf(Integer.parseInt(collectorTmp, 16));
                } else {
                    levelTmp = Utilities.StringCompleter(Integer.toHexString(intDataArr[7]), 2, "0", true).substring(1, 2);
                    collectorTmp = Utilities.StringCompleter(Integer.toHexString(intDataArr[8]), 2, "0", true);
                    collector = String.valueOf(Integer.parseInt(collectorTmp, 16));
                }
                double dTmp = (double) (Integer.parseInt(levelTmp, 16) + 5) / 10.0;
                if (dTmp < 0.7)
                    level = "WEAK";
                else if (dTmp <= 1.4)
                    level = "GOOD";
                else //if(dTmp > 1.4)
                    level = "FULL";
            } else if (Utilities.StringCompleter(Integer.toHexString(intDataArr[5]), 2, "0", true).substring(0, 1).equals("e")) {
                collector = "N/A";
                level = "TAMPER";
            }
        }
        else
        {
            hexValue = Utilities.StringCompleter(Integer.toHexString(intDataArr[8]), 2, "0", true).substring(1) + Utilities.StringCompleter(Integer.toHexString(intDataArr[12]), 2, "0", true)+ Utilities.StringCompleter(Integer.toHexString(intDataArr[13]), 2, "0", true);

            //  hexValue = Utilities.StringCompleter(Integer.toHexString(intDataArr[11]), 2, "0", true) + Utilities.StringCompleter(Integer.toHexString(intDataArr[12]), 2, "0", true)+ Utilities.StringCompleter(Integer.toHexString(intDataArr[13]), 2, "0", true);
            id = Utilities.StringCompleter(Integer.toString(Integer.parseInt(hexValue, 16)), 5, "0", true);

            system = Utilities.StringCompleter(Integer.toHexString(intDataArr[5]), 2, "0", true).substring(1, 2);
            system = "0";
            levelTmp = "";
            level = "";
            collectorTmp = "";
            collector = "";

            if (Utilities.StringCompleter(Integer.toHexString(intDataArr[5]), 2, "0", true).substring(0, 1).equals("2")) {
                /*
                if (Utilities.StringCompleter(Integer.toHexString(intDataArr[6]), 2, "0", true).substring(0, 1).equals("0")) {
                    levelTmp =  Utilities.StringCompleter(Integer.toHexString(intDataArr[9]), 2, "0", true).substring(0, 2);
                    collectorTmp = Utilities.StringCompleter(Integer.toHexString(intDataArr[10]), 2, "0", true).substring(0, 2);
                    collector = String.valueOf(Integer.parseInt(collectorTmp, 16));
                } else {
                    levelTmp = Utilities.StringCompleter(Integer.toHexString(intDataArr[9]), 2, "0", true).substring(0, 2);
                    collectorTmp = Utilities.StringCompleter(Integer.toHexString(intDataArr[10]), 2, "0", true);
                    collector = String.valueOf(Integer.parseInt(collectorTmp, 16));
                }
                */
                collector = String.valueOf(intDataArr[10]);
                //level =  String.valueOf(intDataArr[10]-256);

                if (intDataArr[9]-256 <= -113)
                    level = "WEAK";
                else if (intDataArr[9] <= -104)
                    level = "GOOD";
                else //if(dTmp > 1.4)
                    level = "FULL";
                //level =  String.valueOf(intDataArr[9]-256);
            } else if (Utilities.StringCompleter(Integer.toHexString(intDataArr[5]), 2, "0", true).substring(0, 1).equals("e")) {
                collector = "N/A";
                level = "TAMPER";
            }
        }



        _rssiRows.add(new RssiRow(_dateFormat.format(new Date()), id, system, collector, level));

        String tmpLog = _dateFormat.format(new Date()) + " - " + id + " - " + system + " - " + collector + " - " + level;
        Log.d("MyActivity", tmpLog);

        TableLayout stk = (TableLayout) findViewById(R.id.table_main);

        TableRow tbrow = new TableRow(this);
        TextView textViewTime = new TextView(this);
        textViewTime.setText(_dateFormat.format(new Date()) + "  ");
        textViewTime.setTextColor(Color.BLACK);
        textViewTime.setBackgroundColor(rowBgColor);
        textViewTime.setGravity(Gravity.CENTER);
        textViewTime.setTextSize(16);
        tbrow.addView(textViewTime);

        TextView textViewID = new TextView(this);
        textViewID.setText(id);
        textViewID.setTextColor(Color.BLACK);
        textViewID.setBackgroundColor(rowBgColor);
        textViewID.setGravity(Gravity.CENTER);
        textViewID.setTextSize(16);
        tbrow.addView(textViewID);

        TextView textViewSystem = new TextView(this);
        textViewSystem.setText(system);
        textViewSystem.setTextColor(Color.BLACK);
        textViewSystem.setBackgroundColor(rowBgColor);
        textViewSystem.setGravity(Gravity.CENTER);
        textViewSystem.setTextSize(16);
        tbrow.addView(textViewSystem);

        TextView textViewCollector = new TextView(this);
        textViewCollector.setText(collector);
        textViewCollector.setTextColor(Color.BLACK);
        textViewCollector.setBackgroundColor(rowBgColor);
        textViewCollector.setGravity(Gravity.CENTER);
        textViewCollector.setTextSize(16);
        tbrow.addView(textViewCollector);

        TextView textViewLevel = new TextView(this);
        textViewLevel.setText(level);
        textViewLevel.setTextColor(Color.BLACK);
        if (level.equals("WEAK")) {
            textViewLevel.setBackgroundColor(Color.RED);
        } else if (level.equals("GOOD")) {
            textViewLevel.setBackgroundColor(Color.GREEN);
        } else if (level.equals("TAMPER")) {
            textViewLevel.setBackgroundColor(Color.YELLOW);
        } else {
            textViewLevel.setBackgroundColor(Color.GREEN);
        }
        textViewLevel.setGravity(Gravity.CENTER);
        textViewLevel.setTextSize(16);
        tbrow.addView(textViewLevel);
        stk.addView(tbrow, 1);

    }

    private void AddReadsRow(int[] intDataArr, boolean isTen) // Add a new row to the Read meter table
    {

        int rowBgColor;

        if (RowColorGray) {
            RowColorGray = false;
            rowBgColor = Color.LTGRAY;
        } else {
            RowColorGray = true;
            rowBgColor = Color.WHITE;
        }
        String sTmp;
        String hexValue;
        String id;
        String system;
        String collector;
        String level;
        if (!isTen) // If the message NOT include 10 digit for reading
        {
            sTmp = Integer.toHexString(intDataArr[5]).substring(0, 1);
            if (!(Integer.toHexString(intDataArr[5]).substring(0, 1).equals("1") || Integer.toHexString(intDataArr[5]).substring(0, 1).equals("F") || Integer.toHexString(intDataArr[5]).substring(0, 1).equals("f")))
                return; // Incorrect data types;

             hexValue = Utilities.StringCompleter(Integer.toHexString(intDataArr[9]), 2, "0", true) + Utilities.StringCompleter(Integer.toHexString(intDataArr[10]), 2, "0", true);
             id = Utilities.StringCompleter(Integer.toString(Integer.parseInt(hexValue, 16)), 5, "0", true);

             system = Utilities.StringCompleter(Integer.toHexString(intDataArr[5]), 2, "0", true).substring(1, 2);

             collector = "";
             level = "";

            if ((Integer.toHexString(intDataArr[5]).substring(0, 1).equals("F") || Integer.toHexString(intDataArr[5]).substring(0, 1).equals("f")) || Integer.toHexString(intDataArr[5]).substring(0, 1).equals("7"))
                collector = "TAMPER";
            else if (Integer.toHexString(intDataArr[5]).substring(0, 1).equals("1")) {
                collector = Integer.toString(Integer.parseInt(Utilities.StringCompleter(Integer.toHexString(intDataArr[6]), 2, "0", true) + Utilities.StringCompleter(Integer.toHexString(intDataArr[7]), 2, "0", true) + Utilities.StringCompleter(Integer.toHexString(intDataArr[8]), 2, "0", true), 16));

            }
        }
        else // If the message include 10 digit for reading
        {
            sTmp = Integer.toHexString(intDataArr[5]).substring(0, 1);
            if (!(Integer.toHexString(intDataArr[5]).substring(0, 1).equals("1") || Integer.toHexString(intDataArr[5]).substring(0, 1).equals("F") || Integer.toHexString(intDataArr[5]).substring(0, 1).equals("f")))
                return; // Incorrect data types;

            hexValue = Utilities.StringCompleter(Integer.toHexString(intDataArr[11]), 2, "0", true) +
                    Utilities.StringCompleter(Integer.toHexString(intDataArr[12]), 2, "0", true) +
                    Utilities.StringCompleter(Integer.toHexString(intDataArr[13]), 2, "0", true);
            id = Utilities.StringCompleter(Integer.toString(Integer.parseInt(hexValue, 16)), 5, "0", true);

            system = Utilities.StringCompleter(Integer.toHexString(intDataArr[5]), 2, "0", true).substring(1, 2);

            collector = "";
            level = "";

            if ((Integer.toHexString(intDataArr[5]).substring(0, 1).equals("F") || Integer.toHexString(intDataArr[5]).substring(0, 1).equals("f")) || Integer.toHexString(intDataArr[5]).substring(0, 1).equals("7"))
                collector = "TAMPER";
            else if (Integer.toHexString(intDataArr[5]).substring(0, 1).equals("1")) {
                collector = Integer.toString(Integer.parseInt(Utilities.StringCompleter(Integer.toHexString(intDataArr[6]), 2, "0", true) +
                        Utilities.StringCompleter(Integer.toHexString(intDataArr[7]), 2, "0", true) +
                        Utilities.StringCompleter(Integer.toHexString(intDataArr[8]), 2, "0", true) +
                        Utilities.StringCompleter(Integer.toHexString(intDataArr[9]), 2, "0", true) +
                        Utilities.StringCompleter(Integer.toHexString(intDataArr[10]), 2, "0", true), 16));

            }
        }




        String tmpLog = _dateFormat.format(new Date()) + " - " + id + " - " + system + " - " + collector;
        Log.d("MyActivity", tmpLog);


        TableLayout stk = (TableLayout) findViewById(R.id.table_main);

        TableRow tbrow = new TableRow(this); // Create new row
        TextView textViewTime = new TextView(this);
        textViewTime.setText(_dateFormat.format(new Date()) + "  ");
        textViewTime.setTextColor(Color.BLACK);
        textViewTime.setBackgroundColor(rowBgColor);
        textViewTime.setGravity(Gravity.CENTER);
        textViewTime.setTextSize(16);
        tbrow.addView(textViewTime);

        TextView textViewID = new TextView(this);
        textViewID.setText(id);
        textViewID.setTextColor(Color.BLACK);
        textViewID.setBackgroundColor(rowBgColor);
        textViewID.setGravity(Gravity.CENTER);
        textViewID.setTextSize(16);
        tbrow.addView(textViewID);

        TextView textViewSystem = new TextView(this);
        textViewSystem.setText(system);
        textViewSystem.setTextColor(Color.BLACK);
        textViewSystem.setBackgroundColor(rowBgColor);
        textViewSystem.setGravity(Gravity.CENTER);
        textViewSystem.setTextSize(16);
        tbrow.addView(textViewSystem);

        TextView textViewCollector = new TextView(this);

        textViewCollector.setText(collector);
        textViewCollector.setTextColor(Color.BLACK);
        textViewCollector.setBackgroundColor(rowBgColor);
        if (collector.equals("TAMPER"))
            textViewCollector.setBackgroundColor(Color.YELLOW);
        textViewCollector.setGravity(Gravity.CENTER);
        textViewCollector.setTextSize(16);
        tbrow.addView(textViewCollector);

        stk.addView(tbrow, 1); // add the new row to table

    }

    private void InputSpecMtuFilterDialog() {
        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        final CheckBox kpeCheck = new CheckBox(this);

        kpeCheck.setText("KPE");
        kpeCheck.setBackgroundColor(Color.GRAY);

        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(7)});
        input.setHint("MTU Address");

        LinearLayout lay = new LinearLayout(this);
        lay.setOrientation(LinearLayout.VERTICAL);
        lay.addView(input);

        lay.addView(kpeCheck);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Filter")
                .setMessage("Enter an MTU ID")
                .setView(lay)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String address = input.getText().toString();
                        if (address.length() > 0) {
                            if (kpeCheck.isChecked()) {
                                if (Integer.parseInt(address) <= 16777214) {
                                    MeganetInstances.getInstance().GetMeganetEngine().SetSpecMtuFilter(address, true);

                                }
                            }
                            else {
                                if (Integer.parseInt(address) <= 65535) {
                                    MeganetInstances.getInstance().GetMeganetEngine().SetSpecMtuFilter(address, false);

                                }
                            }


                        }

                        // deal with the editable
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Do nothing.
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

    private void InputRangeMtuFilterDialog() {
        // Set an EditText view to get user input
        final EditText input1 = new EditText(this);
        final EditText input2 = new EditText(this);
        final CheckBox kpeCheck = new CheckBox(this);

        kpeCheck.setText("KPE");
        kpeCheck.setBackgroundColor(Color.GRAY);

        input1.setInputType(InputType.TYPE_CLASS_NUMBER);
        input1.setFilters(new InputFilter[]{new InputFilter.LengthFilter(7)});
        input1.setHint("Lower limit");

        input2.setInputType(InputType.TYPE_CLASS_NUMBER);
        input2.setFilters(new InputFilter[]{new InputFilter.LengthFilter(7)});
        input2.setHint("Upper limit");

        LinearLayout lay = new LinearLayout(this);
        lay.setOrientation(LinearLayout.VERTICAL);
        lay.addView(input1);
        lay.addView(input2);

        lay.addView(kpeCheck);


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Filter")
                .setTitle("Update Filter")
                .setMessage("Set ID for each limit")
                .setView(lay)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String address1 = input1.getText().toString();
                        String address2 = input2.getText().toString();

                        if (kpeCheck.isChecked()) {
                            if (address1.length() > 0 && address2.length() > 0) {
                                if (Integer.parseInt(address1) <= 16777214 && Integer.parseInt(address2) <= 16777214) {
                                    MeganetInstances.getInstance().GetMeganetEngine().SetRangeFilter(address1, address2, true);

                                }
                            }
                        }
                        else {
                            if (address1.length() > 0 && address2.length() > 0) {
                                if (Integer.parseInt(address1) <= 65535 && Integer.parseInt(address2) <= 65535) {
                                    MeganetInstances.getInstance().GetMeganetEngine().SetRangeFilter(address1, address2, false);

                                }
                            }
                        }


                        // deal with the editable
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Do nothing.
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











        /*



        new AlertDialog.Builder(this)
                .setTitle("Update Filter")
                .setMessage("Set ID for each limit (must be between 0 to 65535)")
                .setView(lay)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String address1 = input1.getText().toString();
                        String address2 = input2.getText().toString();

                        if (kpeCheck.isChecked()) {
                            if (address1.length() > 0 && address2.length() > 0) {
                                if (Integer.parseInt(address1) <= 65535 && Integer.parseInt(address2) <= 65535) {
                                    MeganetInstances.getInstance().GetMeganetEngine().SetRangeFilter(address1, address2, true);

                                }
                            }
                        }
                        else {
                            if (address1.length() > 0 && address2.length() > 0) {
                                if (Integer.parseInt(address1) <= 65535 && Integer.parseInt(address2) <= 65535) {
                                    MeganetInstances.getInstance().GetMeganetEngine().SetRangeFilter(address1, address2, false);

                                }
                            }
                        }


                        // deal with the editable
                    }
                })

                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Do nothing.
                    }
                }).show();

                */
    }

    private void SaveLogToFile(String fileName_prm, String dataStr_prm, boolean appen_prm) {
        File file = new File(getFilesDir(), fileName_prm);
        String pathStr = file.getPath();

        if (!appen_prm) {

            file.delete();
        }

        String filename = fileName_prm; //"myfile";
        String string = "//Hello world!";
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(filename, MODE_PRIVATE);
            outputStream.write(string.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Reads Page", // TODO: Define a title for the content shown.
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
                "Reads Page", // TODO: Define a title for the content shown.
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
