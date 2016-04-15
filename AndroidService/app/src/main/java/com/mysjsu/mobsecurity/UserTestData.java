package com.mysjsu.mobsecurity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Poornima on 3/14/16.
 */
public class UserTestData {
    List<AppTest> appTestList;
    String userId;
    String androidId;
    int incorrectPswdAttemptCount;

    public UserTestData(UserData userData) {
        this.userId = userData.userId;
        this.androidId = userData.androidId;
        appTestList = new ArrayList<AppTest>();
        String wifi = null;
        boolean isSecure = false;
        for (Wifi w : userData.wifis) {
            wifi = w.wifi;
            isSecure = w.isSecured;
        }
        for (App app : userData.apps) {
            appTestList.add(new AppTest(app, userData.locs.get(0), wifi, isSecure));
        }
        incorrectPswdAttemptCount = userData.incorrectPswdAttemptCount;
    }

    public List<AppTest> getApps() {
        return appTestList;
    }

    public void setApps(List<AppTest> apps) {
        this.appTestList = apps;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAndroidId() {
        return androidId;
    }

    public void setAndroidId(String androidId) {
        this.androidId = androidId;
    }


    public int getIncorrectPswdAttemptCount() {
        return incorrectPswdAttemptCount;
    }

    public void setIncorrectPswdAttemptCount(int incorrectPswdAttemptCount) {
        this.incorrectPswdAttemptCount = incorrectPswdAttemptCount;
    }
}

class AppTest {

    long lastAccessedTimeStamp;
    long appAccessedDuration;
    String appname = "";
    float totalTxBytes;
    float totalRxBytes;
    String wiFiName;
    String network;
    float lastKnownLat;
    float lastKnownLong;


    public AppTest(App app, Location curLoc, String wifi, boolean isSecure) {
        this.totalRxBytes = app.totalRxBytes;
        this.totalTxBytes = app.totalTxBytes;
        this.appname = app.appname;
        this.lastAccessedTimeStamp = app.lastAccessedTimeStamp;
        this.appAccessedDuration = app.appAccessedDuration;
        this.lastKnownLat = curLoc.lastKnownLat;
        this.lastKnownLong = curLoc.lastKnownLong;
        this.wiFiName = wifi;
        if (isSecure) {
            network = "secure";
        } else if ("mobile".equals(wiFiName)) {
            network = "mobile";
        } else {
            network = "unsecure";
        }
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

    public long getLastAccessedTimeStamp() {
        return lastAccessedTimeStamp;
    }

    public void setLastAccessedTimeStamp(long lastAccessedTimeStamp) {
        this.lastAccessedTimeStamp = lastAccessedTimeStamp;
    }

    public long getAppAccessedDuration() {
        return appAccessedDuration;
    }

    public void setAppAccessedDuration(long appAccessedDuration) {
        this.appAccessedDuration = appAccessedDuration;
    }

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public float getTotalTxBytes() {
        return totalTxBytes;
    }

    public void setTotalTxBytes(float totalTxBytes) {
        this.totalTxBytes = totalTxBytes;
    }

    public float getTotalRxBytes() {
        return totalRxBytes;
    }

    public void setTotalRxBytes(float totalRxBytes) {
        this.totalRxBytes = totalRxBytes;
    }
}