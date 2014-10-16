package com.kuisma.kari.smartdolly;

/**
 * Created by kari on 5.10.2014.
 */
public class Protocol {
    class TargetObject {
        public final static char Dolly  = 'D';
        public final static char Motor  = 'M';
        public final static char Camera = 'C';
    }

    class Command {
        public final static char GET            = 'G';
        public final static char SET            = 'S';
        public final static char GET_RESPONSE   = 'H';
        public final static char SET_RESPONSE   = 'T';
    }

    class Property {
        class Dolly {
            public final static char STATE              = 'A';
            public final static char PING               = 'B';
            public final static char INTERVAL           = 'C';
            public final static char TOTAL_TIME         = 'D';
            public final static char ELAPSED_TIME       = 'E';
            public final static char TOTAL_SHOTS        = 'F';
            public final static char SHOTS_TAKEN        = 'G';
            public final static char MAX_DISTANCE       = 'H';
            public final static char TOTAL_DISTANCE     = 'I';
            public final static char CURRENT_POSITION   = 'J';
            public final static char MOTION_PER_CYCLE   = 'K';
            public final static char LIMIT_1            = 'L';
            public final static char LIMIT_2            = 'M';
            public final static char MEASURING          = 'N';
            public final static char HOMING             = 'O';
            public final static char BAT_VOLTAGE        = 'P';
        }
        class Motor {
            public final static char STEPS_PER_UNIT     = 'A';
            public final static char SPEED              = 'B';
            public final static char RAMP               = 'C';
            public final static char POST_TIME          = 'D';
            public final static char DIRECTION          = 'E';
        }
        class Camera {
            public final static char EXPOSURE_TIME      = 'A';
            public final static char POST_EXPOSURE_TIME = 'B';
            public final static char FOCUS_TIME         = 'C';
            public final static char FOCAL_LENGTH       = 'D';
            public final static char EXPOSURE_MODE      = 'E';
        }
    }
    public final static int SYNC_BYTE = 'X';
    public final static String VALUE_SEPARATOR = ":";

    public static char targetObject(String msg) {
        return msg.charAt(0);
    }

    public static boolean isSyncByte(int b) {
        return (b == SYNC_BYTE);
    }

    public static char property(String msg)
    {
        return msg.charAt(1);
    }

    public static char command(String msg)
    {
        return msg.charAt(2);
    }

    public static String dataStr(String msg)
    {
        String[] msgArray = msg.split(VALUE_SEPARATOR);
        if(msgArray.length > 1)
        {
            return msgArray[1];
        }
        return "";
    }

    public static int data(String msg)
    {
        String[] msgArray = msg.split(VALUE_SEPARATOR);
        int v = 0;
        if(msgArray.length > 1)
        {
            v = Integer.parseInt(msgArray[1]);
        }
        return v;
    }
}
