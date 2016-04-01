package com.mysjsu.mobsecurity;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.TrafficStats;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Poornima on 3/15/16.
 */
public class UserDataUtil {
    Context context;
    public UserDataUtil(Context context) {
        this.context = context;
    }

    void getStats(UserData user) {
        String android_id = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        String email = getEmail(context);
        if (user == null) {
            user = new UserData(android_id, email);
        } else {
            if (user.androidId.equals(android_id)) {
                if (!user.userId.equals(email)) {
                    // TODO: email id changed
                    // send an alert mail
                }
            } else {
                // device got rebooted
            }
        }
        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && context.checkSelfPermission(Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED) {
            Map<String, Location> locMap = getLocationMap(user);
            android.location.Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Location currLocation = addLastLocationToMap(user, location, locMap);
            WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = addWifiDetails(user, wifiMgr);
            UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            //TODO After debating use case for this method, add specific data to OutlierDetector
            getAppUsageDataList(user, usageStatsManager);
            OutlierDetector outlierDetector = OutlierDetector.getOutlierDetectorInstance(context, user.userId, currLocation, wifiInfo, null);
        }
    }

    //TODO  Debate Use case for this method
    private void getAppUsageDataList(UserData user, UsageStatsManager lUsageStatsManager) {
        List<UsageStats> lUsageStatsList = lUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1), System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1));
        Map<String, App> installedapps = getInstalledApps(false, user);
        for (UsageStats lUsageStats : lUsageStatsList) {
            if (installedapps.containsKey(lUsageStats.getPackageName()) && lUsageStats.getTotalTimeInForeground() > 0) {
                App pinfo = installedapps.get(lUsageStats.getPackageName());
                pinfo.lastAccessedTimeStamp = lUsageStats.getLastTimeUsed();
                pinfo.appAccessedDuration = lUsageStats.getTotalTimeInForeground();
            }
        }
    }

    private WifiInfo addWifiDetails(UserData user, WifiManager wifiMgr) {
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        user.wifis.add(wifiInfo.getSSID().replace("\"", ""));
        return wifiInfo;
    }


    //TODO Write comments explaining this method, Review method with team mates
    private Location addLastLocationToMap(UserData user, android.location.Location location, Map<String, Location> locMap) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        String latLongKey = latitude + "," + longitude;
        Location currLocation;
        if (locMap.containsKey(latLongKey)) {
            currLocation = locMap.get(latLongKey);
            locMap.get(latLongKey).lastSeenTime = System.currentTimeMillis();
        } else {
            currLocation = new Location(latitude, longitude, System.currentTimeMillis());
            user.locs.add(currLocation);
        }
        return currLocation;
    }


    //TODO Change the implementation to get the mapping directly from memory
    @NonNull
    private Map<String, Location> getLocationMap(UserData user) {
        Map<String, Location> locMap = new HashMap<String, Location>();
        for (Location l : user.locs) {
            double lat = l.lastKnownLat;
            double lon = l.lastKnownLong;
            String ll = lat + "," + lon;
            locMap.put(ll, l);
        }
        return locMap;
    }

    static String getEmail(Context context) {
        AccountManager accountManager = AccountManager.get(context);
        Account account = getAccount(accountManager);

        if (account == null) {
            return null;
        } else {
            return account.name;
        }
    }

    private static Account getAccount(AccountManager accountManager) {
        Account[] accounts = accountManager.getAccountsByType("com.google");
        Account account;
        if (accounts.length > 0) {
            account = accounts[0];
        } else {
            account = null;
        }
        return account;
    }

    private Map<String, App> getInstalledApps(boolean getSysPackages, UserData user) {
        Map<String, App> res = new HashMap<String, App>();
        if (user != null) {
            for (App a : user.apps) {
                res.put(a.appname, a);
            }
        }
        List<PackageInfo> packs = context.getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);
            if ((p.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                continue;
            }
            App newInfo = res.get(p.packageName);
            if (newInfo == null) {
                newInfo = new App(p.packageName);
                res.put(p.packageName, newInfo);
                user.apps.add(newInfo);
            }
            int uid = p.applicationInfo.uid;
            newInfo.totalRxBytes = TrafficStats.getUidRxBytes(uid);
            newInfo.totalTxBytes = TrafficStats.getUidTxBytes(uid);
        }
        return res;
    }
}
