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
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.dataware.medsurveillance.includes.ActionItem;
import com.dataware.medsurveillance.includes.AlertDialogManager;
import com.dataware.medsurveillance.includes.ConnectionDetector;
import com.dataware.medsurveillance.includes.DatabaseHandler;
import com.dataware.medsurveillance.includes.FormsGridCardAdapter;
import com.dataware.medsurveillance.includes.JSONParserCat;
import com.dataware.medsurveillance.includes.QuickAction;
import com.devspark.appmsg.AppMsg;

public class ListPersonsFormsActivity extends SherlockActivity{
	final ArrayList<String> pID = new ArrayList<String>(); 
  	final ArrayList<String> pName = new ArrayList<String>(); 
  	String idedt,nameedt;
   
  	FormsGridCardAdapter adapter;
	
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
    String hast,gobi,anthro,refer,personalID,famID,userID;	
	
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
        	personalID = extras.getString("personalID");
        	famID = extras.getString("famID");
        	userID = extras.getString("userID");	
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
    		alert.showAlertDialog(ListPersonsFormsActivity.this, "Internet Connection Error","No working internet connection", false);
    		/*pList = new ArrayList<HashMap<String, String>>();
    		pList.addAll(db.getAllPatientsInfo(task));
    		if(!pList.isEmpty()){
		    	gridView=(GridView)findViewById(R.id.patient_card);
					
							
				// Getting adapter by passing  data ArrayList
		        adapter=new GridAdapter(GridPediatricActivity.this, pList);        
		        gridView.setAdapter(adapter);
		        
	
		        // Click event for single list row
		        gridView.setOnItemClickListener(new OnItemClickListener() {
	
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						    HashMap<String, String> editP = new HashMap<String, String>();
						    editP = pList.get(position);
							currentPic = position; 
							idedt = editP.get(KEY_ID);
							nameedt  = editP.get(KEY_NAME);
							ideditkey = editP.get("IDKey");
							syncmenu = editP.get(KEY_SYNC);
						    quickAction.show(view); 
						     
					}
				});	
	    	}
	    	else if(pList.isEmpty()){
	    		AppMsg.makeText(ListDemographicActivity.this, "Sorry no un synced patient cards found on your device", AppMsg.STYLE_ALERT).show();    			
	    	}*/
    	}
    	else if(cd.isConnectingToInternet()){
    		new GetFamilies().execute();	   
    	}        
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.menu_listforms, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
 
	        case R.id.addhast:
	        	if(hast.equals("0")){
		        	Intent addhast = new Intent(getApplicationContext(), HastActivity.class);
			        Bundle extras = new Bundle();
			        extras.putString("personalID", personalID);
			        extras.putString("userID", userID);
			        extras.putString("famID", famID);
			        addhast.putExtras(extras);
			        startActivity(addhast); 
	        	}
	        	else if(!hast.equals("0")){
	        		AppMsg.makeText(ListPersonsFormsActivity.this, "HAST form filled", AppMsg.STYLE_ALERT).show();    			
	    	    	
	        	}
	            break;
	        
	        case R.id.addgobi:
	        	if(gobi.equals("0")){
		        	Intent addgobi = new Intent(getApplicationContext(), GobiActivity.class);
			        Bundle extrasnone = new Bundle();
			        extrasnone.putString("personalID", personalID);
			        extrasnone.putString("userID", userID);
			        extrasnone.putString("famID", famID);
			        addgobi.putExtras(extrasnone);
			        startActivity(addgobi); 
	        	}
	        	else if(!gobi.equals("0")){
	        		AppMsg.makeText(ListPersonsFormsActivity.this, "GOBI-FFF form filled", AppMsg.STYLE_ALERT).show();
	        	}
	            break;
	            
	        case R.id.addanthro:
	        	if(anthro.equals("0")){
		        	Intent addanthro = new Intent(getApplicationContext(), AnthropometricActivity.class);
			        Bundle extrasminor = new Bundle();
			        extrasminor.putString("personalID", personalID);
			        extrasminor.putString("userID", userID);
			        extrasminor.putString("famID", famID);
			        addanthro.putExtras(extrasminor);
			        startActivity(addanthro); 
	        	}
	        	else if(!anthro.equals("0")){
	        		AppMsg.makeText(ListPersonsFormsActivity.this, "Anthropometric form filled", AppMsg.STYLE_ALERT).show();
	        	}
	            break;
	            
	        /*case R.id.addreferral:
	        	Intent addreferral = new Intent(getApplicationContext(), ReferralActivity.class);
		        Bundle extrasnew = new Bundle();
		        extrasnew.putString("personalID", personalID);
		        addreferral.putExtras(extrasnew);
		        startActivity(addreferral);
	            break;*/
	            
	        
        }
 
        return true;
    }
	
	/**
     * Background Async Task 
     * */
    class GetFamilies extends AsyncTask<String, String, String> {
	    protected void onPreExecute() {
	    	super.onPreExecute();
	    	pDialog = new ProgressDialog(ListPersonsFormsActivity.this);
            pDialog.setMessage("Loading Family Members Forms...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
	    }

	    protected String doInBackground(String... params1) {
	    	
			DatabaseHandler db = new DatabaseHandler(getApplicationContext());
			pList = new ArrayList<HashMap<String, String>>();
	    	
	    	List<NameValuePair> params = new ArrayList<NameValuePair>();
	    	params.add(new BasicNameValuePair("tag", "field_personalforms"));
	    	params.add(new BasicNameValuePair("personID", personalID));
	    	
	    	// getting JSON string from URL
            String json = jsonParser.makeHttpRequest(URL, "POST",params);
 
            // Check your log cat for JSON reponse
            Log.d("JSON: ", json);
            
            try {
                JSONObject jObj = new JSONObject(json);
                if (jObj != null) {                   
                    hast = jObj.getString("hast");
                    gobi = jObj.getString("gobi");
                    refer = jObj.getString("refer");
                    anthro = jObj.getString("anthro");
                    
                    if(!hast.equals("0")){
		                HashMap<String, String> map = new HashMap<String, String>();	                 
		                map.put("title", "Hast Form Filled");           
		                pList.add(map);   
                    }
                    
                    if(!gobi.equals("0")){
		                HashMap<String, String> map = new HashMap<String, String>();	                 
		                map.put("title", "Gobi-FFF Form Filled");           
		                pList.add(map);   
                    }
                    
                    if(!refer.equals("0")){
		                HashMap<String, String> map = new HashMap<String, String>();	                 
		                map.put("title", "Referral Form Filled");           
		                pList.add(map);   
                    }
                    
                    if(!anthro.equals("0")){
		                HashMap<String, String> map = new HashMap<String, String>();	                 
		                map.put("title", "Anthropometric Form Filled");           
		                pList.add(map);   
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
		        adapter=new FormsGridCardAdapter(ListPersonsFormsActivity.this, pList);        
		        gridView.setAdapter(adapter);
		        
	
		        // Click event for single list row
		        gridView.setOnItemClickListener(new OnItemClickListener() {
	
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						    HashMap<String, String> editP = new HashMap<String, String>();
						    editP = pList.get(position);
							currentPic = position; 
							idedt = editP.get("IDKey");
						    quickAction.show(view); 
						     
					}
				});	
	    	}
	    	else if(pList.isEmpty()){
	    		AppMsg.makeText(ListPersonsFormsActivity.this, "Sorry no family member cards found please add", AppMsg.STYLE_ALERT).show();	    		   			
	    	}
	    	pDialog.dismiss();
	    }
	}   
    
    @Override
   	public void onBackPressed() {		
   		Intent members = new Intent(getApplicationContext(), ListPersonsActivity.class);
   		Bundle extras = new Bundle();
        extras.putString("userID", userID);
        extras.putString("famID", famID);
        members.putExtras(extras);
        startActivity(members);     
        finish();	
   	}
}
