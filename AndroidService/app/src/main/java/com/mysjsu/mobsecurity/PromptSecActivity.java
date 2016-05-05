package com.mysjsu.mobsecurity;

/**
 * Created by Poornima on 10/21/15.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class PromptSecActivity extends Activity {
    private static final String TAG = "PromptSecActivity";
    PromptSecKey ps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Activity act = this;
        super.onCreate(savedInstanceState);
        Intent intnt = getIntent();

        PromptSecCallback cb = new PromptSecCallback() {
            @Override
            public void call(boolean res) {
                if (res) {
                    Toast.makeText(PromptSecActivity.this, "Authenticated!", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Toast.makeText(PromptSecActivity.this, "Incorrect Password", Toast
                            .LENGTH_SHORT).show();
                }
                act.finish();
                act.onBackPressed();
            }
        };
        String msg = intnt.getStringExtra("msg");

        UserDataUtil userDataUtil = new UserDataUtil(this);
        // Get user name and email from extras passed from GoogleLoginActivity and update UI
        final String emailid = userDataUtil.getEmail(this);
        ps = new PromptSecKey(emailid, this, cb, msg);
        ps.authenticate();
    }

    @Override
    protected void onDestroy() {
        if (isFinishing()) {
            ps.alertDialog.dismiss();
            Log.d("PSA", "destroy");
        }
        super.onDestroy();
    }
}