package com.mysjsu.mobsecurity.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;

import com.google.gson.Gson;
import com.mysjsu.mobsecurity.CreateUserAsyncTask;
import com.mysjsu.mobsecurity.LoginDataBaseAdapter;
import com.mysjsu.mobsecurity.UserDBObj;
import com.mysjsu.mobsecurity.UserData;
import com.mysjsu.mobsecurity.UserDataUtil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Poornima on 3/15/16.
 */
public class AlarmReceiver extends BroadcastReceiver {

    UserData user = null;
    String fileName = "mobSec.json";
    CreateUserAsyncTask createUserAsyncTask;

    @Override
    public void onReceive(Context context, Intent intent) {

        UserDataUtil userDataUtil = new UserDataUtil(context);
        Gson gson = new Gson();
        String android_id = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int hourOfDay = calendar.get(Calendar.HOUR);
        // TODO making it write every hour. Uncomment to write every day
//        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

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
        // get Instance  of Database Adapter
        LoginDataBaseAdapter loginDataBaseAdapter = new LoginDataBaseAdapter(context);
        loginDataBaseAdapter = loginDataBaseAdapter.open();
        final UserDBObj userDB = loginDataBaseAdapter.getSinlgeEntry(email);
        if (user == null) {
            user = new UserData(android_id, email, userDB.getOccupation(), userDB.getUserName());
        } else {
            long prevStartTime = user.getStatsStartTime();
            long newStartTime = calendar.getTimeInMillis();
            if (prevStartTime != newStartTime) {
                // Day changed. Write daily object to mongodb
                Log.i("SmrtSec", "Writing to Mongolab");
                createUserAsyncTask = new CreateUserAsyncTask();
                createUserAsyncTask.execute(gson.toJson(user));
                user = new UserData(android_id, email, userDB.getOccupation(), userDB.getUserName());
                user.setStatsStartTime(newStartTime);
            }
        }
        userDataUtil.getStats(user, hourOfDay, userDB.getOccupation(), userDB.getUserName());
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
    }
}