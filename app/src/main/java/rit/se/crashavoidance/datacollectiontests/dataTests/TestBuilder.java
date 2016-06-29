package rit.se.crashavoidance.datacollectiontests.dataTests;

import android.content.Context;

/**
 * Created by Dan on 6/29/2016.
 */
public class TestBuilder {

    public TestBuilder(){}
    private Context context;
    public DataTest buildTest(int testID, Context context){
        this.context = context;
        DataTest theTest = null;

        switch (testID){
            case 1: theTest = new DiscoverServicesTest(context);
                    break;
            case 2: theTest = new DummyTest1();
                    break;
        }

        return theTest;
    };

}



