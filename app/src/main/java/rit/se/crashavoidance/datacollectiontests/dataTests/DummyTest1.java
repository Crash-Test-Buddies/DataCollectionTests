package rit.se.crashavoidance.datacollectiontests.dataTests;

import org.jdeferred.impl.DeferredObject;

/**
 * Created by Dan on 6/28/2016.
 */
public class DummyTest1 implements DataTest {

    public void run(){
        System.out.println("Dummy 1 ran.");
    }

    @Override
    public DeferredObject getDeferredObject() {
        return null;
    }

}
