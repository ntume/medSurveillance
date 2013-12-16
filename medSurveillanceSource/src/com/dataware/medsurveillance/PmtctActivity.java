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
import com.dataware.medsurveillance.GobiActivity.gobi;
import com.dataware.medsurveillance.includes.AlertDialogManager;
import com.dataware.medsurveillance.includes.ConnectionDetector;
import com.dataware.medsurveillance.includes.DatabaseHandler;
import com.dataware.medsurveillance.includes.GPSTracker;
import com.dataware.medsurveillance.includes.JSONParserCat;
import com.dataware.medsurveillance.includes.UserFunctions;
import com.devspark.appmsg.AppMsg;

public class PmtctActivity extends SherlockActivity {
	//String URL = "http://www.ntume.byethost7.com/medsurv/apiRequests.php";
	String URL = "http://ntumedev.me/medsurv/apiRequests.php";
	Button btnsave,btnreset;
	
	ToggleButton antenatal,adv_htc,postnatal,adv_type_feed;
	Spinner method_feeding;
	String strantenatal,stradv_htc,strpostnatal,stradv_type_feed,strmethod_feeding;
	
	String errmsgs;
	String userID,famID,personalID,pmtctID;
	
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
			    
		setContentView(R.layout.activity_pmtct);
		
		cd = new ConnectionDetector(getApplicationContext());	
		
		builder = new AlertDialog.Builder(this);
		
		Bundle extras = getIntent().getExtras();
        if (extras!=null)
        {
        	famID = extras.getString("famID");
        }
        
    	antenatal = (ToggleButton) findViewById(R.id.antenatal);
    	adv_htc = (ToggleButton) findViewById(R.id.adv_htc);
    	postnatal = (ToggleButton) findViewById(R.id.postnatal);
    	adv_type_feed = (ToggleButton) findViewById(R.id.adv_type_feed);
    	
    	method_feeding = (Spinner) findViewById(R.id.method_feeding);
    	
        btnsave = (Button) findViewById(R.id.btnsave);
        btnreset = (Button) findViewById(R.id.btnreset);
        
        btnsave.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v){
				if(cd.isConnectingToInternet()){
				    new pmtct().execute();			        
				}
				else if(!cd.isConnectingToInternet()){
					AppMsg.makeText(PmtctActivity.this, "No Internet Connection Found, Please Connect", AppMsg.STYLE_ALERT).show();
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
    class pmtct extends AsyncTask<String, String, String> {
 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(PmtctActivity.this);
            pDialog.setMessage("Saving PMTCT information. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
 
        /**
         * Getting url in background thread
         * */
        protected String doInBackground(String... param) {
            String res = "0";
            
        	strantenatal = String.valueOf(antenatal.isChecked());
        	stradv_htc = String.valueOf(adv_htc.isChecked());
        	strpostnatal = String.valueOf(postnatal.isChecked());
        	stradv_type_feed = String.valueOf(adv_type_feed.isChecked());
        	
        	strmethod_feeding = String.valueOf(method_feeding.getSelectedItem());
                   	
			DatabaseHandler db = new DatabaseHandler(getApplicationContext());
						
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("tag", "pmtct"));
			params.add(new BasicNameValuePair("famID", famID));
            params.add(new BasicNameValuePair("antenatal", strantenatal));
            params.add(new BasicNameValuePair("HCTAdvised", stradv_htc));
            params.add(new BasicNameValuePair("postnatal", strpostnatal));
            params.add(new BasicNameValuePair("methFeeding", strmethod_feeding));
            params.add(new BasicNameValuePair("typeFeeding", stradv_type_feed));
            
            // getting JSON string from URL
            String json = jsonParser.makeHttpRequest(URL, "POST",params);
 
            // Check your log cat for JSON reponse
            Log.d("JSON: ", json);
            
			try {
				jObj = new JSONObject(json);
			    if (jObj.getString(KEY_SUCCESS) != null) {
			        
			        res = jObj.getString(KEY_SUCCESS);
			        if(Integer.parseInt(res) == 1){
			            
			        	pmtctID = jObj.getString("ID");
			         
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
    			Intent openApp = new Intent(getApplicationContext(), HomeActivity.class);
		        Bundle extras = new Bundle();
		        extras.putString("famID", famID);
		        openApp.putExtras(extras);
		        startActivity(openApp);         		
        	}
        	else if(Integer.parseInt(res) == 0){
        		AppMsg.makeText(PmtctActivity.this, errmsgs, AppMsg.STYLE_ALERT).show();
        	}        	
            pDialog.dismiss();
        }
    }
	

}
