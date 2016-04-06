package com.mysjsu.mobsecurity;

/**
 * Created by Poornima on 10/21/15.
 */

import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SignUPActivity extends AppCompatActivity {
    EditText editTextUserName, editTextOccupation, editTextPassword, editTextConfirmPassword, editTextEmergencyContact, editTextOldPass;
    TextView email;
    Button btnCreateAccount;
    private static final String TAG = "SignUPActivity";
    UsageStatsManager mUsageStatsManager;
    private MenuItem profileMenu;

    LoginDataBaseAdapter loginDataBaseAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        mUsageStatsManager = (UsageStatsManager) this.getSystemService(Context.USAGE_STATS_SERVICE);
        // get Instance  of Database Adapter
        loginDataBaseAdapter = new LoginDataBaseAdapter(this);
        loginDataBaseAdapter = loginDataBaseAdapter.open();

        // Get Refferences of Views
        editTextUserName = (EditText) findViewById(R.id.editTextUserName);

        email = (TextView) findViewById(R.id.userEmail);
        UserDataUtil userDataUtil = new UserDataUtil(this);
        // Get user name and email from extras passed from GoogleLoginActivity and update UI
        final String emailid = userDataUtil.getEmail(this);
        email.setText(emailid);

        editTextEmergencyContact = (EditText) findViewById(R.id.editTextEmergencyContact);
        editTextOccupation = (EditText) findViewById(R.id.editTextOcc);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextConfirmPassword = (EditText) findViewById(R.id.editTextConfirmPassword);
        editTextOldPass = (EditText) findViewById(R.id.editTextOldPassword);
        btnCreateAccount = (Button) findViewById(R.id.buttonCreateAccount);
        final UserDBObj user = loginDataBaseAdapter.getSinlgeEntry(emailid);
        // update if user already present if not insert
        final boolean update = user != null;
        if (update) {
            editTextUserName.setText(user.userName);
            editTextEmergencyContact.setText(user.emergContact);
            editTextOccupation.setText(user.occupation);
            editTextOldPass.setVisibility(View.VISIBLE);
            btnCreateAccount.setText("Update Account");
        } else {
            editTextOldPass.setVisibility(View.INVISIBLE);
            btnCreateAccount.setText("Create Account");
        }
        btnCreateAccount.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        String userName = editTextUserName.getText().toString();
                        String emergenCon = editTextEmergencyContact.getText().toString();
                        String password = editTextPassword.getText().toString();
                        String occ = editTextOccupation.getText().toString();
                        String oldPassword = editTextOldPass.getText().toString();
                        String confirmPassword = editTextConfirmPassword.getText().toString();
                        editTextOldPass.setText("");
                        editTextPassword.setText("");
                        editTextConfirmPassword.setText("");
                        boolean resetPass = false;
                        if (update) {
                            if (!user.password.equals(oldPassword)) {
                                Toast.makeText(getApplicationContext(), "Incorrect current password. Need it to update data", Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                        // check if any of the fields are vaccant
                        if (password.equals("") || confirmPassword.equals("")) {
                            if(!update) {
                                Toast.makeText(getApplicationContext(), "Password shouldn't be empty", Toast.LENGTH_LONG).show();
                                return;
                            }
                            resetPass = false;
                        } else {
                            // check if both password matches
                            if (!password.equals(confirmPassword)) {
                                Toast.makeText(getApplicationContext(), "Password does not match", Toast.LENGTH_LONG).show();
                                return;
                            } else {
                                resetPass = true;
                            }
                        }
                        if (!update) {
                            // Save the Data in Database
                            loginDataBaseAdapter.insertEntry(new UserDBObj(userName, password, emergenCon, occ, emailid));
                            Toast.makeText(getApplicationContext(), "Account Successfully Created ", Toast.LENGTH_LONG).show();
                        } else {
                            if (resetPass) {
                                loginDataBaseAdapter.updateEntry(new UserDBObj(userName, password, emergenCon, occ, emailid), true);
                                Toast.makeText(getApplicationContext(), "Password Successfully Updated ", Toast.LENGTH_LONG).show();
                            } else {
                                loginDataBaseAdapter.updateEntry(new UserDBObj(userName, user.password, emergenCon, occ, emailid), false);
                                Toast.makeText(getApplicationContext(), "Account Successfully Updated ", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }

        );

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        boolean resultMenu = super.onCreateOptionsMenu(menu);
        profileMenu = menu.findItem(R.id.action_profile);
//        startServiceMenu = menu.findItem(÷R.id.start_service);
        return resultMenu;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_profile:
                Intent intent = new Intent(this, PromptSecActivity2.class);
                startActivity(intent);
                return true;
//            case R.id.start_service:
//                PromptSecCallback cb = new PromptSecCallback() {
//                    @Override
//                    public void call(boolean res) {
//                        if (res) {
//                            Toast.makeText(GoogleLoginActivity.this, "Authenticated!", Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(GoogleLoginActivity.this, "Incorrect Password", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                };
//                PromptSecKey ps = new PromptSecKey(currentAccount, this, cb);
//                ps.authenticate();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        loginDataBaseAdapter.close();
    }
}