package com.kp.meganet.meganetkp;

/**
 * Created by alex on 11/22/2015.
 */
public class CommonReadsData {

    private eDataType _dataType;

    private String _time;
    private String _id;
    private String _system;
    private String _coll;
    private String _lvl;

    public enum eDataType
    {
        NONE,
        RSSI,
        READINGS
    }

    // Empty constructor
    public CommonReadsData()
    {
        _dataType = eDataType.NONE;
    }
    // constructor RSSI
    public CommonReadsData(String time, String id, String system, String coll, String lvl)
    {
        _time = time;
        _id = id;
        _system = system;
        _coll = coll;
        _lvl = lvl;

        _dataType = eDataType.RSSI;
    }

    // constructor Readings
    public CommonReadsData(String time, String id, String system, String coll)
    {
        _time = time;
        _id = id;
        _system = system;
        _coll = coll;

        _dataType = eDataType.READINGS;
    }

    public void SetDataType(eDataType value)
    {
        _dataType = value;
    }
    public eDataType GetDataType()
    {
        return _dataType;
    }

    ////////////////////////////////////////////////////////////
    void SetTime(String value)
    {
        _time = value;
    }
    void SetId(String value)
    {
        _id = value;
    }
    void SetSystem(String value)
    {
        _system = value;
    }
    void SetColl(String value)
    {
        _coll = value;
    }
    void SetLvl(String value)
    {
        _lvl = value;
    }

    /////////////////////////////////////////////////////////
    String GetTime(String value)
    {
        return _time;
    }
    String GetId(String value)
    {
        return _id;
    }
    String GetSystem(String value)
    {
        return _system;
    }
    String GetColl(String value)
    {
        return _coll;
    }
    String GetLvl(String value)
    {
        return _lvl;
    }

}
