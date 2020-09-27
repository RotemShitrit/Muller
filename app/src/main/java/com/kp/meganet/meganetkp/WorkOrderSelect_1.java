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
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.IOException;
import java.util.ArrayList;

public class WorkOrderSelect_1 extends AppCompatActivity {
    @Override
    public void onBackPressed() {
        finish();
    }

    private Button exitButton;
    private TableLayout tl;

    FTPClient mFTPClient = null;
    String[] columns = {"No.", "List Name", "Installations"}; // columns of the table that present the files in FTP

    ArrayList<String> name_of_files = new ArrayList<>();
    ArrayList<String> lst = new ArrayList<>() ;
    String tmp;
    String[] tmp2;
    int cnt = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_order_selec_1);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        exitButton  = (Button) findViewById(R.id.buttonBack);
        tl = (TableLayout) findViewById(R.id.table1);
        new connection().execute();

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public class connection extends AsyncTask<Void, Void, Void> {

        Boolean connect = true;

        @Override
        protected Void doInBackground(Void... voids) {
            //getting shared preferences that saved the ftp details
            SharedPreferences sp = getSharedPreferences("ftp", MODE_PRIVATE);

            String host = sp.getString("host", "host");
            String userName = sp.getString("username", "username");
            String password = sp.getString("password", "password");
            int port = sp.getInt("port", (int) 2l);
            String[] file_names ;

            mFTPClient = new FTPClient();
            try { // try to connect to FTP
                mFTPClient.connect(host,port);
                if (mFTPClient.login(userName, password)) {
                    mFTPClient.enterLocalPassiveMode();
                    mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
                    mFTPClient.changeWorkingDirectory("/Work Orders"); // open Work Orders folder

                    // getting name of files in FTP
                    file_names = mFTPClient.listNames();
                    for (String name : file_names)
                    {
                        if (name.startsWith("WO_")) // add all file that start with "WO_"
                        {
                            name_of_files.add(name);
                            name = name.substring(0,name.indexOf("."));
                            tmp = String.valueOf(cnt) + ", WO_" + name.split("_")[1] + ", " + name.split("_")[5];
                            lst.add(tmp);
                            cnt ++;
                        }
                    }
                    connect = true;
                    mFTPClient.logout();
                    mFTPClient.disconnect();
                }
                else { // cannot connect to FTP
                    connect = false;
                }
            } catch (IOException e) { // cannot connect to FTP
                e.printStackTrace();
                connect = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (!connect) { // If we didn't success to connect to FTP
                AlertDialog.Builder dialog = new AlertDialog.Builder(WorkOrderSelect_1.this);
                dialog.setTitle("Can't connect to FTP!");
                dialog.setCancelable(true);
                dialog.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                finish();
                            }
                        });
                AlertDialog alertDialog=dialog.create();
                alertDialog.show();
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
            }
            else { // create a table layout and present all of the files in FTP on the table layout
                TableRow tr_head = new TableRow(WorkOrderSelect_1.this);
                for (String col : columns) {
                    TextView tv = new TextView(WorkOrderSelect_1.this);
                    tv.setPadding(5, 5, 5, 5);
                    tv.setGravity(Gravity.CENTER_HORIZONTAL);
                    tv.setTextSize(20);
                    tv.setTextColor(Color.BLACK);
                    tv.setBackgroundColor(Color.LTGRAY);
                    tv.setText(col);
                    tr_head.addView(tv);
                }
                tl.addView(tr_head, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));

                for (String str : lst) {
                    tmp2 = str.split(",");
                    TableRow tr = new TableRow(WorkOrderSelect_1.this);

                    for (String s : tmp2) {
                        TextView tv = new TextView(WorkOrderSelect_1.this);
                        tv.setPadding(7, 7, 7, 7);
                        tv.setGravity(Gravity.CENTER_HORIZONTAL);
                        tv.setTextSize(18);
                        tv.setTextColor(Color.BLACK);
                        tv.setText(s);
                        tr.addView(tv);
                    }
                    tr.setBackgroundColor(Color.WHITE);
                    tr.setClickable(true);
                    tr.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int index = Integer.parseInt((String) ((TextView) ((TableRow) v).getChildAt(0)).getText());
                            String fileName = name_of_files.get(index - 1);

                            v.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
                            MeganetInstances.getInstance().GetMeganetEngine().SetAddressName((String) ((TextView) ((TableRow) v).getChildAt(1)).getText());
                            Toast.makeText(getApplicationContext(), "Work Order", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(WorkOrderSelect_1.this, WorkOrderSelect_3.class);
                            intent.putExtra("file name", fileName);
                            startActivity(intent);
                        }

                    });

                    tl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
                }
            }
        }
    }
}

