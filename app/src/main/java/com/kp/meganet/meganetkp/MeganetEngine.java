package com.kp.meganet.meganetkp;

import android.bluetooth.BluetoothAdapter;
import android.util.Log;

import java.io.Console;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

/**
 * Created by alex on 11/22/2015.
 */
public class MeganetEngine extends BTengine {

    public enum ePromptType {
        REGULAR,
        PAIRING,
        TEN_CHR_PAIRING
    }

    public enum commandType {
        NONE,
        PROMPT,
        PROMPT_SYNC,
        READ,
        RDM,
        RDM_RECEIVE,
        RDM_OPEN,
        RDM_CLOSE,
        READ_AFTER_WRITE,
        METER_POWER_OFF,
        METER_SLEEP,
        RSNT_ISM_ON,
        RSNT_ISM_OFF,
        RSNT_VHF_ON,
        RSNT_VHF_OFF,
        RSNT_READ_INIT1,
        RSNT_READ_INIT2,
        RSNT_READ_DIS_INIT1,
        RSNT_READ_DIS_INIT2,
        RST_SET_FREQ,
        WRITE_RESULT,
        WRITE_CMD,
        //WRITE_DATA,
        READ_METER,
        GET_RSNT_VERSION,
        COLLECT_READS,
        SCAN_LISTENING_MCS,
        CLEAR_MC_HISTORY,
        SET_RF_FILTER,
        PULSE_READ,
        NEW_PULSE_READ,
        PULSE_SYNC,
        PULSE_WRITE_REQ,
        PULSE_WRITE,
        GET_LOG,
        REQ_LOG,
        GET_METER_SN,
        TIME_REQUEST
    }

    public enum eReadType
    {
        NONE,
        FIELD_VERIF_1,
        FIELD_VERIF_2,
        READ_METER,
        DRIVE_BY,
        RDM
    }

    private ePromptType _promtType;
    private String _promptName;
    private String _myAddress;
    private String _unitAddress;
    private String _ndevice;
    private String _deviceVersion;
    private volatile boolean _allowReceiveFlg;
    private volatile commandType _currentCommand;
    private Vector<byte[]> _dataBuffer;
    private DataAnalizer _dataAnalazer;
    private boolean _isPair;
    private boolean _startPrompt;
    iCallback _consumer;
    private boolean is_new_pulse;
    iReadMeterCallBack _readMeterConsumer;
    iPulseCallback _pulseConsumer;
    private boolean _is_extended;
    private int new_pulse_port;

    private Timer _promptTimer;

    byte[] _unitDataArr;
    byte[] _userDataArr;
    Map<String, QryParams> _unitParams;
    Map<String, QryParams> _userParams;
    String _rsntVersion;
    private eReadType _currentReadType;
    private String _rssiFreq1, _rssiFreq2, _readMeterFreq, _rdmMeterFreq;
    private String _useKpack;
    private String _currentFileterStr;


    private String last_sn; // new meter SN
    private String last_read; // new read

    //private boolean use_old;
    private String last_old_sn; // old meter SN
    private String last_old_read; // old read
    private String account_number; // Account number

    private String address_name; // Address name
    private String last_latitude; // LAT
    private String last_longitude; // LONG

    private String _qr_address;


    int global_port_num;
    int global_port_sn;
    int global_port_read;

    int programm_type_id;

    private byte[] _promptArr;
    private byte[] _msgArr;
    private volatile int _timerCount;

    ////////////////////////////////////////////////////////////////
    private boolean dataLoaded = false;

    public String get_deviceVersion() { return _deviceVersion; }
    public ePromptType get_promtType(){ return _promtType; }
    public commandType getCurrentCommand() {
        return _currentCommand;
    }
    public int get_timerCount() {
        return _timerCount;
    }
    public void increase_timerCount()
    {
        _timerCount++;
    }
    public void reset_timerCount() {
        _timerCount = 0;
    }

