package com.mysjsu.mobsecurity;

/**
 * Created by Poornima on 4/7/16.
 */
public class Wifi {
    String wifi;
    boolean isSecured;
    boolean isCurrent;


    public Wifi(String s, boolean security,boolean isCurrent) {
        wifi = s;
        isSecured = security;
        this.isCurrent=isCurrent;
    }

    public Wifi(Wifi wifi) {
        this.wifi=wifi.wifi;
        this.isSecured=wifi.isSecured;
        this.isCurrent=wifi.isCurrent;
    }

    @Override
    public boolean equals(Object o) {
        Wifi wo = (Wifi) o;
        return wo.wifi.equals(wifi);
    }

    @Override
    public int hashCode() {
        return wifi.hashCode();
    }
}
