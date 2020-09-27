package com.kp.meganet.meganetkp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

public class History_Log_3 extends AppCompatActivity {
    FTPClient mFTPClient = null; //for access to FTP
    String new_fileName;// name of file
    String dataFile = ""; //all data that file include
    Button upload;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_log_3);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        SimpleDateFormat formatter = new SimpleDateFormat("MMddyy_HHmmss");
        Date now_date = new Date(System.currentTimeMillis());
        new_fileName = MeganetInstances.getInstance().GetMeganetEngine().GetLastAccSN() + "_" +
                MeganetInstances.getInstance().GetMeganetEngine().GetUnitAddress() + "_" + formatter.format(now_date) + ".csv";

        dataFile = getIntent().getStringExtra("data file");

        upload = (Button) findViewById(R.id.uploadToFTPbtn);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // upload the log to the FTP.
                new uploadFile().execute();
            }
        });


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
                        FileOutputStream fos = openFileOutput(new_fileName, MODE_PRIVATE);
                        OutputStreamWriter outputWriter = new OutputStreamWriter(fos);
                        outputWriter.write(dataFile);
                        outputWriter.close();

                        StringBuffer sb = new StringBuffer( "ftp://" );
                        sb.append( userName );
                        sb.append( ':' );
                        sb.append( password );
                        sb.append( '@' );
                        sb.append( host );
                        sb.append( "/History Log/" );
                        sb.append( new_fileName );
                        //sb.append( ";type=i" );

                        BufferedInputStream bis = null;
                        BufferedOutputStream bos = null;

                        try
                        {
                            URL url = new URL( sb.toString() );
                            URLConnection urlc = url.openConnection();
                            urlc.setDoOutput(true);
                            bos = new BufferedOutputStream( urlc.getOutputStream() );
                            bis = new BufferedInputStream( openFileInput(new_fileName) );

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
                AlertDialog.Builder dialog = new AlertDialog.Builder(History_Log_3.this);
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
                Intent intent = new Intent(History_Log_3.this, History_Log_4.class);
                startActivity(intent);
            }
        }
    }

}
