package rit.se.crashavoidance.datacollectiontests.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.List;

/**
 * Created by Dan on 7/12/2016.
 */
public class IntentBridge {

    DBParcelable toSend;
    Context context;

    public IntentBridge(Context mainAct, DBParcelable toSend){
        this.toSend = toSend;
        this.context = mainAct;
    }

    public void broadcast(){
        startBackgroundService();
        Intent i = new Intent();
        i.setAction(Intent.ACTION_SEND);
        i.putExtra("record", toSend);
        i.setType("wifiParcel");
        context.sendBroadcast(i);
    }


    public void startBackgroundService() {
        Intent intent = new Intent(context, DBHandlerService.class);
        //Intent exIntent = createExplicitFromImplicitIntent(context, intent);
        context.startService(intent);
    }

    public static Intent createExplicitFromImplicitIntent(Context context, Intent implicitIntent) {
        // Retrieve all services that can match the given intent
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);

        // Make sure only one match was found
        if (resolveInfo == null || resolveInfo.size() != 1) {
            return null;
        }

        // Get component info and create ComponentName
        ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);

        // Create a new intent. Use the old one for extras and such reuse
        Intent explicitIntent = new Intent(implicitIntent);

        // Set the component to be explicit
        explicitIntent.setComponent(component);

        return explicitIntent;
    }

}
