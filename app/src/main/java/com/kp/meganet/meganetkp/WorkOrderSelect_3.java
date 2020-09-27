package com.kp.meganet.meganetkp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class WorkOrderSelect_3 extends AppCompatActivity {

    private Button updateWorkOrderButton;
    private Button backButton;
    private TableLayout t2;

    FTPClient mFTPClient = null;
    String fileName;
    String[] columns = {"No.", "Address", "Account"};
    String tmp;
    String[] tmp2;
    String dataFile;
    int cnt = 1;
    String new_fileName;

    ArrayList<String> file_data = new ArrayList<>();
    ArrayList<String> lst = new ArrayList<>() ;

    //qr code scanner object

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_order_select_3);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        updateWorkOrderButton = (Button) findViewById(R.id.buttonUpdateWO);
        backButton = (Button) findViewById(R.id.buttonBack);

        t2 = (TableLayout) findViewById(R.id.table2);
        fileName = getIntent().getStringExtra("file name"); // save name of file
        dataFile = getIntent().getStringExtra("data file"); // save data in file
        new connection().execute(); // connect to FTP in order to read file

        updateWorkOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //count number of the installation that were submited and upload the updated file to FTP
                boolean nullFlag;
                int completed_accounts = 0;

                // check for each line in the file if it was submited.
                // count the number of completed accounts.
                System.out.println(dataFile);
                String[] splited_dataFile = dataFile.split("\n");
                for (String line : splited_dataFile)
                {
                    nullFlag = false;
                    String[] splited_line = line.split(",");
                    if(splited_line.length < 9)
                        nullFlag = true;
                    for(int i=0; i<splited_line.length && nullFlag == false; i++)
                    {
                        System.out.println(splited_line[i]);
                        splited_line[i] = splited_line[i].trim();
                        if (splited_line[i].equals("") || splited_line[i].equals(null) || splited_line[i].equals("null"))
                            nullFlag=true;
                    }
                    if (nullFlag==false)
                        completed_accounts ++;
                }

                // getting the new file name by the submited lines in the file.
                String[] splited_fileName = fileName.split("_");
                splited_fileName[4] = String.valueOf(completed_accounts);
                new_fileName = TextUtils.join("_", splited_fileName);

                // upload the new file with the submited lines.
                new uploadFile().execute();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WorkOrderSelect_3.this, WorkOrderSelect_1.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(WorkOrderSelect_3.this, WorkOrderSelect_1.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public class uploadFile extends AsyncTask<Void, Void, Void> {

        Boolean connect = true;

        @Override
    protected Void doInBackground(Void... voids) {

        //getting shared preferences
        SharedPreferences sp = getSharedPreferences("ftp", MODE_PRIVATE);

        String host = sp.getString("host", "host");
        String userName = sp.getString("username", "username");
        String password = sp.getString("password", "password");
        int port = sp.getInt("port", (int) 2l);

        try {
            mFTPClient = new FTPClient();
            mFTPClient.connect(host,port);

            if (mFTPClient.login(userName, password)) {
                mFTPClient.enterLocalPassiveMode(); // important!
                mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
                mFTPClient.changeWorkingDirectory("/Complete Work Orders");

                int returnCode = mFTPClient.getReplyCode();
                if (returnCode == 550) {
                    mFTPClient.makeDirectory("Complete Work Orders");
                }

                try {
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
                    sb.append( "/Complete Work Orders/" );
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
                    Log.e("Exception", "File write failed: " + e.toString());
                    connect = false;
                }
            }
            else
                connect = false;

        } catch (Exception e) {
            e.printStackTrace();
            connect = false;
        }
        return null;
    }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (!connect) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(WorkOrderSelect_3.this);
                dialog.setTitle("Problem with the uploading file to FTP!");
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
                Toast.makeText(getApplicationContext(),"Uploaded to FTP!",Toast.LENGTH_LONG).show();
            }
        }
    }

    public class connection extends AsyncTask<Void, Void, Void> {

        Boolean connect = true;

        @Override
        protected Void doInBackground(Void... voids) {

            //getting shared preferences that save details of FTP
            SharedPreferences sp = getSharedPreferences("ftp", MODE_PRIVATE);

            String host = sp.getString("host", "host");
            String userName = sp.getString("username", "username");
            String password = sp.getString("password", "password");
            int port = sp.getInt("port", (int) 2l);

            mFTPClient = new FTPClient();
            try { //try to connect to FTP
                mFTPClient.connect(host, port);
                if (mFTPClient.login(userName, password)) {
                    mFTPClient.enterLocalPassiveMode();
                    mFTPClient.changeWorkingDirectory("/Work Orders");

                    // read file with input stream
                    InputStream inStream = mFTPClient.retrieveFileStream(fileName);
                    InputStreamReader isr = new InputStreamReader(inStream, "UTF8");

                    // Read the data that file include
                    int data = isr.read();
                    String contents = "";
                    while(data != -1){
                        char theChar = (char) data;
                        contents = contents + theChar;
                        data = isr.read();
                    }
                    isr.close();
                    connect = true;
                    mFTPClient.disconnect();

                    String[] splited = contents.split("\n"); // splited include all lines in file
                    String[] splited2;
                    for (int i = 0; i < splited.length; i++)
                    {
                        file_data.add(splited[i]);
                        splited2 = splited[i].split(",");
                        tmp = String.valueOf(cnt) + ", " + splited2[0] + ", " + splited2[1]; // arrange the details to match the table by columns
                        lst.add(tmp);
                        cnt++; // count number of lines
                    }
                    if (dataFile == null)
                        dataFile = TextUtils.join("\n",file_data);
                }
                else
                    connect = false;
            } catch (IOException e) {
                e.printStackTrace();
                connect = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (!connect) { // problem with connection to FTP
                AlertDialog.Builder dialog = new AlertDialog.Builder(WorkOrderSelect_3.this);
                dialog.setTitle("Can't connect to FTP!");
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

            } else { // create table layout and add all lines in file to the table
                TableRow tr_head = new TableRow(WorkOrderSelect_3.this);
                for (String col : columns) { //add columns title
                    TextView tv = new TextView(WorkOrderSelect_3.this);
                    tv.setPadding(5, 5, 5, 5);
                    tv.setGravity(Gravity.CENTER_HORIZONTAL);
                    tv.setTextSize(20);
                    tv.setTextColor(Color.BLACK);
                    tv.setBackgroundColor(Color.LTGRAY);
                    tv.setText(col);
                    tr_head.addView(tv);
                }
                t2.addView(tr_head, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));


                for (String str : lst) { // add all line as a row table
                    tmp2 = str.split(",");
                    TableRow tr = new TableRow(WorkOrderSelect_3.this);

                    for (String s : tmp2) {
                        TextView tv = new TextView(WorkOrderSelect_3.this);
                        tv.setPadding(7, 7, 7, 7);
                        tv.setGravity(Gravity.CENTER_HORIZONTAL);
                        tv.setTextSize(18);
                        tv.setTextColor(Color.BLACK);
                        tv.setText(s);
                        tr.addView(tv);
                    }
                    tr.setBackgroundColor(Color.WHITE);
                    tr.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int index = Integer.parseInt((String) ((TextView) ((TableRow) v).getChildAt(0)).getText());
                            String account_number = file_data.get(index - 1).split(",")[1];
                            MeganetInstances.getInstance().GetMeganetEngine().SetAccountNumber(account_number);
                            String address = file_data.get(index - 1).split(",")[0];
                            MeganetInstances.getInstance().GetMeganetEngine().SetAddressName(address);
                            String meterSN = file_data.get(index - 1).split(",")[2];
                            MeganetInstances.getInstance().GetMeganetEngine().SetLastOldAccSN(meterSN);
                            v.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));

                            Intent intent = new Intent(WorkOrderSelect_3.this, WorkOrderSelect_8.class);
                            intent.putExtra("address", address);
                            intent.putExtra("account", account_number);
                            intent.putExtra("file name", fileName);
                            intent.putExtra("data file", dataFile);
                            intent.putExtra("meter SN", meterSN);
                            startActivity(intent);
                        }
                    });
                    t2.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
                }
            }
        }
    }
}
