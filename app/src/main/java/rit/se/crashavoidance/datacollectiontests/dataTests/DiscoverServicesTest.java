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

import org.jdeferred.Deferred;
import org.jdeferred.impl.DeferredObject;

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

    WifiDirectHandler wifiDirectHandler;
    Context context;
    final String stepName = "DiscoverService";
    boolean serviceBound = false;
    private DeferredObject deferredObject;
    private DBParcelable.Builder discover;

    public DiscoverServicesTest(Context context){
        this.context = context;
        this.deferredObject = new DeferredObject();
        this.discover = new DBParcelable.Builder(stepName);
        Log.i("Tester", "Instantiating");
    }

    @Override
    public void run(WifiDirectHandler wifiDirectHandler) {
        discover.start();
        wifiDirectHandler.continuouslyDiscoverServices();
        Log.i("Tester", " Device info: " + wifiDirectHandler.getThisDeviceInfo());
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
        if(action.equals(WifiDirectHandler.Action.DNS_SD_SERVICE_AVAILABLE)) {
            discover.end();
            wifiDirectHandler.stopServiceDiscovery();
            Log.i("Tester", "Service discovered");
            DBParcelable parcelable = discover.build();
            Intent i = new Intent();
            i.setComponent(new ComponentName("rit.se.crashavoidance.datacollector", "rit.se.crashavoidance.datacollector.DBHandlerService"));
            i.putExtra("record", parcelable);
            ComponentName c = context.startService(i);
            Log.i("Tester", parcelable.toString());
            deferredObject.resolve("Completed Test");
        }
    }
}
