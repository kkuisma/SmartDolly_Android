package com.kuisma.kari.smartdolly;

/**
 * Created by kari on 5.10.2014.
 */
public interface CommunicationDomain {
    void sendPing();
    void sendCommand(String msg);

}
