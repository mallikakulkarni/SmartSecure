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
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by Poornima on 3/15/16.
 */
public class UserDataUtil {
    Context context;

    static final Set<String> WHITELIST = new HashSet<String>();

    static {
        WHITELIST.add("com.android.chrome");
        WHITELIST.add("com.google.android.youtube");
    }

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
        LocationManager locationManager = (LocationManager) context.getSystemService(Context
                .LOCATION_SERVICE);
        if (context.getPackageManager().checkPermission(Manifest.permission
                .ACCESS_COARSE_LOCATION, context.getPackageName()) == PackageManager
                .PERMISSION_GRANTED && context.getPackageManager().checkPermission(Manifest
                .permission.GET_ACCOUNTS, context.getPackageName()) == PackageManager
                .PERMISSION_GRANTED) {
            Map<String, Location> locMap = new HashMap<String, Location>();
            for (com.mysjsu.mobsecurity.Location l : user.locs) {
                l.isCurrent = false;
                float lat = l.lastKnownLat;
                float lon = l.lastKnownLong;
                String ll = lat + "," + lon;
                locMap.put(ll, l);
            }
            android.location.Location l = locationManager.getLastKnownLocation(LocationManager
                    .NETWORK_PROVIDER);
            float lat = round(l.getLatitude());
            float lon = round(l.getLongitude());
            String ll = lat + "," + lon;
            if (locMap.containsKey(ll)) {
                locMap.get(ll).isCurrent = true;
                locMap.get(ll).lastSeenTime = System.currentTimeMillis();

            } else {
                com.mysjsu.mobsecurity.Location loc = new com.mysjsu.mobsecurity.Location(lat,
                        lon, System.currentTimeMillis());
                loc.isCurrent = true;
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
            if (currentSSID.equals("0x")) {
                currentSSID = "mobile";
            }

            for (Wifi wifi : user.wifis) {
                if (wifi.wifi.equals(currentSSID))
                    wifi.isCurrent = true;
                else
                    wifi.isCurrent = false;
            }
            user.wifis.add(new Wifi(currentSSID, getSecurity(activeConfig), true));

            UsageStatsManager lUsageStatsManager = (UsageStatsManager) context.getSystemService
                    (Context.USAGE_STATS_SERVICE);
            List<UsageStats> lUsageStatsList = lUsageStatsManager.queryUsageStats
                    (UsageStatsManager.INTERVAL_DAILY,
                            user.statsStartTime, user.statsStartTime + TimeUnit.DAYS.toMillis(1));


            Map<String, App> installedapps = getInstalledApps(false, user);
            for (UsageStats lUsageStats : lUsageStatsList) {
                if (installedapps.containsKey(lUsageStats.getPackageName())) {
                    App pinfo = installedapps.get(lUsageStats.getPackageName());
                    if ((lUsageStats.getTotalTimeInForeground() == 0 && pinfo.appAccessedDuration
                            == 0)) {
                        installedapps.remove(lUsageStats.getPackageName());
                        continue;
                    }
                    pinfo.lastAccessedTimeStamp = Math.max(pinfo.lastAccessedTimeStamp,
                            lUsageStats.getLastTimeUsed());
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
        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_EAP) || config
                .allowedKeyManagement.get(WifiConfiguration.KeyMgmt.IEEE8021X)) {
            return true;
        }
        return (config.wepKeys[0] != null) ? true : false;
    }
    // round to 3 digits to get location accurate upto 100mts (http://gis.stackexchange.com/a/8674)

    public static float round(double d) {
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
                a.appAccessedDuration = 0;
                res.put(a.appname, a);
            }
        }

        List<PackageInfo> packs = context.getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);
            if (!WHITELIST.contains(p.packageName) && (p.applicationInfo.flags & ApplicationInfo
                    .FLAG_INSTALLED) != 0) {
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
        res.remove("com.mysjsu.mobsecurity");
        return res;
    }

    public UserData getChangedUserData(UserData prevUserData, UserData curUserData) {

        if (prevUserData == null) {
            return null;
        }
        HashMap<String, App> prevAppMap = new HashMap<String, App>();
        HashMap<String, App> curAppMap = new HashMap<String, App>();
        Set<String> allApps = new HashSet<String>();
        //    public UserData(String androidId, String userId, String gender, String userName) {
        boolean changed = false;
        UserData diffUserData = new UserData(curUserData.androidId, curUserData.userId,
                curUserData.gender, curUserData.userName);
        for (App app : prevUserData.apps) {
            prevAppMap.put(app.appname, app);
            allApps.add(app.appname);
        }
        for (App app : curUserData.apps) {
            curAppMap.put(app.appname, app);
            allApps.add(app.appname);
        }
        for (String appName : allApps) {
            App prevApp = prevAppMap.get(appName);
            App currApp = curAppMap.get(appName);
            if (prevApp == null && currApp != null) {
                // new app used
                diffUserData.apps.add(currApp);
                changed = true;
            } else if (prevApp != null && currApp != null) {
                // check if app's stats changed
                if (!prevApp.equals(currApp)) {
                    App diffApp = new App(currApp);
                    diffApp.appAccessedDuration = currApp.appAccessedDuration - prevApp
                            .appAccessedDuration;
                    diffApp.totalTxBytes = currApp.totalTxBytes - prevApp.totalTxBytes;
                    diffApp.totalRxBytes = currApp.totalRxBytes - prevApp.totalRxBytes;
                    diffUserData.apps.add(diffApp);
                    changed = true;
                }
            }
        }

        Location curLoc = null;
        for (Location l : curUserData.locs) {
            if (l.isCurrent) {
                curLoc = l;
            }
        }
        if (curLoc != null)
            diffUserData.locs.add(curLoc);
        Location prevLoc = null;
        for (Location l : prevUserData.locs) {
            if (l.isCurrent) {
                prevLoc = l;
            }
        }
//        if (curLoc != null && !curLoc.equals(prevLoc)) {
//            changed = true;
//        }

        Wifi curWifi = null;
        for (Wifi l : curUserData.wifis) {
            if (l.isCurrent) {
                curWifi = l;
            }
        }
        if (curWifi != null)
            diffUserData.wifis.add(curWifi);
        else {
            Log.d("SmartSec", "Wifi IS NULL");
        }
        Wifi prevWifi = null;
        for (Wifi l : prevUserData.wifis) {
            if (l.isCurrent) {
                prevWifi = l;
            }
        }
//        if (curWifi != null && !curWifi.equals(prevWifi)) {
//            changed = true;
//        }
        if (changed) {
            return diffUserData;
        }
        return null;


    }
}
