package rit.se.crashavoidance.datacollectiontests.dataTests;

import org.jdeferred.impl.DeferredObject;

import rit.se.crashavoidance.datacollectiontests.persistence.WifiDirectDBHelper;
import rit.se.crashavoidance.datacollectiontests.service.DBParcelable;

/**
 * Created by Dan on 6/28/2016.
 */
public class DummyTest1 implements DataTest {
    WifiDirectDBHelper helper;
    private DeferredObject deferredObject = new DeferredObject();
    public DummyTest1(WifiDirectDBHelper helper){
        this.helper = helper;
    }

    public void run(){
        System.out.println("Dummy 1 ran.");
        DBParcelable.Builder builder = new DBParcelable.Builder("dummy1");
        builder.start();

        builder.end();
        DBParcelable parcelable = builder.build();
        helper.insertStepTimerRecord(parcelable);
        deferredObject.resolve("Completed Test");
    }

    @Override
    public DeferredObject getDeferredObject() {
        return deferredObject;
    }

}
