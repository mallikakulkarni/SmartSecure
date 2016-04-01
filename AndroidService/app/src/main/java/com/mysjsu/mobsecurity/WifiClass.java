package com.mysjsu.mobsecurity;

import android.net.wifi.WifiInfo;

import org.json.JSONObject;

/**
 * Created by mallika on 3/13/16.
 */
public class WifiClass {

    //TODO Integrate Apoorva's code here.
    public void applyRule(WifiInfo wifis) {
        boolean secure = false;
        if (!secure) {
            System.out.println("Unsecure");
            takeAction();
        }
    }

    private void takeAction() {

    }
}
