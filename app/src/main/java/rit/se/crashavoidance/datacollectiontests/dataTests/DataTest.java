package rit.se.crashavoidance.datacollectiontests.dataTests;

import org.jdeferred.impl.DeferredObject;

/**
 * Created by Dan on 6/28/2016.
 */
public interface DataTest {

    public void run();

    public DeferredObject getDeferredObject();

}
