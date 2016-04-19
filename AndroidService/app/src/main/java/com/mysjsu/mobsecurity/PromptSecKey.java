package com.mysjsu.mobsecurity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Created by Poornima on 12/6/15.
 */
public class PromptSecKey {
    String emailId;
    LoginDataBaseAdapter loginDataBaseAdapter;
    UserDBObj user;
    Context context;
    PromptSecCallback callback;
    String message="";

    public PromptSecKey(String emailId, Context context, PromptSecCallback callback) {
        this.emailId = emailId;
        loginDataBaseAdapter = new LoginDataBaseAdapter(context);
        loginDataBaseAdapter = loginDataBaseAdapter.open();
        user = loginDataBaseAdapter.getSinlgeEntry(emailId);
        loginDataBaseAdapter.close();
        this.context = context;
        this.callback = callback;
    }
    public PromptSecKey(String emailId, Context context, PromptSecCallback callback,String message) {
       this(emailId,context,callback);
        this.message=message;
    }

    public void authenticate() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.prompts, null);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.secKey);
        class R {
            String p = null;
        }
        final R r = new R();
        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (user == null) {
                                    callback.call(false);
                                    return;
                                }
                                r.p = userInput.getText().toString();
                                callback.call(r.p.equals(user.password));
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                callback.call(false);
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }
}
