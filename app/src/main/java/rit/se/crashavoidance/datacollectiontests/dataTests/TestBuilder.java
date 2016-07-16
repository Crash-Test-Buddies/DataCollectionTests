package rit.se.crashavoidance.datacollectiontests.dataTests;

import android.content.Context;

import rit.se.crashavoidance.datacollectiontests.persistence.WifiDirectDBHelper;

/**
 * Created by Dan on 6/29/2016.
 */
public class TestBuilder {

    public TestBuilder(){}
    private Context context;
    WifiDirectDBHelper helper;
    public DataTest buildTest(int testID, Context context){
        this.context = context;
        helper = new WifiDirectDBHelper(context);
        DataTest theTest = null;

        switch (testID){
            case 1: theTest = new DiscoverServicesTest(context);
                    break;
            case 2: theTest = new DummyTest1(helper);
                    break;
            case 3: theTest = new ConnectToServiceTest(context);
                    break;
            case 4: theTest = new ConnectToServiceTestSlave(context);
                    break;
        }

        return theTest;
    };

}



