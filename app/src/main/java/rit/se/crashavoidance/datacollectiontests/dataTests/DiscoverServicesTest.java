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

import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.rit.se.wifibuddy.WifiDirectHandler;
import rit.se.crashavoidance.datacollectiontests.service.DBParcelable;

/**
 * Created by Dan on 6/28/2016.
 */
public class DiscoverServicesTest implements DataTest {
    private ServiceConnection wifiConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            WifiDirectHandler.WifiTesterBinder binder = (WifiDirectHandler.WifiTesterBinder) service;
            wifiDirectHandler = binder.getService();
            serviceBound = true;
            // Test calls must go in here
            startTime = new Date().getTime();
            wifiDirectHandler.continuouslyDiscoverServices();
            Log.i("Tester", " Device info: " + wifiDirectHandler.getThisDeviceInfo());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i("Tester", "Disconnected??? whyyyyy");
            serviceBound = false;
        }
    };

    WifiDirectHandler wifiDirectHandler;
    BroadcastReceiver receiver;
    Context context;
    final String stepName = "DiscoverService";
    long startTime;
    long endTime;

    boolean serviceBound = false;

    public DiscoverServicesTest(Context context){
        this.context = context;
        Log.i("Tester", "Instantiating");
    }

    public void run() {
        Log.i("Tester", "starting the test");
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiDirectHandler.Action.DNS_SD_SERVICE_AVAILABLE);
        receiver = new BroadcastReceiver() {
            // We only need to listen for one intent
            @Override
            public void onReceive(Context context, Intent intent) {
                // We only need to listen for one intent
                endTime = new Date().getTime();
                wifiDirectHandler.stopDiscoveringServices();
                Log.i("Tester", "Service discovered");
                DBParcelable parcelable = new DBParcelable(stepName, startTime, endTime);
                Intent i = new Intent();
                i.setComponent(new ComponentName("rit.se.crashavoidance.datacollector", "rit.se.crashavoidance.datacollector.DBHandlerService"));
                i.putExtra("record", parcelable);
                ComponentName c = context.startService(i);
                Log.i("Tester", parcelable.toString());
                if(serviceBound) {
                try {
                    context.unbindService(wifiConnection);
                }catch (Exception e){
                    //TODO: cause of this exception?
                    Log.e("TAG", "Caught exception", e);
                }
                }
                LocalBroadcastManager.getInstance(context).unregisterReceiver(this);
            }
        };
        LocalBroadcastManager.getInstance(context).registerReceiver(receiver, filter);
        /* This binds to the service, which calls continuouslyDiscoverServices
           It needs to be done this way because the call is asynchronous so the logic needs
           to be in the callback */
        Log.i("Tester", "Binding to the service");
        context.bindService(new Intent(context, WifiDirectHandler.class), wifiConnection, Context.BIND_AUTO_CREATE);
    }
}
