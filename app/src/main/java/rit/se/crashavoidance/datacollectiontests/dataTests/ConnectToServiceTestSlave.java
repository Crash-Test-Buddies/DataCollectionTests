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

    private Context context;
    private WifiDirectHandler wifiDirectHandler;
    private DeferredObject deferredObject;

    public ConnectToServiceTestSlave(Context context) {
        this.context = context;
        this.deferredObject = new DeferredObject();
    }

    @Override
    public void run() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiDirectHandler.Action.COMMUNICATION_DISCONNECTED);
        filter.addAction(WifiDirectHandler.Action.SERVICE_CONNECTED);
        LocalBroadcastManager.getInstance(context).registerReceiver(receiver, filter);

        context.bindService(new Intent(context, WifiDirectHandler.class), wifiConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public DeferredObject getDeferredObject() {
        return deferredObject;
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(WifiDirectHandler.COMMUNICATION_DISCONNECTED)) {
                wifiDirectHandler.addLocalService("Wi-Fi Buddy", new HashMap<String, String>());
                Log.i("Tester", "Added p2p service");
            }
        }
    };

    private ServiceConnection wifiConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            WifiDirectHandler.WifiTesterBinder binder = (WifiDirectHandler.WifiTesterBinder) service;
            wifiDirectHandler = binder.getService();
            Log.i("Tester", "service bound");
            // Test calls must go below here or in the broadcast receiver
            wifiDirectHandler.addLocalService("Wi-Fi Buddy", new HashMap<String, String>());
            Log.i("Tester", "Added p2p service");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i("Tester", "disconnected from WiFi-Buddy");
        }
    };
}
