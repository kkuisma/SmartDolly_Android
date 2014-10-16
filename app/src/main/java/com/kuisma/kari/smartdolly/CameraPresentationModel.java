package com.kuisma.kari.smartdolly;

import android.util.Log;

import org.robobinding.annotation.PresentationModel;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

/**
 * Created by kari on 3.10.2014.
 */
@PresentationModel
public class CameraPresentationModel extends BasePresentationModel {
    private static final String TAG = "CameraPresentationModel";

    private String exposureTime;
    public String getExposureTime() { return exposureTime; }
    public void setExposureTime(String exposureTime) {
        this.exposureTime = exposureTime;
        setPropertyRemote(Protocol.Property.Camera.EXPOSURE_TIME, this.exposureTime);
    }

    private String postExposureTime;
    public String getPostExposureTime() { return postExposureTime; }
    public void setPostExposureTime(String postExposureTime) {
        this.postExposureTime = postExposureTime;
        setPropertyRemote(Protocol.Property.Camera.POST_EXPOSURE_TIME, this.postExposureTime);
    }

    private String focusTime;
    public String getFocusTime() { return focusTime; }
    public void setFocusTime(String focusTime) {
        this.focusTime = focusTime;
        setPropertyRemote(Protocol.Property.Camera.FOCUS_TIME, this.focusTime);
    }

    private String focalLength;
    public String getFocalLength() { return focalLength; }
    public void setFocalLength(String focalLength) {
        this.focalLength = focalLength;
        setPropertyRemote(Protocol.Property.Camera.FOCAL_LENGTH, this.focalLength);
    }

    private boolean exposureMode;
    public boolean getExposureMode() { return exposureMode; }
    public void setExposureMode(boolean exposureMode) { this.exposureMode = exposureMode; }

    CameraPresentationModel(MainDomain mainDomain, CommunicationDomain comms)
    {
        super(mainDomain, comms);
    }

    @Override
    public char objectId() { return Protocol.TargetObject.Camera; }

    public void getInitValues()
    {
        getPropertyRemote(Protocol.Property.Camera.EXPOSURE_TIME);
        getPropertyRemote(Protocol.Property.Camera.POST_EXPOSURE_TIME);
        getPropertyRemote(Protocol.Property.Camera.FOCUS_TIME);
        getPropertyRemote(Protocol.Property.Camera.FOCAL_LENGTH);
        getPropertyRemote(Protocol.Property.Camera.EXPOSURE_MODE);
    }

    public void handleMessage(String msg)
    {
        String data = Protocol.dataStr(msg);
        char property = Protocol.property(msg);
        char command = Protocol.command(msg);
        Log.i(TAG, "CMD = " + command + ", PROPERTY = " + property + " and DATA = " + data);
        if(command == Protocol.Command.GET_RESPONSE || command == Protocol.Command.SET || command == Protocol.Command.GET) {
            newValueFromDevice = true;
            switch (property) {
                case Protocol.Property.Camera.EXPOSURE_TIME:
                    setExposureTime(data);
                    break;
                case Protocol.Property.Camera.POST_EXPOSURE_TIME:
                    setPostExposureTime(data);
                    break;
                case Protocol.Property.Camera.FOCUS_TIME:
                    setFocusTime(data);
                    break;
                case Protocol.Property.Camera.FOCAL_LENGTH:
                    setFocalLength(data);
                    break;
                case Protocol.Property.Camera.EXPOSURE_MODE:
                    setExposureMode(!data.equals("0"));
                    break;
            }
            newValueFromDevice = false;
        }
    }

}
