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

public class DemographicActivity extends SherlockActivity implements OnSeekBarChangeListener {
	//String URL = "http://www.ntume.byethost7.com/medsurv/apiRequests.php";
	String URL = "http://ntumedev.me/medsurv/apiRequests.php";
	Button btnsave,btnreset;
	
	SeekBar sliderfamilynum,sliderhouserooms;
	FormEditText famnum,numfamily,houserooms,presaddress,prevaddress,medhistory;
	Spinner famtypespinner,languagespinner,breadwinnerspinner,incomespinner,sourceincomespinner,housingspinner,presstayspinner,prevstayspinner;
	String famnumber,numberfam,numrooms,presadd,prevadd,famtype,language,breadwinner,income,sourceincome,housing,presstay,prevstay,famMedicalHistory,gpsCordinates;
	String errmsgs;
	String userID,famID;
	
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
			    
		setContentView(R.layout.activity_demographic);
		
		cd = new ConnectionDetector(getApplicationContext());	
		
		builder = new AlertDialog.Builder(this);
		
		Bundle extras = getIntent().getExtras();
        if (extras!=null)
        {
        	userID = extras.getString("userID");
        }
		

        gps = new GPSTracker(DemographicActivity.this);

        // check if GPS enabled     
        if(gps.canGetLocation()){                 
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
        }else{
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
		
		famnum = (FormEditText) findViewById(R.id.famnum);
		numfamily = (FormEditText) findViewById(R.id.numfamily);	
		houserooms = (FormEditText) findViewById(R.id.houserooms);
		presaddress = (FormEditText) findViewById(R.id.presaddress);	
		prevaddress = (FormEditText) findViewById(R.id.prevaddress);
		medhistory = (FormEditText) findViewById(R.id.medhistory);	
		 
		famtypespinner = (Spinner) findViewById(R.id.famtypespinner);
		languagespinner = (Spinner) findViewById(R.id.languagespinner);	
		breadwinnerspinner = (Spinner) findViewById(R.id.breadwinnerspinner);
		incomespinner = (Spinner) findViewById(R.id.incomespinner);
		sourceincomespinner = (Spinner) findViewById(R.id.sourceincomespinner);
		housingspinner = (Spinner) findViewById(R.id.housingspinner);
		presstayspinner = (Spinner) findViewById(R.id.presstayspinner);
		prevstayspinner = (Spinner) findViewById(R.id.prevstayspinner);
		 
		sliderfamilynum = (SeekBar) findViewById(R.id.sliderfamilynum);
		sliderhouserooms = (SeekBar) findViewById(R.id.sliderhouserooms);
		
		sliderfamilynum.setOnSeekBarChangeListener(this);
		sliderhouserooms.setOnSeekBarChangeListener(this);
		
        btnsave = (Button) findViewById(R.id.btnsave);
        btnreset = (Button) findViewById(R.id.btnreset);
        
        btnsave.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v){
				if(cd.isConnectingToInternet()){
				    FormEditText[] allFields    = { famnum,numfamily,houserooms,presaddress,medhistory};
					boolean allValid = true;
			        for (FormEditText field: allFields) {
			            allValid = field.testValidity() && allValid;
			        }
			        if (allValid) {
			        	new demographic().execute();
			        } else {
			        	AppMsg.makeText(DemographicActivity.this, "Some of your information is missing", AppMsg.STYLE_ALERT).show();
			        }
				}
				else if(!cd.isConnectingToInternet()){
					AppMsg.makeText(DemographicActivity.this, "No Internet Connection Found, Please Connect", AppMsg.STYLE_ALERT).show();
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
		
		    case R.id.sliderfamilynum:
		    	numfamily.setText(progress + " Family Members");
		        break;
	
		    case R.id.sliderhouserooms:
		    	houserooms.setText(progress + " Rooms");
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
     * Background Async Task to demographic
     * */
    class demographic extends AsyncTask<String, String, String> {
 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(DemographicActivity.this);
            pDialog.setMessage("Saving family information. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
 
        /**
         * Getting url in background thread
         * */
        protected String doInBackground(String... param) {
            String res = "0";
            
            
            String coordinates = latitude +","+ longitude;
            
        	famnumber = famnum.getText().toString();
        	numberfam = numfamily.getText().toString();
        	numrooms = houserooms.getText().toString();
        	presadd = presaddress.getText().toString();
        	prevadd = prevaddress.getText().toString();
        	famMedicalHistory = medhistory.getText().toString();
        	famtype = String.valueOf(famtypespinner.getSelectedItem());
        	language = String.valueOf(languagespinner.getSelectedItem());
        	breadwinner = String.valueOf(breadwinnerspinner.getSelectedItem());
        	income = String.valueOf(incomespinner.getSelectedItem());
        	sourceincome = String.valueOf(sourceincomespinner.getSelectedItem());
        	housing = String.valueOf(housingspinner.getSelectedItem());
        	presstay = String.valueOf(presstayspinner.getSelectedItem());
        	prevstay = String.valueOf(prevstayspinner.getSelectedItem());
        	
			DatabaseHandler db = new DatabaseHandler(getApplicationContext());
			
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("tag", "demographic"));
			params.add(new BasicNameValuePair("userID", userID));
            params.add(new BasicNameValuePair("famNum", famnumber));
            params.add(new BasicNameValuePair("famSize", numberfam));
            params.add(new BasicNameValuePair("hseSize", numrooms));
            params.add(new BasicNameValuePair("presAdd", presadd));
            params.add(new BasicNameValuePair("prevAdd", prevadd));
            params.add(new BasicNameValuePair("famType", famtype));
            params.add(new BasicNameValuePair("language", language));
            params.add(new BasicNameValuePair("breadWinner", breadwinner));
            params.add(new BasicNameValuePair("familyIncome", income));
            params.add(new BasicNameValuePair("srcIncome", sourceincome));
            params.add(new BasicNameValuePair("hseType", housing));
            params.add(new BasicNameValuePair("presAddTime", presstay));
            params.add(new BasicNameValuePair("prevAddTime", prevstay));
            params.add(new BasicNameValuePair("gpsCordinates", coordinates));
            params.add(new BasicNameValuePair("famMedicalHistory", famMedicalHistory));

            // getting JSON string from URL
            String json = jsonParser.makeHttpRequest(URL, "POST",params);
 
            // Check your log cat for JSON reponse
            Log.d("JSON: ", json);
            
			try {
				jObj = new JSONObject(json);
			    if (jObj.getString(KEY_SUCCESS) != null) {
			        
			        res = jObj.getString(KEY_SUCCESS);
			        if(Integer.parseInt(res) == 1){
			            
			            famID = jObj.getString("ID");
			         
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
    			Intent personal = new Intent(getApplicationContext(), PersonalInfoActivity.class);
		        Bundle extras = new Bundle();
		        extras.putString("famID", famID);
		        extras.putString("userID", userID);
		        personal.putExtras(extras);
		        startActivity(personal);   
		        finish();
        	}
        	else if(Integer.parseInt(res) == 0){
        		AppMsg.makeText(DemographicActivity.this, errmsgs, AppMsg.STYLE_ALERT).show();
        	}        	
            pDialog.dismiss();
        }
    }
	

}
