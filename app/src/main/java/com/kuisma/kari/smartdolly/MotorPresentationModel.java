package com.kuisma.kari.smartdolly;

import android.util.Log;

import org.robobinding.annotation.PresentationModel;

/**
 * Created by kari on 3.10.2014.
 */
@PresentationModel
public class MotorPresentationModel extends BasePresentationModel {
    private static final String TAG = "MotorPresentationModel";

    private String stepsPerUnit;
    public String getStepsPerUnit() { return stepsPerUnit; }
    public void setStepsPerUnit(String stepsPerUnit) {
        this.stepsPerUnit = stepsPerUnit;
        setPropertyRemote(Protocol.Property.Motor.STEPS_PER_UNIT, this.stepsPerUnit);
    }

    private String speed;
    public String getSpeed() { return speed; }
    public void setSpeed(String speed) {
        this.speed = speed;
        setPropertyRemote(Protocol.Property.Motor.SPEED, this.speed);
    }

    private String ramp;
    public String getRamp() { return ramp; }
    public void setRamp(String ramp) {
        this.ramp = ramp;
        setPropertyRemote(Protocol.Property.Motor.RAMP, this.ramp);
    }

    private String postTime;
    public String getPostTime() { return postTime; }
    public void setPostTime(String postTime) {
        this.postTime = postTime;
        setPropertyRemote(Protocol.Property.Motor.POST_TIME, this.postTime);
    }

    private boolean direction;
    public boolean getDirection() { return direction; }
    public void setDirection(boolean direction) { this.direction = direction; }

    MotorPresentationModel(MainDomain mainDomain, CommunicationDomain comms)
    {
        super(mainDomain, comms);
    }

    @Override
    public char objectId() { return Protocol.TargetObject.Motor; }

    public void getInitValues()
    {
        getPropertyRemote(Protocol.Property.Motor.STEPS_PER_UNIT);
        getPropertyRemote(Protocol.Property.Motor.SPEED);
        getPropertyRemote(Protocol.Property.Motor.RAMP);
        getPropertyRemote(Protocol.Property.Motor.POST_TIME);
        getPropertyRemote(Protocol.Property.Motor.DIRECTION);
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
                case Protocol.Property.Motor.STEPS_PER_UNIT:
                    setStepsPerUnit(data);
                    break;
                case Protocol.Property.Motor.SPEED:
                    setSpeed(data);
                    break;
                case Protocol.Property.Motor.RAMP:
                    setRamp(data);
                    break;
                case Protocol.Property.Motor.POST_TIME:
                    setPostTime(data);
                    break;
                case Protocol.Property.Motor.DIRECTION:
                    setDirection(!data.equals("0"));
                    break;
            }
            newValueFromDevice = false;
        }
    }
}
