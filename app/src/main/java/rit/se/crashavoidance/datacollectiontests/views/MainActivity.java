package rit.se.crashavoidance.datacollectiontests.views;



import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Spinner;

import org.jdeferred.DoneCallback;
import org.jdeferred.Promise;

import edu.rit.se.wifibuddy.WifiDirectHandler;
import rit.se.crashavoidance.datacollectiontests.R;
import rit.se.crashavoidance.datacollectiontests.dataTests.DataTest;
import rit.se.crashavoidance.datacollectiontests.dataTests.TestBuilder;


public class MainActivity extends AppCompatActivity implements WifiBuddyAccessor {

    private NumberPicker numberPicker;
    private Spinner testPicker;
    private Button runButton;
    private TestBuilder testBuilder = new TestBuilder();
    private DataTest currentTest;
    private boolean wifiBuddyBound = false;
    private WifiDirectHandler wifiDirectHandler;

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
        runButton.setEnabled(false);
        View.OnClickListener runBtnListener = new View.OnClickListener() {
            public void onClick(View v) {
                executeTest(numberPicker.getValue());

            }
        };
        runButton.setOnClickListener(runBtnListener);

        registerBroadcastReceiver();
        bindService(new Intent(this, WifiDirectHandler.class), wifiConnection, Context.BIND_AUTO_CREATE);
    }


    public void executeTest(final int numRuns) {
        if (numRuns == 0) {
            Log.i("Tester", "All tests completed");
            return;
        }

        int testNum = testPicker.getSelectedItemPosition() + 1;
        currentTest = testBuilder.buildTest(testNum, this);
        Promise promise = currentTest.getDeferredObject().promise();
        promise.done(new DoneCallback() {
            @Override
            public void onDone(Object result) {
                Log.i("Tester", "Single test completed");
                currentTest.cleanUp();
                executeTest(numRuns - 1);
            }
        });
        Log.i("Tester", "Running a test");
        currentTest.run(wifiDirectHandler);
    }

    @Override
    public WifiDirectHandler getWifiDirectHandler() {
        return wifiDirectHandler;
    }

    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiDirectHandler.Action.COMMUNICATION_DISCONNECTED);
        filter.addAction(WifiDirectHandler.Action.SERVICE_CONNECTED);
        filter.addAction(WifiDirectHandler.Action.DNS_SD_SERVICE_AVAILABLE);
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, filter);
    }

    private ServiceConnection wifiConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            WifiDirectHandler.WifiTesterBinder binder = (WifiDirectHandler.WifiTesterBinder) service;
            wifiDirectHandler = binder.getService();
            wifiBuddyBound = true;
            runButton.setEnabled(true);
            Log.i("Tester", "service bound");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i("Tester", "disconnected from WiFi-Buddy");
            wifiBuddyBound = false;
        }
    };

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(currentTest != null) {
                currentTest.handleIntent(intent);
            }
        }
    };
}
