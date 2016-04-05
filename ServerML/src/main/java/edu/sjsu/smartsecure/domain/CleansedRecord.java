package edu.sjsu.smartsecure.domain;

import org.bson.types.ObjectId;

/**
 * Created by mallika on 4/4/16.
 */
public class CleansedRecord {
    private ObjectId _id;
    private String profession;
    private float megaBytesRx;
    private float megaBytesTx;
    private boolean isSecurityAppPresent;
    //TODO Other fields
    private boolean isSafe;

    public CleansedRecord(String profession, float megaBytesRx, float megaBytesTx, boolean isSecurityAppPresent, boolean isSafe) {
        this.profession = profession;
        this.megaBytesRx = megaBytesRx;
        this.megaBytesTx = megaBytesTx;
        this.isSecurityAppPresent = isSecurityAppPresent;
        this.isSafe = isSafe;
    }

    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public float getMegaBytesRx() {
        return megaBytesRx;
    }

    public void setMegaBytesRx(float megaBytesRx) {
        this.megaBytesRx = megaBytesRx;
    }

    public float getMegaBytesTx() {
        return megaBytesTx;
    }

    public void setMegaBytesTx(float megaBytesTx) {
        this.megaBytesTx = megaBytesTx;
    }

    public boolean isSecurityAppPresent() {
        return isSecurityAppPresent;
    }

    public void setIsSecurityAppPresent(boolean isSecurityAppPresent) {
        this.isSecurityAppPresent = isSecurityAppPresent;
    }

    public boolean isSafe() {
        return isSafe;
    }

    public void setIsSafe(boolean isSafe) {
        this.isSafe = isSafe;
    }
}
