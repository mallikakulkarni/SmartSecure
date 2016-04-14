package com.mysjsu.mobsecurity;

/**
 * Created by Poornima on 10/21/15.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.Settings;
import android.util.Log;

public class LoginDataBaseAdapter {
    static final String DATABASE_NAME = "mobsec.db";
    static final int DATABASE_VERSION = 7;
    public static final int NAME_COLUMN = 1;
    // TODO: Create public field for each column in your table.
    // SQL Statement to create a new database.
    static final String DATABASE_CREATE = "create table " + "LOGIN" +
            "( " + "ID" + " integer primary key autoincrement," + "USERNAME  text,PASSWORD text, CONTACT text," +
            " GENDER text, EMAIL text, ADDRESS1 text, LATLON1 text, ADDRESS2 text, LATLON2 text, ADDRESS3 text, LATLON3 text, " +
            "ADDRESS4 text, LATLON4 text, ADDRESS5 text, LATLON5 text); ";
    // Variable to hold the database instance
    public SQLiteDatabase db;
    // Context of the application using the database.
    private final Context context;
    // Database open/upgrade helper
    private DataBaseHelper dbHelper;

    public LoginDataBaseAdapter(Context _context) {
        context = _context;
        dbHelper = new DataBaseHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public LoginDataBaseAdapter open() throws SQLException {
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        db.close();
    }

    public SQLiteDatabase getDatabaseInstance() {
        return db;
    }

    public void insertEntry(UserDBObj u) {
        ContentValues newValues = new ContentValues();
        // Assign values for each row.
        newValues.put("USERNAME", u.userName);
        newValues.put("PASSWORD", u.password);
        newValues.put("EMAIL", u.email);
        newValues.put("GENDER", u.gender);
        newValues.put("CONTACT", u.emergContact);
        for (int i = 1; i <= 5; i++) {
            newValues.put("ADDRESS" + i, u.address[i - 1]);
            newValues.put("LATLON" + i, u.latLon[i - 1]);
        }

        // Insert the row into your table
        db.insert("LOGIN", null, newValues);
        ///Toast.makeText(context, "Reminder Is Successfully Saved", Toast.LENGTH_LONG).show();
    }

    public int deleteEntry(String UserName) {
        //String id=String.valueOf(ID);
        String where = "USERNAME=?";
        int numberOFEntriesDeleted = db.delete("LOGIN", where, new String[]{UserName});
        // Toast.makeText(context, "Number fo Entry Deleted Successfully : "+numberOFEntriesDeleted, Toast.LENGTH_LONG).show();
        return numberOFEntriesDeleted;
    }

    public UserDBObj getSinlgeEntry(String email) {
        Cursor cursor = db.query("LOGIN", null, " EMAIL=?", new String[]{email}, null, null, null);
        if (cursor.getCount() < 1) // UserName Not Exist
        {
            cursor.close();
            return null;
        }
        cursor.moveToFirst();
        String password = cursor.getString(cursor.getColumnIndex("PASSWORD"));
        String contact = cursor.getString(cursor.getColumnIndex("CONTACT"));
        String gender = cursor.getString(cursor.getColumnIndex("GENDER"));
        String userName = cursor.getString(cursor.getColumnIndex("USERNAME"));
        String[] address = new String[5];
        String[] latlon = new String[5];
        String androidId = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        for (int i = 1; i <= 5; i++) {
            address[i - 1] = cursor.getString(cursor.getColumnIndex("ADDRESS" + i));
            latlon[i - 1] = cursor.getString(cursor.getColumnIndex("LATLON" + i));
        }
        UserDBObj u = new UserDBObj(androidId,userName, password, contact, gender, email, address, latlon);
        cursor.close();
        return u;
    }


    public void updateEntry(UserDBObj u, boolean resetPass) {
        ContentValues updatedValues = new ContentValues();
        // Assign values for each row.
        updatedValues.put("USERNAME", u.userName);
        if (resetPass) {
            updatedValues.put("PASSWORD", u.password);
        }
//        updatedValues.put("EMAIL", u.email);
        updatedValues.put("GENDER", u.gender);
        updatedValues.put("CONTACT", u.emergContact);
        for (int i = 1; i <= 5; i++) {
            updatedValues.put("ADDRESS" + i, u.address[i - 1]);
            updatedValues.put("LATLON" + i, u.latLon[i - 1]);
        }
        String where = "EMAIL = ?";
        db.update("LOGIN", updatedValues, where, new String[]{u.email});
    }
}

