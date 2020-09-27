package com.kp.meganet.meganetkp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class WorkOrderSelect_6 extends AppCompatActivity {

    private LocationManager locationMangaer = null;
    private LocationListener locationListener = null;
    private EditText editTextCaption;

    private Button btnGetLocation;
    private Button btnSave;
    private TextView editLatitude;
    private TextView editLongitude;
    private ProgressBar pb;

    private static final String TAG = "Debug";
    private Boolean flag = false;

    String fileName;
    String dataFile;

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(WorkOrderSelect_6.this, WorkOrderSelect_5.class);
        intent.putExtra("file name", fileName);
        intent.putExtra("data file", dataFile);
        startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_order_select_6);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        editTextCaption = (EditText) findViewById(R.id.editText);
        btnSave = (Button) findViewById(R.id.buttonSave);

        fileName = getIntent().getStringExtra("file name");
        dataFile = getIntent().getStringExtra("data file");

        //if you want to lock screen for always Portrait mode
        setRequestedOrientation(ActivityInfo
                .SCREEN_ORIENTATION_PORTRAIT);

        //pb = (ProgressBar) findViewById(R.unitID.progressBar1);
        //pb.setVisibility(View.INVISIBLE);

        editLatitude = (TextView) findViewById(R.id.textViewSN);
        editLongitude = (TextView) findViewById(R.id.textViewID);

        btnGetLocation = (Button) findViewById(R.id.buttonGetGPS);
        btnGetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = displayGpsStatus();
                if (flag) {
                    Log.v(TAG, "onClick");
                    editTextCaption.setText("Please Wait Until Coordinates will received");
           /* editLocation.setText("Please!! move your device to"+
                    " see the changes in coordinates."+"\nWait..");
            */
                    //pb.setVisibility(View.VISIBLE);
                    locationListener = new MyLocationListener();
                    if (ActivityCompat.checkSelfPermission(WorkOrderSelect_6.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(WorkOrderSelect_6.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    locationMangaer.requestLocationUpdates(LocationManager
                            .GPS_PROVIDER, 5000, 10, locationListener);
                } else {
                    alertbox("Gps Status!!", "Your GPS is: OFF");
                }
            }
        });

        locationMangaer = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MeganetInstances.getInstance().GetMeganetEngine().SetLastLatitude(editLatitude.getText().toString());
                MeganetInstances.getInstance().GetMeganetEngine().SetLastLongitude(editLongitude.getText().toString());

                Intent intent = new Intent(WorkOrderSelect_6.this, WorkOrderSelect_8.class);
                intent.putExtra("file name", fileName);
                intent.putExtra("data file", dataFile);
                startActivity(intent);
            }
        });

    }

    /*----Method to Check GPS is enable or disable ----- */
    private Boolean displayGpsStatus() {
        ContentResolver contentResolver = getBaseContext()
                .getContentResolver();
        boolean gpsStatus = Settings.Secure
                .isLocationProviderEnabled(contentResolver,
                        LocationManager.GPS_PROVIDER);
        if (gpsStatus) {
            return true;

        } else {
            return false;
        }
    }

    /*----------Method to create an AlertBox ------------- */
    protected void alertbox(String title, String mymessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("\nThe GPS on your device is disabled!\n" +
                "Please enable GPS and try again.\n")
                .setCancelable(false)
                //.setTitle("** Gps Status **") - canceled on 21.11.2019
                .setPositiveButton("Gps On",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // finish the current activity
                                // AlertBoxAdvance.this.finish();
                                Intent myIntent = new Intent(
                                        Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(myIntent);
                                dialog.cancel();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // cancel the dialog box
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
        Button bq = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        bq.setBackgroundColor(Color.WHITE);
        bq.setTextColor(Color.RED);

        bq = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        bq.setBackgroundColor(Color.WHITE);
        bq.setTextColor(Color.RED);
    }

    /*----------Listener class to get coordinates ------------- */
    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {

            editLatitude.setText("");
            editLongitude.setText("");
            //pb.setVisibility(View.INVISIBLE);
            Toast.makeText(getBaseContext(),"Location changed : Lat: " +
                            loc.getLatitude()+ " Lng: " + loc.getLongitude(),
                    Toast.LENGTH_SHORT).show();
            String longitude = " " +loc.getLongitude();
            Log.v(TAG, longitude);
            String latitude = " " +loc.getLatitude();
            Log.v(TAG, latitude);

            /*----------to get City-Name from coordinates ------------- */
            String cityName=null;
            Geocoder gcd = new Geocoder(getBaseContext(),
                    Locale.getDefault());
            List<Address> addresses;
            try {
                addresses = gcd.getFromLocation(loc.getLatitude(), loc
                        .getLongitude(), 1);
                if (addresses.size() > 0)
                    System.out.println(addresses.get(0).getLocality());
                cityName=addresses.get(0).getLocality();
            } catch (IOException e) {
                e.printStackTrace();
            }

            String s = longitude+"\n"+latitude +
                    "\n\nMy Currrent City is: "+cityName;
            editLatitude.setText(latitude);
            editLongitude.setText(longitude);

            editTextCaption.setText("New Meter and Node Info");
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider,
                                    int status, Bundle extras) {
        }
    }
}


/*{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_order_select_6);


    }
}
*/