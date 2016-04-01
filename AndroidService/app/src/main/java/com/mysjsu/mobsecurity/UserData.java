package com.mysjsu.mobsecurity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Poornima on 3/14/16.
 */
public class UserData {
    int cntr = 0;
    List<App> apps;
    List<Location> locs;
    String userId;
    String androidId;
    Set<String> wifis;
    int incorrectPswdAttemptCount;

    public UserData(String androidId, String userId) {
        this.userId = userId;
        this.androidId = androidId;
        apps = new ArrayList<App>();
        locs = new ArrayList<Location>();
        wifis = new HashSet<String>();
        incorrectPswdAttemptCount = 0;
    }
}

class App {

    long lastAccessedTimeStamp;
    long appAccessedDuration;
    //  String appCategory;
    int appCrashCount;
    String appname = "";
    float totalTxBytes;
    float totalRxBytes;

    public App(long lastAccessedTimeStamp, long appAccessedDuration, int appCrashCount, String appname, float totalTxBytes, float totalRxBytes) {
        this.lastAccessedTimeStamp = lastAccessedTimeStamp;
        this.appAccessedDuration = appAccessedDuration;
        this.appCrashCount = appCrashCount;
        this.appname = appname;
        this.totalTxBytes = totalTxBytes;
        this.totalRxBytes = totalRxBytes;
    }


    public App(String name) {
        appname = name;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(appname);
        sb.append("-");
        sb.append(lastAccessedTimeStamp);
        sb.append("-");
        sb.append(appAccessedDuration);
        sb.append("-");
        sb.append(totalTxBytes);
        sb.append("-");
        sb.append(totalRxBytes);
        sb.append("\r\n");
        return sb.toString();
    }
}

class Location {
    double lastKnownLat;
    double lastKnownLong;
    long startTime;
    long lastSeenTime;

    public Location(double lastKnownLat, double lastKnownLong, long startTime) {
        this.lastKnownLat = lastKnownLat;
        this.lastKnownLong = lastKnownLong;
        this.startTime = startTime;
    }
}