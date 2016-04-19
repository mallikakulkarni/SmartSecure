package com.mysjsu.mobsecurity.receivers;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;


import com.mysjsu.mobsecurity.LoginDataBaseAdapter;
import com.mysjsu.mobsecurity.PromptSecCallback;
import com.mysjsu.mobsecurity.UserDBObj;

import java.util.regex.Pattern;

/**
 * Created by Poornima on 12/5/15.
 */
public class PeriodicTaskReceiverAppInstall extends BroadcastReceiver {

    private static final String TAG = "AppInstall";
    private static final String INTENT_ACTION = "com.apps.finalproj.mobsec.PERIODIC_TASK_HEART_BEAT";
    public static final String BACKGROUND_SERVICE_BATTERY_CONTROL = "battery_service";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!isNullOrEmpty(intent.getAction())) {
            Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
            Account[] accounts = AccountManager.get(context).getAccounts();
            UserDBObj user = null;
            for (Account account : accounts) {
                if (emailPattern.matcher(account.name).matches()) {
                    String emailId = account.name;
                    LoginDataBaseAdapter loginDataBaseAdapter;
                    loginDataBaseAdapter = new LoginDataBaseAdapter(context);
                    loginDataBaseAdapter = loginDataBaseAdapter.open();
                    user = loginDataBaseAdapter.getSinlgeEntry(emailId);
                    Log.d(TAG, "Email:" + emailId);
                    if (user != null) {
                        Log.d(TAG, "Name:" + user.getUserName());
                        break;
                    } else {
                        Log.d(TAG, "NOName:");
                    }
                }
            }
            if (user == null) {
                return;
            }
            if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
                PromptSecCallback cb = new PromptSecCallback() {
                    @Override
                    public void call(boolean res) {
                        if (res) {
                            Toast.makeText(new Activity(), "Authenticated!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(new Activity(), "Incorrect Password.. Should send an email", Toast.LENGTH_SHORT).show();
                            // Send mail
                        }
                    }
                };

                Intent intent1 = new Intent("android.intent.category.LAUNCHER");
                intent1.setClassName("com.mysjsu.mobsecurity", "com.mysjsu.mobsecurity.PromptSecActivity");
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent1);

//                restartPeriodicTaskHeartBeat(context);
            } else if (intent.getAction().equals(INTENT_ACTION)) {
                doPeriodicTask(context);
            }
        }
    }

    private boolean isNullOrEmpty(String action) {
        if (action == null || action.isEmpty())
            return true;
        return false;
    }

    private void doPeriodicTask(Context context) {
        // Periodic task(s) go here ...
    }

//    public void restartPeriodicTaskHeartBeat(Context context) {
//
//        SharedPreferences sharedPreferences = PreferenceManager
//                .getDefaultSharedPreferences(context);
//        boolean isBatteryOk = sharedPreferences.getBoolean(BACKGROUND_SERVICE_BATTERY_CONTROL, true);
//        Intent alarmIntent = new Intent(context, PeriodicTaskReceiverAppInstall.class);
//        boolean isAlarmUp = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_NO_CREATE) != null;
//        if (isBatteryOk && !isAlarmUp) {
//            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//            alarmIntent.setAction(INTENT_ACTION);
//            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
//            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);
//        }
//    }
//
//    public void stopPeriodicTaskHeartBeat(Context context) {
//        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        Intent alarmIntent = new Intent(context, PeriodicTaskReceiverAppInstall.class);
//        alarmIntent.setAction(INTENT_ACTION);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
//        alarmManager.cancel(pendingIntent);
//    }
}