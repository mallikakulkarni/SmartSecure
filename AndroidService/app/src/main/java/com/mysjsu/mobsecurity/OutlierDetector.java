package com.mysjsu.mobsecurity;

import android.content.Context;
import android.net.wifi.WifiInfo;

/**
 * Created by mallika on 3/17/16.
 */
public class OutlierDetector {
    private String userId;
    private WifiInfo wifi;
    private Location location;
    private App appData;
    private static OutlierDetector outlierDetector = null;
    private Context context;

    private OutlierDetector (Context context, String userId, Location location, WifiInfo wifi, App appData) {
        this.userId = userId;
        this.appData = appData;
        this.wifi = wifi;
        this.location = location;
        this.context = context;
    }

    public static OutlierDetector getOutlierDetectorInstance(Context context, String userId, Location location, WifiInfo wifi, App appData) {
        if (outlierDetector == null) {
            outlierDetector = new OutlierDetector(context, userId, location, wifi, appData);
        }
        return outlierDetector;
    }

    public void calculateRiskAndNotify() {
        calculateRiskForWifi();
        calculateRiskForLocation();
        calculateRiskForAppUsage();
    }

    private void calculateRiskForWifi() {
        WifiClass wifiClass = new WifiClass();
        wifiClass.applyRule(wifi);
    }

    private void calculateRiskForLocation() {
        LocationClass locationClass = new LocationClass(context);
        locationClass.applyRule(userId, location);
    }

    private void calculateRiskForAppUsage() {
        AppClass appClass = new AppClass();
        appClass.applyRule(userId, appData);
    }
}
