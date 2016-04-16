package com.mysjsu.mobsecurity;

/**
 * Created by Poornima on 10/21/15.
 */

import android.Manifest;
import android.app.AppOpsManager;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

public class SignUPActivity extends AppCompatActivity {
    EditText editTextUserName, editTextPassword, editTextConfirmPassword, editTextEmergencyContact, editTextOldPass;
    EditText[] addressList;
    private static final int MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS = 100;
    TextView email;
    TextView[] latLon;
    Button btnCreateAccount;
    RadioButton femaleRadio, maleRadio;

    private static final String TAG = "SignUPActivity";
    UsageStatsManager mUsageStatsManager;
    private MenuItem profileMenu;
    private String gender = "male";

    LoginDataBaseAdapter loginDataBaseAdapter;

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radio_male:
                if (checked)
                    gender = "male";
                break;
            case R.id.radio_female:
                if (checked)
                    gender = "female";
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        getPermission();
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
        if (emailid == null) {
            Log.e("FATAL", "Email id is null");
            return;
        }
        email.setText(emailid);

        editTextEmergencyContact = (EditText) findViewById(R.id.editTextEmergencyContact);
        addressList = new EditText[5];

        addressList[0] = (EditText) findViewById(R.id.address1);
        addressList[1] = (EditText) findViewById(R.id.address2);
        addressList[2] = (EditText) findViewById(R.id.address3);
        addressList[3] = (EditText) findViewById(R.id.address4);
        addressList[4] = (EditText) findViewById(R.id.address5);
        latLon = new TextView[5];
        latLon[0] = (TextView) findViewById(R.id.latLon1);
        latLon[1] = (TextView) findViewById(R.id.latLon2);
        latLon[2] = (TextView) findViewById(R.id.latLon3);
        latLon[3] = (TextView) findViewById(R.id.latLon4);
        latLon[4] = (TextView) findViewById(R.id.latLon5);
        maleRadio = (RadioButton) findViewById(R.id.radio_male);
        femaleRadio = (RadioButton) findViewById(R.id.radio_female);
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
            if (user.gender.equals("male")) {
                maleRadio.setChecked(true);
            } else
                femaleRadio.setChecked(true);
            editTextOldPass.setVisibility(View.VISIBLE);
            for (int i = 0; i < 5; i++) {
                addressList[i].setText(user.address[i]);
                latLon[i].setText(user.latLon[i]);
            }
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
                        String oldPassword = editTextOldPass.getText().toString();
                        String confirmPassword = editTextConfirmPassword.getText().toString();
                        String[] homeAddress = new String[5];
                        String[] latLonStr = new String[5];
                        String androidId = Settings.Secure.getString(getContentResolver(),
                                Settings.Secure.ANDROID_ID);

                        for (int i = 0; i < 5; i++) {
                            homeAddress[i] = addressList[i].getText().toString();
                            if (homeAddress[i] != null && !homeAddress[i].isEmpty()) {
                                GeoPoint gp = getLocationFromAddress(homeAddress[i]);
                                if (gp != null) {
                                    latLonStr[i] = gp.toString();
                                    latLon[i].setText(latLonStr[i]);
                                }
                                else {
                                    latLonStr[i]="0,0";
                                }
                            }
                        }
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
                            if (!update) {
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
                        Gson gson = new Gson();
                        CreateMainUserAsyncTask createMainUserAsyncTask = new CreateMainUserAsyncTask();

                        if (!update) {
                            // Save the Data in Database
                            UserDBObj mainUserDbObject = new UserDBObj(androidId, userName, password, emergenCon, gender, emailid, homeAddress, latLonStr);
                            loginDataBaseAdapter.insertEntry(mainUserDbObject);
                            createMainUserAsyncTask.execute(gson.toJson(mainUserDbObject));
                            Toast.makeText(getApplicationContext(), "Account Successfully Created ", Toast.LENGTH_LONG).show();
                        } else {
                            if (resetPass) {
                                UserDBObj mainUserDbObject = new UserDBObj(androidId,userName, password, emergenCon, gender, emailid, homeAddress, latLonStr);
                                createMainUserAsyncTask.execute(gson.toJson(mainUserDbObject));
                                loginDataBaseAdapter.updateEntry(mainUserDbObject, true);
                                Toast.makeText(getApplicationContext(), "Password Successfully Updated ", Toast.LENGTH_LONG).show();
                            } else {
                                UserDBObj mainUserDbObject = new UserDBObj(androidId,userName, user.password, emergenCon, gender, emailid, homeAddress, latLonStr);
                                createMainUserAsyncTask.execute(gson.toJson(mainUserDbObject));
                                loginDataBaseAdapter.updateEntry(mainUserDbObject, false);
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

    void getPermission() {
        if (!hasPermission()) {
            requestPermission();
        }
        if (getPackageManager().checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, getPackageName()) != PackageManager.PERMISSION_GRANTED || getPackageManager().checkPermission(Manifest.permission.GET_ACCOUNTS, getPackageName()) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.GET_ACCOUNTS}, MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("MonitoringActivity", "resultCode " + resultCode);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS:
                getPermission();
                break;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Log.i("PERM", "Granted");
//                    getPermission();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Log.i("PERM", "Denied");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void requestPermission() {
        Toast.makeText(this, "Need to request permission", Toast.LENGTH_SHORT).show();
        startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS);
    }

    private boolean hasPermission() {
        AppOpsManager appOps = (AppOpsManager)
                getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }


    class GeoPoint {
        float lat;
        float lon;

        public GeoPoint(float lat, float lon) {
            this.lat = lat;
            this.lon = lon;
        }

        @Override
        public String toString() {
            return lat + "," + lon;
        }
    }

    public GeoPoint getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(this);
        List<Address> address;
        GeoPoint p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null || address.size()<= 0) {
                Toast.makeText(getApplicationContext(), "Couldn't geo-code address:" + strAddress, Toast.LENGTH_LONG).show();
                return null;
            }
            Address location = address.get(0);

            p1 = new GeoPoint(UserDataUtil.round(location.getLatitude()),
                    UserDataUtil.round(location.getLongitude()));

            return p1;
        } catch (IOException e) {
            return p1;
        }
    }

    public void handleOnClick(View view) {
        TextView v = (TextView) view;
        String ll = v.getText().toString();
        if (ll != null && !ll.isEmpty()) {
            Intent searchAddress = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + ll + "(" + ll + ")&z=11"));
            startActivity(searchAddress);
        }
    }
}
