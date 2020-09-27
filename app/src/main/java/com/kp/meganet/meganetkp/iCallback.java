package com.kp.meganet.meganetkp;

import java.util.List;
import java.util.Map;

/**
 * Created by alex on 11/22/2015.
 */
public interface iCallback {

    public abstract void SetReadData(Map<String, QryParams> data_prm);
    public abstract void ReadLog(byte[] dataArr_prm);
    public  abstract void GetTime(byte[] dataArr_prm);
    public abstract boolean PairData(String deviceName_prm, String ndevice_pam, boolean titleOnly);
    public abstract void OnParameters(String deviceName_prm, List<QryParams> parameters);
    public abstract void OnRead(String deviceName_prm, String ndevice_pam);
    public abstract void OnProgramm(boolean result_prm, String err_prm);
    public abstract void OnPowerOff(boolean result_prm, String err_prm);
    public abstract void OnSleep(boolean result_prm, String err_prm);
    public abstract void OnErrorCb(String error_prm);
    public abstract void OnMessageCb(String message_prm);
}