    public synchronized void waitProducer() {
        while (!dataLoaded) {
            try {
                wait(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void waitConsumer() {
        while (dataLoaded) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void dataLoaded() {
        dataLoaded = true;
        notify();
    }

    public synchronized void dataConsumed() {
        dataLoaded = false;
        notify();
    }

    public void SetCurrentProgrammType(int type_prm)
    {
        programm_type_id = type_prm;
    }
    public int GetCurrentProgrammType()
    {
        return programm_type_id;
    }

    public void SetAddressName(String acc_name_prm) { address_name = acc_name_prm; }
    public String GetAddressName() { return address_name; }

    public void SetCurrentReadType(eReadType type_prm)
    {
        _currentReadType = type_prm;
    }
    public eReadType GetCurrentReadType()
    {
        return _currentReadType;
    }

    public void SetQrAddress(String addr_prm)
    {
        _qr_address = addr_prm;
    }
    public String GetQrAddress()
    {
        return _qr_address;
    }

    public String GetRSNTVersion()
    {
        return _rsntVersion;
    }

    public void InitFrequency(String rssi1, String rssi2, String readMeter, String rdmMeter, String kpack)
    {
        _rssiFreq1 = rssi1;
        _rssiFreq2 = rssi2;
        _readMeterFreq = readMeter;
        _rdmMeterFreq = rdmMeter;
        _useKpack = kpack;
    }

    public String GetFrequency()
    {
        String freq;

        switch (_currentReadType) {
            case FIELD_VERIF_1:
                freq = _rssiFreq1;
                break;

            case FIELD_VERIF_2:
                freq = _rssiFreq2;
                break;

            case READ_METER:
                freq = _readMeterFreq;
                break;

            case NONE:
                freq = _rdmMeterFreq;

            case DRIVE_BY:
                freq = _rdmMeterFreq;
                break;

            default:
                return ""; // Error
        }
        return freq;
    }

    public void SetFrequency()
    {
        String freq;

        switch (_currentReadType) {
            case FIELD_VERIF_1:
                freq = _rssiFreq1;
                break;

            case FIELD_VERIF_2:
                freq = _rssiFreq2;
                break;

            case READ_METER:
                freq = _readMeterFreq;
                break;
            case NONE:
                freq = _rdmMeterFreq;
                break;

            case DRIVE_BY:
                freq = _readMeterFreq;
                break;

            default:
                return; // Error
        }

        byte[] writeArr = new byte[9];
        String freqTmp, freqTmp1;

        freqTmp = Integer.toHexString((int) (Double.parseDouble(freq) * 1000000.0d));

        freqTmp1 = Utilities.StringCompleter(freqTmp, 8, "0", true);

        writeArr[0] = 2;
        writeArr[1] = 7;
        writeArr[2] = 82;//Asc("R");
        writeArr[3] = 4;
        writeArr[4] = (byte)Integer.parseInt(freqTmp1.substring(0, 2), 16);
        writeArr[5] = (byte)Integer.parseInt(freqTmp1.substring(2, 4), 16);
        writeArr[6] = (byte)Integer.parseInt(freqTmp1.substring(4, 6), 16);
        writeArr[7] = (byte)Integer.parseInt(freqTmp1.substring(6, 8), 16);
        if((_useKpack.equals("1")) && ((_currentReadType == eReadType.READ_METER) || (_currentReadType == eReadType.FIELD_VERIF_1) || (_currentReadType == eReadType.FIELD_VERIF_2) || (_currentReadType == eReadType.RDM)))
            writeArr[8] = 0;
        else
            writeArr[8] = 1;

        _currentCommand = commandType.RST_SET_FREQ;
        StartCollectData(true, false);

        SendData(writeArr);
    }

    public void CollectReads()
    {
        byte[] arr = new byte[6];

        arr[0] = 2;
        arr[1] = 4;
        arr[2] = 82;

        arr[3] = 11;
        arr[4] = (byte)255;
        arr[5] = (byte)255;

        _currentCommand = commandType.COLLECT_READS;
        StartCollectData(true, true);
        SendData(arr);
    }

    public void ScanLinteningMCs()
    {
        byte[] arr = new byte[6];

        arr[0] = 2;
        arr[1] = 4;
        arr[2] = 82;

        arr[3] = 12;
        arr[4] = (byte)255;
        arr[5] = (byte)255;

        _currentCommand = commandType.SCAN_LISTENING_MCS;
        StartCollectData(true, true);
        SendData(arr);
    }

    public void ClearMcHistory()
    {
        byte[] arr = new byte[6];

        arr[0] = 2;
        arr[1] = 4;
        arr[2] = 82;

        arr[3] = 13;
        arr[4] = (byte)255;
        arr[5] = (byte)255;

        _currentCommand = commandType.SCAN_LISTENING_MCS;
        StartCollectData(true, true);
        SendData(arr);
    }

    public boolean SetReadMetersRSNT(boolean flg) //
    {
        byte[] arr = new byte[4];

        if(flg)
        {
            arr[0] = 2;
            arr[1] = 2;
            arr[2] = 82;
            arr[3] = 28;
            _currentCommand = commandType.RSNT_ISM_ON;
            StartCollectData(true, false);
            SendData(arr);

        }
        else
        {
            arr[0] = 2;
            arr[1] = 2;
            arr[2] = 82;
            arr[3] = 29;
            _currentCommand = commandType.RSNT_ISM_OFF;
            StartCollectData(true, false);
            SendData(arr);

        }

        return true;
    }

    public void StartCollectData(boolean start_prm, boolean infinit_prm)
    {
        _startCollect = start_prm;
        if(!start_prm)
        {
            _isInfinitReceive = false;
            return;
        }

        if(infinit_prm)
        {
            _isInfinitReceive = true;
        }
        else
        {
            _isInfinitReceive = false;
            _startCollectDateTag = System.currentTimeMillis();
        }
    }

    public void ReadMeters(boolean flg)
    {
        if(flg)
        {
            //_rawReceive = true;
            StartCollectData(true, true);
            _currentCommand = commandType.READ_METER;
        }
        else
        {
            _rawReceive = false;
            _startCollect = false;
            _currentCommand = commandType.NONE;
        }
    }

    public boolean InitReadMeter(iReadMeterCallBack callBack_prm)
    {
        _readMeterConsumer = callBack_prm;
        return true;
    }


    public boolean InitPulse(iPulseCallback callBack_prm)
    {
        String addrTmp = "";

        String myAddress_prm = MeganetInstances.getInstance().GetMeganetDb().getSetting(1).GetKeyValue();
        if(!isNumeric(myAddress_prm))
        {
            myAddress_prm = "00001";
        }
        if(myAddress_prm.length() <= 5) {
            addrTmp = myAddress_prm;
        }
        else {
            addrTmp = myAddress_prm.substring(0,5);
        }

        _myAddress = Utilities.StringCompleter(addrTmp, 5, "0", true);

        _pulseConsumer = callBack_prm;

        return true;
    }

    public String GetFilter()
    {
        return _currentFileterStr;
    }

    public void SetFilterAll()
    {
        _currentFileterStr = "Receive All";

        byte[] arr = new byte[6];

        arr[0] = 2;
        arr[1] = 4;
        arr[2] = 82;

        arr[3] = 3;
        arr[4] = (byte) 0xeFF;
        arr[5] = (byte) 0xeFF;

        _currentCommand = commandType.SET_RF_FILTER;
        StartCollectData(true, false);
        SendData(arr);
    }

    public void SetFilterNone()
    {
        _currentFileterStr = "Receive None";

        byte[] arr = new byte[6];

        arr[0] = 2;
        arr[1] = 4;
        arr[2] = 82;

        arr[3] = 3;
        arr[4] = 0;
        arr[5] = 0;

        _currentCommand = commandType.SET_RF_FILTER;
        StartCollectData(true, false);
        SendData(arr);
    }

    public void SetSpecMtuFilter(String mtuAddress_prm, boolean kpe_prm)
    {
        //Utilities.StringCompleter(mtuAddress_prm, 5, "0", true)
        _currentFileterStr = Utilities.StringCompleter(mtuAddress_prm, 7, "0", true);
        byte[] arr;
        if(kpe_prm)
        {
            arr = new byte[7];

            arr[0] = 2;
            arr[1] = 5;
            arr[2] = 82;

            arr[3] = 3;
            arr[4] = (byte)Integer.parseInt(Utilities.StringCompleter(Integer.toHexString(Integer.parseInt(mtuAddress_prm)), 6, "0", true).substring(0, 2), 16);
            arr[5] = (byte)Integer.parseInt(Utilities.StringCompleter(Integer.toHexString(Integer.parseInt(mtuAddress_prm)), 6, "0", true).substring(2, 4), 16);
            arr[6] = (byte)Integer.parseInt(Utilities.StringCompleter(Integer.toHexString(Integer.parseInt(mtuAddress_prm)), 6, "0", true).substring(4, 6), 16);
        }
        else
        {
            arr = new byte[6];

            arr[0] = 2;
            arr[1] = 4;
            arr[2] = 82;

            arr[3] = 3;
            arr[4] = (byte)Integer.parseInt(Utilities.StringCompleter(Integer.toHexString(Integer.parseInt(mtuAddress_prm)), 4, "0", true).substring(0, 2), 16);
            arr[5] = (byte)Integer.parseInt(Utilities.StringCompleter(Integer.toHexString(Integer.parseInt(mtuAddress_prm)), 4, "0", true).substring(2, 4), 16);
        }

        _currentCommand = commandType.SET_RF_FILTER;
        StartCollectData(true, false);
        SendData(arr);
    }

    public void SetRangeFilter(String start_prm, String end_prm, boolean kpe_prm)
    {
        _currentFileterStr = "Recieve Range: " + Utilities.StringCompleter(start_prm, 7, "0", true) + " - " + Utilities.StringCompleter(end_prm, 7, "0", true);

        byte[] arr;

        if (kpe_prm) {
            arr = new byte[10];

            arr[0] = 2;
            arr[1] = 8;
            arr[2] = 82;

            arr[3] = 7;
            arr[4] = (byte)Integer.parseInt(Utilities.StringCompleter(Integer.toHexString(Integer.parseInt(start_prm)), 6, "0", true).substring(0, 2), 16);
            arr[5] = (byte)Integer.parseInt(Utilities.StringCompleter(Integer.toHexString(Integer.parseInt(start_prm)), 6, "0", true).substring(2, 4), 16);
            arr[6] = (byte)Integer.parseInt(Utilities.StringCompleter(Integer.toHexString(Integer.parseInt(start_prm)), 6, "0", true).substring(4, 6), 16);

            arr[7] = (byte)Integer.parseInt(Utilities.StringCompleter(Integer.toHexString(Integer.parseInt(end_prm)), 6, "0", true).substring(0, 2), 16);
            arr[8] = (byte)Integer.parseInt(Utilities.StringCompleter(Integer.toHexString(Integer.parseInt(end_prm)), 6, "0", true).substring(2, 4), 16);
            arr[9] = (byte)Integer.parseInt(Utilities.StringCompleter(Integer.toHexString(Integer.parseInt(end_prm)), 6, "0", true).substring(4, 6), 16);
        }
        else {
            arr = new byte[8];


            arr[0] = 2;
            arr[1] = 6;
            arr[2] = 82;

            arr[3] = 7;
            arr[4] = (byte)Integer.parseInt(Utilities.StringCompleter(Integer.toHexString(Integer.parseInt(start_prm)), 4, "0", true).substring(0, 2), 16);
            arr[5] = (byte)Integer.parseInt(Utilities.StringCompleter(Integer.toHexString(Integer.parseInt(start_prm)), 4, "0", true).substring(2, 4), 16);

            arr[6] = (byte)Integer.parseInt(Utilities.StringCompleter(Integer.toHexString(Integer.parseInt(end_prm)), 4, "0", true).substring(0, 2), 16);
            arr[7] = (byte)Integer.parseInt(Utilities.StringCompleter(Integer.toHexString(Integer.parseInt(end_prm)), 4, "0", true).substring(2, 4), 16);
        }


        _currentCommand = commandType.SET_RF_FILTER;
        StartCollectData(true, false);
        SendData(arr);
    }

    MeganetEngine(BluetoothAdapter btAdapter_prm) {
        super(btAdapter_prm);

        _currentReadType = eReadType.NONE;
        _currentFileterStr = "Receive All";
    }

    public byte CheckSum(byte[] MainString)
    {
        byte Outcome;
        Integer i;
        Outcome = 0;
        for (i = 0; i < MainString.length; i++)
        {
            Outcome ^= MainString[i];
        }

        return Outcome;
    }


    void ReInitProperties(String rssi1, String rssi2, String readMeter, String rdmMeter, String myAddress_prm, String useKpack_prm)
    {
        String addrTmp;

        _rssiFreq1 = rssi1;
        _rssiFreq2 = rssi2;
        _readMeterFreq = readMeter;
        _rdmMeterFreq = rdmMeter;
        if(!isNumeric(myAddress_prm))
        {
            myAddress_prm = "00001";
        }
        if(myAddress_prm.length() <= 5) {
            addrTmp = myAddress_prm;
        }
        else {
            addrTmp = myAddress_prm.substring(0,5);
        }

        _myAddress = Utilities.StringCompleter(addrTmp, 5, "0", true);

        _useKpack = useKpack_prm;

    }

    void InitProgramming(iCallback callBack_prm, String myAddress_prm)
    {
        String addrTmp = "";
        _consumer = callBack_prm;

        if(!isNumeric(myAddress_prm))
        {
            myAddress_prm = "00001";
        }
        if(myAddress_prm.length() <= 5) {
            addrTmp = myAddress_prm;
        }
        else {
            addrTmp = myAddress_prm.substring(0,5);
        }

        _myAddress = Utilities.StringCompleter(addrTmp, 5, "0", true);
    }

    void MeganetInit() {

        _startPrompt = false;
        _dataAnalazer = new DataAnalizer();

        _promtType = ePromptType.REGULAR;
        _myAddress = "";

        _dataBuffer = new Vector<byte[]>();
        _allowReceiveFlg = false;
        _rawReceive = false;
        _currentCommand = commandType.NONE;

        _promptTimer = new Timer();
        _promptTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                TimerMethod();
            }

        }, 0, 5000);

    }

    private void TimerMethod()
    {
        //This method is called directly by the timer
        //and runs in the same thread as the timer.

        //We call the method that will work with the UI
        //through the runOnUiThread method.

        if((_currentCommand == commandType.PROMPT || _currentCommand == commandType.PROMPT_SYNC) && _startPrompt && _timerCount < 10)
        {
            _startCollectDateTag = System.currentTimeMillis();
            _startCollect = true;
            SendData(_promptArr);
            _timerCount++;
            if(_currentCommand == commandType.PROMPT)
                _consumer.OnMessageCb("Waiting for MTU response");
            else if(_currentCommand == commandType.PROMPT_SYNC)
            _pulseConsumer.OnMessageCb("Waiting for Node response");
            // Replace "MTU" to "Node" on 21.11.2019
        }
        else if(_currentCommand == commandType.WRITE_CMD || _currentCommand == commandType.WRITE_RESULT || _currentCommand == commandType.READ_AFTER_WRITE)
        {
            if(_timerCount < 10)
            {
                _timerCount++;
                _consumer.OnMessageCb("Waiting for MTU response");
            }
            else
            {
                _consumer.OnProgramm(false, "No response from MTU, please try again");
                _currentCommand = commandType.NONE;
            }
        }
        else if ((_currentCommand == commandType.GET_METER_SN || _currentCommand == commandType.TIME_REQUEST ||
                    _currentCommand == commandType.REQ_LOG ||  _currentCommand == commandType.GET_LOG))
        {
            if( _timerCount <= 5) {
                _startCollectDateTag = System.currentTimeMillis();
                _startCollect = true;
                SendData(_msgArr);
                _timerCount++;
            }
            else {
                _startCollect = false;
            }
        }
        else if((_currentCommand == commandType.READ))
            // for programming option - if the list not up then send again read msg 3 times at most
            // Added on 13.9.2020
        {
            if( _timerCount <= 3)
            {
                _consumer.OnMessageCb("Reading parameters..");
                SendData(_msgArr);
                _timerCount++;
            }
            else {
                _timerCount = 0;
            }
        }
        else
            _timerCount = 0;

    }
    ////////////////////////////////////////////////////////////////
    public void PromptSync(ePromptType promptType_prm, String promptName_prm, int port_prm) {

        _startCollectDateTag = System.currentTimeMillis();
        _startCollect = true;
        String promptStr = "";
        byte[] promptArr;
        byte[] promptNameArr = promptName_prm.getBytes();
        _unitAddress = "";
        _unitDataArr = null;
        _unitParams = null;

        _promtType = promptType_prm;
        _promptName = promptName_prm;
        _is_extended = false;
        is_new_pulse = true;
        new_pulse_port = port_prm;

        _currentCommand = commandType.PROMPT_SYNC;

        switch(promptType_prm) {
            case REGULAR:
                _is_extended = false;
                promptArr = new byte[2 + promptName_prm.length() + 1];
                promptArr[0] = (byte) 0xe02; // Start sign
                promptArr[1] = (byte)(promptNameArr.length + 1); // Length of message
                for(int i = 0; i < promptNameArr.length; i++)
                {
                    promptArr[i+2] = promptNameArr[i];
                }
                promptArr[2 + promptNameArr.length] = (byte) 0xe08; // Prompt (Command Type)

                _startPrompt = true;
                _promptArr = promptArr;
                //SendData(promptStr.getBytes());
                break;
            case PAIRING: {
                _is_extended = false;

                promptArr = new byte[2 + promptName_prm.length() + 5]; // 1,2 начало и размерб 3... промпт имя. 2 байта FF-FF адрес юнита. 2 байта мой адрес 00-01 и команда 8 - промпт. И тго 2+размер промпта + 5
                promptArr[0] = (byte) 0xe02;  // Start sign
                promptArr[1] = (byte) (promptNameArr.length + 5); // Length of message
                for (int i = 0; i < promptNameArr.length; i++) {
                    promptArr[i + 2] = promptNameArr[i];
                }

                promptArr[2 + promptNameArr.length] = (byte) 0xeFF;
                promptArr[3 + promptNameArr.length] = (byte) 0xeFF;

                int s1, s2;
                s1 = 0;
                s2 = 0;
                // Convert to hex and split

                try {
                    s1 = Integer.parseInt(Utilities.StringCompleter(Integer.toHexString(Integer.parseInt(_myAddress)), 4, "0", true).substring(0, 2), 16);
                    s2 = Integer.parseInt(Utilities.StringCompleter(Integer.toHexString(Integer.parseInt(_myAddress)), 4, "0", true).substring(2, 4), 16);
                } catch (NumberFormatException nfe) {

                }
                promptArr[4 + promptNameArr.length] = (byte) s1;
                promptArr[5 + promptNameArr.length] = (byte) s2;

                promptArr[6 + promptNameArr.length] = (byte) 0xe08; // Prompt (Command Type)

                _startPrompt = true;
                _promptArr = promptArr;
            }
            break;

            case TEN_CHR_PAIRING: {
                _is_extended = true;

                promptArr = new byte[2 + promptName_prm.length() + 5]; // 1,2 начало и размерб 3... промпт имя. 2 байта FF-FF адрес юнита. 2 байта мой адрес 00-01 и команда 8 - промпт. И тго 2+размер промпта + 5
                promptArr[0] = (byte) 0xe02; // Start sign
                promptArr[1] = (byte) (promptNameArr.length + 5);  // Length of message
                for (int i = 0; i < promptNameArr.length; i++) {
                    promptArr[i + 2] = promptNameArr[i];
                }

                promptArr[2 + promptNameArr.length] = (byte) 0xeFF;
                promptArr[3 + promptNameArr.length] = (byte) 0xeFF;
                promptArr[4 + promptNameArr.length] = (byte) 0xeFF;

                int s1;
                s1 = 0;

                // Convert to hex and split

                try {
                    s1 = Integer.parseInt(Utilities.StringCompleter(Integer.toHexString(Integer.parseInt(_myAddress)), 4, "0", true).substring(2, 4), 16);
                } catch (NumberFormatException nfe) {

                }
                promptArr[5 + promptNameArr.length] = (byte) s1;
                promptArr[6 + promptNameArr.length] = (byte) 0xe08; // Prompt (Command Type)

                _startPrompt = true;
                _promptArr = promptArr;
            }
            break;
            default:
                break;
        }


        /*

        promptArr = new byte[2 + promptName_prm.length() + 5]; // 1,2 начало и размерб 3... промпт имя. 2 байта FF-FF адрес юнита. 2 байта мой адрес 00-01 и команда 8 - промпт. И тго 2+размер промпта + 5
        promptArr[0] = (byte) 0xe02;
        promptArr[1] = (byte) (promptNameArr.length + 5);
        for (int i = 0; i < promptNameArr.length; i++) {
            promptArr[i + 2] = promptNameArr[i];
        }

        promptArr[2 + promptNameArr.length] = (byte) 0xeFF;
        promptArr[3 + promptNameArr.length] = (byte) 0xeFF;
        promptArr[4 + promptNameArr.length] = (byte) 0xeFF;

        int s1;
        new_pulse_port = port_prm;
            s1 = 0;

        // Convert to hex and split

        try {
            s1 = Integer.parseInt(Utilities.StringCompleter(Integer.toHexString(Integer.parseInt(_myAddress)), 4, "0", true).substring(2, 4), 16);
        } catch (NumberFormatException nfe) {

        }
        promptArr[5 + promptNameArr.length] = (byte) s1;

        promptArr[6 + promptNameArr.length] = (byte) 0xe08;

        _startPrompt = true;
        _promptArr = promptArr;

        */
    }

    public void Prompt(ePromptType promptType_prm, String promptName_prm) {

        _startCollectDateTag = System.currentTimeMillis();
        _startCollect = true;
        String promptStr = "";
        byte[] promptArr;
        byte[] promptNameArr = promptName_prm.getBytes();
        _unitAddress = "";
        _unitDataArr = null;
        _unitParams = null;

        _promtType = promptType_prm;
        _promptName = promptName_prm;
        _is_extended = false;

        switch(promptType_prm) {
            case REGULAR:
                // 1: Send Prompt
                _currentCommand = commandType.PROMPT;
                promptArr = new byte[2 + promptName_prm.length() + 1];
                promptArr[0] = (byte) 0xe02; // Start sign
                promptArr[1] = (byte)(promptNameArr.length + 1); // Length of message
                for(int i = 0; i < promptNameArr.length; i++)
                {
                    promptArr[i+2] = promptNameArr[i];
                }
                promptArr[2 + promptNameArr.length] = (byte) 0xe08; // Prompt (Command Type)

                _startPrompt = true;
                _promptArr = promptArr;
                //SendData(promptStr.getBytes());
                break;
            case PAIRING: {
                _currentCommand = commandType.PROMPT;

                promptArr = new byte[2 + promptName_prm.length() + 5]; // 1,2 начало и размерб 3... промпт имя. 2 байта FF-FF адрес юнита. 2 байта мой адрес 00-01 и команда 8 - промпт. И тго 2+размер промпта + 5
                promptArr[0] = (byte) 0xe02;  // Start sign
                promptArr[1] = (byte) (promptNameArr.length + 5); // Length of message
                for (int i = 0; i < promptNameArr.length; i++) {
                    promptArr[i + 2] = promptNameArr[i];
                }

                promptArr[2 + promptNameArr.length] = (byte) 0xeFF;
                promptArr[3 + promptNameArr.length] = (byte) 0xeFF;

                int s1, s2;
                s1 = 0;
                s2 = 0;
                // Convert to hex and split

                try {
                    s1 = Integer.parseInt(Utilities.StringCompleter(Integer.toHexString(Integer.parseInt(_myAddress)), 4, "0", true).substring(0, 2), 16);
                    s2 = Integer.parseInt(Utilities.StringCompleter(Integer.toHexString(Integer.parseInt(_myAddress)), 4, "0", true).substring(2, 4), 16);
                } catch (NumberFormatException nfe) {

                }
                promptArr[4 + promptNameArr.length] = (byte) s1;
                promptArr[5 + promptNameArr.length] = (byte) s2;

                promptArr[6 + promptNameArr.length] = (byte) 0xe08; // Prompt (Command Type)

                _startPrompt = true;
                _promptArr = promptArr;
            }
                break;

            case TEN_CHR_PAIRING: {

                _currentCommand = commandType.PROMPT;
                _is_extended = true;

                promptArr = new byte[2 + promptName_prm.length() + 5]; // 1,2 начало и размерб 3... промпт имя. 2 байта FF-FF адрес юнита. 2 байта мой адрес 00-01 и команда 8 - промпт. И тго 2+размер промпта + 5
                promptArr[0] = (byte) 0xe02; // Start sign
                promptArr[1] = (byte) (promptNameArr.length + 5);  // Length of message
                for (int i = 0; i < promptNameArr.length; i++) {
                    promptArr[i + 2] = promptNameArr[i];
                }

                promptArr[2 + promptNameArr.length] = (byte) 0xeFF;
                promptArr[3 + promptNameArr.length] = (byte) 0xeFF;
                promptArr[4 + promptNameArr.length] = (byte) 0xeFF;

                int s1;
                s1 = 0;

                // Convert to hex and split

                try {
                    s1 = Integer.parseInt(Utilities.StringCompleter(Integer.toHexString(Integer.parseInt(_myAddress)), 4, "0", true).substring(2, 4), 16);
                } catch (NumberFormatException nfe) {

                }
                promptArr[5 + promptNameArr.length] = (byte) s1;
                promptArr[6 + promptNameArr.length] = (byte) 0xe08; // Prompt (Command Type)

                _startPrompt = true;
                _promptArr = promptArr;
            }
                break;
            default:
                break;
        }
    }

    public boolean MeterPowerOff()
    {
        _currentCommand = commandType.WRITE_CMD;
        byte[] ReadArr;
        byte[] promptNameArr = _promptName.getBytes();
        if(_isPair) {


            ReadArr = new byte[2 + _promptName.length() + 5]; // 1,2 начало и размерб 3... промпт имя. 2 байта FF-FF адрес юнита. 2 байта мой адрес 00-01 и команда 8 - промпт. И тго 2+размер промпта + 5
            ReadArr[0] = (byte) 0xe02; // Start sign
            ReadArr[1] = (byte) (promptNameArr.length + 5);  // Length of message
            for (int i = 0; i < promptNameArr.length; i++) {
                ReadArr[i + 2] = promptNameArr[i];
            }

            String hexAddress;


            if(_is_extended)
            {
                hexAddress = Integer.toHexString(Integer.parseInt(_unitAddress));
                hexAddress = Utilities.StringCompleter(hexAddress, 6, "0", true);
                ReadArr[2 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(0, 2), 16);
                ReadArr[3 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(2, 4), 16);
                ReadArr[4 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(4, 6), 16);
                /////////////////
                hexAddress = Integer.toHexString(Integer.parseInt(_myAddress));
                hexAddress = Utilities.StringCompleter(hexAddress, 2, "0", true);

                ReadArr[5 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(0, 2), 16);
            }
            else
            {
                hexAddress = Integer.toHexString(Integer.parseInt(_unitAddress));
                hexAddress = Utilities.StringCompleter(hexAddress, 4, "0", true);
                ReadArr[2 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(0, 2), 16);
                ReadArr[3 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(2, 4), 16);
                /////////////////
                hexAddress = Integer.toHexString(Integer.parseInt(_myAddress));
                hexAddress = Utilities.StringCompleter(hexAddress, 4, "0", true);

                ReadArr[4 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(0, 2), 16);
                ReadArr[5 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(2, 4), 16);
            }

            ReadArr[6 + promptNameArr.length] = (byte) 0xe97; // Disconnect (Command Type)

            _startCollectDateTag = System.currentTimeMillis();
            _startCollect = true;
            _currentCommand = commandType.METER_POWER_OFF;
            SendData(ReadArr);
        }
        else
        {
            ReadArr = new byte[2 + promptNameArr.length + 1];
            ReadArr[0] = (byte) 0xe02; // Start sign
            ReadArr[1] = (byte)(promptNameArr.length + 1); // Length of message
            for(int i = 0; i < promptNameArr.length; i++)
            {
                ReadArr[i+2] = promptNameArr[i];
            }
            ReadArr[2 + promptNameArr.length] = (byte) 0xe97; // Disconnect (Command Type)
            _startCollectDateTag = System.currentTimeMillis();
            _startCollect = true;
            _currentCommand = commandType.METER_POWER_OFF;
            SendData(ReadArr);
        }
        return true;
    }

    public boolean MeterSleep()
    {
        _currentCommand = commandType.WRITE_CMD;

        byte[] ReadArr;
        byte[] promptNameArr = _promptName.getBytes();

        if(_isPair) {
            ReadArr = new byte[2 + _promptName.length() + 5]; // 1,2 начало и размерб 3... промпт имя. 2 байта FF-FF адрес юнита. 2 байта мой адрес 00-01 и команда 8 - промпт. И тго 2+размер промпта + 5
            ReadArr[0] = (byte) 0xe02;
            ReadArr[1] = (byte) (promptNameArr.length + 5);
            for (int i = 0; i < promptNameArr.length; i++) {
                ReadArr[i + 2] = promptNameArr[i];
            }

            String hexAddress;
            if(_is_extended)
            {
                hexAddress = Integer.toHexString(Integer.parseInt(_unitAddress));
                hexAddress = Utilities.StringCompleter(hexAddress, 6, "0", true);
                ReadArr[2 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(0, 2), 16);
                ReadArr[3 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(2, 4), 16);
                ReadArr[4 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(4, 6), 16);
                /////////////////
                hexAddress = Integer.toHexString(Integer.parseInt(_myAddress));
                hexAddress = Utilities.StringCompleter(hexAddress, 2, "0", true);

                ReadArr[5 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(0, 2), 16);
            }
            else
            {
                hexAddress = Integer.toHexString(Integer.parseInt(_unitAddress));
                hexAddress = Utilities.StringCompleter(hexAddress, 4, "0", true);
                ReadArr[2 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(0, 2), 16);
                ReadArr[3 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(2, 4), 16);
                /////////////////
                hexAddress = Integer.toHexString(Integer.parseInt(_myAddress));
                hexAddress = Utilities.StringCompleter(hexAddress, 4, "0", true);

                ReadArr[4 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(0, 2), 16);
                ReadArr[5 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(2, 4), 16);
            }

            ReadArr[6 + promptNameArr.length] = (byte) 0xe93;

            _startCollectDateTag = System.currentTimeMillis();
            _startCollect = true;
            _currentCommand = commandType.METER_SLEEP;
            SendData(ReadArr);
        }
        else
        {
            ReadArr = new byte[2 + promptNameArr.length + 1];
            ReadArr[0] = (byte) 0xe02;
            ReadArr[1] = (byte)(promptNameArr.length + 1);
            for(int i = 0; i < promptNameArr.length; i++)
            {
                ReadArr[i+2] = promptNameArr[i];
            }
            ReadArr[2 + promptNameArr.length] = (byte) 0xe93;
            _startCollectDateTag = System.currentTimeMillis();
            _startCollect = true;
            _currentCommand = commandType.WRITE_CMD;
            SendData(ReadArr);
        }
        return true;
    }

    public boolean RDM_Command(boolean IsOpen, boolean is_kpe)
    {
        _currentCommand = commandType.WRITE_CMD;


        byte[] ReadArr;
        byte[] promptNameArr = _promptName.getBytes();

        String hexAddress;
        if(is_kpe)
        {
            ReadArr = new byte[8]; // 1,2 начало и размерб 3... промпт имя. 2 байта FF-FF адрес юнита. 2 байта мой адрес 00-01 и команда 8 - промпт. И тго 2+размер промпта + 5
            ReadArr[0] = (byte) 0xe02;
            ReadArr[1] = (byte) 0xe06;
            ReadArr[2] = (byte) 0xe52;
            ReadArr[3] = (byte) 0xe45;
            if(IsOpen)
                ReadArr[4] = (byte) 0xe01;
            else
                ReadArr[4] = (byte) 0xe02;
            hexAddress = Integer.toHexString(Integer.parseInt(_unitAddress));
            hexAddress = Utilities.StringCompleter(hexAddress, 6, "0", true);
            ReadArr[5] = (byte)Integer.parseInt(hexAddress.substring(0, 2), 16);
            ReadArr[6] = (byte)Integer.parseInt(hexAddress.substring(2, 4), 16);
            ReadArr[7] = (byte)Integer.parseInt(hexAddress.substring(4, 6), 16);

        }
        else
        {
            ReadArr = new byte[7]; // 1,2 начало и размерб 3... промпт имя. 2 байта FF-FF адрес юнита. 2 байта мой адрес 00-01 и команда 8 - промпт. И тго 2+размер промпта + 5
            ReadArr[0] = (byte) 0xe02;
            ReadArr[1] = (byte) 0xe05;
            ReadArr[2] = (byte) 0xe52;
            ReadArr[3] = (byte) 0xe52;
            if(IsOpen)
                ReadArr[4] = (byte) 0xe01;
            else
                ReadArr[4] = (byte) 0xe02;
            hexAddress = Integer.toHexString(Integer.parseInt(_unitAddress));
            hexAddress = Utilities.StringCompleter(hexAddress, 4, "0", true);
            ReadArr[5] = (byte)Integer.parseInt(hexAddress.substring(0, 2), 16);
            ReadArr[6] = (byte)Integer.parseInt(hexAddress.substring(2, 4), 16);

        }

        _startCollectDateTag = System.currentTimeMillis();
        _startCollect = true;
        if(IsOpen)
            _currentCommand = commandType.RDM_OPEN;
        else
            _currentCommand = commandType.RDM_CLOSE;
        SendData(ReadArr);

        return true;
    }

    public void Sync(int port_num)
    {
        String pulsPromptName = "KPMTWPN";
        byte[] promptNameArr = pulsPromptName.getBytes();
        byte [] PulseArr;
        PulseArr = new byte[2 + promptNameArr.length + 1];
        PulseArr[0] = (byte) 0xe02;
        PulseArr[1] = (byte)(promptNameArr.length + 1);
        for(int i = 0; i < promptNameArr.length; i++)
        {
            PulseArr[i+2] = promptNameArr[i];
        }
        PulseArr[2 + promptNameArr.length] = (byte)(port_num + 16);
        _startCollectDateTag = System.currentTimeMillis();
        _startCollect = true;
        _currentCommand = commandType.PULSE_READ;
        SendData(PulseArr);
    }

    public void ReadPulsePort(int port_num)
    {
        String pulsPromptName = "KPMTWPN";
        byte[] promptNameArr = pulsPromptName.getBytes();
        byte [] PulseArr;
        PulseArr = new byte[2 + promptNameArr.length + 1];
        PulseArr[0] = (byte) 0xe02;
        PulseArr[1] = (byte)(promptNameArr.length + 1);
        for(int i = 0; i < promptNameArr.length; i++)
        {
            PulseArr[i+2] = promptNameArr[i];
        }
        PulseArr[2 + promptNameArr.length] = (byte)(port_num + 16);
        _startCollectDateTag = System.currentTimeMillis();
        _startCollect = true;
        _currentCommand = commandType.PULSE_READ;
        SendData(PulseArr);
    }

    public void WritePulsePort(int port_num, int sn, int reading)
    {
        global_port_num = port_num;
        global_port_sn = sn;
        global_port_read = reading;


        String pulsPromptName = "KPMTWPN";
        byte[] promptNameArr = pulsPromptName.getBytes();
        byte [] PulseArr;
        PulseArr = new byte[2 + promptNameArr.length + 1];
        PulseArr[0] = (byte) 0xe02;
        PulseArr[1] = (byte)(promptNameArr.length + 1);
        for(int i = 0; i < promptNameArr.length; i++)
        {
            PulseArr[i+2] = promptNameArr[i];
        }
        PulseArr[2 + promptNameArr.length] = (byte)(port_num + 112);
        _startCollectDateTag = System.currentTimeMillis();
        _startCollect = true;
        _currentCommand = commandType.PULSE_WRITE_REQ;
        SendData(PulseArr);

    }

    public void RSNTVersionRequest()
    {
        byte [] arr = new byte[4];
        arr[0] = 2;
        arr[1] = 2;
        arr[2] = 82;
        arr[3] = 10;

        _currentCommand = commandType.GET_RSNT_VERSION;
        StartCollectData(true, false);
        SendData(arr);
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////
    public boolean Programm(Map<String, QryParams> dataParams_prm) {
        _userParams = dataParams_prm;
        _userDataArr = _dataAnalazer.ApplyData(_unitDataArr, dataParams_prm, _promptName.length(), _isPair);

        if(_userDataArr.length <= 0)
            return false;

        //_currentCommand = commandType.WRITE_CMD;

        byte[] ReadArr;
        byte[] promptNameArr = _promptName.getBytes();
        if(_isPair)
        {
            ReadArr = new byte[2 + _promptName.length() + 5]; // 1,2 начало и размерб 3... промпт имя. 2 байта FF-FF адрес юнита. 2 байта мой адрес 00-01 и команда 8 - промпт. И тго 2+размер промпта + 5
            ReadArr[0] = (byte) 0xe02;
            ReadArr[1] = (byte)(promptNameArr.length + 5);
            for(int i = 0; i < promptNameArr.length; i++)
            {
                ReadArr[i+2] = promptNameArr[i];
            }

            String hexAddress;
            if(_is_extended)
            {
                hexAddress = Integer.toHexString(Integer.parseInt(_unitAddress));
                hexAddress = Utilities.StringCompleter(hexAddress, 6, "0", true);
                ReadArr[2 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(0, 2), 16);
                ReadArr[3 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(2, 4), 16);
                ReadArr[4 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(4, 6), 16);
                /////////////////
                hexAddress = Integer.toHexString(Integer.parseInt(_myAddress));
                hexAddress = Utilities.StringCompleter(hexAddress, 2, "0", true);

                ReadArr[5 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(0, 2), 16);
            }
            else
            {
                hexAddress = Integer.toHexString(Integer.parseInt(_unitAddress));
                hexAddress = Utilities.StringCompleter(hexAddress, 4, "0", true);
                ReadArr[2 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(0, 2), 16);
                ReadArr[3 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(2, 4), 16);
                /////////////////
                hexAddress = Integer.toHexString(Integer.parseInt(_myAddress));
                hexAddress = Utilities.StringCompleter(hexAddress, 4, "0", true);

                ReadArr[4 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(0, 2), 16);
                ReadArr[5 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(2, 4), 16);
            }

            ReadArr[6 + promptNameArr.length] = (byte) 0xe07; // Program request (Command Type)

            _startCollectDateTag = System.currentTimeMillis();
            _startCollect = true;
            _currentCommand = commandType.WRITE_CMD;
            SendData(ReadArr);
        }
        else
        {
            ReadArr = new byte[2 + promptNameArr.length + 1];
            ReadArr[0] = (byte) 0xe02;
            ReadArr[1] = (byte)(promptNameArr.length + 1);
            for(int i = 0; i < promptNameArr.length; i++)
            {
                ReadArr[i+2] = promptNameArr[i];
            }
            ReadArr[2 + promptNameArr.length] = (byte) 0xe07; // Program request (Command Type)
            _startCollectDateTag = System.currentTimeMillis();
            _startCollect = true;
            _currentCommand = commandType.WRITE_CMD;
            SendData(ReadArr);
        }

        return true;
    }

    public boolean SendData(byte[] dataArr_prm)
    {
        _allowReceiveFlg = false;
        _dataBuffer.clear();
        _allowReceiveFlg = true;
        sendDataToPairedDevice(dataArr_prm);
        return false;
    }

    @Override
    protected void LostConnection(String erroMessage)
    {
        _consumer.OnErrorCb("Lost Connection with paired device");
    }

    private String AddressPrepare(String my_addr, String unit_addr, boolean unit_typ)
    {
        return "";
    }

    @Override
    protected void DataProcess(byte[] dataArr_prm)
    {
        try {
            String pulsPromptName;

            if(_allowReceiveFlg)
            {
                switch(_currentCommand) {
                    case GET_RSNT_VERSION:
                        _rsntVersion = Integer.toString(dataArr_prm[4]) + "." + String.format("%02d", dataArr_prm[5]);
                        _currentCommand = commandType.NONE;
                        break;

                    case RSNT_READ_INIT1:

                        if(dataArr_prm.length == 3)
                        {
                            if(dataArr_prm[0] == 2 && dataArr_prm[1] == 1 && dataArr_prm[2] == 6)
                            {
                                // Acc
                                Log.d("", "ack");
                                SetFrequency();
                            }
                        }

                        break;
                    case RSNT_ISM_ON:
                    {
                        byte[] arr = new byte[4];
                        arr[0] = 2;
                        arr[1] = 2;
                        arr[2] = 82;
                        arr[3] = 45;
                        _currentCommand = commandType.RSNT_VHF_OFF;
                        StartCollectData(true, false);
                        SendData(arr);
                    }

                        break;


                    case RSNT_ISM_OFF:
                    {
                        byte[] arr = new byte[4];

                        arr[0] = 2;
                        arr[1] = 2;
                        arr[2] = 82;
                        arr[3] = 46;
                        _currentCommand = commandType.RSNT_VHF_ON;
                        StartCollectData(true, false);
                        SendData(arr);
                    }
                        break;

                    case  RSNT_VHF_ON:
                        SetFrequency();

                        break;

                    case RSNT_READ_INIT2:
                        if(dataArr_prm.length == 3)
                        {
                            if (dataArr_prm[0] == 2 && dataArr_prm[1] == 1 && dataArr_prm[2] == 6)
                            {
                                // Acc
                                Log.d("", "ack");
                                SetFrequency();
                            }
                        }
                        break;

                    case RSNT_READ_DIS_INIT1:

                        if(dataArr_prm.length == 3)
                        {
                            if(dataArr_prm[0] == 2 && dataArr_prm[1] == 1 && dataArr_prm[2] == 6)
                            {
                                // Acc
                                Log.d("", "ack");
                            }
                        }
                        break;

                    case RSNT_READ_DIS_INIT2:
                        if(dataArr_prm.length == 3)
                        {
                            if(dataArr_prm[0] == 2 && dataArr_prm[1] == 1 && dataArr_prm[2] == 6)
                            {
                                // Acc
                                Log.d("", "ack");
                            }
                        }
                        break;

                    case RST_SET_FREQ:
                        if(dataArr_prm.length == 3)
                        {
                            if(dataArr_prm[0] == 2 && dataArr_prm[1] == 1 && dataArr_prm[2] == 6)
                            {
                                // Acc
                                //SetReadMetersRSNT(true);
                            }
                        }
                        break;

                    case SET_RF_FILTER:
                        if(dataArr_prm[0] == 2 && dataArr_prm[1] == 1 && dataArr_prm[2] == 6)
                        {
                            // Acc
                            _readMeterConsumer.OnFilterSet(true);
                        }
                        else
                            _readMeterConsumer.OnFilterSet(false);

                        break;

                    case COLLECT_READS:
                        ReadMeters(true);
                        break;

                    case SCAN_LISTENING_MCS:
                        ReadMeters(true);
                        break;

                    case CLEAR_MC_HISTORY:
                        ReadMeters(true);
                        break;

                    case READ_METER:
                        _readMeterConsumer.OnReadMeters(dataArr_prm);
                        break;

                    case PROMPT:
                        try {
                            if(_promtType == ePromptType.PAIRING || _promtType == ePromptType.TEN_CHR_PAIRING)
                            {
                                _startPrompt = false;
                                _isPair = true;
                                String addr1, addr2, addr3, tmpAddr;
                                if(_promtType == ePromptType.PAIRING)
                                {
                                    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                                    // Verify my address

                                    // Convert to hex string each byte and combine bytes to address
                                    addr1 = Utilities.StringCompleter(Integer.toHexString((int) dataArr_prm[2 + _promptName.length()] & 0xff), 2, "0", true);
                                    addr2 = Utilities.StringCompleter(Integer.toHexString((int) dataArr_prm[3 + _promptName.length()] & 0xff), 2, "0", true);
                                    tmpAddr = addr1 + addr2;

                                    if(!_myAddress.equals(Utilities.StringCompleter(Integer.decode(tmpAddr).toString(), 5, "0", true)))
                                        _consumer.OnErrorCb("Pair meganet address is not valid! Refuse.");

                                    // Get unit address
                                    // Convert to hex string each byte and combine bytes to address

                                    addr1 = Utilities.StringCompleter(Integer.toHexString((int) dataArr_prm[4 + _promptName.length()] & 0xff), 2, "0", true);
                                    addr2 = Utilities.StringCompleter(Integer.toHexString((int) dataArr_prm[5 + _promptName.length()] & 0xff), 2, "0", true);
                                    tmpAddr = addr1 + addr2;
                                    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                                }
                                else
                                {
                                    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                                    // Convert to hex string each byte and combine bytes to address
                                    addr1 = Utilities.StringCompleter(Integer.toHexString((int) dataArr_prm[2 + _promptName.length()] & 0xff), 2, "0", true);

                                    tmpAddr = addr1;
                                    if(!_myAddress.equals(Utilities.StringCompleter(Integer.decode(tmpAddr).toString(), 5, "0", true)))
                                        _consumer.OnErrorCb("Pair meganet address is not valid! Refuse.");

                                    // Get unit address
                                    // Convert to hex string each byte and combine bytes to address
                                    addr1 = Utilities.StringCompleter(Integer.toHexString((int) dataArr_prm[3 + _promptName.length()] & 0xff), 2, "0", true);
                                    addr2 = Utilities.StringCompleter(Integer.toHexString((int) dataArr_prm[4 + _promptName.length()] & 0xff), 2, "0", true);
                                    addr3 = Utilities.StringCompleter(Integer.toHexString((int) dataArr_prm[5 + _promptName.length()] & 0xff), 2, "0", true);
                                    tmpAddr = addr1 + addr2 + addr3; // deviceName.substring(deviceName.length()-2, deviceName.length());
                                    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                                }
                                _unitAddress = Utilities.StringCompleter(String.valueOf(Integer.parseInt("FEDCC", 16)), 7, "0", true);
                                _unitAddress = Utilities.StringCompleter(String.valueOf(Integer.parseInt(tmpAddr, 16)), 7, "0", true);
                                //_myAddress = _unitAddress;

                                byte[] tmpPromptArr = new byte[dataArr_prm.length - 6 - _promptName.length()];
                                for(int i = 0; i < dataArr_prm.length - 6 - _promptName.length(); i++)
                                {
                                    tmpPromptArr[i] = dataArr_prm[i + 6 + _promptName.length()];
                                }

                                // Get Name and version
                                String deviceName = new String(tmpPromptArr); // tmpDeviceName.substring(0, tmpDeviceName.length()-3);

                                // Get NDevice
                                _ndevice = deviceName.substring(deviceName.length()-2, deviceName.length());
                                _deviceVersion = deviceName.substring(0, deviceName.length() - 2);

                                StartCollectData(true, false);
                                _consumer.PairData(deviceName.substring(0, deviceName.length()), _ndevice, false);
                            }
                            else
                            {
                                _startPrompt = false;
                                _isPair = false;
                                _currentCommand = commandType.NONE;

                                byte[] tmpPromptArr = new byte[dataArr_prm.length - 2 - _promptName.length()];
                                for(int i = 0; i < dataArr_prm.length - 2 - _promptName.length(); i++)
                                {
                                    tmpPromptArr[i] = dataArr_prm[i + 2 + _promptName.length()];
                                }

                                // Get Name and version
                                String deviceName = new String(tmpPromptArr); // tmpDeviceName.substring(0, tmpDeviceName.length()-3);
                                _consumer.PairData(deviceName.substring(0, deviceName.length()), _ndevice, true);
                                // Get NDevice
                                _ndevice = deviceName.substring(deviceName.length()-2, deviceName.length());
                                //_deviceVersion = deviceName.substring(0, deviceName.length() - 2);

                                _currentCommand = commandType.READ;
                                StartCollectData(true, false);

                                byte[] promptArr;
                                byte[] promptNameArr = _promptName.getBytes();

                                promptArr = new byte[2 + _promptName.length() + 1];
                                promptArr[0] = (byte) 0xe02;
                                promptArr[1] = (byte)(promptNameArr.length + 1);
                                for(int i = 0; i < promptNameArr.length; i++)
                                {
                                    promptArr[i+2] = promptNameArr[i];
                                }
                                promptArr[2 + promptNameArr.length] = (byte) 0xe01;
                                SendData(promptArr);
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } ;
                        break;

                    case PROMPT_SYNC:
                        try {
                            if(_promtType == ePromptType.PAIRING || _promtType == ePromptType.TEN_CHR_PAIRING) {
                                _startPrompt = false;
                                _isPair = true;
                                String addr1, addr2, addr3, tmpAddr;

                                if(_promtType == ePromptType.PAIRING) {
                                    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                                    // Verify my address

                                    // Convert to hex string each byte and combine bytes to address
                                    addr1 = Utilities.StringCompleter(Integer.toHexString((int) dataArr_prm[2 + _promptName.length()] & 0xff), 2, "0", true);
                                    addr2 = Utilities.StringCompleter(Integer.toHexString((int) dataArr_prm[3 + _promptName.length()] & 0xff), 2, "0", true);
                                    tmpAddr = addr1 + addr2;

                                    if(!_myAddress.equals(Utilities.StringCompleter(Integer.decode(tmpAddr).toString(), 5, "0", true)))
                                        _consumer.OnErrorCb("Pair meganet address is not valid! Refuse.");

                                    // Get unit address
                                    // Convert to hex string each byte and combine bytes to address

                                    addr1 = Utilities.StringCompleter(Integer.toHexString((int) dataArr_prm[4 + _promptName.length()] & 0xff), 2, "0", true);
                                    addr2 = Utilities.StringCompleter(Integer.toHexString((int) dataArr_prm[5 + _promptName.length()] & 0xff), 2, "0", true);
                                    tmpAddr = addr1 + addr2;
                                    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                                    //_unitAddress = Utilities.StringCompleter(String.valueOf(Integer.parseInt(tmpAddr, 16)), 7, "0", true);

                                } else {
                                    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                                    // Verify my address

                                    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                                    // Convert to hex string each byte and combine bytes to address
                                    addr1 = Utilities.StringCompleter(Integer.toHexString((int) dataArr_prm[2 + _promptName.length()] & 0xff), 2, "0", true);

                                    tmpAddr = addr1;
                                    if (!_myAddress.equals(Utilities.StringCompleter(Integer.decode(tmpAddr).toString(), 5, "0", true)))
                                        _pulseConsumer.OnMessageCb("Pair meganet address is not valid! Refuse.");

                                    // Get unit address
                                    // Convert to hex string each byte and combine bytes to address
                                    addr1 = Utilities.StringCompleter(Integer.toHexString((int) dataArr_prm[3 + _promptName.length()] & 0xff), 2, "0", true);
                                    addr2 = Utilities.StringCompleter(Integer.toHexString((int) dataArr_prm[4 + _promptName.length()] & 0xff), 2, "0", true);
                                    addr3 = Utilities.StringCompleter(Integer.toHexString((int) dataArr_prm[5 + _promptName.length()] & 0xff), 2, "0", true);
                                    tmpAddr = addr1 + addr2 + addr3; // deviceName.substring(deviceName.length()-2, deviceName.length());
                                    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                                }

                                //_unitAddress = Utilities.StringCompleter(String.valueOf(Integer.parseInt("FEDCC", 16)), 7, "0", true);
                                _unitAddress = Utilities.StringCompleter(String.valueOf(Integer.parseInt(tmpAddr, 16)), 7, "0", true);
                                //_myAddress = _unitAddress;

                                byte[] tmpPromptArr = new byte[dataArr_prm.length - 6 - _promptName.length()];
                                for(int i = 0; i < dataArr_prm.length - 6 - _promptName.length(); i++)
                                {
                                    tmpPromptArr[i] = dataArr_prm[i + 6 + _promptName.length()];
                                }

                                // Get Name and version
                                String deviceName = new String(tmpPromptArr); // tmpDeviceName.substring(0, tmpDeviceName.length()-3);

                                // Get NDevice
                                _ndevice = deviceName.substring(deviceName.length()-2, deviceName.length());
                                _deviceVersion = deviceName.substring(0, deviceName.length() - 2);

                                StartCollectData(true, false);
                                _pulseConsumer.PairData(deviceName.substring(0, deviceName.length()), _ndevice, false);

                                _currentCommand = commandType.NEW_PULSE_READ;
                                StartCollectData(true, false);

                                byte[] promptArr;
                                byte[] promptNameArr = _promptName.getBytes();

                                //promptArr = new byte[2 + _promptName.length() + 2];
                                promptArr = new byte[2 + _promptName.length() + 1];
                                promptArr[0] = (byte) 0xe02;
                                promptArr[1] = (byte)(promptNameArr.length + 1);
                                for(int i = 0; i < promptNameArr.length; i++)
                                {
                                    promptArr[i+2] = promptNameArr[i];
                                }

                                if (programm_type_id == 1)
                                    promptArr[2 + promptNameArr.length] = (byte) 0xe50;
                                else if (programm_type_id == 2 || programm_type_id == 4)
                                    promptArr[2 + promptNameArr.length] = (byte) 0xe4D;
                                else if(programm_type_id == 44 || programm_type_id == 22)
                                    promptArr[2 + promptNameArr.length] = (byte) 0xe45;
                                else
                                    promptArr[2 + promptNameArr.length] = (byte) 0xe57;

                                //promptArr[2 + promptNameArr.length] = (byte) 0xe42;
                                if(new_pulse_port ==1)
                                    promptArr[3 + promptNameArr.length] = (byte) 0xe00;
                                else
                                    promptArr[3 + promptNameArr.length] = (byte) 0xe01;

                                SendData(promptArr);
                            }
                        }
                        catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        break;
                    case READ: // changed at 13.9.2020
                        _unitDataArr = null;
                        _unitDataArr = dataArr_prm;
                        _unitParams = _dataAnalazer.AnalizeData(dataArr_prm, _ndevice, _promptName.length(), _isPair); // check here!!
                        android.os.SystemClock.sleep(500);
                        _consumer.SetReadData(_unitParams); // check here!!
                        _currentCommand = commandType.NONE;
                        _msgArr = null;
                        break;

                    case TIME_REQUEST:
                        _startCollectDateTag = System.currentTimeMillis();
                        StartCollectData(true, false);
                        _consumer.GetTime(dataArr_prm);
                        break;

                    case GET_LOG:
                        _startCollectDateTag = System.currentTimeMillis();
                        StartCollectData(true, false);
                        _consumer.ReadLog(dataArr_prm);
                        break;

                    case REQ_LOG:
                        _startCollectDateTag = System.currentTimeMillis();
                        StartCollectData(true, false);
                        _consumer.ReadLog(dataArr_prm);
                        break;

                    case GET_METER_SN: {
                        Long meterSN =
                                Long.parseLong(Utilities.StringCompleter(Integer.toHexString((int) dataArr_prm[7] & 0xff), 2, "0", true) +
                                        Utilities.StringCompleter(Integer.toHexString((int) dataArr_prm[8] & 0xff), 2, "0", true) +
                                        Utilities.StringCompleter(Integer.toHexString((int) dataArr_prm[9] & 0xff), 2, "0", true) +
                                        Utilities.StringCompleter(Integer.toHexString((int) dataArr_prm[10] & 0xff), 2, "0", true) +
                                        Utilities.StringCompleter(Integer.toHexString((int) dataArr_prm[11] & 0xff), 2, "0", true), 16);


                        Long reading =
                                Long.parseLong(Utilities.StringCompleter(Integer.toHexString((int) dataArr_prm[12] & 0xff), 2, "0", true) +
                                        Utilities.StringCompleter(Integer.toHexString((int) dataArr_prm[13] & 0xff), 2, "0", true) +
                                        Utilities.StringCompleter(Integer.toHexString((int) dataArr_prm[14] & 0xff), 2, "0", true) +
                                        Utilities.StringCompleter(Integer.toHexString((int) dataArr_prm[15] & 0xff), 2, "0", true) +
                                        Utilities.StringCompleter(Integer.toHexString((int) dataArr_prm[16] & 0xff), 2, "0", true), 16);

                        _startCollectDateTag = System.currentTimeMillis();
                        StartCollectData(true, false);
                        _consumer.OnRead(meterSN.toString(), reading.toString());
                    }
                        break;

                    case WRITE_CMD:
                        if((dataArr_prm[0] == 2 && dataArr_prm[1] == _promptName.length()+1 && dataArr_prm[_promptName.length()+2] == 6) || (dataArr_prm[0] == 2 && dataArr_prm[1] == _promptName.length()+5 && dataArr_prm[_promptName.length()+6] == 6))
                        {
                            // Acc
                            _currentCommand = commandType.WRITE_RESULT;
                            StartCollectData(true, false);
                            SendData(_userDataArr);
                        }

                        break;
                    case RDM:
                        if((dataArr_prm[0] == 2 && dataArr_prm[1] == _promptName.length()+1 && dataArr_prm[_promptName.length()+2] == 6) || (dataArr_prm[0] == 2 && dataArr_prm[1] == _promptName.length()+5 && dataArr_prm[_promptName.length()+6] == 6))
                        {
                            // Acc
                            _currentCommand = commandType.RDM_RECEIVE;
                            StartCollectData(true, true);
                        }

                        break;
                    case RDM_OPEN:
                        android.os.SystemClock.sleep(500);
                        if(dataArr_prm[0] == 2 && dataArr_prm[1] == 1 && dataArr_prm[2] == 3) // if Tamper
                        {
                            _currentCommand = commandType.NONE;
                            _consumer.OnProgramm(true, "RDM Tamper");
                        }

                        else if((dataArr_prm[0] == 2 && dataArr_prm[1] == 1 && dataArr_prm[2] == 1) || (dataArr_prm[0] == 2 && dataArr_prm[1] == 1 && dataArr_prm[2] == 6))
                        {
                            // Acc
                            if(dataArr_prm[0] == 2 && dataArr_prm[1] == 1 && dataArr_prm[2] == 1)
                            {
                                _currentCommand = commandType.NONE;
                                _consumer.OnProgramm(true, "Open");
                            }
                        }
                        else
                        {
                            _currentCommand = commandType.NONE;
                           _consumer.OnProgramm(false, "Open Failed");
                        }

                        break;
                    case RDM_CLOSE:
                        android.os.SystemClock.sleep(500);
                        if(dataArr_prm[0] == 2 && dataArr_prm[1] == 1 && dataArr_prm[2] == 3) // if Tamper
                        {
                            _currentCommand = commandType.NONE;
                            _consumer.OnProgramm(true, "RDM Tamper");
                        }

                        else if((dataArr_prm[0] == 2 && dataArr_prm[1] == 1 && dataArr_prm[2] == 2) || (dataArr_prm[0] == 2 && dataArr_prm[1] == 1 && dataArr_prm[2] == 6))
                        {
                            // Acc
                            if(dataArr_prm[0] == 2 && dataArr_prm[1] == 1 && dataArr_prm[2] == 2)
                            {
                                _currentCommand = commandType.NONE;
                                _consumer.OnProgramm(true, "Close");
                            }
                        }
                        else
                        {
                            _currentCommand = commandType.NONE;
                            _consumer.OnProgramm(false, "Close Failed");
                        }

                        break;
                    case METER_POWER_OFF:
                        if((dataArr_prm[0] == 2 && dataArr_prm[1] == _promptName.length()+1 && dataArr_prm[_promptName.length()+2] == 6) || (dataArr_prm[0] == 2 && dataArr_prm[1] == _promptName.length()+5 && dataArr_prm[_promptName.length()+6] == 6))
                        {
                            // Acc
                            _currentCommand = commandType.NONE;
                            _consumer.OnPowerOff(true, "Power Off Success");
                        }

                        break;

                    case METER_SLEEP:
                        if((dataArr_prm[0] == 2 && dataArr_prm[1] == _promptName.length()+1 && dataArr_prm[_promptName.length()+2] == 6) || (dataArr_prm[0] == 2 && dataArr_prm[1] == _promptName.length()+5 && dataArr_prm[_promptName.length()+6] == 6))
                        {
                            // Acc
                            _currentCommand = commandType.NONE;
                            _consumer.OnSleep(true, "Sleep Success");
                        }

                        break;

                    case WRITE_RESULT:
                        if((dataArr_prm[0] == 2 && dataArr_prm[1] == _promptName.length()+1 && dataArr_prm[_promptName.length()+2] == 6) || (dataArr_prm[0] == 2 && dataArr_prm[1] == _promptName.length()+5 && dataArr_prm[_promptName.length()+6] == 6))
                        {
                            if(_isPair)
                            {
                                // Acc
                                byte[] ReadArr;
                                byte[] promptNameArr = _promptName.getBytes();

                                ReadArr = new byte[2 + _promptName.length() + 5]; // 1,2 начало и размерб 3... промпт имя. 2 байта FF-FF адрес юнита. 2 байта мой адрес 00-01 и команда 8 - промпт. И тго 2+размер промпта + 5
                                ReadArr[0] = (byte) 0xe02;
                                ReadArr[1] = (byte)(promptNameArr.length + 5);
                                for(int i = 0; i < promptNameArr.length; i++)
                                {
                                    ReadArr[i+2] = promptNameArr[i];
                                }

                                String hexAddress;
                                if(_is_extended)
                                {
                                    hexAddress = Integer.toHexString(Integer.parseInt(_unitAddress));
                                    hexAddress = Utilities.StringCompleter(hexAddress, 6, "0", true);
                                    ReadArr[2 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(0, 2), 16);
                                    ReadArr[3 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(2, 4), 16);
                                    ReadArr[4 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(4, 6), 16);
                                    /////////////////
                                    hexAddress = Integer.toHexString(Integer.parseInt(_myAddress));
                                    hexAddress = Utilities.StringCompleter(hexAddress, 2, "0", true);

                                    ReadArr[5 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(0, 2), 16);
                                }
                                else
                                {
                                    hexAddress = Integer.toHexString(Integer.parseInt(_unitAddress));
                                    hexAddress = Utilities.StringCompleter(hexAddress, 4, "0", true);
                                    ReadArr[2 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(0, 2), 16);
                                    ReadArr[3 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(2, 4), 16);
                                    /////////////////
                                    hexAddress = Integer.toHexString(Integer.parseInt(_myAddress));
                                    hexAddress = Utilities.StringCompleter(hexAddress, 4, "0", true);

                                    ReadArr[4 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(0, 2), 16);
                                    ReadArr[5 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(2, 4), 16);
                                }

                                ReadArr[6 + promptNameArr.length] = (byte) 0xe01;

                                _startCollectDateTag = System.currentTimeMillis();
                                _startCollect = true;
                                _currentCommand = commandType.READ_AFTER_WRITE;
                                SendData(ReadArr);
                            }
                            else
                            {
                                byte[] promptArr;
                                byte[] promptNameArr = _promptName.getBytes();

                                promptArr = new byte[2 + _promptName.length() + 1];
                                promptArr[0] = (byte) 0xe02;
                                promptArr[1] = (byte)(promptNameArr.length + 1);
                                for(int i = 0; i < promptNameArr.length; i++)
                                {
                                    promptArr[i+2] = promptNameArr[i];
                                }
                                promptArr[2 + promptNameArr.length] = (byte) 0xe01;
                                _startCollectDateTag = System.currentTimeMillis();
                                _startCollect = true;
                                _currentCommand = commandType.READ_AFTER_WRITE;
                                SendData(promptArr);
                            }

                        }
                        else
                        {
                            _consumer.OnProgramm(false, "Programming failed");
                        }
                        break;

                    case READ_AFTER_WRITE:
                    {
                        _currentCommand = commandType.NONE;
                        if(_userDataArr.length != dataArr_prm.length)
                        {
                            _consumer.OnProgramm(false, "Verif write data fail");
                            return;
                        }

                        for(int i = 0; i < dataArr_prm.length; i++)
                        {
                            if(_userDataArr[i] != dataArr_prm[i])
                            {
                                _consumer.OnProgramm(false, "Verif write data fail");
                                return;
                            }
                        }
                        _consumer.OnProgramm(true, "");
                    }
                        break;

                    case NEW_PULSE_READ: {
                        if (dataArr_prm.length != 17) {
                            _currentCommand = commandType.NONE;
                            _pulseConsumer.OnErrorRead("Error read meter data");
                            break;
                        }
                        pulsPromptName = "KPMTWPN";
                        Long meterSN =
                                Long.parseLong(Utilities.StringCompleter(Integer.toHexString((int) dataArr_prm[7 ] & 0xff), 2, "0", true) +
                                Utilities.StringCompleter(Integer.toHexString((int) dataArr_prm[8 ] & 0xff), 2, "0", true) +
                                Utilities.StringCompleter(Integer.toHexString((int) dataArr_prm[9 ] & 0xff), 2, "0", true) +
                                        Utilities.StringCompleter(Integer.toHexString((int) dataArr_prm[10 ] & 0xff), 2, "0", true) +
                                Utilities.StringCompleter(Integer.toHexString((int) dataArr_prm[11 ] & 0xff), 2, "0", true), 16);


                        Long reading  =
                                Long.parseLong(Utilities.StringCompleter(Integer.toHexString((int) dataArr_prm[12 ] & 0xff), 2, "0", true) +
                                        Utilities.StringCompleter(Integer.toHexString((int) dataArr_prm[13 ] & 0xff), 2, "0", true) +
                                        Utilities.StringCompleter(Integer.toHexString((int) dataArr_prm[14 ] & 0xff), 2, "0", true) +
                                        Utilities.StringCompleter(Integer.toHexString((int) dataArr_prm[15 ] & 0xff), 2, "0", true) +
                                        Utilities.StringCompleter(Integer.toHexString((int) dataArr_prm[16 ] & 0xff), 2, "0", true), 16);

                        _currentCommand = commandType.NONE;

                        _pulseConsumer.OnRead(meterSN.toString(), reading.toString());
                    }
                        break;

                    case PULSE_READ: {
                        if (dataArr_prm.length != 18) {
                            _currentCommand = commandType.NONE;
                            _pulseConsumer.OnErrorRead("Error read meter data");
                            break;
                        }
                        pulsPromptName = "KPMTWPN";
                        Long meterSN = Long.parseLong(Utilities.StringCompleter(Integer.toHexString((int) dataArr_prm[2 + pulsPromptName.length()] & 0xff), 2, "0", true) +
                                Utilities.StringCompleter(Integer.toHexString((int) dataArr_prm[3 + pulsPromptName.length()] & 0xff), 2, "0", true) +
                                Utilities.StringCompleter(Integer.toHexString((int) dataArr_prm[4 + pulsPromptName.length()] & 0xff), 2, "0", true) +
                                Utilities.StringCompleter(Integer.toHexString((int) dataArr_prm[5 + pulsPromptName.length()] & 0xff), 2, "0", true), 16);


                        Long reading = Long.parseLong(Utilities.StringCompleter(Integer.toHexString((int) dataArr_prm[6 + pulsPromptName.length()] & 0xff), 2, "0", true) +
                                Utilities.StringCompleter(Integer.toHexString((int) dataArr_prm[7 + pulsPromptName.length()] & 0xff), 2, "0", true) +
                                Utilities.StringCompleter(Integer.toHexString((int) dataArr_prm[8 + pulsPromptName.length()] & 0xff), 2, "0", true) +
                                Utilities.StringCompleter(Integer.toHexString((int) dataArr_prm[9 + pulsPromptName.length()] & 0xff), 2, "0", true), 16);
                        _currentCommand = commandType.NONE;

                        _pulseConsumer.OnRead(meterSN.toString(), reading.toString());
                    }
                        break;
                    case PULSE_WRITE_REQ:
                        pulsPromptName = "KPMTWPN";
                        if(pulsPromptName.length() + 2 != dataArr_prm.length)
                        {
                            if(dataArr_prm[pulsPromptName.length()+2] == 6)
                            {
                                String hexstrSn = Utilities.StringCompleter(Integer.toHexString(global_port_sn),8, "0", true);
                                String hexstrRead = Utilities.StringCompleter(Integer.toHexString(global_port_read),8, "0", true);

                                byte[] promptNameArr = pulsPromptName.getBytes();
                                byte [] PulseArr;
                                PulseArr = new byte[2 + promptNameArr.length + 1 + 8 + 1];
                                PulseArr[0] = (byte) 0xe02;
                                PulseArr[1] = (byte)(promptNameArr.length + 1 + 8);

                                for(int i = 0; i < promptNameArr.length; i++)
                                {
                                    PulseArr[i+2] = promptNameArr[i];
                                }

                                PulseArr[2 + promptNameArr.length] = (byte)Integer.parseInt(hexstrSn.substring(0, 2), 16);
                                PulseArr[3 + promptNameArr.length] = (byte)Integer.parseInt(hexstrSn.substring(2, 4), 16);
                                PulseArr[4 + promptNameArr.length] = (byte)Integer.parseInt(hexstrSn.substring(4, 6), 16);
                                PulseArr[5 + promptNameArr.length] = (byte)Integer.parseInt(hexstrSn.substring(6, 8), 16);

                                PulseArr[6 + promptNameArr.length] = (byte)Integer.parseInt(hexstrRead.substring(0, 2), 16);
                                PulseArr[7 + promptNameArr.length] = (byte)Integer.parseInt(hexstrRead.substring(2, 4), 16);
                                PulseArr[8 + promptNameArr.length] = (byte)Integer.parseInt(hexstrRead.substring(4, 6), 16);
                                PulseArr[9 + promptNameArr.length] = (byte)Integer.parseInt(hexstrRead.substring(6, 8), 16);
                                byte[] calcChSum = new byte[PulseArr.length - 1 + promptNameArr.length ];
                                int idx = 0;
                                for(int i =2 + promptNameArr.length; i <  PulseArr.length; i++)
                                {
                                    calcChSum[idx] = PulseArr[i];
                                    idx++;
                                }
                                PulseArr[10 + promptNameArr.length] = CheckSum(calcChSum);



                                _startCollectDateTag = System.currentTimeMillis();
                                _startCollect = true;
                                _currentCommand = commandType.PULSE_WRITE;
                                SendData(PulseArr);
                            }
                        }
                        break;

                    case PULSE_WRITE:
                        pulsPromptName = "KPMTWPN";
                        if((dataArr_prm[0] == 2 && dataArr_prm[1] == pulsPromptName.length()+1 && dataArr_prm[pulsPromptName.length()+2] == 6))
                        {
                           _pulseConsumer.OnWriteResult(true);
                        }
                        else
                            _pulseConsumer.OnWriteResult(false);
                        break;

                    default:
                        break;
                }
            }
        }catch( Exception ex)
        {
            ex.printStackTrace();
        }
    }

    protected void Disconnect()
    {
        byte[] ReadArr;
        if (_allowReceiveFlg) {
            // Get device data by _ndevice - device - _deviceVersion - virsion
            byte[] promptNameArr = _promptName.getBytes();
            ReadArr = new byte[2 + _promptName.length() + 5];
            // 1 - stx
            // 2 - length
            // 3 - type MT
            // 4,5,6 - unit address
            // 7 - my address
            // 8 - type command
            ReadArr[0] = (byte) 0xe02; // Start sign
            ReadArr[1] = (byte) 0xe06; // Length of message
            for (int i = 0; i < promptNameArr.length; i++) {
                ReadArr[i + 2] = promptNameArr[i];
            }

            String hexAddress;
            if(_is_extended)
            {
                hexAddress = Integer.toHexString(Integer.parseInt(_unitAddress));
                hexAddress = Utilities.StringCompleter(hexAddress, 6, "0", true);
                ReadArr[2 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(0, 2), 16);
                ReadArr[3 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(2, 4), 16);
                ReadArr[4 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(4, 6), 16);
                /////////////////
                hexAddress = Integer.toHexString(Integer.parseInt(_myAddress));
                hexAddress = Utilities.StringCompleter(hexAddress, 2, "0", true);

                ReadArr[5 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(0, 2), 16);
            }
            else
            {
                hexAddress = Integer.toHexString(Integer.parseInt(_unitAddress));
                hexAddress = Utilities.StringCompleter(hexAddress, 4, "0", true);
                ReadArr[2 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(0, 2), 16);
                ReadArr[3 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(2, 4), 16);
                /////////////////
                hexAddress = Integer.toHexString(Integer.parseInt(_myAddress));
                hexAddress = Utilities.StringCompleter(hexAddress, 4, "0", true);

                ReadArr[4 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(0, 2), 16);
                ReadArr[5 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(2, 4), 16);
            }
            android.os.SystemClock.sleep(500);
            ReadArr[6 + promptNameArr.length] = (byte) 0xe97; // Disconnect command

            _startCollect = false;
            _currentCommand = commandType.NONE;
            SendData(ReadArr);
        }
    }

    protected void TimeRequest() {
        byte[] ReadArr;
        if (_allowReceiveFlg) {
            // Get device data by _ndevice - device - _deviceVersion - virsion
            byte[] promptNameArr = _promptName.getBytes();
            ReadArr = new byte[2 + _promptName.length() + 5];
            // 1 - stx
            // 2 - length
            // 3 - type MT
            // 4,5,6 - unit address
            // 7 - my address
            // 8 - type command
            ReadArr[0] = (byte) 0xe02; // Start sign
            ReadArr[1] = (byte) 0xe06; // Length of message
            for (int i = 0; i < promptNameArr.length; i++) {
                ReadArr[i + 2] = promptNameArr[i];
            }

            String hexAddress;
            if(_is_extended)
            {
                hexAddress = Integer.toHexString(Integer.parseInt(_unitAddress));
                hexAddress = Utilities.StringCompleter(hexAddress, 6, "0", true);
                ReadArr[2 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(0, 2), 16);
                ReadArr[3 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(2, 4), 16);
                ReadArr[4 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(4, 6), 16);
                /////////////////
                hexAddress = Integer.toHexString(Integer.parseInt(_myAddress));
                hexAddress = Utilities.StringCompleter(hexAddress, 2, "0", true);

                ReadArr[5 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(0, 2), 16);
            }
            else
            {
                hexAddress = Integer.toHexString(Integer.parseInt(_unitAddress));
                hexAddress = Utilities.StringCompleter(hexAddress, 4, "0", true);
                ReadArr[2 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(0, 2), 16);
                ReadArr[3 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(2, 4), 16);
                /////////////////
                hexAddress = Integer.toHexString(Integer.parseInt(_myAddress));
                hexAddress = Utilities.StringCompleter(hexAddress, 4, "0", true);

                ReadArr[4 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(0, 2), 16);
                ReadArr[5 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(2, 4), 16);
            }
            android.os.SystemClock.sleep(500);
            ReadArr[6 + promptNameArr.length] = (byte) 0xe11; // Time request command\

            _startCollectDateTag = System.currentTimeMillis();
            StartCollectData(true,true);
            _currentCommand = commandType.TIME_REQUEST;
            _msgArr = ReadArr;
            SendData(ReadArr);
        }
    }


    protected void GetMeterSN(int input_num) {
        byte[] ReadArr;
        if (_allowReceiveFlg) {

            // Get device data by _ndevice - device - _deviceVersion - virsion

            byte[] promptNameArr = _promptName.getBytes();

            ReadArr = new byte[2 + _promptName.length() + 6]; // 1,2 начало и размерб 3... промпт имя. 2 байта FF-FF адрес юнита. 2 байта мой адрес 00-01 и команда 8 - промпт. И тго 2+размер промпта + 5
            ReadArr[0] = (byte) 0xe02;
            ReadArr[1] = (byte) (promptNameArr.length + 6);
            for (int i = 0; i < promptNameArr.length; i++) {
                ReadArr[i + 2] = promptNameArr[i];
            }

            String hexAddress;
            if (_is_extended) {
                hexAddress = Integer.toHexString(Integer.parseInt(_unitAddress));
                hexAddress = Utilities.StringCompleter(hexAddress, 6, "0", true);
                ReadArr[2 + promptNameArr.length] = (byte) Integer.parseInt(hexAddress.substring(0, 2), 16);
                ReadArr[3 + promptNameArr.length] = (byte) Integer.parseInt(hexAddress.substring(2, 4), 16);
                ReadArr[4 + promptNameArr.length] = (byte) Integer.parseInt(hexAddress.substring(4, 6), 16);
                /////////////////
                hexAddress = Integer.toHexString(Integer.parseInt(_myAddress));
                hexAddress = Utilities.StringCompleter(hexAddress, 2, "0", true);

                ReadArr[5 + promptNameArr.length] = (byte) Integer.parseInt(hexAddress.substring(0, 2), 16);
            } else {
                hexAddress = Integer.toHexString(Integer.parseInt(_unitAddress));
                hexAddress = Utilities.StringCompleter(hexAddress, 4, "0", true);
                ReadArr[2 + promptNameArr.length] = (byte) Integer.parseInt(hexAddress.substring(0, 2), 16);
                ReadArr[3 + promptNameArr.length] = (byte) Integer.parseInt(hexAddress.substring(2, 4), 16);
                /////////////////
                hexAddress = Integer.toHexString(Integer.parseInt(_myAddress));
                hexAddress = Utilities.StringCompleter(hexAddress, 4, "0", true);

                ReadArr[4 + promptNameArr.length] = (byte) Integer.parseInt(hexAddress.substring(0, 2), 16);
                ReadArr[5 + promptNameArr.length] = (byte) Integer.parseInt(hexAddress.substring(2, 4), 16);
            }

            android.os.SystemClock.sleep(500);
            ReadArr[6 + promptNameArr.length] = (byte) 0xe42; // Read Meter SN
            ReadArr[7 + promptNameArr.length] = (byte) input_num;

            _startCollectDateTag = System.currentTimeMillis();
            StartCollectData(true,false);
            _currentCommand = commandType.GET_METER_SN;
            _msgArr = ReadArr;
            SendData(ReadArr);
        }
    }


    protected void GetLog(int input_num, int start, int len, boolean request)
    {
        byte[] ReadArr;
        if(_allowReceiveFlg) {
            // Get device data by _ndevice - device - _deviceVersion - virsion
            byte[] promptNameArr = _promptName.getBytes();
            ReadArr = new byte[2 + _promptName.length() + 10]; // 1,2 начало и размерб 3... промпт имя. 2 байта FF-FF адрес юнита. 2 байта мой адрес 00-01 и команда 8 - промпт. И тго 2+размер промпта + 5
                // 1 - stx
                // 2 - length
                // 3 - type MT
                // 4,5,6 - unit address
                // 7 - my address
                // 8 - type command
                // 9 - input number (00/01)
                // 10,11 - start index
                // 12, 13 - length of log
                //
            ReadArr[0] = (byte) 0xe02; // Start sign
            ReadArr[1] = (byte) 0xe0B; // Length of message
            for(int i = 0; i < promptNameArr.length; i++)
            {
                ReadArr[i+2] = promptNameArr[i];
            }

            String hexAddress;
            if(_is_extended)
            {
                hexAddress = Integer.toHexString(Integer.parseInt(_unitAddress));
                hexAddress = Utilities.StringCompleter(hexAddress, 6, "0", true);
                ReadArr[2 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(0, 2), 16);
                ReadArr[3 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(2, 4), 16);
                ReadArr[4 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(4, 6), 16);
                /////////////////
                hexAddress = Integer.toHexString(Integer.parseInt(_myAddress));
                hexAddress = Utilities.StringCompleter(hexAddress, 2, "0", true);

                ReadArr[5 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(0, 2), 16);
            }
            else
            {
                hexAddress = Integer.toHexString(Integer.parseInt(_unitAddress));
                hexAddress = Utilities.StringCompleter(hexAddress, 4, "0", true);
                ReadArr[2 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(0, 2), 16);
                ReadArr[3 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(2, 4), 16);
                /////////////////
                hexAddress = Integer.toHexString(Integer.parseInt(_myAddress));
                hexAddress = Utilities.StringCompleter(hexAddress, 4, "0", true);

                ReadArr[4 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(0, 2), 16);
                ReadArr[5 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(2, 4), 16);
            }

            ReadArr[6 + promptNameArr.length] = (byte) 0xe10; // Log request command
            ReadArr[7 + promptNameArr.length] = (byte) input_num; // input number

            //Start index
            ReadArr[8 + promptNameArr.length] = (byte)((start >> 8) & 0xFF);
            ReadArr[9 + promptNameArr.length] = (byte)(start & 0xFF);

            //Quantity of messages
            ReadArr[10 + promptNameArr.length] = (byte)((len >> 8) & 0xFF);
            ReadArr[11 + promptNameArr.length] = (byte)(len & 0xFF);

            _startCollectDateTag = System.currentTimeMillis();
            _startCollect = true;
            _isInfinitReceive = false;

            if(request) {
                _currentCommand = commandType.REQ_LOG;
                _msgArr = ReadArr;
            }
            else {
                _currentCommand = commandType.GET_LOG;
            }
            SendData(ReadArr);
        }
    }

    protected void PairingDevice(boolean yesno_prm, boolean rdm_prm)
    {
        byte[] ReadArr;

        if(yesno_prm)
        {
            if(_allowReceiveFlg) {

                // Get device data by _ndevice - device - _deviceVersion - virsion

                byte[] promptNameArr = _promptName.getBytes();

                ReadArr = new byte[2 + _promptName.length() + 5]; // 1,2 начало и размерб 3... промпт имя. 2 байта FF-FF адрес юнита. 2 байта мой адрес 00-01 и команда 8 - промпт. И тго 2+размер промпта + 5
                // 1 - stx
                // 2 - length
                // 3 - type MT
                // 4,5 - GUP address
                // 6,7 - Unit address
                //
                ReadArr[0] = (byte) 0xe02; // Start sign
                ReadArr[1] = (byte)(promptNameArr.length + 5); // Length of message
                for(int i = 0; i < promptNameArr.length; i++)
                {
                    ReadArr[i+2] = promptNameArr[i];
                }

                String hexAddress;
                if(_is_extended)
                {
                    hexAddress = Integer.toHexString(Integer.parseInt(_unitAddress));
                    hexAddress = Utilities.StringCompleter(hexAddress, 6, "0", true);
                    ReadArr[2 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(0, 2), 16);
                    ReadArr[3 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(2, 4), 16);
                    ReadArr[4 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(4, 6), 16);
                    /////////////////
                    hexAddress = Integer.toHexString(Integer.parseInt(_myAddress));
                    hexAddress = Utilities.StringCompleter(hexAddress, 2, "0", true);

                    ReadArr[5 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(0, 2), 16);
                }
                else
                {
                    hexAddress = Integer.toHexString(Integer.parseInt(_unitAddress));
                    hexAddress = Utilities.StringCompleter(hexAddress, 4, "0", true);
                    ReadArr[2 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(0, 2), 16);
                    ReadArr[3 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(2, 4), 16);
                    /////////////////
                    hexAddress = Integer.toHexString(Integer.parseInt(_myAddress));
                    hexAddress = Utilities.StringCompleter(hexAddress, 4, "0", true);

                    ReadArr[4 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(0, 2), 16);
                    ReadArr[5 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(2, 4), 16);
                }

                android.os.SystemClock.sleep(500);
                if(rdm_prm) {
                    ReadArr[6 + promptNameArr.length] = (byte) 0xe30; // VHF RX mode
                }
                else
                    ReadArr[6 + promptNameArr.length] = (byte) 0xe01; // Read command


                _startCollectDateTag = System.currentTimeMillis();
                _startCollect = true;
                if(rdm_prm)
                    _currentCommand = commandType.RDM;
                else
                    _currentCommand = commandType.READ;

                _timerCount = 0;
                _msgArr = ReadArr;
                SendData(ReadArr);
            }
        }
    }

    public void SetLastLatitude(String lat_prm)
    {
        last_latitude = lat_prm;
    }
    public String GetLastLatitude()
    {
        return last_latitude;
    }

    public void SetLastLongitude(String long_prm)
    {
        last_longitude = long_prm;
    }
    public String GetLastLongitude()
    {
        return last_longitude;
    }

   // public boolean GetUseOldSnRead() { return use_old; }
   // public void UseOldSnRead(boolean flg_prm) { use_old = flg_prm; }

    public void SetLastOldAccSN(String sn_prm) { last_old_sn = sn_prm; }
    public void SetLastOldAccRead(String read_prm) { last_old_read = read_prm; }

    public String GetLastOldAccSN()
    {
        return last_old_sn ;
    }
    public String GetLastOldAccRead() { return last_old_read; }

    public void SetLastAccSN(String sn_prm)
    {
        last_sn = sn_prm;
    }
    public void SetLastAccRead(String read_prm) { last_read = read_prm; }

    public String GetLastAccSN() { return last_sn; }
    public String GetLastAccRead() { return last_read; }

    public void SetAccountNumber(String acc_prm) { account_number = acc_prm; }
    public String GetAccountNumber() { return account_number; }

    public String GetUnitAddress() {return _unitAddress; }
    public void SetUnitAddress(String unitAddress_prm)
    {
        _unitAddress = unitAddress_prm;
    }


    protected void NewPulsePairingDevice(boolean yesno_prm, boolean rdm_prm)///////////////////////////////////////////////////////////////////////////////////////////
    {
        byte[] ReadArr;

        if(yesno_prm)
        {
            if(_allowReceiveFlg) {

                // Get device data by _ndevice - device - _deviceVersion - virsion

                byte[] promptNameArr = _promptName.getBytes();

                ReadArr = new byte[2 + _promptName.length() + 6]; // 1,2 начало и размерб 3... промпт имя. 2 байта FF-FF адрес юнита. 2 байта мой адрес 00-01 и команда 8 - промпт. И тго 2+размер промпта + 5
                ReadArr[0] = (byte) 0xe02;
                ReadArr[1] = (byte)(promptNameArr.length + 6);
                for(int i = 0; i < promptNameArr.length; i++)
                {
                    ReadArr[i+2] = promptNameArr[i];
                }

                String hexAddress;
                if(_is_extended)
                {
                    hexAddress = Integer.toHexString(Integer.parseInt(_unitAddress));
                    hexAddress = Utilities.StringCompleter(hexAddress, 6, "0", true);
                    ReadArr[2 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(0, 2), 16);
                    ReadArr[3 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(2, 4), 16);
                    ReadArr[4 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(4, 6), 16);
                    /////////////////
                    hexAddress = Integer.toHexString(Integer.parseInt(_myAddress));
                    hexAddress = Utilities.StringCompleter(hexAddress, 2, "0", true);

                    ReadArr[5 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(0, 2), 16);
                }
                else
                {
                    hexAddress = Integer.toHexString(Integer.parseInt(_unitAddress));
                    hexAddress = Utilities.StringCompleter(hexAddress, 4, "0", true);
                    ReadArr[2 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(0, 2), 16);
                    ReadArr[3 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(2, 4), 16);
                    /////////////////
                    hexAddress = Integer.toHexString(Integer.parseInt(_myAddress));
                    hexAddress = Utilities.StringCompleter(hexAddress, 4, "0", true);

                    ReadArr[4 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(0, 2), 16);
                    ReadArr[5 + promptNameArr.length] = (byte)Integer.parseInt(hexAddress.substring(2, 4), 16);
                }

                android.os.SystemClock.sleep(500);
                if(rdm_prm) {
                    ReadArr[6 + promptNameArr.length] = (byte) 0xe30; // RDM
                }
                else
                    ReadArr[6 + promptNameArr.length] = (byte) 0xe42; // Read Meter SN

                if(new_pulse_port == 1)
                    ReadArr[7 + promptNameArr.length] = (byte) 0xe00;
                else
                    ReadArr[7 + promptNameArr.length] = (byte) 0xe01;

                _startCollectDateTag = System.currentTimeMillis();
                _startCollect = true;

                if(rdm_prm)
                    _currentCommand = commandType.RDM;
                else
                    _currentCommand = commandType.NEW_PULSE_READ;

                SendData(ReadArr);
            }
        }
    }


    @Override
    protected void JunkBuffer(byte[] dataArr_prm)
    {
        if(_allowReceiveFlg)
        {
            int tmp = dataArr_prm.length;
            System.out.println(tmp);
        }
    }

    @Override
    protected void ReceiveTimeout() {
        // TODO Auto-generated method stub

    }

    public static boolean isNumeric(String str)
    {
        try
        {
            Integer d = Integer.parseInt(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }

}