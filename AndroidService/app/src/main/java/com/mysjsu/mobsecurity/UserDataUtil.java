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
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.SystemClock;
import android.provider.Settings;

import java.text.DecimalFormat;
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

    public void getStats(UserData user, int hourOfDay, String occ, String userName) {
        String android_id = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        String email = getEmail(context);
        if (user == null) {
            user = new UserData(android_id, email, occ, userName);
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
        user.upTime = SystemClock.uptimeMillis();
        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && context.checkSelfPermission(Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED) {
            Map<String, Location> locMap = new HashMap<String, Location>();
            for (com.mysjsu.mobsecurity.Location l : user.locs) {
                float lat = l.lastKnownLat;
                float lon = l.lastKnownLong;
                String ll = lat + "," + lon;
                locMap.put(ll, l);
            }
            android.location.Location l = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            float lat = round(l.getLatitude());
            float lon = round(l.getLongitude());
            String ll = lat + "," + lon;
            if (locMap.containsKey(ll)) {
                locMap.get(ll).lastSeenTime = System.currentTimeMillis();
            } else {
                com.mysjsu.mobsecurity.Location loc = new com.mysjsu.mobsecurity.Location(lat, lon, System.currentTimeMillis());
                user.locs.add(loc);
            }
            WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiConfiguration activeConfig = null;

            try {
                for (WifiConfiguration conn : wifiMgr.getConfiguredNetworks()) {
                    if (conn.status == WifiConfiguration.Status.CURRENT) {
                        activeConfig = conn;
                        break;
                    }
                }
            } catch (NullPointerException e) {

            }
            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
            String currentSSID = wifiInfo.getSSID().replace("\"", "");

            user.wifis.add(new Wifi(currentSSID, getSecurity(activeConfig)));
            UsageStatsManager lUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            List<UsageStats> lUsageStatsList = lUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, user.statsStartTime, System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1));


            Map<String, App> installedapps = getInstalledApps(false, user);
            for (UsageStats lUsageStats : lUsageStatsList) {
                if (installedapps.containsKey(lUsageStats.getPackageName())) {
                    App pinfo = installedapps.get(lUsageStats.getPackageName());
                    if (lUsageStats.getTotalTimeInForeground() == 0 && pinfo.appAccessedDuration == 0) {
                        installedapps.remove(lUsageStats.getPackageName());
                        continue;
                    }
                    pinfo.lastAccessedTimeStamp = Math.max(pinfo.lastAccessedTimeStamp, lUsageStats.getLastTimeUsed());
                    pinfo.hrs[hourOfDay] = 1;
                    pinfo.appAccessedDuration += lUsageStats.getTotalTimeInForeground();
                }
            }
            user.apps.clear();
            for (String app : installedapps.keySet()) {
                App app1 = installedapps.get(app);
                if (app1.appAccessedDuration != 0) {
                    user.apps.add(app1);
                }
            }
        }
    }

    static boolean getSecurity(WifiConfiguration config) {
        if (config == null) {
            return false;
        }
        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_PSK)) {
            return true;
        }
        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_EAP) || config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.IEEE8021X)) {
            return true;
        }
        return (config.wepKeys[0] != null) ? true : false;
    }
    // round to 3 digits to get location accurate upto 100mts (http://gis.stackexchange.com/a/8674)

    private float round(double d) {
        return (Math.round(d * 1000.0)) / 1000.0F;
    }

    public static String getEmail(Context context) {
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
            }
            int uid = p.applicationInfo.uid;
            newInfo.totalRxBytes = TrafficStats.getUidRxBytes(uid);
            newInfo.totalTxBytes = TrafficStats.getUidTxBytes(uid);
        }
        return res;
    }
}
