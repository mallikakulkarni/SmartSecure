package com.mysjsu.mobsecurity;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class ResetPswdEmailAsyncTask extends AsyncTask<String, Void, String> {
    public static final String POST_URL = "http://ec2-50-18-85-190.us-west-1.compute.amazonaws" +
            ".com:8080/smartsecure/ResetPassword";
    String resultMessage;

    @Override
    protected String doInBackground(String... arg0) {
        try {
            Log.i("message:", "reaching ResetPswdEmailAsyncTask Task");

            String userJson = arg0[0];
            //Creating the URL to MongoDB
            URL url = new URL(POST_URL);
            //Create Http Connection
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("content-type", "application/json");
            conn.setDoOutput(true);
            conn.setRequestProperty("Accept", "application/json");
//
//            Log.i("message :", "request URL is " + url);
//            Log.i("message :", "POST data is " + userJson);

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
                Log.d("Email_REQ", userJson);
                Log.d("Email_RESP", responseStrBuilder.toString());
                resultMessage = responseStrBuilder.toString();
                Log.i("message:", "response : success, code is" + responseCode + " message is " +
                        conn.getResponseMessage());
                return resultMessage;


            } else {
                Log.i("message:", "response : fail, code is " + responseCode);
                return null;
            }
        } catch (Exception e) {
            Log.i("message:", "Exception occured " + e.toString());
            return null;
        }
    }

}
