package com.mysjsu.mobsecurity;

/**
 * Created by Poornima on 12/6/15.
 */
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

    String userName;
    String password;
    String emergContact;
    String gender;
    String email;
    String[] address;
    String[] latLon;

    public UserDBObj(String un, String pass, String cont, String occ, String em, String[] address, String[] latLon) {
        this.userName = un;
        this.password = pass;
        this.emergContact = cont;
        this.gender = occ;
        this.email = em;
        this.address = address;
        this.latLon = latLon;
    }
}
