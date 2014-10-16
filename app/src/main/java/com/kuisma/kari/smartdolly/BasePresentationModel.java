package com.kuisma.kari.smartdolly;

import android.util.Log;

/**
 * Created by kari on 5.10.2014.
 */
public class BasePresentationModel {
    private static final String TAG = "BasePresentationModel";

    protected CommunicationDomain mComms;
    protected MainDomain mMain;
    protected boolean newValueFromDevice;

    BasePresentationModel(MainDomain mainDomain, CommunicationDomain comms)
    {
        mComms = comms;
        mMain = mainDomain;
        newValueFromDevice = true;
    }

    protected char objectId() { return '.'; }

    public void setPropertyRemote(char property, String val)
    {
        if(newValueFromDevice) // Property value was received from the device (either as a GET response or SET), don't send it back!
        {
            return;
        }
        else // Property value was set on the phone UI, send it to device!
        {
            String setMsg = new String();
            setMsg += objectId();
            setMsg += property;
            setMsg += Protocol.Command.SET;
            setMsg += Protocol.VALUE_SEPARATOR;
            setMsg += val;
            mComms.sendCommand(setMsg);
        }
    }

    public void getPropertyRemote(char property) {
        String getRequest = new String();
        getRequest += objectId();
        getRequest += property;
        getRequest += Protocol.Command.GET;
        getRequest += Protocol.VALUE_SEPARATOR;
        Log.i(TAG, "Object " + objectId() + " is making a GET REQUEST for property: " + property);
        mComms.sendCommand(getRequest);
    }
}
