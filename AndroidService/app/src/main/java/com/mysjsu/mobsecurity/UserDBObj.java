package com.mysjsu.mobsecurity;

/**
 * Created by Poornima on 12/6/15.
 */
class UserLocation {
    float lat = 0;
    float lon = 0;

    public UserLocation(String ll) {
        if (ll != null && !ll.isEmpty()) {
            String[] parts = ll.split(",");
            lat = Float.parseFloat(parts[0]);
            lon = Float.parseFloat(parts[1]);
        }
    }

    @Override
    public String toString() {
        return lat + "," + lon;
    }
}

public class UserDBObj {

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmergContact() {
        return emergContact;
    }

    public void setEmergContact(String emergContact) {
        this.emergContact = emergContact;
    }

    public String getOccupation() {
        return gender;
    }

    public void setOccupation(String occupation) {
        this.gender = occupation;
    }

    String _id;
    String userName;
    String password;
    String emergContact;
    String gender;
    String email;

    public int getInCorrPass() {
        return inCorrPass;
    }

    int inCorrPass;
    String[] address;
    UserLocation[] latLon;



    public UserDBObj(String _id, String un, String pass, String cont, String occ, String em,
                     String[] address, String[] latLonStr) {
        this._id = _id;
        this.userName = un;
        this.password = pass;
        this.emergContact = cont;
        this.gender = occ;
        this.email = em;
        this.address = address;
        this.latLon = new UserLocation[latLonStr.length];
        for (int i = 0; i < latLonStr.length; i++) {
            this.latLon[i] = new UserLocation(latLonStr[i]);
        }
        this.inCorrPass = 0;
    }
}
