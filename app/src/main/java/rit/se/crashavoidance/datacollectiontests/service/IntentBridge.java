package rit.se.crashavoidance.datacollectiontests.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import rit.se.crashavoidance.datacollectiontests.views.MainActivity;

/**
 * Created by Dan on 7/12/2016.
 */
public class IntentBridge {

    DBParcelable toSend;
    Context mainAct;

    public IntentBridge(Context mainAct, DBParcelable toSend){
        this.toSend = toSend;
        this.mainAct = mainAct;
    }

    public void broadcast(){
        Intent i = new Intent("");
        i.putExtra("record", toSend);
        mainAct.sendBroadcast(i);
    }

}
