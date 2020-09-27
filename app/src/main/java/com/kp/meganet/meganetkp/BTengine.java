package com.kp.meganet.meganetkp;

import android.app.Activity;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by alex on 11/22/2015.
 */
public abstract class BTengine {

    public enum btMode {
        ON, OFF
    }

    protected BluetoothAdapter _btAdapter;
    protected String				_connectedAddress;
    protected volatile boolean		_stopWork;
    private int						_readBufferPosition;
    private InputStream             _mmInputStream;
    private OutputStream            _mmOutStream;
    private Thread					_workerThread;
    private BluetoothDevice         _device;
    private BluetoothSocket         _socket;
    protected volatile boolean		_startCollect;
    public volatile long			_startCollectDateTag;
    protected volatile int			_timeoutMilliSec;
    protected volatile boolean      _isInfinitReceive;
    protected Set<BluetoothDevice> _pairedDevices;
    protected byte 					_packetStart; //This is the ASCII code for a start collect data
    protected boolean               _rawReceive;
    protected boolean               _errorNotify;

    BTengine(BluetoothAdapter btAdapter_prm) {
        _btAdapter = btAdapter_prm;
        _socket = null;
        _device = null;
        _timeoutMilliSec = 100000;
        _isInfinitReceive = false;
        _errorNotify = false;
    }

    public btMode GetStatus() {
        if (_btAdapter.isEnabled())	{
            return btMode.ON;
        }
        else {
            return btMode.OFF;
        }
    }
    public boolean On()	{
        try	{
            if (!_btAdapter.isEnabled()) {
                _btAdapter.enable();
            }
        }
        catch(Exception e) {
        }

        return true;

    }

    public boolean Off() {
        if (_btAdapter.isEnabled()) {
            _btAdapter.disable();
        }
        return true;
    }

    public Map<String, String> GetDeviceList() {
        if (_btAdapter.isDiscovering()) {
            _btAdapter.cancelDiscovery();
        }
        else {
            _btAdapter.startDiscovery();

        }
        _pairedDevices = _btAdapter.getBondedDevices();

        Map<String, String>devMap = new HashMap<String, String>();
        // put it's one to the adapter
        for(BluetoothDevice device : _pairedDevices)
            devMap.put(device.getName(), device.getAddress());

        return devMap;
    }

    public boolean ConnectTo(String pairName_prm) {
        boolean connectStat = true;
        _connectedAddress = pairName_prm;
        try {
            _device = _btAdapter.getRemoteDevice(_connectedAddress);
            // BluetoothSocket socket
            // =device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"));

            Method m = null;
            try {
                m = _device.getClass().getMethod("createRfcommSocket",
                        new Class[] { int.class });
            }
            catch (Exception e) {
                e.printStackTrace();
                connectStat = false;
            }
            try	{
                _socket = (BluetoothSocket) m.invoke(_device, 1);
            }
            catch (Exception e) {
                e.printStackTrace();
                connectStat = false;
            }
            _mmOutStream = _socket.getOutputStream();
            if(!_socket.isConnected()) {
                _btAdapter.cancelDiscovery();
                _socket.connect();
                _mmInputStream = _socket.getInputStream();
                beginListenForData();

            }
            _startCollect = false;
            _errorNotify = false;
        }
        catch (Exception e) {
            Log.d("TAG", "Exception during write", e);
            connectStat = false;
        }

        return connectStat;
    }

