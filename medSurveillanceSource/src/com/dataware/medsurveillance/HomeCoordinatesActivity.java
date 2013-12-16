package com.dataware.medsurveillance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.dataware.medsurveillance.includes.ActionItem;
import com.dataware.medsurveillance.includes.AlertDialogManager;
import com.dataware.medsurveillance.includes.ConnectionDetector;
import com.dataware.medsurveillance.includes.DatabaseHandler;
import com.dataware.medsurveillance.includes.GridAdapter;
import com.dataware.medsurveillance.includes.HomeCoordinatesAdapter;
import com.dataware.medsurveillance.includes.JSONParserCat;
import com.dataware.medsurveillance.includes.QuickAction;
import com.devspark.appmsg.AppMsg;

public class HomeCoordinatesActivity extends SherlockActivity{
	final ArrayList<String> pID = new ArrayList<String>(); 
  	final ArrayList<String> pName = new ArrayList<String>(); 
  	String idedt,nameedt;
   
  	HomeCoordinatesAdapter adapter;
	
	GridView gridView;
	
	ArrayList<HashMap<String, String>> pList;
    
	private ProgressDialog pDialog;
	
	//quick action id
  	private static final int ID_EDIT = 1;
  	private static final int ID_SYNC   = 2;
  	private static final int ID_SEARCH = 3;
  	private static final int ID_INFO   = 4;
  	private static final int ID_ERASE  = 5;	
  	private static final int ID_SHARE     = 6;

  	int currentPic;
  	QuickAction quickAction;
  	
  	String task;
	
  	AlertDialogManager alert = new AlertDialogManager();
  	
  	 // JSON parser class
    JSONParserCat jsonParser = new JSONParserCat();
 
    //String URL = "http://www.ntume.byethost7.com/medsurv/apiRequests.php";
    private static final String URL = "http://ntumedev.me/medsurv/apiRequests.php";
    private static final String SYNCURL = "http://ntumedev.me/medsurv/apiRequests.php";
       
    // json keys
    
	static final String KEY_SUCCESS = "success"; // parent node	
	static final String KEY_MESSAGE = "msg";
	static final String KEY_ERROR = "error";
	static final String KEY_DATA = "data";	
	    
	// JSONArray
    JSONArray data = null;
    String success,error,userID,famID;	
	
