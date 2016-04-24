package com.mysjsu.mobsecurity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class GetFeedbackAsyncTask extends AsyncTask<String, Void, Boolean> {
    public static final String POST_URL = "http://ec2-50-18-85-190.us-west-1.compute.amazonaws" +
            ".com:8080/smartsecure/EvalDataPost";
    Context context;

    public GetFeedbackAsyncTask(Context context) {
        this.context = context;
    }

    static Map<Integer, String> msgs = new HashMap<Integer, String>();

    @Override
    protected Boolean doInBackground(String... arg0) {
        try {
            Log.i("message:", "reaching GetFeedbackAsyncTask Task");

            String userJson = arg0[0];
            // UserTestDataQueryBuilder qb = new UserTestDataQueryBuilder();

            //Creating the URL to MongoDB
            URL url = new URL(POST_URL);

            //Create Http Connection
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("content-type", "application/json");
            conn.setDoOutput(true);
            conn.setRequestProperty("Accept", "application/json");
            //Sending Data
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(conn.getOutputStream());
            outputStreamWriter.write(userJson);
            outputStreamWriter.flush();
            outputStreamWriter.close();

            int responseCode = conn.getResponseCode();

            if (responseCode < 205) {
                BufferedReader br = new BufferedReader(new InputStreamReader((conn
                        .getInputStream())));
                StringBuilder responseStrBuilder = new StringBuilder();

                String inputStr;
                while ((inputStr = br.readLine()) != null) {
                    responseStrBuilder.append(inputStr);
                }
                br.close();
                Log.d("DECTREE_REQ", userJson);
                Log.d("DECTREE_RESP", responseStrBuilder.toString());
                String resultMessage;
                resultMessage=responseStrBuilder.toString();

                if (resultMessage != null){
                    if(resultMessage.contains("Alert")){
                        getSecuritykey(resultMessage);
                    }
                    else if(resultMessage.contains("Warning")){
                        Toast.makeText(new Activity(), resultMessage, Toast.LENGTH_SHORT).show();

                    }
                }
                Log.i("message:", "response : success, code is" + responseCode + " message is " +
                        conn.getResponseMessage());
                return true;
            } else {
                Log.i("message:", "response : fail, code is " + responseCode);
                return false;
            }

        } catch (Exception e) {
            Log.i("message:", "Exception occured " + e.toString());
            return false;
        }

    }

    void getSecuritykey(String msg) {
        PromptSecCallback cb = new PromptSecCallback() {
            @Override
            public void call(boolean res) {
                if (res) {
                    Toast.makeText(new Activity(), "Authenticated!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(new Activity(), "Incorrect Password.. Should send an email",
                            Toast.LENGTH_SHORT).show();
                    // Send mail
                }
            }
        };

        Intent intent1 = new Intent("android.intent.category.LAUNCHER");
        intent1.setClassName("com.mysjsu.mobsecurity", "com.mysjsu.mobsecurity.PromptSecActivity");
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent1.putExtra("msg", msg);
        context.startActivity(intent1);

    }

}
