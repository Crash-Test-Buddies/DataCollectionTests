package rit.se.crashavoidance.datacollectiontests.dataTests;

import android.content.Context;

import org.jdeferred.impl.DeferredObject;

import java.util.Random;

import rit.se.crashavoidance.datacollectiontests.service.DBParcelable;
import rit.se.crashavoidance.datacollectiontests.service.IntentBridge;

/**
 * Created by Dan on 6/28/2016.
 */
public class DummyTest1 implements DataTest {

    private Context context;
    private DeferredObject deferredObject;

    public DummyTest1(Context context){
        this.deferredObject = new DeferredObject();
        this.context = context;
    };

    public void run(){
        Random rand = new Random();
        Long one = rand.nextLong();
        Long two = rand.nextLong();
        DBParcelable parcelable;
        if (one > two){
            parcelable = new DBParcelable("DummyStep", two, one);
        } else {
            parcelable = new DBParcelable("DummyStep", one, two);
        }

        IntentBridge bridge = new IntentBridge(context, parcelable);
        bridge.broadcast();
        System.out.println("Dummy 1 ran.");
    }

    @Override
    public DeferredObject getDeferredObject() {
        return deferredObject;
    }

}
