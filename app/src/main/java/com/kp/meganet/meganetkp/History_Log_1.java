package com.kp.meganet.meganetkp;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

public class History_Log_1 extends AppCompatActivity {
    Button back_btn;
    Button get_history;
    Button uploadFiles;
    String fileName;
    FTPClient mFTPClient = null; //for access to FTP


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_log_1);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        uploadFiles = (Button) findViewById(R.id.uploadBtn);
        back_btn = (Button) findViewById(R.id.backBtn);
        get_history = (Button) findViewById(R.id.getHistoryBtn);
        get_history.setOnClickListener(new View.OnClickListener() {
            // Click on get history button open the option to choose the device and insert unitID manually or scan QR code
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(History_Log_1.this, History_Log_1_1.class);
                startActivity(intent);
            }
        });

        uploadFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fileintent = new Intent(Intent.ACTION_GET_CONTENT);
                fileintent.setType("*/*");
                try {
                    startActivityForResult(fileintent, 10);
                } catch (ActivityNotFoundException e) {
                    Log.e("tag", "No activity can handle picking a file. Showing alternatives.");
                }
            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode)
        {
            case 10:
                if(resultCode == RESULT_OK)
                {
                    fileName = data.getData().getPath().substring(data.getData().getPath().lastIndexOf("/")+1);
                    new uploadFile().execute();
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_getlog, menu);
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

            case R.id.menu_getlog_field_verif:
                super.onBackPressed();
                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.FIELD_VERIF_1);
                intent = new Intent(History_Log_1.this, ReadsActivity.class);
                startActivity(intent);
                break;

            case R.id.menu_getlog_ranman:
                super.onBackPressed();
                Toast.makeText(getApplicationContext(), "RANMAN RSSI", Toast.LENGTH_LONG).show();
                String url = MeganetInstances.getInstance().GetMeganetDb().getSetting(7).GetKeyValue();//"http://www.google.com";
                if (!url.startsWith("http://") && !url.startsWith("https://"))
                    url = "http://" + url;
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                break;

            case R.id.menu_getlog_read_meter:
                super.onBackPressed();
                Toast.makeText(getApplicationContext(), "Read Meter", Toast.LENGTH_LONG).show();

                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.READ_METER);
                intent = new Intent(History_Log_1.this, ReadsActivity.class);
                startActivity(intent);
                break;

            case R.id.menu_getlog_rdm:
                super.onBackPressed();
                Toast.makeText(getApplicationContext(), "RDM Control", Toast.LENGTH_LONG).show();

                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.NONE);
                intent = new Intent(History_Log_1.this, RDM_Controll.class);
                startActivity(intent);
                break;

            case R.id.menu_getlog_settings:
                super.onBackPressed();
                Toast.makeText(getApplicationContext(), "Settings", Toast.LENGTH_LONG).show();

                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.NONE);
                intent = new Intent(History_Log_1.this, SettingsActivity.class);
                startActivity(intent);
                // TODO Something
                break;

            case R.id.menu_getlog_ftp:
                super.onBackPressed();
                Toast.makeText(getApplicationContext(), "FTP", Toast.LENGTH_LONG).show();

                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.NONE);
                intent = new Intent(History_Log_1.this, FTP_Controll.class);
                startActivity(intent);
                // TODO Something
                break;

            case R.id.menu_getlog_program:
                Toast.makeText(getApplicationContext(), "Programming", Toast.LENGTH_LONG).show();

                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.NONE);
                intent = new Intent(History_Log_1.this, QrCodeActivity.class);
                startActivity(intent);
                // TODO Something
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    public class uploadFile extends AsyncTask<Void, Void, Void> {
        Boolean connect = true;

        @Override
        protected Void doInBackground(Void... voids) {

            //getting shared preferences that saved the ftp details
            SharedPreferences sp = getSharedPreferences("ftp", MODE_PRIVATE);

            String host = sp.getString("host", "host");
            String userName = sp.getString("username", "username");
            String password = sp.getString("password", "password");
            int port = sp.getInt("port", (int) 2l);

            try { // try to connect to FTP and open the History Log folder. It not exist - create this folder
                mFTPClient = new FTPClient();
                mFTPClient.connect(host,port);

                if (mFTPClient.login(userName, password)) {
                    mFTPClient.enterLocalPassiveMode(); // important!
                    mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
                    mFTPClient.changeWorkingDirectory("/History Log");

                    int returnCode = mFTPClient.getReplyCode();
                    if (returnCode == 550) {
                        mFTPClient.makeDirectory("History Log");
                    }

                    try { // try to open new file and write data in it with output streamer

                        StringBuffer sb = new StringBuffer( "ftp://" );
                        sb.append( userName );
                        sb.append( ':' );
                        sb.append( password );
                        sb.append( '@' );
                        sb.append( host );
                        sb.append( "/History Log/" );
                        sb.append( fileName );
                        //sb.append( ";type=i" );

                        BufferedInputStream bis = null;
                        BufferedOutputStream bos = null;

                        try
                        {
                            URL url = new URL( sb.toString() );
                            URLConnection urlc = url.openConnection();
                            urlc.setDoOutput(true);

                            File dir = new File( Environment.getExternalStorageDirectory().getAbsolutePath()+ "/saved_logs");
                            if(dir.exists()) {
                                File file = new File(dir, fileName);
                                FileOutputStream fos = openFileOutput(fileName, MODE_PRIVATE);
                                OutputStreamWriter outputWriter = new OutputStreamWriter(fos);
                                StringBuilder text = new StringBuilder();
                                try {
                                    BufferedReader br = new BufferedReader(new FileReader(file));
                                    String line;
                                    while ((line = br.readLine()) != null) {
                                        text.append(line);
                                        text.append('\n');
                                    }
                                    br.close();
                                    outputWriter.write(String.valueOf(text));
                                    outputWriter.close();

                                } catch (IOException e) {
                                    //You'll need to add proper error handling here
                                }
                            }

                            bos = new BufferedOutputStream( urlc.getOutputStream() );
                            bis = new BufferedInputStream( openFileInput(fileName) );

                            int i;
                            // read byte by byte until end of stream
                            while ((i = bis.read()) != -1)
                            {
                                if(i==10)
                                {
                                    bos.write('\r');
                                    bos.write('\n');
                                    bos.flush();
                                }
                                else
                                    bos.write( i );
                            }
                        }
                        finally
                        {
                            if (bis != null)
                                try {
                                    bis.close();
                                } catch (IOException ioe) {
                                    ioe.printStackTrace();
                                }
                            if (bos != null)
                                try {
                                    bos.close();
                                } catch (IOException ioe) {
                                    ioe.printStackTrace();
                                }
                        }


                        connect = true;
                        mFTPClient.logout();
                        mFTPClient.disconnect();

                    } catch (IOException e) {
                        connect = false;
                        Log.e("Exception", "File write failed: " + e.toString());
                    }
                }
                else
                    connect = false;
            } catch (Exception e) {
                connect = false;
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (!connect) { // If there is a problem with the writing file to FTP or with the connection to FTP
                AlertDialog.Builder dialog = new AlertDialog.Builder(History_Log_1.this);
                dialog.setTitle("Problem with FTP connection!");
                dialog.setCancelable(true);
                dialog.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                finish();
                            }
                        });
                AlertDialog alertDialog = dialog.create();
                alertDialog.show();
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
            }
            else {
                Toast.makeText(getApplicationContext(), "Uploaded to FTP!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(History_Log_1.this, History_Log_4.class);
                startActivity(intent);
            }
        }
    }
}