	// Connection detector class
    ConnectionDetector cd;
    // flag for Internet connection status
    Boolean isInternetPresent = false;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_gridcard);            
        
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        Bundle extras = getIntent().getExtras();
        
        if (extras!=null)
        {        	
        	userID = extras.getString("userID");     
        	famID = extras.getString("famID");
        }
        
        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        cd = new ConnectionDetector(getApplicationContext());
        
        quickAction = new QuickAction(this, QuickAction.HORIZONTAL);
        //quick action implementation
        ActionItem edit 	= new ActionItem(ID_EDIT, "Edit", getResources().getDrawable(R.drawable.ic_edit));      
        ActionItem sync 		= new ActionItem(ID_SYNC, "Sync", getResources().getDrawable(R.drawable.ic_sync));
        
        
        //add action items into QuickAction
        quickAction.addActionItem(edit);
  	    quickAction.addActionItem(sync);
        
        //Set listener for action item clicked
  		quickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {			
  			@Override
  			public void onItemClick(QuickAction source, int pos, int actionId) {				
  				ActionItem actionItem = quickAction.getActionItem(pos);
                 
  				//here we can filter which action item was clicked with pos or actionId parameter
  				if (actionId == ID_EDIT) {
  					Intent editPatient = new Intent(getApplicationContext(), HomeActivity.class);
  			        Bundle extras = new Bundle();
  			        extras.putString("famid", idedt);
  			        extras.putString("task", "edit");
  			        editPatient.putExtras(extras);
  			        startActivity(editPatient);       
  				} else if (actionId == ID_SYNC) {
  					/*if(syncmenu.equals("true")){
  						alert.showAlertDialog(GridPediatricActivity.this, "Sync Patient","This Patients Information is upto Date Online", false);  		    			
  					}*/
  				}else {
  					Toast.makeText(getApplicationContext(), actionItem.getTitle() + " selected", Toast.LENGTH_SHORT).show();
  				}
  			}
  		});
       
  		if (!cd.isConnectingToInternet()) {
    		alert.showAlertDialog(HomeCoordinatesActivity.this, "Internet Connection Error","No working internet connection", false);    		
    	}
    	else if(cd.isConnectingToInternet()){
    		new GetFamiliesCoordinates().execute();	   
    	}        
    }
	
		
	/**
     * Background Async Task 
     * */
    class GetFamiliesCoordinates extends AsyncTask<String, String, String> {
	    protected void onPreExecute() {
	    	super.onPreExecute();
	    	pDialog = new ProgressDialog(HomeCoordinatesActivity.this);
            pDialog.setMessage("Loading Homes...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
	    }

	    protected String doInBackground(String... params1) {
	    	
			DatabaseHandler db = new DatabaseHandler(getApplicationContext());
			pList = new ArrayList<HashMap<String, String>>();
	    	
	    	List<NameValuePair> params = new ArrayList<NameValuePair>();
	    	params.add(new BasicNameValuePair("tag", "fetch_homes"));
	    	params.add(new BasicNameValuePair("userID", "1"));
	    	
	    	// getting JSON string from URL
            String json = jsonParser.makeHttpRequest(URL, "POST",params);
 
            // Check your log cat for JSON reponse
            Log.d("JSON: ", json);
            
            try {
                JSONObject jObj = new JSONObject(json);
                if (jObj != null) {                   
                    success = jObj.getString(KEY_SUCCESS);
                    error = jObj.getString(KEY_ERROR);
                    data = jObj.getJSONArray(KEY_DATA);
                    Log.d("Success: ", success);
                    if (data != null && success.equals("1")) {
                    	Log.d("JSON: xxxx", Integer.toString(data.length()));
                    	 for (int i = 0; i < data.length(); i++) {
                             JSONObject c = data.getJSONObject(i);
                             HashMap<String, String> map = new HashMap<String, String>();
                             
                             map.put("homenumber", c.getString("homenumber"));
                             map.put("address", c.getString("address"));
                             map.put("lat", c.getString("lat"));
                             map.put("lon", c.getString("lon"));
                             
                             pList.add(map);               			     
                    	 }                    	 
                    	
                     }
                    
                    else if(error.equals("1")){
                    	//alert.showAlertDialog(DashboardActivity.this, "Retail Groups Error","Sorry no retail groups at the moment, please check again later", false);
                    }
                }
                
            } catch (JSONException e) {
                e.printStackTrace();
            }                      
	        return null;
	    }

	    protected void onPostExecute(String aResult) {

	    	if(!pList.isEmpty()){
		    	gridView=(GridView)findViewById(R.id.grid_card);
					
							
				// Getting adapter by passing  data ArrayList
		        adapter=new HomeCoordinatesAdapter(HomeCoordinatesActivity.this, pList);        
		        gridView.setAdapter(adapter);
		        
	
		        // Click event for single list row
		        gridView.setOnItemClickListener(new OnItemClickListener() {
	
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						    HashMap<String, String> editP = new HashMap<String, String>();
						    editP = pList.get(position);
							currentPic = position; 
						    //quickAction.show(view); 
							Intent members = new Intent(getApplicationContext(), MapNavigateActivity.class);
							Bundle extras = new Bundle();
					        extras.putString("userID", userID);
					        extras.putString("medicalservice", editP.get("homenumber"));
					        extras.putString("address", editP.get("address"));
					        extras.putString("lat", editP.get("lat"));
					        extras.putString("lon", editP.get("lon"));
					        members.putExtras(extras);
					        startActivity(members); 
					        finish();
					}
				});	
	    	}
	    	else if(pList.isEmpty()){
	    		AppMsg.makeText(HomeCoordinatesActivity.this, "Sorry no member cards found please add new family member", AppMsg.STYLE_ALERT).show();
	    		   			
	    	}
	    	pDialog.dismiss();
	    }
	}   
    
    @Override
   	public void onBackPressed() {		
   		Intent families = new Intent(getApplicationContext(), ListDemographicActivity.class);
   		Bundle extras = new Bundle();
        extras.putString("userID", userID);
        families.putExtras(extras);
        startActivity(families);     
        finish();	
   	}
}
