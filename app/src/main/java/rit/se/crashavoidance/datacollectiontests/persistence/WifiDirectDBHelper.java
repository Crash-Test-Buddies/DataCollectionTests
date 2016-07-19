package rit.se.crashavoidance.datacollectiontests.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import rit.se.crashavoidance.datacollectiontests.service.DBParcelable;


/**
 * Created by Chris on 6/12/2016.
 * Handles database transactions and performs initial creation of database
 */
public class WifiDirectDBHelper extends SQLiteOpenHelper {
    public static final String TAG = "DataCollector";
    // Strings for basic SQL command/keywords
    private static final String TEXT = " TEXT";
    private static final String INTEGER = " INTEGER";
    private static final String DOUBLE = " DOUBLE";
    private static final String FOREIGN_KEY = " FOREIGN KEY(";
    private static final String REFERENCES = ") REFERENCES ";
    private static final String COMMA = ", ";
    public static final String PRIMARY_KEY = " INTEGER PRIMARY KEY";
    private static final String CREATE_TABLE = "CREATE TABLE ";
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS ";
    private Context context;

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "WifiDirect.db";

    // Strings for creating tables
    private static final String SQL_CREATE_PHONE_TABLE =
            CREATE_TABLE + PhoneContract.PhoneEntry.TABLE_NAME + "("
                + PhoneContract.PhoneEntry._ID + PRIMARY_KEY
                + COMMA + PhoneContract.PhoneEntry.COLUMN_NAME_DEVICE_ADDRESS + TEXT
                + COMMA + PhoneContract.PhoneEntry.COLUMN_NAME_BRAND + TEXT
                + COMMA + PhoneContract.PhoneEntry.COLUMN_NAME_OS + INTEGER
                + COMMA + PhoneContract.PhoneEntry.COLUMN_NAME_MANUFACTURER + TEXT
                + COMMA + PhoneContract.PhoneEntry.COLUMN_NAME_MODEL + TEXT
                + COMMA + PhoneContract.PhoneEntry.COLUMN_NAME_STATUS + TEXT
                + ")";

    private static final String SQL_CREATE_RUN_TABLE =
            CREATE_TABLE + RunContract.RunEntry.TABLE_NAME + "("
                + RunContract.RunEntry._ID + PRIMARY_KEY
                + COMMA + RunContract.RunEntry.COLUMN_NAME_START_TIME + INTEGER
                + COMMA + RunContract.RunEntry.COLUMN_NAME_END_TIME + INTEGER
                + ")";
    private static final String SQL_CREATE_STEP_TABLE = "" +
            CREATE_TABLE + StepContract.StepEntry.TABLE_NAME + "("
                + StepContract.StepEntry._ID + PRIMARY_KEY
                + COMMA + StepContract.StepEntry.COLUMN_NAME_STEP_NAME + TEXT
                + ")";

    private static final String SQL_CREATE_STEP_TIMER_TABLE =
            CREATE_TABLE + StepTimerContract.StepEntry.TABLE_NAME + "("
                + StepTimerContract.StepEntry._ID + PRIMARY_KEY
                + COMMA + StepTimerContract.StepEntry.COLUMN_NAME_STEP_NAME + TEXT
                + COMMA + StepTimerContract.StepEntry.COLUMN_NAME_PHONE_ID + INTEGER
                + COMMA + StepTimerContract.StepEntry.COLUMN_NAME_START_TIME + INTEGER
                + COMMA + StepTimerContract.StepEntry.COLUMN_NAME_END_TIME + INTEGER
                + COMMA + StepTimerContract.StepEntry.COLUMN_NAME_LATITUDE + DOUBLE
                + COMMA + StepTimerContract.StepEntry.COLUMN_NAME_LONGITUDE + DOUBLE
                + COMMA + StepTimerContract.StepEntry.COLUMN_NAME_STATUS + TEXT
                + COMMA + FOREIGN_KEY + StepTimerContract.StepEntry.COLUMN_NAME_PHONE_ID + REFERENCES
                + PhoneContract.PhoneEntry.TABLE_NAME + "(" + PhoneContract.PhoneEntry._ID + ")"
                + ")";

