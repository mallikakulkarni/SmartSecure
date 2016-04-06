package com.mysjsu.mobsecurity;

/**
 * Created by Poornima on 10/21/15.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class PromptSecActivity extends Activity {
    private static final String TAG = "PromptSecActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Activity act = this;
        super.onCreate(savedInstanceState);
        PromptSecCallback cb = new PromptSecCallback() {
            @Override
            public void call(boolean res) {
                if (res) {
                    Toast.makeText(PromptSecActivity.this, "Authenticated!", Toast.LENGTH_SHORT).show();
                    act.finish();
                    act.onBackPressed();
                } else {
                    Toast.makeText(PromptSecActivity.this, "Incorrect Password", Toast.LENGTH_SHORT).show();
                }
            }
        };
        Intent intnt = getIntent();
        UserDataUtil userDataUtil = new UserDataUtil(this);
        // Get user name and email from extras passed from GoogleLoginActivity and update UI
        final String emailid = userDataUtil.getEmail(this);
        PromptSecKey ps = new PromptSecKey(emailid, this, cb);
        ps.authenticate();
    }
}