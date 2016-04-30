package com.mysjsu.mobsecurity;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AppOpsManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.mysjsu.mobsecurity.receivers.AlarmReceiver;
import com.mysjsu.mobsecurity.receivers.DeviceBootReceiver;

public class MonitoringActivity extends AppCompatActivity {

    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Retrieve a PendingIntent that will perform a broadcast */
        Intent alarmIntent = new Intent(MonitoringActivity.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(MonitoringActivity.this, 0, alarmIntent, 0);

        findViewById(R.id.startAlarm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start();
            }
        });

        findViewById(R.id.stopAlarm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
    }

    public void start() {
//        ComponentName receiver = new ComponentName(this, DeviceBootReceiver.class.getSimpleName());
//        PackageManager pm = getPackageManager();
//
//        pm.setComponentEnabledSetting(receiver,
//                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
//                PackageManager.DONT_KILL_APP);
        AlarmManager manager = (AlarmManager) MonitoringActivity.this.getSystemService(Context.ALARM_SERVICE);
        // 5 mins

        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), DeviceBootReceiver.interval, pendingIntent);
        Toast.makeText(this, "SmartSecurity Set", Toast.LENGTH_SHORT).show();
        onBackPressed();
    }

    public void cancel() {
//        ComponentName receiver = new ComponentName(this, DeviceBootReceiver.class.getSimpleName());
//        PackageManager pm = getPackageManager();
//
//        pm.setComponentEnabledSetting(receiver,
//                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
//                PackageManager.DONT_KILL_APP);
        AlarmManager manager = (AlarmManager) MonitoringActivity.this.getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
        Toast.makeText(this, "SmartSecurity Canceled", Toast.LENGTH_SHORT).show();
        onBackPressed();
    }
}