    // Strings for dropping tables
    private static final String SQL_DROP_PHONE_TABLE =
            DROP_TABLE + PhoneContract.PhoneEntry.TABLE_NAME;
    private static final String SQL_DROP_RUN_TABLE =
            DROP_TABLE + RunContract.RunEntry.TABLE_NAME;
    private static final String SQL_DROP_STEP_TABLE =
            DROP_TABLE + StepContract.StepEntry.TABLE_NAME;
    private static final String SQL_DROP_STEP_TIMER_TABLE =
            DROP_TABLE + StepTimerContract.StepEntry.TABLE_NAME;

    public WifiDirectDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    /**
     * Called when database is opened (i.e. getWritableDatabase is called)
     * @param db
     */
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_PHONE_TABLE);
//        db.execSQL(SQL_CREATE_RUN_TABLE);
        db.execSQL(SQL_CREATE_STEP_TABLE);
        db.execSQL(SQL_CREATE_STEP_TIMER_TABLE);
        try {
            populateSteps(db);
        } catch (Exception e) {
            Log.e(TAG, "Exception parsing Step file", e);
        }
        populatePhoneInfo(db);
    }

    /**
     * Called when the database file exists but the stored version number is lower than requested
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        dropTables(db);
        onCreate(db);
    }

    /**
     * Read sql statements from a file and execute
     * @param db
     * @throws IOException
     */
    public void populateSteps(SQLiteDatabase db) throws IOException {
        URL url = getClass().getResource("StepEntries");
        File file = new File(url.getPath());
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line = "";
        db.beginTransaction();
        ContentValues values = new ContentValues();
        while ((line = bufferedReader.readLine()) != null){
            values.put(StepContract.StepEntry.COLUMN_NAME_STEP_NAME, line);
            // Insert the new row, returning the primary key value of the new row
            long newRowId;
            newRowId = db.insert(
                    StepContract.StepEntry.TABLE_NAME
                    ,null
                    ,values);

        }
        db.endTransaction();
    }

    public void populatePhoneInfo(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        // Get General phone information from Build class
        String myDeviceModel = Build.MODEL;
        String brand = Build.BRAND;
        String manufacturer = Build.MANUFACTURER;
        int osVersion = Build.VERSION.SDK_INT;
        // For the device Address and Device name we need a WifiP2pDevice object
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        String deviceAddress = info.getMacAddress();
        values.put(PhoneContract.PhoneEntry.COLUMN_NAME_BRAND, brand);
        values.put(PhoneContract.PhoneEntry.COLUMN_NAME_DEVICE_ADDRESS, deviceAddress);
        values.put(PhoneContract.PhoneEntry.COLUMN_NAME_MANUFACTURER, manufacturer);
        values.put(PhoneContract.PhoneEntry.COLUMN_NAME_OS, osVersion);
        values.put(PhoneContract.PhoneEntry.COLUMN_NAME_MODEL, myDeviceModel);
        long newRowId = db.insert(PhoneContract.PhoneEntry.TABLE_NAME
                                    ,null
                                    ,values);
    }



    /**
     * Drop all database tables
     * @param db
     */
    private void dropTables(SQLiteDatabase db){
        db.execSQL(SQL_DROP_PHONE_TABLE);
        db.execSQL(SQL_CREATE_RUN_TABLE);
        db.execSQL(SQL_CREATE_STEP_TABLE);
        db.execSQL(SQL_CREATE_STEP_TIMER_TABLE);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    /**
     * Inserts a row in the step timer table based on information passed to the
     * background service
     * @param parcelableObject
     */
    public void insertStepTimerRecord(DBParcelable parcelableObject) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            // Since we only have one phone record this id is always 1
            values.put(StepTimerContract.StepEntry.COLUMN_NAME_PHONE_ID, 1);
            values.put(StepTimerContract.StepEntry.COLUMN_NAME_START_TIME, parcelableObject.getStartTime());
            values.put(StepTimerContract.StepEntry.COLUMN_NAME_END_TIME, parcelableObject.getEndTime());
            values.put(StepTimerContract.StepEntry.COLUMN_NAME_STEP_NAME, parcelableObject.getStep());
            values.put(StepTimerContract.StepEntry.COLUMN_NAME_STATUS, "Ready");
            long newRowId = db.insert(StepTimerContract.StepEntry.TABLE_NAME
                    , null
                    , values);
        } finally {
            db.close();
        }
    }

    public Cursor getPhoneInfo() {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {PhoneContract.PhoneEntry.COLUMN_NAME_BRAND
            ,PhoneContract.PhoneEntry.COLUMN_NAME_DEVICE_ADDRESS
            ,PhoneContract.PhoneEntry.COLUMN_NAME_MANUFACTURER
            ,PhoneContract.PhoneEntry.COLUMN_NAME_MODEL
            ,PhoneContract.PhoneEntry.COLUMN_NAME_OS};
        Cursor phoneInfo = db.query(PhoneContract.PhoneEntry.TABLE_NAME, columns, null, null, null, null, null);
        return phoneInfo;
    }

    /**
     * Update Step timer records to Uploaded
     * @param idsToDelete ids to update with
     */
    public void deleteStepTimerRecords(List<Long> idsToDelete){
        SQLiteDatabase db = null;
        try {
            Log.i(TAG, "Updating step timer records");
            db = this.getWritableDatabase();
            String sql = "Delete from " + StepTimerContract.StepEntry.TABLE_NAME +
                    " where " + StepTimerContract.StepEntry._ID + " = ?";
            SQLiteStatement update = db.compileStatement(sql);
            db.beginTransaction();
            for (long id : idsToDelete) {
                update.bindLong(1, id);
                update.execute();
                Log.i(TAG, "Deleting record with id + " + id);
            }
            db.setTransactionSuccessful();
        } finally {
            if (db != null) {
                if (db.isOpen()) {
                    db.endTransaction();
                }
                db.close();
            }
            Log.i(TAG, "Finished deleting records");

        }
    }


    /**
     * Get the number of step records ready to be uploaded so we know how many batches to grab
     * @return
     */
    public long getReadyStepCount(){
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = StepTimerContract.StepEntry.COLUMN_NAME_STATUS + " = ?";
        String[] selectionArgs = {"Ready"};
        // Selects number of records based on selection criteria
        long numReady = DatabaseUtils.queryNumEntries(db, StepTimerContract.StepEntry.TABLE_NAME, selection, selectionArgs);
        return numReady;
    }

    /**
     * Get a batch of stepTimer records that are ready to be uploaded to the central database
     * @param batchLimit Max number of records to be returned
     * @return Cursor containing up to batchLimit number of StepTimer records ready to be sent to central database
     */
    public Cursor getStepBatch(String batchLimit){
        SQLiteDatabase db = this.getReadableDatabase();
        // Columns to select
        String[] columns = {StepTimerContract.StepEntry._ID
            ,StepTimerContract.StepEntry.COLUMN_NAME_START_TIME
            ,StepTimerContract.StepEntry.COLUMN_NAME_END_TIME
            ,StepTimerContract.StepEntry.COLUMN_NAME_LATITUDE
            ,StepTimerContract.StepEntry.COLUMN_NAME_LONGITUDE
            ,StepTimerContract.StepEntry.COLUMN_NAME_STEP_NAME};
        // Query for records with status of Ready
        String selection = StepTimerContract.StepEntry.COLUMN_NAME_STATUS + " = ?";
        String[] selectionArgs = {"Ready"};
        // Query for up to 1000 StepTimer records with status of ready
        Cursor stepBatch = db.query(StepTimerContract.StepEntry.TABLE_NAME, columns, selection, selectionArgs, null, null, null, batchLimit);
        return stepBatch;
    }

    /**
     * Used by MainActivity to display all values in the database
     * @param Query
     * @return
     */
    public ArrayList<Cursor> getData(String Query){
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[] { "mesage" };
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2= new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);


        try{
            String maxQuery = Query ;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);


            //add value to cursor2
            Cursor2.addRow(new Object[] { "Success" });

            alc.set(1,Cursor2);
            if (null != c && c.getCount() > 0) {


                alc.set(0,c);
                c.moveToFirst();

                return alc ;
            }
            return alc;
        } catch(SQLException sqlEx){
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+sqlEx.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        } catch(Exception ex){

            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+ex.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        }


    }


}