package rit.se.crashavoidance.datacollectiontests.service;

import android.app.IntentService;
import android.content.Intent;

import rit.se.crashavoidance.datacollectiontests.persistence.WifiDirectDBHelper;


/**
 * Created by Chris on 6/19/2016.
 * Handles intents sent by other apps to insert records into the
 * database
 */
public class DBHandlerService extends IntentService{

    public static final String PARCELABLE_NAME = "record";
    public static final String STATUS = "status";
    WifiDirectDBHelper dbHelper;

    public DBHandlerService(){
        super("DBHandlerService");
        dbHelper = new WifiDirectDBHelper(this.getApplicationContext());
    }

    /**
     * Handle intents specific to this service
     * @param intent
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        String status = "";
        DBParcelable dbObject = intent.getParcelableExtra(PARCELABLE_NAME);
        dbHelper.insertStepTimerRecord(dbObject);
    }
}
