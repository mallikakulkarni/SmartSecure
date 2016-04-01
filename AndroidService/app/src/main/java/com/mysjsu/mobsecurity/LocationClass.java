package com.mysjsu.mobsecurity;

import android.content.Context;

import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by mallika on 3/13/16.
 */
public class LocationClass {
    Context context;
    public LocationClass(Context context) {
        this.context = context;
    }

    //TODO Write method to figure out the risk depending on model
    public void applyRule(String userId, Location location) {

    }

    private void takeAction() {

    }
}
