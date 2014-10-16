package com.kuisma.kari.smartdolly;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.os.Handler;
import android.util.Log;

/**
 * Created by kari on 3.10.2014.
 */
public class ConnectionManager extends Thread implements CommunicationDomain {
    private final UUID DOLLY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String TAG = "ConnectionManager";

    private BluetoothSocket mSocket;
    private BluetoothDevice mDevice;
    private InputStream mInStream;
    private OutputStream mOutStream;
    private final Handler mHandler;
    private String data;

    public ConnectionManager(BluetoothDevice device, Handler handler)
    {
        mDevice = device;
        mHandler = handler;
        BluetoothSocket tmpSocket = null;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        try {
            tmpSocket = mDevice.createRfcommSocketToServiceRecord(DOLLY_UUID);
        } catch (IOException e) {}
        mSocket = tmpSocket;

        try {
            tmpIn = mSocket.getInputStream();
            tmpOut = mSocket.getOutputStream();
        } catch (IOException e) {}
        mInStream = tmpIn;
        mOutStream = tmpOut;
    }

    public void run()
    {
        try {
            Log.i(TAG, "connect socket...");
            mSocket.connect();
        } catch (IOException connectException) {
            Log.i(TAG, "socket connect FAILED! Reason:" + connectException.getMessage());
            try {
                mSocket.close();
            } catch (IOException closeException) { }
            return;
        }

        byte[] buffer = new byte[1024];
        int bytes;

        while (true)
        {
            try {
                if(syncByteFound())
                {
                    int msgLength = readMessageLength();
                    if(msgLength > 0 && msgLength < 256)
                    {
                        bytes = 0;
                        while(msgLength-- > 0)
                        {
                            buffer[bytes++] = (byte)mInStream.read();
                        }
                        data = new String(buffer, 0, bytes);
                        mHandler.obtainMessage(MainActivity.BT_MESSAGE_RECEIVED, bytes, -1, data).sendToTarget();
                    }
                }
            } catch (IOException e) {
                break;
            }
        }
    }

    private boolean syncByteFound()
    {
        try {
            return Protocol.isSyncByte(mInStream.read());
        } catch(IOException e) {
            return false;
        }
    }

    private int readMessageLength()
    {
        try {
            return mInStream.read();
        } catch (IOException e) {
            return 0;
        }
    }

    public void cancel()
    {
        try {
            mSocket.close();
        } catch (IOException e) { }
    }

    public void sendCommand(String msg) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(Protocol.SYNC_BYTE);
            outputStream.write(msg.length());
            outputStream.write(msg.getBytes());
            mOutStream.write(outputStream.toByteArray());
        } catch (IOException e) { }
    }

    public void sendPing() {
        String pingMsg = new String();
        pingMsg += Protocol.TargetObject.Dolly;
        pingMsg += Protocol.Property.Dolly.PING;
        pingMsg += Protocol.Command.GET_RESPONSE;
        pingMsg += Protocol.VALUE_SEPARATOR;
        sendCommand(pingMsg);
    }
}
