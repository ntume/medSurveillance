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

public class HastActivity extends SherlockActivity {
	//String URL = "http://www.ntume.byethost7.com/medsurv/apiRequests.php";
	String URL = "http://ntumedev.me/medsurv/apiRequests.php";
	Button btnsave,btnreset;
	
	ToggleButton hiv_status,hiv_knowledge,motivate_vct,referred_vct,tb_signs,compliance,cured;
	Spinner vct_period,tb,treatment;
	String strhiv_status,strhiv_knowledge,strmotivate_vct,strreferred_vct,strtb_signs,strcompliance,strcured,strvct_period,strtb,strtreatment;
	String errmsgs;
	String userID,famID,personalID,hastID;
	
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
			    
		setContentView(R.layout.activity_hast);
		
		cd = new ConnectionDetector(getApplicationContext());	
		
		builder = new AlertDialog.Builder(this);
		
		Bundle extras = getIntent().getExtras();
        if (extras!=null)
        {
        	personalID = extras.getString("personalID");
        	famID = extras.getString("famID");
        	userID = extras.getString("userID");	
        }
		
    	hiv_status = (ToggleButton) findViewById(R.id.hiv_status);
    	hiv_knowledge = (ToggleButton) findViewById(R.id.hiv_knowledge);	
    	motivate_vct = (ToggleButton) findViewById(R.id.motivate_vct);
    	referred_vct = (ToggleButton) findViewById(R.id.referred_vct);
    	tb_signs = (ToggleButton) findViewById(R.id.tb_signs);
    	compliance = (ToggleButton) findViewById(R.id.compliance);			 
    	cured = (ToggleButton) findViewById(R.id.cured);
				
    	vct_period = (Spinner) findViewById(R.id.vct_period);
    	tb = (Spinner) findViewById(R.id.tb);
    	treatment = (Spinner) findViewById(R.id.treatment);
		
        btnsave = (Button) findViewById(R.id.btnsave);
        btnreset = (Button) findViewById(R.id.btnreset);
        
        btnsave.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v){
				if(cd.isConnectingToInternet()){
				    new hast().execute();			        
				}
				else if(!cd.isConnectingToInternet()){
					AppMsg.makeText(HastActivity.this, "No Internet Connection Found, Please Connect", AppMsg.STYLE_ALERT).show();
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
    class hast extends AsyncTask<String, String, String> {
 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(HastActivity.this);
            pDialog.setMessage("Saving HAST information. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
 
        /**
         * Getting url in background thread
         * */
        protected String doInBackground(String... param) {
            String res = "0";
            
        	strhiv_status = String.valueOf(hiv_status.isChecked());
        	strhiv_knowledge = String.valueOf(hiv_knowledge.isChecked());
        	strmotivate_vct = String.valueOf(motivate_vct.isChecked());
        	strreferred_vct = String.valueOf(referred_vct.isChecked());
        	strtb_signs = String.valueOf(tb_signs.isChecked());
        	strcompliance = String.valueOf(compliance.isChecked());
        	strcured = String.valueOf(cured.isChecked());
        	strvct_period = String.valueOf(vct_period.getSelectedItem());
        	strtb = String.valueOf(tb.getSelectedItem());
        	strtreatment = String.valueOf(treatment.getSelectedItem());            
                   	
			DatabaseHandler db = new DatabaseHandler(getApplicationContext());
			
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("tag", "hast"));
			params.add(new BasicNameValuePair("personalID", personalID));
            params.add(new BasicNameValuePair("HIVStatus", strhiv_status));
            params.add(new BasicNameValuePair("HIVKnowledge", strhiv_knowledge));
            params.add(new BasicNameValuePair("vct", strvct_period));
            params.add(new BasicNameValuePair("vctMotivated", strmotivate_vct));
            params.add(new BasicNameValuePair("vctReferred", strreferred_vct));
            params.add(new BasicNameValuePair("TBSigns", strtb_signs));
            params.add(new BasicNameValuePair("TBType", strtb));
            params.add(new BasicNameValuePair("onTreatment", strtreatment));
            params.add(new BasicNameValuePair("treatmentCompliance", strcompliance));
            params.add(new BasicNameValuePair("cured", strcured));
            
            // getting JSON string from URL
            String json = jsonParser.makeHttpRequest(URL, "POST",params);
 
            // Check your log cat for JSON reponse
            Log.d("JSON: ", json);
            
			try {
				jObj = new JSONObject(json);
			    if (jObj.getString(KEY_SUCCESS) != null) {
			        
			        res = jObj.getString(KEY_SUCCESS);
			        if(Integer.parseInt(res) == 1){
			            
			        	hastID = jObj.getString("ID");
			         
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
        		AppMsg.makeText(HastActivity.this, errmsgs, AppMsg.STYLE_ALERT).show();
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
