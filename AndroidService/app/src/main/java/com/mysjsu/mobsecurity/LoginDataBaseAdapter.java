package com.mysjsu.mobsecurity;

/**
 * Created by Poornima on 10/21/15.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class LoginDataBaseAdapter {
    static final String DATABASE_NAME = "mobsec.db";
    static final int DATABASE_VERSION = 1;
    public static final int NAME_COLUMN = 1;
    // TODO: Create public field for each column in your table.
    // SQL Statement to create a new database.
    static final String DATABASE_CREATE = "create table " + "LOGIN" +
            "( " + "ID" + " integer primary key autoincrement," + "USERNAME  text,PASSWORD text, CONTACT text, OCCUPATION text, EMAIL text); ";
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
        newValues.put("OCCUPATION", u.occupation);
        newValues.put("CONTACT", u.emergContact);

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
        String occ = cursor.getString(cursor.getColumnIndex("OCCUPATION"));
        String userName = cursor.getString(cursor.getColumnIndex("USERNAME"));
        UserDBObj u = new UserDBObj(userName, password, contact, occ, email);
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
        updatedValues.put("OCCUPATION", u.occupation);
        updatedValues.put("CONTACT", u.emergContact);
        String where = "EMAIL = ?";
        db.update("LOGIN", updatedValues, where, new String[]{u.email});
    }
}