    protected void sendDataToPairedDevice(byte[] dataArr_prm) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            _mmOutStream.write(dataArr_prm);
        }
        catch (Exception e) {
            Log.d("TAG", "Exception during write", e);
        }
    }

    protected void beginListenForData()	{
        final Handler handler = new Handler();
        _packetStart = 2; //This is the ASCII code for a start collect data

        _stopWork = false; // flag for stop work
        _readBufferPosition = 0; // index for the receive msg that we save in read buffer

        _workerThread = new Thread(new Runnable() {
            public void run() {
                byte[] readBuffer = new byte[32768];
                int packetLenght = 0; // legth of a msg
                int receivedDataLenght = 0; // legth of all of the messages that received
                boolean startFound = false;
                while(!Thread.currentThread().isInterrupted() && !_stopWork) {
                    try {
                        if(!_socket.isConnected()) // if the BT not connected
                        {
                            if(!_errorNotify)
                            {
                                LostConnection("Lost");
                                _errorNotify = true;
                            }
                        }
                        if (MeganetInstances.getInstance().GetMeganetEngine().getCurrentCommand()== MeganetEngine.commandType.GET_LOG) {
                            MeganetInstances.getInstance().GetMeganetEngine().increase_timerCount();
                            try {
                                Thread.sleep(9000);
                                _startCollectDateTag = System.currentTimeMillis();

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        int bytesAvailable = _mmInputStream.available(); // Number of bytes in the input stream
                        if(_startCollect && (System.currentTimeMillis() - _startCollectDateTag > _timeoutMilliSec && !_isInfinitReceive)) { //Check if not over time receive
                            ReceiveTimeout();
                            _startCollect = false;
                            packetLenght = 0;
                        }

                        if( MeganetInstances.getInstance().GetMeganetEngine().get_timerCount() >= 5 && bytesAvailable == 0 &&
                                (MeganetInstances.getInstance().GetMeganetEngine().getCurrentCommand()== MeganetEngine.commandType.GET_LOG ||
                                        MeganetInstances.getInstance().GetMeganetEngine().getCurrentCommand()== MeganetEngine.commandType.REQ_LOG) )
                        {
                            DataProcess(null);
                        }

                        if(bytesAvailable > 0) {
                            final byte[] packetBytes = new byte[bytesAvailable];

                            _mmInputStream.read(packetBytes); // read the msg and save in packetBytes
                            if(_startCollect) {
                                if(_rawReceive)
                                {
                                    handler.post(new Runnable() {
                                        public void run() {
                                            DataProcess(packetBytes);
                                        }
                                    });
                                }
                                else
                                {
                                    for(int i = 0; i < bytesAvailable; i++) {
                                        byte b = packetBytes[i];

                                        if(startFound) {  // If we got the start byte of message
                                            readBuffer[_readBufferPosition++] = b;

                                            if(_readBufferPosition > 1 && packetLenght == 0)
                                            {
                                                packetLenght = readBuffer[1]; //initiate the length of a packet
                                            }
                                        }
                                        else {
                                            if(b == _packetStart) {
                                                readBuffer[_readBufferPosition++] = b;
                                                startFound = true;
                                            }
                                        }
                                    }
                                    receivedDataLenght += bytesAvailable;

                                        if(receivedDataLenght >= packetLenght + 2 && packetLenght > 0) {
                                        if(!_isInfinitReceive)
                                            _startCollect = false;
                                        receivedDataLenght = 0;
                                        packetLenght = 0;
                                        final byte[] collectedBytes = new byte[_readBufferPosition];
                                        System.arraycopy(readBuffer, 0, collectedBytes, 0, collectedBytes.length); //Copy readBuffer array to collectedBytes array
                                        _readBufferPosition = 0;
                                        startFound = false;
                                        Arrays.fill(readBuffer, (byte)0); // put zeros in readBuffer
                                        handler.post(new Runnable() {
                                            public void run() {
                                                if(collectedBytes.length > 2)
                                                {
                                                    if (MeganetInstances.getInstance().GetMeganetEngine().getCurrentCommand()== MeganetEngine.commandType.GET_LOG ){
                                                        DataProcess(collectedBytes);
                                                    }
                                                    else if(collectedBytes[1] + 2 == collectedBytes.length )
                                                        DataProcess(collectedBytes);
                                                    else
                                                        return;;
                                                }
                                                else
                                                    return;

                                            }
                                        });
                                    }
                                }
                            }
                            else {
                                packetLenght = 0;
                                receivedDataLenght = 0;
                                handler.post(new Runnable() {
                                    public void run() {
                                        JunkBuffer(packetBytes);;
                                    }
                                });
                            }
                        }
                    }
                    catch (IOException ex) {
                        _stopWork = true;
                    }
                }
            }
        });

        _workerThread.start();
    }
    protected abstract void DataProcess(byte[] dataArr_prm);
    protected abstract void JunkBuffer(byte[] dataArr_prm);
    protected abstract void LostConnection(String erroMessage);
    protected abstract void ReceiveTimeout();

}

