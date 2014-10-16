package com.kuisma.kari.smartdolly;

import java.util.Locale;
import java.util.Set;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Message;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.util.Log;

public class MainActivity extends Activity implements ActionBar.TabListener, MainDomain {
    private static final String TAG = "MainActivity";

    final int REQUEST_ENABLE_BT = 1;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    private DollyPresentationModel mDollyPresentationModel;
    private MotorPresentationModel mMotorPresentationModel;
    private CameraPresentationModel mCameraPresentationModel;

    public DollyPresentationModel getDollyPresentationModel()
    {
        return mDollyPresentationModel;
    }
    public MotorPresentationModel getMotorPresentationModel()
    {
        return mMotorPresentationModel;
    }
    public CameraPresentationModel getCameraPresentationModel()
    {
        return mCameraPresentationModel;
    }

    private enum DollyState
    {
        NOT_CONNECTED,
        CONNECTED
    };
    DollyState dollyState = DollyState.NOT_CONNECTED;

    BluetoothAdapter mBluetoothAdapter;
    ConnectionManager mConnectionManager;

    private String BT_DEVICE_NAME = "myDolly";
    public static final int BT_MESSAGE_RECEIVED = 1;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // ignore orientation change
        if (newConfig.orientation != Configuration.ORIENTATION_LANDSCAPE) {
            super.onConfigurationChanged(newConfig);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        else
        {
            findPairedDevices();
        }
    }

    public Handler messageHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == BT_MESSAGE_RECEIVED) {
                String len = Integer.toString(msg.arg1);
                String msgLen = Integer.toString(msg.arg2);
                String msgData = (String)msg.obj;
                switch(Protocol.targetObject(msgData))
                {
                    case Protocol.TargetObject.Dolly:
                        getDollyPresentationModel().handleMessage(msgData);
                        break;
                    case Protocol.TargetObject.Motor:
                        getMotorPresentationModel().handleMessage(msgData);
                        break;
                    case Protocol.TargetObject.Camera:
                        getCameraPresentationModel().handleMessage(msgData);
                        break;
                }
            }
        }
    };

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data)
    {
        if(requestCode == REQUEST_ENABLE_BT) {
            findPairedDevices();
        }
        else if (requestCode == RESULT_CANCELED) {
        }
    }

    private void findPairedDevices()
    {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().equals(BT_DEVICE_NAME)) {
                    if(mBluetoothAdapter.isDiscovering()) {
                        mBluetoothAdapter.cancelDiscovery();
                    }
                    Toast.makeText(getApplicationContext(), "Connect to the paired Bluetooth device address: " + device.getName(), Toast.LENGTH_LONG).show();
                    mConnectionManager = new ConnectionManager(device, messageHandler);
                    mConnectionManager.start();
                    createPresentationModels();
                    continue;
                }
            }
        }
    }

    private void createPresentationModels() {
        mDollyPresentationModel = new DollyPresentationModel(this, mConnectionManager);
        mMotorPresentationModel = new MotorPresentationModel(this, mConnectionManager);
        mCameraPresentationModel = new CameraPresentationModel(this, mConnectionManager);
    }

    private int noAckPings = 0;
    private final int PING_TIMER_DELAY = 5000; // ms

    private Handler pingTimerHandler = new Handler();

    private Runnable pingTimerTick = new Runnable() {
        @Override
        public void run() {
            noAckPings++;
            if(noAckPings > 3)
            {
                Log.i(TAG, "BT connection LOST!!");
                dollyState = DollyState.NOT_CONNECTED;
                // stop ping timer
                pingTimerHandler.removeCallbacks(pingTimerTick);

                // start connecting... progress ring
            }
            else
            {
                pingTimerHandler.postDelayed(this, PING_TIMER_DELAY);
            }
        }
    };

    public void pingReceived()
    {
        if(dollyState == DollyState.NOT_CONNECTED)
        {
            Log.i(TAG, "BT connection ESTABLISHED!! Let's get init values...");
            dollyState = DollyState.CONNECTED;
            mConnectionManager.sendPing();
            getDollyPresentationModel().getInitValues();
            getCameraPresentationModel().getInitValues();
            getMotorPresentationModel().getInitValues();
            // stop connecting... progress ring

            // start ping timer
            pingTimerHandler.postDelayed(pingTimerTick, PING_TIMER_DELAY);
        }
        else
        {
            noAckPings = 0;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position)
            {
                case 0:
                    return StatusFragment.newInstance();
                case 1:
                    return DollyFragment.newInstance();
                case 2:
                    return MotorFragment.newInstance();
                case 3:
                    return CameraFragment.newInstance();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_status).toUpperCase(l);
                case 1:
                    return getString(R.string.title_dolly).toUpperCase(l);
                case 2:
                    return getString(R.string.title_motor).toUpperCase(l);
                case 3:
                    return getString(R.string.title_camera).toUpperCase(l);
            }
            return null;
        }
    }
}
