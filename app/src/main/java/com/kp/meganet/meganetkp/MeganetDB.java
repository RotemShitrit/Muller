package com.kp.meganet.meganetkp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alex on 11/24/2015.
 */
public class MeganetDB extends SQLiteOpenHelper {

    ArrayList<String> ArrayofName = new ArrayList<String>();
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "meganet_db_ab";

    // settings table name
    private static final String TABLE_SETTINGS = "meganet_settings";

    // settings Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_VALUE = "property_value";

    public MeganetDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        OnSettingsInit();
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_SETTINGS_TABLE = "CREATE TABLE " + TABLE_SETTINGS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_VALUE + " TEXT" + ")";
        db.execSQL(CREATE_SETTINGS_TABLE);


    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTINGS);

        // Create tables again
        onCreate(db);

        //OnSettingsInit();
    }

    private void OnSettingsInit()
    {
        //if(getSettingsCount() > 0)
        //    return;

        CommonSettingsData data = new CommonSettingsData();


        data.SetID(1);
        data.SetKeyName("hh_id");
        data.SetKeyValue("00001");
        if(getSettingsCount(data) <= 0)
            AddProperty(data);

        data.SetID(2);
        data.SetKeyName("field_verif_freq_1");
        data.SetKeyValue("173.375");
        if(getSettingsCount(data) <= 0)
            AddProperty(data);

        data.SetID(3);
        data.SetKeyName("field_verif_freq_2");
        data.SetKeyValue("173.375");
        if(getSettingsCount(data) <= 0)
            AddProperty(data);

        data.SetID(4);
        data.SetKeyName("read_meter_freq");
        data.SetKeyValue("173.375");
        if(getSettingsCount(data) <= 0)
            AddProperty(data);

        data.SetID(5);
        data.SetKeyName("last_bluetooth_pair");
        data.SetKeyValue("");
        if(getSettingsCount(data) <= 0)
            AddProperty(data);

        data.SetID(6);
        data.SetKeyName("last_programm_prompt_type");
        data.SetKeyValue("");
        if(getSettingsCount(data) <= 0)
            AddProperty(data);

        data.SetID(7);
        data.SetKeyName("ranman_url");
        data.SetKeyValue("http://www.google.com");
        if(getSettingsCount(data) <= 0)
            AddProperty(data);

        data.SetID(8);
        data.SetKeyName("use_kpack");
        data.SetKeyValue("0");
        if(getSettingsCount(data) <= 0)
            AddProperty(data);

        data.SetID(9);
        data.SetKeyName("rdm_freq");
        data.SetKeyValue("173.375");
        if(getSettingsCount(data) <= 0)
            AddProperty(data);
    }
    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new setting
    void AddProperty(CommonSettingsData data) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, data.GetKeyName());
        values.put(KEY_VALUE, data.GetKeyValue());

        // Inserting Row
        db.insert(TABLE_SETTINGS, null, values);
        db.close(); // Closing database connection
    }

    // Getting single setting
    CommonSettingsData getSetting(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_SETTINGS, new String[] { KEY_ID,
                        KEY_NAME, KEY_VALUE }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        if(cursor.getCount() > 0)
        {
            CommonSettingsData setting = new CommonSettingsData(Integer.parseInt(cursor.getString(0)),
                    cursor.getString(1), cursor.getString(2));


            return setting;
        }
        return new CommonSettingsData();
    }

    // Getting All settings
    public List<CommonSettingsData> getAllSettings() {
        List<CommonSettingsData> settingtList = new ArrayList<CommonSettingsData>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_SETTINGS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                CommonSettingsData setting = new CommonSettingsData();
                setting.SetID(Integer.parseInt(cursor.getString(0)));
                setting.SetKeyName(cursor.getString(1));
                setting.SetKeyValue(cursor.getString(2));

                String name = cursor.getString(1) +"\n"+ cursor.getString(2);
                ArrayofName.add(name);
                // Adding setting to list
                settingtList.add(setting);
            } while (cursor.moveToNext());
        }

        // return settings list
        return settingtList;
    }

    // Updating single setting
    public int updateProperty(CommonSettingsData data) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, data.GetKeyName());
        values.put(KEY_VALUE, data.GetKeyValue());

        // updating row
        return db.update(TABLE_SETTINGS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(data.GetID()) });
    }

    // Deleting single setting
    public void deleteSetting(CommonSettingsData data) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SETTINGS, KEY_ID + " = ?",
                new String[] { String.valueOf(data.GetID()) });
        db.close();
    }

    // Getting settings Count
    public int getSettingsCount(CommonSettingsData data) {
        String countQuery = "SELECT  * FROM " + TABLE_SETTINGS + " WHERE " + KEY_ID + " = " + data.GetID() + " AND " + KEY_NAME + " = '" + data.GetKeyName() + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();

        // return count
        return cnt;
    }
}
