package rit.se.crashavoidance.datacollectiontests.views;



import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.View.OnClickListener;

import edu.rit.se.wifibuddy.WifiDirectHandler;
import rit.se.crashavoidance.datacollectiontests.R;
import rit.se.crashavoidance.datacollectiontests.dataTests.DataTest;
import rit.se.crashavoidance.datacollectiontests.dataTests.DummyTest1;
import rit.se.crashavoidance.datacollectiontests.dataTests.TestBuilder;


public class MainActivity extends AppCompatActivity {

    NumberPicker numberPicker;
    Spinner testPicker;
    Button runButton;
    TestBuilder testBuilder = new TestBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        testPicker = (Spinner) findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.tests_list, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        testPicker.setAdapter(adapter);


        numberPicker = (NumberPicker) findViewById(R.id.numberpicker);
        numberPicker.setMaxValue(100);
        numberPicker.setMinValue(1);
        numberPicker.setWrapSelectorWheel(true);

        runButton = (Button) findViewById(R.id.runbutton);
        View.OnClickListener runBtnListener = new View.OnClickListener() {
            public void onClick(View v) {
                executeTest(numberPicker.getValue());

            }
        };
        runButton.setOnClickListener(runBtnListener);


    }


    public void executeTest(int numRuns){
        int testNum = testPicker.getSelectedItemPosition() + 1;
        DataTest theTest = testBuilder.buildTest(testNum, this);
        for (int i = 0; i < numRuns; i++){
            theTest.run();
        }
    }

}
