package com.mysjsu.mobsecurity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.LocationManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

/**
 * Created by Poornima on 12/6/15.
 */
public class PromptSecKey {
    String emailId;
    LoginDataBaseAdapter loginDataBaseAdapter;
    UserDBObj user;
    Context context;
    PromptSecCallback callback;
    String message = "";
    private int attempts = 0;
    private boolean isPasswordCorrect;
    AlertDialog alertDialog;
    AlertDialog.Builder alertDialogBuilder;


    public PromptSecKey(String emailId, Context context, PromptSecCallback callback) {
        this.emailId = emailId;
        loginDataBaseAdapter = new LoginDataBaseAdapter(context);
        loginDataBaseAdapter = loginDataBaseAdapter.open();
        user = loginDataBaseAdapter.getSinlgeEntry(emailId);
        this.context = context;
        this.callback = callback;
    }

    public PromptSecKey(String emailId, Context context, PromptSecCallback callback, String
            message) {
        this(emailId, context, callback);
        this.message = message;
    }

    public void authenticate() {
        alertDialogBuilder = new AlertDialog.Builder(
                context);
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.prompts, null);

        TextView msgT = (TextView) promptsView.findViewById(R.id.msgnew);

        if (message != null && !message.isEmpty()) {
            msgT.setText(message);
        } else {
            msgT.setText("");
        }
        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.secKey);
        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (user == null) {
                                    alertDialog.dismiss();
                                    callback.call(false);
                                    return;
                                }
                                String p = userInput.getText().toString();
                                isPasswordCorrect = p.equals(user.password);
                                attempts++;
                                if (attempts <= 3) {
                                    if (!isPasswordCorrect) {
                                        if (attempts == 3) {
                                            Toast.makeText(context, "Too many incorrect password " +
                                                    "attempts" +
                                                    " ", Toast
                                                    .LENGTH_SHORT)
                                                    .show();
                                        } else {
                                            Toast.makeText(context, "Incorrect password!", Toast
                                                    .LENGTH_SHORT)
                                                    .show();
                                        }
                                        alertDialog.dismiss();
                                        user.inCorrPass++;
                                        loginDataBaseAdapter.updateIncorrPassEntry(user);
                                        if (attempts < 3)
                                            authenticate();
                                    } else {
                                        if (message.contains("location")) {
                                            LocationManager locationManager = (LocationManager)
                                                    context.getSystemService(Context
                                                            .LOCATION_SERVICE);
                                            int i;
                                            int j = 0;
                                            for (i = 0; i < user.address.length; i++) {
                                                if (user.address[i] == null || user.address[i]
                                                        .isEmpty()) {
                                                    if (user.address[i].startsWith("Preferred")
                                                            && j != 0) {
                                                        j = i;
                                                    }
                                                    break;
                                                }
                                            }
                                            if (i >= user.address.length) {
                                                i = j;// recycling preferred locations
                                            }
                                            android.location.Location l = locationManager
                                                    .getLastKnownLocation(LocationManager
                                                            .NETWORK_PROVIDER);
                                            float lat = UserDataUtil.round(l.getLatitude());
                                            float lon = UserDataUtil.round(l.getLongitude());
                                            String ll = lat + "," + lon;
                                            user.address[i] = "Preferred Address " + (i - 1);
                                            user.latLon[i] = new UserLocation(ll);
                                            Gson gson = new Gson();
                                            CreateMainUserAsyncTask createMainUserAsyncTask = new
                                                    CreateMainUserAsyncTask();
                                            user.password = SignUPActivity.md5(user.password);
                                            createMainUserAsyncTask.execute(gson.toJson(user));
                                        }
                                        alertDialog.dismiss();
                                        loginDataBaseAdapter.updateEntry(user, false);
                                        loginDataBaseAdapter.close();
                                        callback.call(true);
                                    }
                                }
                            }
                        });

        // create alert dialog
        alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();

    }
}