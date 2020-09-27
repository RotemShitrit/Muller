package com.kp.meganet.meganetkp;

/**
 * Created by alex on 09-Jun-16.
 */
public interface iPulseCallback {
    public abstract void OnRead(String serial_num, String reading);
    public abstract void OnErrorRead(String error);
    public abstract void OnWriteResult(boolean result);
    public abstract boolean PairData(String deviceName_prm, String ndevice_pam, boolean titleOnly);
    public abstract void OnMessageCb(String message_prm);
}
