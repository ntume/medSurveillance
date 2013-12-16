package com.dataware.medsurveillance.includes;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "mduka";

	// table names
	private static final String TABLE_USER = "user";
	private static final String TABLE_TRANS = "transaction";


	// Table Columns names
	//user
	private static final String KEY_FNAME = "firstname";
	private static final String KEY_UID = "uid";
	
	//transaction
	private static final String KEY_TID = "id";
	private static final String KEY_AMOUNT = "amount";
	private static final String KEY_PROVIDER = "provider";
	private static final String KEY_SERVICE = "serviceType";
	private static final String KEY_TYPE = "transType";
	private static final String KEY_STATUS = "status";
	private static final String KEY_CELLNUM = "subscriberref";
	private static final String KEY_DATE = "date";


	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
				+ KEY_UID + " TEXT," 
				+ KEY_FNAME + " TEXT" + ")";
		db.execSQL(CREATE_USER_TABLE);	
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
		// Create tables again
		onCreate(db);
	}
	
	/**
	 * Storing user details in database
	 * */

	public void addUser(String userID, String fname) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_UID, userID); // 
		values.put(KEY_FNAME, fname); // Name
				// Inserting Row
		db.replace(TABLE_USER, null, values);
		db.close(); // Closing database connection
	}
	
	/**
	 * Getting user data from database
	 * */
	public HashMap<String, String> getUserDetails(){
		HashMap<String,String> user = new HashMap<String,String>();
		String selectQuery = "SELECT  * FROM " + TABLE_USER;
		 
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if(cursor.getCount() > 0){
        	user.put("userid", Integer.toString(cursor.getInt(0)));
        	user.put("fname", cursor.getString(1));        	
        }
        cursor.close();
        db.close();
		// return user
		return user;
	}
	

	public int getRowCountUser() {
		String countQuery = "SELECT  * FROM " + TABLE_USER;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		int rowCount = cursor.getCount();
		db.close();
		cursor.close();
		
		// return row count
		return rowCount;
	}
	
	
	
	/**
	 * Re crate database
	 * Delete all tables and create them again
	 * */
	public void resetTables(){
		SQLiteDatabase db = this.getWritableDatabase();
		// Delete All Rows
		//db.delete(TABLE_USER, null, null);		
		db.close();
	}
	
	public void logoff(){
		SQLiteDatabase db = this.getWritableDatabase();
		// Delete All Rows
		db.delete(TABLE_USER, null, null);
		db.close();
	}

}
