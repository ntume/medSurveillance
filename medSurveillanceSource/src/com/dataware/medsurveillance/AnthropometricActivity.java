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
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;

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

public class AnthropometricActivity extends SherlockActivity implements OnSeekBarChangeListener {
	//String URL = "http://www.ntume.byethost7.com/medsurv/apiRequests.php";
	String URL = "http://ntumedev.me/medsurv/apiRequests.php";
	Button btnsave,btnreset;
	
	SeekBar heartratesleep,respiratory,diastolic,systolic;
	FormEditText edttemp,edtdiastolic,edtsystolic,edtpulse,edtrespiratory,edturine,edtweight,edtheight,edtbmi;
	Spinner spinnerbg,spinnerhb;
	String stredttemp,stredtdiastolic,stredtsystolic,stredtpulse,stredtrespiratory,stredturine,stredtweight,stredtheight,stredtbmi,strspinnerbg,strspinnerhb;
	String errmsgs;
	String userID,famID,personalID,anthroID;
	
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
			    
		setContentView(R.layout.activity_anthropometric);
		
		cd = new ConnectionDetector(getApplicationContext());	
		
		builder = new AlertDialog.Builder(this);
		
		Bundle extras = getIntent().getExtras();
        if (extras!=null)
        {
        	personalID = extras.getString("personalID");
        	famID = extras.getString("famID");
        	userID = extras.getString("userID");	
        }
		
        edttemp = (FormEditText) findViewById(R.id.temperature);
        edtdiastolic = (FormEditText) findViewById(R.id.diastolic);	
        edtsystolic = (FormEditText) findViewById(R.id.systolic);
        edtpulse = (FormEditText) findViewById(R.id.pulse);

        edtrespiratory = (FormEditText) findViewById(R.id.respiratory);
        edturine = (FormEditText) findViewById(R.id.urine);			 
        edtweight = (FormEditText) findViewById(R.id.weight);
        edtheight = (FormEditText) findViewById(R.id.height);	
        edtbmi = (FormEditText) findViewById(R.id.bmi);
        
        spinnerbg = (Spinner) findViewById(R.id.bg);
        spinnerhb = (Spinner) findViewById(R.id.hb);
       
        respiratory = (SeekBar) findViewById(R.id.sliderrespiratory);
        diastolic = (SeekBar) findViewById(R.id.sliderdiastolic);	
        systolic = (SeekBar) findViewById(R.id.slidersystolic);
                
        diastolic.setOnSeekBarChangeListener(this);
		respiratory.setOnSeekBarChangeListener(this);
		systolic.setOnSeekBarChangeListener(this);
		
        btnsave = (Button) findViewById(R.id.btnsave);
        btnreset = (Button) findViewById(R.id.btnreset);
        
        btnsave.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v){
				if(cd.isConnectingToInternet()){
				    FormEditText[] allFields    = {edttemp,edtdiastolic,edtsystolic,edtpulse,edtrespiratory,edturine,edtweight,edtheight,edtbmi};
					boolean allValid = true;
			        for (FormEditText field: allFields) {
			            allValid = field.testValidity() && allValid;
			        }
			        if (allValid) {
			        	new anthropometric().execute();
			        } else {
			        	AppMsg.makeText(AnthropometricActivity.this, "Some of your information is missing", AppMsg.STYLE_ALERT).show();
			        }
				}
				else if(!cd.isConnectingToInternet()){
					AppMsg.makeText(AnthropometricActivity.this, "No Internet Connection Found, Please Connect", AppMsg.STYLE_ALERT).show();
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
	
	@Override
	public void onProgressChanged(SeekBar seekbar, int progress, boolean arg2) {
		// TODO Auto-generated method stub
		switch (seekbar.getId()) {

		    case R.id.sliderdiastolic:
		    	edtdiastolic.setText(progress + " mmHg");
		        break;
		    case R.id.sliderrespiratory:
		    	edtrespiratory.setText(progress + " ipm");
		        break;
	
		    case R.id.slidersystolic:
		    	edtsystolic.setText(progress + " mmHg");
		        break;
		    }		
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
		
	}
	

	/**
     * Background Async Task to personal info
     * */
    class anthropometric extends AsyncTask<String, String, String> {
 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AnthropometricActivity.this);
            pDialog.setMessage("Saving Anthropometric information. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
 
        /**
         * Getting url in background thread
         * */
        protected String doInBackground(String... param) {
            String res = "0";
            
            stredttemp = edttemp.getText().toString();
            stredtdiastolic = edtdiastolic.getText().toString();
            stredtsystolic = edtsystolic.getText().toString();
            stredtpulse = edtpulse.getText().toString();
            stredtrespiratory = edtrespiratory.getText().toString();
            stredturine = edturine.getText().toString();
            stredtweight = edtweight.getText().toString();
            stredtheight = edtheight.getText().toString();
            stredtbmi = edtbmi.getText().toString();
            strspinnerbg = String.valueOf(spinnerbg.getSelectedItem());
            strspinnerhb = String.valueOf(spinnerhb.getSelectedItem());
                        
			DatabaseHandler db = new DatabaseHandler(getApplicationContext());
			
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("tag", "anthropometric"));
			params.add(new BasicNameValuePair("persID", personalID));
            params.add(new BasicNameValuePair("systolic", stredtsystolic));
            params.add(new BasicNameValuePair("diastolic", stredtdiastolic));
            params.add(new BasicNameValuePair("pulse", stredtpulse));
            params.add(new BasicNameValuePair("respiratoryRate", stredtrespiratory));
            params.add(new BasicNameValuePair("temperature", stredttemp));
            params.add(new BasicNameValuePair("bg", strspinnerbg));
            params.add(new BasicNameValuePair("hb", strspinnerhb));
            params.add(new BasicNameValuePair("urine", stredturine));
            params.add(new BasicNameValuePair("weight", stredtweight));
            params.add(new BasicNameValuePair("height", stredtheight));
            params.add(new BasicNameValuePair("bmi", stredtbmi));

            // getting JSON string from URL
            String json = jsonParser.makeHttpRequest(URL, "POST",params);
 
            // Check your log cat for JSON reponse
            Log.d("JSON: ", json);
            
			try {
				jObj = new JSONObject(json);
			    if (jObj.getString(KEY_SUCCESS) != null) {
			        
			        res = jObj.getString(KEY_SUCCESS);
			        if(Integer.parseInt(res) == 1){
			            
			        	anthroID = jObj.getString("ID");
			         
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
        		AppMsg.makeText(AnthropometricActivity.this, errmsgs, AppMsg.STYLE_ALERT).show();
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
