package com.mysjsu.mobsecurity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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
                                if (attempts < 3) {
                                    if (!isPasswordCorrect) {
                                        Toast.makeText(context, "Incorrect password!", Toast
                                                .LENGTH_SHORT)
                                                .show();
                                        alertDialog.dismiss();
                                        user.inCorrPass++;
                                        loginDataBaseAdapter.updateIncorrPassEntry(user);
                                        authenticate();
                                    } else {
                                        alertDialog.dismiss();
                                        loginDataBaseAdapter.close();
                                        callback.call(true);
                                    }

                                } else {
                                    if (!isPasswordCorrect) {
                                        Toast.makeText(context, "Too many incorrect password " +
                                                "attempts" +
                                                " ", Toast
                                                .LENGTH_SHORT)
                                                .show();
                                        alertDialog.dismiss();
                                        user.inCorrPass++;
                                        loginDataBaseAdapter.updateIncorrPassEntry(user);
                                        loginDataBaseAdapter.close();
                                        // TODO send mail alert
                                    } else {
                                        alertDialog.dismiss();
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