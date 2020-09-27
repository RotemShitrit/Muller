package com.kp.meganet.meganetkp;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private BluetoothAdapter myBluetoothAdapter;

    private Map<String, String> pairsList;
    private String _lastPairDevice;

    private TextView statusTextView;
    private Spinner pairsSpinner;
    private Switch bluetoothSwitch;

    private Button pairButtorn;
    private Button exitButton;

    private boolean btSupport;
    private boolean isPaired;

    private Toast toast;
    private long lastBackPressTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btSupport = false;
        isPaired = false;

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        exitButton = (Button) findViewById(R.id.buttonCloseApp);
        pairButtorn = (Button) findViewById(R.id.buttonPair);

        statusTextView = (TextView) findViewById(R.id.textViewStatus);
        bluetoothSwitch = (Switch) findViewById(R.id.switch1);

        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(myBluetoothAdapter == null)
        {
            statusTextView.setText("Status: Bluetooth is not supported");

            Toast.makeText(getApplicationContext(), "Your device does not support Bluetooth",
                    Toast.LENGTH_LONG).show();

            bluetoothSwitch.setEnabled(false);
            pairButtorn.setEnabled(false);
        }
        else
        {
            btSupport = true;
            MeganetInstances.getInstance().SetMeganetDb(new MeganetDB(this));

            MeganetInstances.getInstance().SetMeganetEngine(new MeganetEngine(myBluetoothAdapter));



            InitStartApp();

            bluetoothSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        MeganetInstances.getInstance().GetMeganetEngine().On();
                    } else {
                        MeganetInstances.getInstance().GetMeganetEngine().Off();
                        isPaired = false;
                    }

                    InitStartApp();
                }

            });

            exitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ExitApplication();

                }
            });

            ///////////////////////////////////////////////////////////////////////////////////////////////////
            pairButtorn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String macAddr;

                    if (MeganetInstances.getInstance().GetMeganetEngine().GetStatus() == BTengine.btMode.ON) {
                        macAddr = pairsList.get(pairsSpinner.getSelectedItem().toString());
                        if (Connect(v, macAddr)) {

                            Toast.makeText(getApplicationContext(), "Paired with: " + pairsSpinner.getSelectedItem().toString(),
                                    Toast.LENGTH_SHORT).show();
                            statusTextView.setText("Paired with: " + pairsSpinner.getSelectedItem().toString());

                            CommonSettingsData data = new CommonSettingsData(5, "last_bluetooth_pair", pairsSpinner.getSelectedItem().toString());

                            MeganetInstances.getInstance().GetMeganetDb().updateProperty(data);
                            isPaired = true;
                            pairButtorn.setEnabled(false);
                        } else {

                            Toast.makeText(getApplicationContext(), "Can't pair with: " + pairsSpinner.getSelectedItem().toString(),
                                    Toast.LENGTH_SHORT).show();
                            statusTextView.setText("Can't pair with: " + pairsSpinner.getSelectedItem().toString());
                            isPaired = false;

                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Bluetooth not enabled",
                                Toast.LENGTH_SHORT).show();
                        statusTextView.setText("Bluetooth is OFF");
                        isPaired = false;
                    }
                }
            });

        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (this.lastBackPressTime < System.currentTimeMillis() - 4000) {
                toast = Toast.makeText(this, "Press back again to close this app", Toast.LENGTH_LONG);
                toast.show();
                this.lastBackPressTime = System.currentTimeMillis();
            } else {
                if (toast != null) {
                    toast.cancel();
                }
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement - getResources().getString(R.string.title_activity)
        if (id == R.id.action_about) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.app_name) + ": Version: " + getResources().getString(R.string.app_version),
                    Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();
        int a = 0;

        if (id == R.id.nav_rssi) {
            OpenNewActivity(4);
        }
        else if(id == R.id.nav_ftp) {
            OpenNewActivity(9);
        }
        else
        {
            if(!btSupport)
            {
                Toast.makeText(getApplicationContext(), "Your device does not support Bluetooth",
                        Toast.LENGTH_LONG).show();

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);

                return false;
            }
            if (!isPaired)
            {
                Toast.makeText(getApplicationContext(), "Please pair RSINT unit before proceeding",
                        Toast.LENGTH_LONG).show();

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);

                return false;
            }

            Intent intent;


            if (id == R.id.nav_field_verif_1) {
                OpenNewActivity(1);
            } else if (id == R.id.nav_read_meter) {
                OpenNewActivity(3);
            } else if (id == R.id.nav_programm) {
                OpenNewActivity(5);
            } else if (id == R.id.nav_settings) {
                OpenNewActivity(6);
            } else if (id == R.id.nav_rdm) {
                OpenNewActivity(7);
            } else if (id == R.id.nav_orders) {
                OpenNewActivity(8);
            }  else if (id == R.id.nav_log) {
                OpenNewActivity(10);
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void InitStartApp()
    {
        MeganetInstances.getInstance().SetMeganetEngine(new MeganetEngine(myBluetoothAdapter));

        bluetoothSwitch.setEnabled(true);
        MeganetInstances.getInstance().GetMeganetEngine().MeganetInit();
        MeganetInstances.getInstance().GetMeganetEngine().InitFrequency(MeganetInstances.getInstance().GetMeganetDb().getSetting(2).GetKeyValue(), MeganetInstances.getInstance().GetMeganetDb().getSetting(3).GetKeyValue(), MeganetInstances.getInstance().GetMeganetDb().getSetting(4).GetKeyValue(), MeganetInstances.getInstance().GetMeganetDb().getSetting(9).GetKeyValue(), MeganetInstances.getInstance().GetMeganetDb().getSetting(8).GetKeyValue());

        if(MeganetInstances.getInstance().GetMeganetEngine().GetStatus() == BTengine.btMode.ON)
        {
            statusTextView.setText("Status: Bluetooth is ON");

            pairsList = MeganetInstances.getInstance().GetMeganetEngine().GetDeviceList();

            // Pairs list
            String[] array_spinner = pairsList.keySet().toArray(new String[0]);
            pairsSpinner = (Spinner) findViewById(R.id.spinner);
            ArrayAdapter<?> adapter = new ArrayAdapter<Object>(this,
                    android.R.layout.simple_spinner_item, array_spinner);
            pairsSpinner.setAdapter(adapter);

            _lastPairDevice = "";
            _lastPairDevice = MeganetInstances.getInstance().GetMeganetDb().getSetting(5).GetKeyValue();
            if(_lastPairDevice.length() > 0)
                pairsSpinner.setSelection(((ArrayAdapter<String>) pairsSpinner.getAdapter()).getPosition(_lastPairDevice));
            pairButtorn.setEnabled(true);
            bluetoothSwitch.setChecked(true);
        }
        else
        {
            pairButtorn.setEnabled(false);

            statusTextView.setText("Status: Bluetooth is OFF, please turn ON Bluetooth");
            if(pairsSpinner != null)
                pairsSpinner.setAdapter(null);
            bluetoothSwitch.setChecked(false);
        }
    }

    public boolean Connect(View v, String address_prm) {
        Toast.makeText(getApplicationContext(), "Connecting",
                Toast.LENGTH_SHORT).show();

        boolean result = MeganetInstances.getInstance().GetMeganetEngine().ConnectTo(address_prm);
        if(result)
            MeganetInstances.getInstance().GetMeganetEngine().RSNTVersionRequest();

        return  result;

    }

    public void OpenNewActivity(int activityID_prm)
    {
        Intent intent;
        switch(activityID_prm)
        {

            case 1:
                Toast.makeText(getApplicationContext(), "Field Verification", Toast.LENGTH_LONG).show();

                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.FIELD_VERIF_1);
                intent = new Intent(MainActivity.this, ReadsActivity.class);
                startActivity(intent);
                // TODO Something
                break;
            case 2:
                Toast.makeText(getApplicationContext(), "Field Verification 2", Toast.LENGTH_LONG).show();

                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.FIELD_VERIF_2);
                intent = new Intent(MainActivity.this, ReadsActivity.class);
                startActivity(intent);
                // TODO Something
                break;
            case 3:
                Toast.makeText(getApplicationContext(), "Read Meter", Toast.LENGTH_LONG).show();

                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.READ_METER);
                intent = new Intent(MainActivity.this, ReadsActivity.class);
                startActivity(intent);
                // TODO Something
                break;

            case 4:
                Toast.makeText(getApplicationContext(), "RANMAN RSSI", Toast.LENGTH_LONG).show();
                String url = MeganetInstances.getInstance().GetMeganetDb().getSetting(7).GetKeyValue();//"http://www.google.com";
                if (!url.startsWith("http://") && !url.startsWith("https://"))
                    url = "http://" + url;
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));

                //MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.RDM);
                //intent = new Intent(MainActivity.this, ReadsActivity.class);
                //startActivity(intent);
                // TODO Something
                break;

            case 5:
                Toast.makeText(getApplicationContext(), "Programming", Toast.LENGTH_LONG).show();

                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.NONE);
                intent = new Intent(MainActivity.this, QrCodeActivity.class);
                startActivity(intent);
                // TODO Something
                break;
            case 6:
                Toast.makeText(getApplicationContext(), "Settings", Toast.LENGTH_LONG).show();

                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.NONE);
                intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                // TODO Something
                break;
            case 7:
                Toast.makeText(getApplicationContext(), "RDM Control", Toast.LENGTH_LONG).show();
                //MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.NONE);
                intent = new Intent(MainActivity.this, RDM_Controll.class);
                startActivity(intent);
                // TODO Something
                break;

            case 8: // If there is no internet, open WorkOrderSelect_2 (a message to connect internet)
                ConnectivityManager cm = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
                if (cm.getActiveNetworkInfo() != null) {
                    intent = new Intent(MainActivity.this, WorkOrderSelect_1.class);
                }
                else {
                    intent = new Intent(MainActivity.this, WorkOrderSelect_2.class);
                }
                Toast.makeText(getApplicationContext(), "Work Order", Toast.LENGTH_LONG).show();
                //MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.NONE);
                startActivity(intent);
                // TODO Something
                break;

            case 9:
                Toast.makeText(getApplicationContext(), "FTP", Toast.LENGTH_LONG).show();
                intent = new Intent(MainActivity.this, FTP_Controll.class);
                startActivity(intent);
                // TODO Something
                break;

            case 10:
                Toast.makeText(getApplicationContext(), "History Log", Toast.LENGTH_LONG).show();
                intent = new Intent(MainActivity.this, History_Log_1.class);
                startActivity(intent);
                // TODO Something
                break;
        }
    }

    private void ExitApplication()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Close App?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getApplicationContext(), "Close App",
                                Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        moveTaskToBack(true);
						finishAndRemoveTask(); // remove task from recent tasks on phone								
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // some code if you want
                        dialog.dismiss();
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
}
