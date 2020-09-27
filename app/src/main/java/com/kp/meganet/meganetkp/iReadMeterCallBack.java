package com.kp.meganet.meganetkp;

/**
 * Created by alex on 11/22/2015.
 */
public interface iReadMeterCallBack {
    public abstract void OnReadMeters(byte[] data_prm);
    public abstract void OnFilterSet(boolean status);
}
