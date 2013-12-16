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
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.ToggleButton;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.andreabaccega.widget.FormEditText;
import com.dataware.medsurveillance.includes.AlertDialogManager;
import com.dataware.medsurveillance.includes.ConnectionDetector;
import com.dataware.medsurveillance.includes.DatabaseHandler;
import com.dataware.medsurveillance.includes.GPSTracker;
import com.dataware.medsurveillance.includes.JSONParserCat;
import com.dataware.medsurveillance.includes.UserFunctions;
import com.devspark.appmsg.AppMsg;

public class PersonalInfoActivity extends SherlockActivity {
	//String URL = "http://www.ntume.byethost7.com/medsurv/apiRequests.php";
	String URL = "http://ntumedev.me/medsurv/apiRequests.php";
	Button btnsave,btnreset;
	
	FormEditText idNum,fullname, medic_hist, surg_hist;
	Spinner gender,educ,emp,caregiver,dom_violence;
	ToggleButton mental,mentaltreat,litrate,smoker,alcohol;
	String stridNum,strfullname, strmedic_hist, strsurg_hist,strgender,streduc,stremp,strcaregiver,strdom_violence,strmental,strmentaltreat,strlitrate,strsmoker,stralcohol;
	String errmsgs;
	String userID,famID,personalID;
	
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
			    
		setContentView(R.layout.activity_personalinfo);
		
		cd = new ConnectionDetector(getApplicationContext());	
		
		builder = new AlertDialog.Builder(this);
		
		Bundle extras = getIntent().getExtras();
        if (extras!=null)
        {
        	famID = extras.getString("famID");
        	userID = extras.getString("userID");
        }
		
		
		idNum = (FormEditText) findViewById(R.id.idNum);
		fullname = (FormEditText) findViewById(R.id.fullname);	
		medic_hist = (FormEditText) findViewById(R.id.medic_hist);
		surg_hist = (FormEditText) findViewById(R.id.surg_hist);

		mental = (ToggleButton) findViewById(R.id.mental);
		mentaltreat = (ToggleButton) findViewById(R.id.mentaltreat);			 
		litrate = (ToggleButton) findViewById(R.id.litrate);
		smoker = (ToggleButton) findViewById(R.id.smoker);	
		alcohol = (ToggleButton) findViewById(R.id.alcohol);
		
		gender = (Spinner) findViewById(R.id.gender);
		educ = (Spinner) findViewById(R.id.educ);
		emp = (Spinner) findViewById(R.id.emp);
		caregiver = (Spinner) findViewById(R.id.caregiver);
		dom_violence = (Spinner) findViewById(R.id.dom_violence);		 
		
        btnsave = (Button) findViewById(R.id.btnsave);
        btnreset = (Button) findViewById(R.id.btnreset);
        
        btnsave.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v){
				if(cd.isConnectingToInternet()){
				    FormEditText[] allFields    = {idNum,fullname, medic_hist, surg_hist};
					boolean allValid = true;
			        for (FormEditText field: allFields) {
			            allValid = field.testValidity() && allValid;
			        }
			        if (allValid) {
			        	new personalinfo().execute();
			        } else {
			        	AppMsg.makeText(PersonalInfoActivity.this, "Some of your information is missing", AppMsg.STYLE_ALERT).show();
			        }
				}
				else if(!cd.isConnectingToInternet()){
					AppMsg.makeText(PersonalInfoActivity.this, "No Internet Connection Found, Please Connect", AppMsg.STYLE_ALERT).show();
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
    class personalinfo extends AsyncTask<String, String, String> {
 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(PersonalInfoActivity.this);
            pDialog.setMessage("Saving personal information. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
 
        /**
         * Getting url in background thread
         * */
        protected String doInBackground(String... param) {
            String res = "0";
            
            stridNum = idNum.getText().toString();
            strfullname = fullname.getText().toString();
        	strmedic_hist = medic_hist.getText().toString();
        	strsurg_hist = surg_hist.getText().toString();
        
        	strmental = String.valueOf(mental.isChecked());
        	strmentaltreat = String.valueOf(mentaltreat.isChecked());
        	strlitrate = String.valueOf(litrate.isChecked());
        	strsmoker = String.valueOf(smoker.isChecked());
        	stralcohol = String.valueOf(alcohol.isChecked());
        	
        	strgender = String.valueOf(gender.getSelectedItem());
        	streduc = String.valueOf(educ.getSelectedItem());
        	stremp = String.valueOf(emp.getSelectedItem());
        	strcaregiver = String.valueOf(caregiver.getSelectedItem());
        	strdom_violence = String.valueOf(dom_violence.getSelectedItem());
        	
			DatabaseHandler db = new DatabaseHandler(getApplicationContext());
			
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("tag", "personal"));
			params.add(new BasicNameValuePair("famID", famID));
            params.add(new BasicNameValuePair("IDNum", stridNum));
            params.add(new BasicNameValuePair("fullName", strfullname));
            params.add(new BasicNameValuePair("medicalHistory", strmedic_hist));
            params.add(new BasicNameValuePair("surgicalHistory", strsurg_hist));
            params.add(new BasicNameValuePair("mental", strmental));
            params.add(new BasicNameValuePair("mentalTreatment", strmentaltreat));
            params.add(new BasicNameValuePair("literacy", strlitrate));
            params.add(new BasicNameValuePair("smoker", strsmoker));
            params.add(new BasicNameValuePair("alcohol", stralcohol));
            params.add(new BasicNameValuePair("gender", strgender));
            params.add(new BasicNameValuePair("educationLevel", streduc));
            params.add(new BasicNameValuePair("employment", stremp));
            params.add(new BasicNameValuePair("careGiver", strcaregiver));
            params.add(new BasicNameValuePair("domesticViolence", strdom_violence));

            // getting JSON string from URL
            String json = jsonParser.makeHttpRequest(URL, "POST",params);
 
            // Check your log cat for JSON reponse
            Log.d("JSON: ", json);
            
			try {
				jObj = new JSONObject(json);
			    if (jObj.getString(KEY_SUCCESS) != null) {
			        
			        res = jObj.getString(KEY_SUCCESS);
			        if(Integer.parseInt(res) == 1){
			            
			        	personalID = jObj.getString("ID");
			         
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
        		AppMsg.makeText(PersonalInfoActivity.this, errmsgs, AppMsg.STYLE_ALERT).show();
        	}        	
            pDialog.dismiss();
        }
    }
	

}
