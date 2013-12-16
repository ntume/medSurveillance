package com.dataware.medsurveillance;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.andreabaccega.widget.FormEditText;
import com.dataware.medsurveillance.includes.AlertDialogManager;
import com.dataware.medsurveillance.includes.ConnectionDetector;
import com.dataware.medsurveillance.includes.DatabaseHandler;
import com.dataware.medsurveillance.includes.JSONParserCat;
import com.dataware.medsurveillance.includes.UserFunctions;
import com.devspark.appmsg.AppMsg;

public class LoginActivity extends SherlockActivity {
	private static final int RESULT_SETTINGS = 1;
	//String URL = "http://www.ntume.byethost7.com/medsurv/apiRequests.php";
	String URL = "http://ntumedev.me/medsurv/apiRequests.php";
	Button btnLogin;
	FormEditText inputUsername;
	FormEditText inputPassword;
	TextView loginErrorMsg;
	String username;
	String password;
	Boolean authenticated = false;
	String errmsgs;
	String userID;
	
	// Connection detector class
    ConnectionDetector cd;
    // flag for Internet connection status
    Boolean isInternetPresent = false;
    
    // Progress Dialog
    private ProgressDialog pDialog;
    
    JSONParserCat jsonParser = new JSONParserCat();
	
	JSONObject jObj = new JSONObject();
	InputStream in = null;
    String connectionMade = "False";
    // Alert dialog manager
    AlertDialogManager alert = new AlertDialogManager();
    
    private static String KEY_SUCCESS = "success";
    private static String KEY_ERROR = "error";
    
    //AHSApplication ahsApp;
    UserFunctions userFunction = new UserFunctions();
    public static String Language;
    
    AlertDialog.Builder builder;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
			    
		setContentView(R.layout.activity_login);
		
		cd = new ConnectionDetector(getApplicationContext());	
		
		builder = new AlertDialog.Builder(this);
		
		String msg = "Please Log On To Access the App";
		String task = "false";
		String logmsg="";
		
		Bundle extras = getIntent().getExtras();
        
        if (extras!=null)
        {        	
        	task = extras.getString("task");     
        	logmsg = extras.getString("usermsg");     
        }
        
        if(task.equals("false"))		
        	AppMsg.makeText(LoginActivity.this, msg, AppMsg.STYLE_INFO).show();
        
        else if(task.equals("true"))	
        	AppMsg.makeText(LoginActivity.this, "Logged off successfully", AppMsg.STYLE_CONFIRM).show();
        
        else if(task.equals("true_expire"))	
        	AppMsg.makeText(LoginActivity.this, logmsg, AppMsg.STYLE_CONFIRM).show();
        
        else
        	AppMsg.makeText(LoginActivity.this, task, AppMsg.STYLE_CONFIRM).show();
        
        inputUsername = (FormEditText) findViewById(R.id.loginEmail);
		inputPassword = (FormEditText) findViewById(R.id.loginPassword);
		loginErrorMsg = (TextView) findViewById(R.id.login_error);		
        Button loginbtn = (Button) findViewById(R.id.btnLogin);
        

        loginbtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v){
				if(cd.isConnectingToInternet()){
				    FormEditText[] allFields    = { inputUsername, inputPassword };
					boolean allValid = true;
			        for (FormEditText field: allFields) {
			            allValid = field.testValidity() && allValid;
			        }
			        if (allValid) {
			        	new login().execute();
			        } else {
			        	AppMsg.makeText(LoginActivity.this, "Some of your log in information missing", AppMsg.STYLE_ALERT).show();
			        }
				}
				else if(!cd.isConnectingToInternet()){
					AppMsg.makeText(LoginActivity.this, "No Internet Connection Found, Please Connect", AppMsg.STYLE_ALERT).show();
				}
			}		
        });
	}

	
    /**
     * Background Async Task to login
     * */
    class login extends AsyncTask<String, String, String> {
 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage("Logging on. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
 
        /**
         * Getting url in background thread
         * */
        protected String doInBackground(String... param) {
            String res = "0";
            username = inputUsername.getText().toString();
			password = inputPassword.getText().toString();
			DatabaseHandler db = new DatabaseHandler(getApplicationContext());
			
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("tag", "login"));
            params.add(new BasicNameValuePair("username", username));
            params.add(new BasicNameValuePair("password", password));

            // getting JSON string from URL
            String json = jsonParser.makeHttpRequest(URL, "POST",params);
 
            // Check your log cat for JSON reponse
            Log.d("JSON: ", json);
            
			try {
				jObj = new JSONObject(json);
			    if (jObj.getString(KEY_SUCCESS) != null) {
			        
			        res = jObj.getString(KEY_SUCCESS);
			        if(Integer.parseInt(res) == 1){
			            
			            JSONObject json_user = jObj.getJSONObject("user");
				           				                                  
			            userID = json_user.getString("userID");
			            String fname = json_user.getString("fullname");
			            
			            db.logoff();
			            
			            db.addUser(userID, fname);
			            authenticated = true;
			         
			        }else if(Integer.parseInt(res) == 0){
			        	authenticated = false;	
			        	errmsgs = jObj.getString("error_msg");
			        }
			    }
			} catch (JSONException e) {
				Log.w("****************check", e);
				pDialog.dismiss();
			}	
 
            return res;
        }
 
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String res) {
            		
        	if(authenticated){        		
    			Intent openApp = new Intent(getApplicationContext(), HomeActivity.class);
		        Bundle extras = new Bundle();		        
		        extras.putString("userID", userID);
		        openApp.putExtras(extras);
		        startActivity(openApp);  
		        finish();
        	}
        	else if(!authenticated){
        		AppMsg.makeText(LoginActivity.this, errmsgs, AppMsg.STYLE_ALERT).show();
        	}        	
            pDialog.dismiss();
        }
    }
    
    @Override
	public void onBackPressed() {		
    	builder
    	.setTitle("Confirm Exit of App")
    	.setMessage("Are you sure you want to quit?")
    	.setIcon(R.drawable.alert3)
    	.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int which) {			      	
    	    	finish();
    	    }
    	})
    	.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int which) {			      	
    	    	
    	    }
    	})					//Do nothing on no
    	.show();      	
	}

}
