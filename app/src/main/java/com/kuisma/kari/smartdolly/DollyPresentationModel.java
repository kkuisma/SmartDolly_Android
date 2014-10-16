package com.kuisma.kari.smartdolly;

import android.util.Log;

import org.robobinding.annotation.PresentationModel;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

/**
 * Created by kari on 2.10.2014.
 */
@PresentationModel
public class DollyPresentationModel extends BasePresentationModel {
    private static final String TAG = "DollyPresentationModel";

    DollyPresentationModel(MainDomain mainDomain, CommunicationDomain comms)
    {
        super(mainDomain, comms);
    }

    private String interval;
    public String getInterval() { return interval; }
    public void setInterval(String interval) {
        this.interval = interval;
        setPropertyRemote(Protocol.Property.Dolly.INTERVAL, this.interval);
    }

    private String totalTime;
    public String getTotalTime() { return totalTime; }
    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
        setPropertyRemote(Protocol.Property.Dolly.TOTAL_TIME, this.interval);
    }

    private String elapsedTime;
    public String getElapsedTime() { return elapsedTime; }
    public void setElapsedTime(String elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    private String totalShots;
    public String getTotalShots() { return totalShots; }
    public void setTotalShots(String totalShots) {
        this.totalShots = totalShots;
        setPropertyRemote(Protocol.Property.Dolly.TOTAL_SHOTS, this.interval);
    }

    private String shotsTaken;
    public String getShotsTaken() { return shotsTaken; }
    public void setShotsTaken(String shotsTaken) {
        this.shotsTaken = shotsTaken;
    }

    private String maxDistance;
    public String getMaxDistance() { return maxDistance; }
    public void setMaxDistance(String maxDistance) {
        this.maxDistance = maxDistance;
        setPropertyRemote(Protocol.Property.Dolly.MAX_DISTANCE, this.interval);
    }

    private String totalDistance;
    public String getTotalDistance() { return totalDistance; }
    public void setTotalDistance(String totalDistance) {
        this.totalDistance = totalDistance;
        setPropertyRemote(Protocol.Property.Dolly.TOTAL_DISTANCE, this.interval);
    }

    private String currentPosition;
    public String getCurrentPosition() { return currentPosition; }
    public void setCurrentPosition(String currentPosition) {
        this.currentPosition = currentPosition;
    }

    private String motionPerCycle;
    public String getMotionPerCycle() { return motionPerCycle; }
    public void setMotionPerCycle(String motionPerCycle) {
        this.motionPerCycle = motionPerCycle;
        setPropertyRemote(Protocol.Property.Dolly.MOTION_PER_CYCLE, this.interval);
    }

    private boolean state;
    public boolean isState() { return state; }
    public void setState(boolean state) { this.state = state; }

    private boolean limit1;
    public boolean getLimit1() { return limit1; }
    public void setLimit1(boolean limit1) { this.limit1 = limit1; }

    private boolean limit2;
    public boolean getLimit2() { return limit2; }
    public void setLimit2(boolean limit2) { this.limit2 = limit2; }

    private boolean measuring;
    public boolean isMeasuring() { return measuring; }

    private boolean homing;
    public boolean isHoming() { return homing; }

    private String batVoltage;
    public String getBatVoltage() { return batVoltage; }
    public void setBatVoltage(String batVoltage) { this.batVoltage = batVoltage; }

    @Override
    public char objectId() { return Protocol.TargetObject.Dolly; }

    public void getInitValues()
    {
        getPropertyRemote(Protocol.Property.Dolly.INTERVAL);
        getPropertyRemote(Protocol.Property.Dolly.TOTAL_TIME);
        getPropertyRemote(Protocol.Property.Dolly.ELAPSED_TIME);
        getPropertyRemote(Protocol.Property.Dolly.TOTAL_SHOTS);
        getPropertyRemote(Protocol.Property.Dolly.SHOTS_TAKEN);
        getPropertyRemote(Protocol.Property.Dolly.MAX_DISTANCE);
        getPropertyRemote(Protocol.Property.Dolly.TOTAL_DISTANCE);
        getPropertyRemote(Protocol.Property.Dolly.CURRENT_POSITION);
        getPropertyRemote(Protocol.Property.Dolly.MOTION_PER_CYCLE);
        getPropertyRemote(Protocol.Property.Dolly.LIMIT_1);
        getPropertyRemote(Protocol.Property.Dolly.LIMIT_2);
        getPropertyRemote(Protocol.Property.Dolly.MEASURING);
        getPropertyRemote(Protocol.Property.Dolly.HOMING);
        getPropertyRemote(Protocol.Property.Dolly.BAT_VOLTAGE);
    }

    public void start()
    {
        state = !state;
        String stateStr = state ? "1" : "0";
        setPropertyRemote(Protocol.Property.Dolly.STATE, stateStr);
    }

    public void goHome()
    {
        homing = !homing;
        String homingStr = homing ? "1" : "0";
        setPropertyRemote(Protocol.Property.Dolly.HOMING, homingStr);
    }

    public void measure()
    {
        measuring = !measuring;
        String measuringStr = measuring ? "1" : "0";
        setPropertyRemote(Protocol.Property.Dolly.MEASURING, measuringStr);
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
                case Protocol.Property.Dolly.STATE:
                    setState(!data.equals("0"));
                    break;
                case Protocol.Property.Dolly.PING:
                    Log.i(TAG, "PING is here!!!");
                    mMain.pingReceived();
                    break;
                case Protocol.Property.Dolly.INTERVAL:
                    setInterval(data);
                    break;
                case Protocol.Property.Dolly.TOTAL_SHOTS:
                    setTotalShots(data);
                    break;
                case Protocol.Property.Dolly.TOTAL_TIME:
                    setTotalTime(data);
                    break;
                case Protocol.Property.Dolly.ELAPSED_TIME:
                    long timeInSeconds = 0;
                    try {
                        timeInSeconds = Integer.parseInt(data);
                    } catch(NumberFormatException nfe) { }
                    String timeStr = new SimpleDateFormat("HH:mm:ss").format(timeInSeconds * 1000L);
                    setElapsedTime(timeStr);
                    break;
                case Protocol.Property.Dolly.MAX_DISTANCE:
                    setMaxDistance(data);
                    break;
                case Protocol.Property.Dolly.TOTAL_DISTANCE:
                    setTotalDistance(data);
                    break;
                case Protocol.Property.Dolly.MOTION_PER_CYCLE:
                    setMotionPerCycle(data);
                    break;
                case Protocol.Property.Dolly.SHOTS_TAKEN:
                    setShotsTaken(data);
                    break;
                case Protocol.Property.Dolly.CURRENT_POSITION:
                    setCurrentPosition(data);
                    break;
                case Protocol.Property.Dolly.BAT_VOLTAGE:
                    int batVoltageInMillivolts = 0;
                    try {
                        batVoltageInMillivolts = Integer.parseInt(data);
                    } catch(NumberFormatException nfe) { }
                    String volts = new DecimalFormat("#0.0V").format(batVoltageInMillivolts/1000.0);
                    setBatVoltage(volts);
                    break;
                case Protocol.Property.Dolly.LIMIT_1:
                    setLimit1(!data.equals("0"));
                    break;
                case Protocol.Property.Dolly.LIMIT_2:
                    setLimit2(!data.equals("0"));
                    break;
                case Protocol.Property.Dolly.MEASURING:
                    //setMeasuring(!data.equals("0"));
                    break;
                case Protocol.Property.Dolly.HOMING:
                    //setHoming(!data.equals("0"));
                    break;
            }
            newValueFromDevice = false;
        }
    }
}
