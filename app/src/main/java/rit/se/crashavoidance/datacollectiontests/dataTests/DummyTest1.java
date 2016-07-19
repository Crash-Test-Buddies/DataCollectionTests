package rit.se.crashavoidance.datacollectiontests.dataTests;

import android.content.Intent;

import org.jdeferred.impl.DeferredObject;

import edu.rit.se.wifibuddy.WifiDirectHandler;

/**
 * Created by Dan on 6/28/2016.
 */
public class DummyTest1 implements DataTest {

    public void run(WifiDirectHandler wifiDirectHandler){
        System.out.println("Dummy 1 ran.");
    }

    @Override
    public DeferredObject getDeferredObject() {
        return null;
    }

    @Override
    public void cleanUp() {

    }

    @Override
    public void handleIntent(Intent intent) {

    }

}
