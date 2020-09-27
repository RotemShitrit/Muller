package com.kp.meganet.meganetkp;

/**
 * Created by alex on 11/23/2015.
 */
public class RssiRow {

    private String _date;
    private String _id;
    private String _system;
    private String _collector;
    private String _level;

    public RssiRow(String date, String id, String system, String collector, String level)
    {
        _date = date;
        _id = id;
        _system = system;
        _collector = collector;
        _level = level;
    }

    String GetDate()
    {
        return  _date;
    }
    String GetId()
    {
        return  _id;
    }
    String GetSystem()
    {
        return  _system;
    }
    String GetCollector()
    {
        return  _collector;
    }
    String GetLevel()
    {
        return  _level;
    }

}
