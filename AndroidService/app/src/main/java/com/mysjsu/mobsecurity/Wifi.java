package com.mysjsu.mobsecurity;

/**
 * Created by Poornima on 4/7/16.
 */
public class Wifi {
    String wifi;
    boolean isSecured;

    public Wifi(String s) {
        wifi = s;
    }

    public Wifi(String s, boolean security) {
        wifi = s;
        isSecured = security;
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
