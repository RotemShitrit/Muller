package com.kp.meganet.meganetkp;

/**
 * Created by alex on 11/23/2015.
 */
public class Utilities {

    public static final String StringCompleter(String str, int len, String complete, boolean toLeft) {
        String retVal = "";

        for (int i = 0; i < len - str.length(); i++)
        {
            retVal += complete;
        }
        if(toLeft)
            return retVal + str;
        else
            return str + retVal;
    }

    public static boolean isNumeric(String str)
    {
        try
        {
            double d = Double.parseDouble(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }
}
