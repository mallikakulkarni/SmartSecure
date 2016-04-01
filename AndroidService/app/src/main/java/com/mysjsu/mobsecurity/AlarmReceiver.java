package com.mysjsu.mobsecurity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Poornima on 3/15/16.
 */
public class AlarmReceiver extends BroadcastReceiver {

    UserData user = null;
    String fileName = "mobSec.json";

    @Override
    public void onReceive(Context context, Intent intent) {

        UserDataUtil userDataUtil = new UserDataUtil(context);
        Gson gson = new Gson();
        String android_id = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        try {
            FileInputStream fis = context.openFileInput(fileName);
            InputStreamReader ir = new InputStreamReader(fis);
            user = gson.fromJson(ir, UserData.class);
            ir.close();
            fis.close();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }

        String email = userDataUtil.getEmail(context);
        if (user == null) {
            user = new UserData(android_id, email);
        }
        userDataUtil.getStats(user);
//        user.apps.clear();
        user.cntr = user.cntr + 1;
        String json = gson.toJson(user);
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(json.getBytes());
            fos.close();

        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
        Log.i("JSON", json);

        // For our recurring task, we'll just display a message
//        Toast.makeText(context, "I'm running", Toast.LENGTH_SHORT).show();
    }
}
