package edu.sjsu.smartsecure.domain;

/**
 * Created by mallika on 4/8/16.
 */
public class RawRecord {
    private String userId;
    private String gender;
    private String appName;
    private String timeStamp;
    private String latitude;
    private String longitude;
    private String wifiType;

    public RawRecord(String gender, String appName, String timeStamp, String latitude, String longitude, String wifiType) {
        this.gender = gender;
        this.appName = appName;
        this.timeStamp = timeStamp;
        this.latitude = latitude;
        this.longitude = longitude;
        this.wifiType = wifiType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getWifiType() {
        return wifiType;
    }

    public void setWifiType(String wifiType) {
        this.wifiType = wifiType;
    }
}
