package com.mysjsu.mobsecurity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Poornima on 3/14/16.
 */
public class UserData {
    String userName;
    List<App> apps;
    List<Location> locs;
    String userId;
    String androidId;
    String gender;
    Set<Wifi> wifis;
    int incorrectPswdAttemptCount;
    long statsStartTime;
    long upTime;

    public UserData(String androidId, String userId, String gender, String userName) {
        this.gender = gender;
        this.userId = userId;
        this.userName = userName;
        this.androidId = androidId;
        apps = new ArrayList<App>();
        locs = new ArrayList<Location>();
        wifis = new HashSet<Wifi>();
        incorrectPswdAttemptCount = 0;
    }

    public UserData(UserData user) {

        this.androidId = user.androidId;
        this.gender = user.gender;
        this.userId = user.userId;
        this.userName = user.userName;
        this.incorrectPswdAttemptCount = user.incorrectPswdAttemptCount;
        apps = new ArrayList<App>();
        for (App app : user.apps) {

            apps.add(new App(app));
        }


        locs = new ArrayList<Location>();
        for (Location loc : user.locs) {
            locs.add(new Location(loc));

        }
        wifis = new HashSet<Wifi>();
        for (Wifi wifi : user.wifis) {
            wifis.add(new Wifi(wifi));

        }

        this.statsStartTime = user.statsStartTime;
        this.upTime = user.upTime;

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public List<App> getApps() {
        return apps;
    }

    public void setApps(List<App> apps) {
        this.apps = apps;
    }

    public List<Location> getLocs() {
        return locs;
    }

    public void setLocs(List<Location> locs) {
        this.locs = locs;
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

    public Set<Wifi> getWifis() {
        return wifis;
    }

    public void setWifis(Set<Wifi> wifis) {
        this.wifis = wifis;
    }

    public int getIncorrectPswdAttemptCount() {
        return incorrectPswdAttemptCount;
    }

    public void setIncorrectPswdAttemptCount(int incorrectPswdAttemptCount) {
        this.incorrectPswdAttemptCount = incorrectPswdAttemptCount;
    }

    public long getStatsStartTime() {
        return statsStartTime;
    }

    public void setStatsStartTime(long statsStartTime) {
        this.statsStartTime = statsStartTime;
    }

    public long getUpTime() {
        return upTime;
    }

    public void setUpTime(long upTime) {
        this.upTime = upTime;
    }
}

class App {

    long lastAccessedTimeStamp;
    long appAccessedDuration;
    byte hrs[];
    //  String appCategory;
    int appCrashCount;
    String appname = "";
    float totalTxBytes;
    float totalRxBytes;

    public App(String name) {
        hrs = new byte[24];
        appname = name;
    }

    public App(App app) {
        this.lastAccessedTimeStamp = app.lastAccessedTimeStamp;
        this.appAccessedDuration = app.appAccessedDuration;
        this.appCrashCount = app.appCrashCount;
        this.appname = app.appname;
        this.totalRxBytes = app.totalRxBytes;
        this.totalTxBytes = app.totalTxBytes;

    }

    @Override
    public boolean equals(Object o) {
        App newApp = (App) o;
        if (lastAccessedTimeStamp == newApp.lastAccessedTimeStamp &&
                appAccessedDuration == newApp.appAccessedDuration /*&&
                totalRxBytes == newApp.totalRxBytes &&
                totalTxBytes == newApp.totalRxBytes*/) {
            return true;
        }
        return false;
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

    public byte[] getHrs() {
        return hrs;
    }

    public void setHrs(byte[] hrs) {
        this.hrs = hrs;
    }

    public int getAppCrashCount() {
        return appCrashCount;
    }

    public void setAppCrashCount(int appCrashCount) {
        this.appCrashCount = appCrashCount;
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

class Location {
    float lastKnownLat;
    float lastKnownLong;
    long startTime;
    long lastSeenTime;
    boolean isCurrent;

    public Location(float lastKnownLat, float lastKnownLong, long startTime) {
        this.lastKnownLat = lastKnownLat;
        this.lastKnownLong = lastKnownLong;
        this.startTime = startTime;
        this.lastSeenTime = startTime;
    }

    public Location(Location locs) {

        this.lastKnownLat = locs.lastKnownLat;
        this.lastKnownLong = locs.lastKnownLong;
        this.startTime = locs.startTime;
        this.lastSeenTime = locs.lastSeenTime;
        this.isCurrent = locs.isCurrent;


    }

    public float getLastKnownLat() {
        return lastKnownLat;
    }

    public void setLastKnownLat(float lastKnownLat) {
        this.lastKnownLat = lastKnownLat;
    }

    public float getLastKnownLong() {
        return lastKnownLong;
    }

    public void setLastKnownLong(float lastKnownLong) {
        this.lastKnownLong = lastKnownLong;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getLastSeenTime() {
        return lastSeenTime;
    }

    public void setLastSeenTime(long lastSeenTime) {
        this.lastSeenTime = lastSeenTime;
    }

    @Override
    public boolean equals(Object o) {
        Location l = (Location) o;
        if (l.lastKnownLat == lastKnownLat && l.lastKnownLong == lastKnownLong) {
            return true;
        }
        return false;
    }
}
