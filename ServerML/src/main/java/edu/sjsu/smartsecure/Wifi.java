package edu.sjsu.smartsecure;

/**
 * Created by mallika on 3/28/16.
 */
public class Wifi {
    private String ssid;

    public Wifi(String ssid) {
        this.ssid = ssid;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }
}
