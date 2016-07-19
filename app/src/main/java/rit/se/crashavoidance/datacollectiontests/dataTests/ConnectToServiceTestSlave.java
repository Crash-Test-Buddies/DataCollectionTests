package rit.se.crashavoidance.datacollectiontests.dataTests;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.jdeferred.impl.DeferredObject;

import java.util.HashMap;

import edu.rit.se.wifibuddy.WifiDirectHandler;

import static rit.se.crashavoidance.datacollectiontests.dataTests.ConnectToServiceTest.SERVICE_NAME;

/**
 * Created by Brett on 7/11/2016.
 */
public class ConnectToServiceTestSlave implements DataTest {

    private WifiDirectHandler wifiDirectHandler;
    private DeferredObject deferredObject;

    public ConnectToServiceTestSlave() {
        this.deferredObject = new DeferredObject();
    }

    @Override
    public void run(WifiDirectHandler wifiDirectHandler) {
        this.wifiDirectHandler = wifiDirectHandler;
        wifiDirectHandler.addLocalService("Wi-Fi Buddy", new HashMap<String, String>());
        Log.i("Tester", "Added p2p service");
    }

    @Override
    public DeferredObject getDeferredObject() {
        return deferredObject;
    }

    @Override
    public void cleanUp() {

    }

    @Override
    public void handleIntent(Intent intent) {
        String action = intent.getAction();
        if(action.equals(WifiDirectHandler.COMMUNICATION_DISCONNECTED)) {
            wifiDirectHandler.addLocalService("Wi-Fi Buddy", new HashMap<String, String>());
            Log.i("Tester", "Added p2p service");
        }
    }
}
