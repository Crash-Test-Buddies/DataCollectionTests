package rit.se.crashavoidance.datacollectiontests.dataTests;

/**
 * Created by Dan on 6/29/2016.
 */
public class TestBuilder {

    public TestBuilder(){}

    public DataTest buildTest(int testID){
        DataTest theTest = null;

        switch (testID){
            case 1: theTest = new DiscoverServicesTest();
                    break;
            case 2: theTest = new DummyTest1();
                    break;
        }

        return theTest;
    };

}



