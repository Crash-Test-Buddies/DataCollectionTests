package rit.se.crashavoidance.datacollectiontests.dataTests;

import android.content.Intent;

import org.jdeferred.impl.DeferredObject;

import edu.rit.se.wifibuddy.WifiDirectHandler;

/**
 * Created by Dan on 6/28/2016.
 */
public interface DataTest {

    void run(WifiDirectHandler wifiDirectHandler);

    DeferredObject getDeferredObject();

    void cleanUp();

    void handleIntent(Intent intent);
}
