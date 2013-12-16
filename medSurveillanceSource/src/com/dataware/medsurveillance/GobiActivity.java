package com.dataware.medsurveillance;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.ToggleButton;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.dataware.medsurveillance.includes.AlertDialogManager;
import com.dataware.medsurveillance.includes.ConnectionDetector;
import com.dataware.medsurveillance.includes.DatabaseHandler;
import com.dataware.medsurveillance.includes.GPSTracker;
import com.dataware.medsurveillance.includes.JSONParserCat;
import com.dataware.medsurveillance.includes.UserFunctions;
import com.devspark.appmsg.AppMsg;

public class GobiActivity extends SherlockActivity {
	//String URL = "http://www.ntume.byethost7.com/medsurv/apiRequests.php";
	String URL = "http://ntumedev.me/medsurv/apiRequests.php";
	Button btnsave,btnreset;
	
	ToggleButton oral_hydration,food_suppliments;
	Spinner growth_monitored,method_of_feeding,immunised,female_educ;
	String stroral_hydration,strfood_suppliments,strgrowth_monitored,strmethod_of_feeding,strimmunised,strfemale_educ;
	
	String errmsgs;
	String userID,famID,personalID,gobiID;
	
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
    
    GPSTracker gps;
    
    double latitude,longitude;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
			    
		setContentView(R.layout.activity_gobi);
		
		cd = new ConnectionDetector(getApplicationContext());	
		
		builder = new AlertDialog.Builder(this);
		
		Bundle extras = getIntent().getExtras();
        if (extras!=null)
        {
        	personalID = extras.getString("personalID");
        	famID = extras.getString("famID");
        	userID = extras.getString("userID");	
        }
        
       	
    	oral_hydration = (ToggleButton) findViewById(R.id.oral_hydration);
    	food_suppliments = (ToggleButton) findViewById(R.id.food_suppliments);
    	
    	growth_monitored = (Spinner) findViewById(R.id.growth_monitored);
    	method_of_feeding = (Spinner) findViewById(R.id.method_of_feeding);
    	immunised = (Spinner) findViewById(R.id.immunised);
    	female_educ = (Spinner) findViewById(R.id.female_educ);
    	
        btnsave = (Button) findViewById(R.id.btnsave);
        btnreset = (Button) findViewById(R.id.btnreset);
        
        btnsave.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v){
				if(cd.isConnectingToInternet()){
				    new gobi().execute();			        
				}
				else if(!cd.isConnectingToInternet()){
					AppMsg.makeText(GobiActivity.this, "No Internet Connection Found, Please Connect", AppMsg.STYLE_ALERT).show();
				}
			}		
        });
        
	}
	
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	
        return super.onCreateOptionsMenu(menu);
    }

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		return super.onOptionsItemSelected(item);
	}
	

	/**
     * Background Async Task to personal info
     * */
    class gobi extends AsyncTask<String, String, String> {
 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(GobiActivity.this);
            pDialog.setMessage("Saving GOBI-FFF information. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
 
        /**
         * Getting url in background thread
         * */
        protected String doInBackground(String... param) {
            String res = "0";
            
        	stroral_hydration = String.valueOf(oral_hydration.isChecked());
        	strfood_suppliments = String.valueOf(food_suppliments.isChecked());
        	
        	strgrowth_monitored = String.valueOf(growth_monitored.getSelectedItem());
        	strmethod_of_feeding = String.valueOf(method_of_feeding.getSelectedItem());
        	strimmunised = String.valueOf(immunised.getSelectedItem());    
        	strfemale_educ = String.valueOf(female_educ.getSelectedItem());
                   	
			DatabaseHandler db = new DatabaseHandler(getApplicationContext());
		
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("tag", "gobi"));
			params.add(new BasicNameValuePair("personalID", personalID));
            params.add(new BasicNameValuePair("growthMonitored", strgrowth_monitored));
            params.add(new BasicNameValuePair("oralHydration", stroral_hydration));
            params.add(new BasicNameValuePair("methFeeding", strmethod_of_feeding));
            params.add(new BasicNameValuePair("immunised", strimmunised));
            params.add(new BasicNameValuePair("femaleEducation", strfemale_educ));
            params.add(new BasicNameValuePair("foodSuppliments", strfood_suppliments));
            
            // getting JSON string from URL
            String json = jsonParser.makeHttpRequest(URL, "POST",params);
 
            // Check your log cat for JSON reponse
            Log.d("JSON: ", json);
            
			try {
				jObj = new JSONObject(json);
			    if (jObj.getString(KEY_SUCCESS) != null) {
			        
			        res = jObj.getString(KEY_SUCCESS);
			        if(Integer.parseInt(res) == 1){
			            
			        	gobiID = jObj.getString("ID");
			         
			        }else if(Integer.parseInt(res) == 0){
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
            		
        	if(Integer.parseInt(res) == 1){        		
    			Intent openApp = new Intent(getApplicationContext(), ListPersonsFormsActivity.class);
		        Bundle extras = new Bundle();
		        extras.putString("famID", famID);
		        extras.putString("userID", userID);
		        extras.putString("personalID", personalID);
		        openApp.putExtras(extras);
		        startActivity(openApp);    
		        finish();
        	}
        	else if(Integer.parseInt(res) == 0){
        		AppMsg.makeText(GobiActivity.this, errmsgs, AppMsg.STYLE_ALERT).show();
        	}        	
            pDialog.dismiss();
        }
    }
	
    @Override
   	public void onBackPressed() {		
    	Intent openApp = new Intent(getApplicationContext(), ListPersonsFormsActivity.class);
        Bundle extras = new Bundle();
        extras.putString("famID", famID);
        extras.putString("userID", userID);
        extras.putString("personalID", personalID);
        openApp.putExtras(extras);
        startActivity(openApp);  
        finish();
   	}
}
