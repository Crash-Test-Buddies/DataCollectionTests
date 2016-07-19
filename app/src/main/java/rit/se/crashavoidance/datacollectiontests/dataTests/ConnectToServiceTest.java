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

    public static final String SERVICE_NAME = "WifiBuddyTestService";

    private final static String stepName = "ConnectToService";

    private WifiDirectHandler wifiDirectHandler;
    private Context context;
    private DeferredObject deferredObject;
    private DBParcelable.Builder discover;
    private DBParcelable.Builder connect;
    private DBParcelable.Builder disconnect;

    public ConnectToServiceTest(Context context) {
        this.context = context;
        deferredObject = new DeferredObject();
        discover = new DBParcelable.Builder("DiscoverService");
        connect = new DBParcelable.Builder("ConnectToService");
        disconnect = new DBParcelable.Builder("DisconnectFromService");
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
            DnsSdService service = wifiDirectHandler.getDnsSdServiceMap().get(intent.getStringExtra(WifiDirectHandler.SERVICE_MAP_KEY));
            if(SERVICE_NAME.equals(service.getInstanceName())) {
                discover.end();
                wifiDirectHandler.stopServiceDiscovery();
                Log.i("Tester", "Service discovered");
                DBParcelable parcelable = discover.build();
                Intent i = new Intent();
                i.setComponent(new ComponentName("rit.se.crashavoidance.datacollector", "rit.se.crashavoidance.datacollector.DBHandlerService"));
                i.putExtra("record", parcelable);
                ComponentName c = context.startService(i);
                Log.i("Tester", parcelable.toString());

                connect.start();
                Log.i("Tester", "Initiating connection to service");
                wifiDirectHandler.initiateConnectToService(service);
            }
        } if (action.equals(WifiDirectHandler.Action.SERVICE_CONNECTED)) {
            connect.end();
            DBParcelable parcelable = connect.build();
            Intent i = new Intent();
            i.setComponent(new ComponentName("rit.se.crashavoidance.datacollector", "rit.se.crashavoidance.datacollector.DBHandlerService"));
            i.putExtra("record", parcelable);
            ComponentName c = context.startService(i);
            Log.i("Tester", parcelable.toString());
            disconnect.start();
            wifiDirectHandler.removeGroup();
            deferredObject.resolve("Completed Test");
        } if (action.equals(WifiDirectHandler.Action.COMMUNICATION_DISCONNECTED)) {
            disconnect.end();
            DBParcelable parcelable = disconnect.build();
            Intent i = new Intent();
            i.setComponent(new ComponentName("rit.se.crashavoidance.datacollector", "rit.se.crashavoidance.datacollector.DBHandlerService"));
            i.putExtra("record", parcelable);
            ComponentName c = context.startService(i);
            Log.i("Tester", parcelable.toString());
            deferredObject.resolve("Completed Test");
        }
    }
}
