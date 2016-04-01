package edu.sjsu.smartsecure;

/**
 * Created by mallika on 3/27/16.
 */
public class Query {
    private String userId;
    private Location location;
    private Wifi wifi;
    private App apps;

    public Query(String userId, Location location, Wifi wifi, App apps) {
        this.userId = userId;
        this.location = location;
        this.location = location;
        this.wifi = wifi;
        this.apps = apps;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Wifi getWifi() {
        return wifi;
    }

    public void setWifi(Wifi wifi) {
        this.wifi = wifi;
    }

    public App getApps() {
        return apps;
    }

    public void setApps(App apps) {
        this.apps = apps;
    }
}
