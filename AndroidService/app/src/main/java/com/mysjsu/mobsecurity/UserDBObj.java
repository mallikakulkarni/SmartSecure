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
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    String userName;
    String password;
    String emergContact;
    String occupation;
    String email;

    public UserDBObj(String un, String pass, String cont, String occ, String em) {
        this.userName = un;
        this.password = pass;
        this.emergContact = cont;
        this.occupation = occ;
        this.email = em;
    }
}
