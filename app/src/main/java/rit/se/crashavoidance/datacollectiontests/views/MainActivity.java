package rit.se.crashavoidance.datacollectiontests.views;



import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Spinner;

import org.jdeferred.DoneCallback;
import org.jdeferred.Promise;

import rit.se.crashavoidance.datacollectiontests.R;
import rit.se.crashavoidance.datacollectiontests.dataTests.DataTest;
import rit.se.crashavoidance.datacollectiontests.dataTests.TestBuilder;


public class MainActivity extends AppCompatActivity {

    NumberPicker numberPicker;
    Spinner testPicker;
    Button runButton;
    TestBuilder testBuilder = new TestBuilder();

    private DataTest persist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize ActionBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);

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


    public void executeTest(final int numRuns){
        if (numRuns == 0) {
            Log.i("Tester", "All tests completed");
            return;
        }

        int testNum = testPicker.getSelectedItemPosition() + 1;
        persist = testBuilder.buildTest(testNum, this);
        Promise promise = persist.getDeferredObject().promise();
        promise.done(new DoneCallback() {
            @Override
            public void onDone(Object result) {
                Log.i("Tester", "Single test completed");
                executeTest(numRuns - 1);
            }
        });
        Log.i("Tester", "Running a test");
        persist.run();
    }
}
