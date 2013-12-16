package com.dataware.medsurveillance.includes;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class UserFunctions {
	
	private JSONParserCat jsonParser;
	 //jsonParser = new JSONParserCat();
	
	private static String userURL = "http://dev.mduka.co.ug/services/user.php";
	
	
		
	private static String login_tag = "login";
	private static String register_tag = "register";
	//private static String questions_tag = "questions";
	
	// constructor
	public UserFunctions(){
		jsonParser = new JSONParserCat();
	}
	

	/**
	 * function make Login Request
	 * @param email
	 * @param password
	 * */
	public String loginUser(String email, String password){
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("act", "1"));
		params.add(new BasicNameValuePair("username", email));
		params.add(new BasicNameValuePair("userpass", password));
		
		String json = jsonParser.makeHttpRequest(userURL, "GET", params);
		
				// return json
	    Log.e("JSON", json);
		return json;
	}
	
	/**
	 * function make Forget Password
	 * @param email
	 * @param 
	 * */
	public String forgetPassword(String username){
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("act", "2"));
		params.add(new BasicNameValuePair("username", username));
		String json = jsonParser.makeHttpRequest(userURL,"GET", params);
		// return json
		// Log.e("JSON", json.toString());
		return json;
	}
	/**
	 * function make register Request
	 * @param name
	 * @param email
	 * @param password
	 * */
	public String registerUser(String fname, String lname, String gender, String email, String password1, String password2){
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("act", "4"));
		params.add(new BasicNameValuePair("lastname", lname));
		params.add(new BasicNameValuePair("firstname", fname));
		params.add(new BasicNameValuePair("email", email));
		params.add(new BasicNameValuePair("password1", password1));
		params.add(new BasicNameValuePair("password2", password2));
		
		// getting JSON Object
		String json = jsonParser.makeHttpRequest(userURL,"GET", params);
		// return json
		return json;
	}
	
	public String updateUser(String fname, String lname, String gender, String email,String uid, String userpass, String username){
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userid", uid));
		params.add(new BasicNameValuePair("act", "5"));
		params.add(new BasicNameValuePair("lastname", lname));
		params.add(new BasicNameValuePair("firstname", fname));
		params.add(new BasicNameValuePair("email", email));
		
		// getting JSON Object
		String json = jsonParser.makeHttpRequest(userURL,"GET", params);
		// return json
		return json;
	}
	
	
	public String changePassword(String Uid, String NewPassword, String oldPassword){
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("act", "3"));
		params.add(new BasicNameValuePair("userid", Uid));		
		params.add(new BasicNameValuePair("password1", oldPassword));	
		params.add(new BasicNameValuePair("password2", NewPassword));	
		// getting JSON Object
		String json = jsonParser.makeHttpRequest(userURL,"GET",  params);
		// return json
		return json;
	}
	
	
	/**
	 * Function get Login status
	 * */
	public boolean isUserLoggedIn(Context context){
		DatabaseHandler db = new DatabaseHandler(context);
		int count = db.getRowCountUser();
		if(count > 0){
			// user logged in
			return true;
		}
		return false;
	}
	
	/**
	 * Function to logout user
	 * Reset Database
	 * */
	public boolean logoutUser(Context context){
		DatabaseHandler db = new DatabaseHandler(context);
		db.resetTables();
		return true;
	}
	
}
