package edu.sjsu.smartsecure;

/**
 * Created by mallika on 3/28/16.
 */
public class App {
    private String appId;
    private String openTime;
    private String closeTime;

    public App(String appId, String openTime, String closeTime) {
        this.appId = appId;
        this.openTime = openTime;
        this.closeTime = closeTime;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getOpenTime() {
        return openTime;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public String getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(String closeTime) {
        this.closeTime = closeTime;
    }
}
