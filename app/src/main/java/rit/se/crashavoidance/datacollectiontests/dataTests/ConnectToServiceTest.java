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

import java.util.Date;

import edu.rit.se.wifibuddy.DnsSdService;
import edu.rit.se.wifibuddy.WifiDirectHandler;
import rit.se.crashavoidance.datacollectiontests.service.DBParcelable;

/**
 * Created by Brett on 7/5/2016.
 */
public class ConnectToServiceTest implements DataTest {

    private final static String stepName = "ConnectToService";

    private WifiDirectHandler wifiDirectHandler;
    private Context context;
    private DeferredObject deferredObject;
    private DBParcelable.Builder discover;
    private DBParcelable.Builder connect;
    private DBParcelable.Builder disconnect;
//    private long endTime;
//    private long startTime;

    public ConnectToServiceTest(Context context) {
        this.context = context;
        deferredObject = new DeferredObject();
    }

    @Override
    public void run() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiDirectHandler.Action.DNS_SD_SERVICE_AVAILABLE);
        filter.addAction(WifiDirectHandler.Action.SERVICE_CONNECTED);
        LocalBroadcastManager.getInstance(context).registerReceiver(receiver, filter);
        /* This binds to the service, which calls continuouslyDiscoverServices
           It needs to be done this way because the call is asynchronous so the logic needs
           to be in the callback */
        Log.i("Tester", "Binding to the service");
        context.bindService(new Intent(context, WifiDirectHandler.class), wifiConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public DeferredObject getDeferredObject() {
        return deferredObject;
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        // We only need to listen for one intent
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(WifiDirectHandler.Action.DNS_SD_SERVICE_AVAILABLE)) {
                endTime = new Date().getTime();
                wifiDirectHandler.stopDiscoveringServices();
                Log.i("Tester", "Service discovered");
                DBParcelable parcelable = new DBParcelable("DiscoverService", startTime, endTime);
                Intent i = new Intent();
                i.setComponent(new ComponentName("rit.se.crashavoidance.datacollector", "rit.se.crashavoidance.datacollector.DBHandlerService"));
                i.putExtra("record", parcelable);
                ComponentName c = context.startService(i);
                Log.i("Tester", parcelable.toString());

                //TODO: Reusing a variable like this is a bad practice
                startTime = new Date().getTime();
                Log.i("Tester", "Initiating connection to service");
                DnsSdService service = wifiDirectHandler.getDnsSdServiceMap().get(intent.getStringExtra(WifiDirectHandler.SERVICE_MAP_KEY));
                wifiDirectHandler.initiateConnectToService(service);

            } if (action.equals(WifiDirectHandler.Action.SERVICE_CONNECTED)) {
                //TODO: Reusing a variable like this is a bad practice
                endTime = new Date().getTime();
                DBParcelable parcelable = new DBParcelable("ConnectToService", startTime, endTime);
                Intent i = new Intent();
                i.setComponent(new ComponentName("rit.se.crashavoidance.datacollector", "rit.se.crashavoidance.datacollector.DBHandlerService"));
                i.putExtra("record", parcelable);
                ComponentName c = context.startService(i);
                Log.i("Tester", parcelable.toString());
                wifiDirectHandler.remove
                deferredObject.resolve("Completed Test");
            }
//            LocalBroadcastManager.getInstance(context).unregisterReceiver(this);
//            deferredObject.resolve("Completed Test");
        }
    };

    private ServiceConnection wifiConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            WifiDirectHandler.WifiTesterBinder binder = (WifiDirectHandler.WifiTesterBinder) service;
            wifiDirectHandler = binder.getService();
            // Test calls must go below here or in the broadcast receiver
            startTime = new Date().getTime();
            wifiDirectHandler.continuouslyDiscoverServices();
            Log.i("Tester", " Device info: " + wifiDirectHandler.getThisDeviceInfo());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i("Tester", "disconnected from WiFi-Buddy");
        }
    };
}
