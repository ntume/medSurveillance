package com.dataware.medsurveillance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.dataware.medsurveillance.includes.ActionItem;
import com.dataware.medsurveillance.includes.DatabaseHandler;
import com.dataware.medsurveillance.includes.JSONParserCat;
import com.dataware.medsurveillance.includes.MedicalServicesAdapter;
import com.dataware.medsurveillance.includes.QuickAction;

public class ViewMedicalServicesActivity  extends SherlockFragmentActivity  {

    private ImageView icon;
    
    
    static final String URL = "http://ntumedev.me/medsurv/apiRequests.php";
	// json node keys
    public static final String KEY_SUCCESS = "success";
    public static final String KEY_ERROR = "error";
    public static final String KEY_ID = "emsID";
    public static final String KEY_NAME = "emsName";
    public static final String KEY_LAT = "emsLat";			
    public static final String KEY_LON = "emsLon";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_TOWN = "town";
    public static final String KEY_CITY = "city";
    public static final String KEY_TYPE = "emsType";
    public static final String KEY_DATA = "data";
    
    final ArrayList<String> emsID = new ArrayList<String>(); 
  	final ArrayList<String> emsName = new ArrayList<String>(); 
  	final ArrayList<String> emsAddress = new ArrayList<String>(); 
  	final ArrayList<String> emsTown = new ArrayList<String>(); 
  	final ArrayList<String> emsCity = new ArrayList<String>(); 
  	final ArrayList<String> emsLat = new ArrayList<String>();
  	final ArrayList<String> emsLon = new ArrayList<String>();
  	final ArrayList<String> emsType = new ArrayList<String>();
  	
	
    JSONParserCat jsonParser = new JSONParserCat();
	
	// JSONArray
    JSONArray data = null;
    String success,error,country;
	
    ListView list;
    MedicalServicesAdapter adapter;
    ArrayList<HashMap<String, String>> medicServicesList;
    
	private ProgressDialog pDialog;
	String tag;
	
	public HashMap<String, String> user;
	
	String countryID,region,number,taggy,search;
	
	//action id
  	private static final int ID_DOWNLOAD     = 1;
  	private static final int ID_ADD   = 2;
  	private static final int ID_LOCATOR = 3;
  	private static final int ID_INFO   = 4;
  	private static final int ID_SHARE     = 6;
  	private int currentPic = 0;
  	
  	QuickAction quickAction;
  	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list);
        icon = (ImageView) findViewById(android.R.id.icon);
        
        Bundle extras = getIntent().getExtras();
        
        if (extras!=null)
        {        	       	
        	countryID = extras.getString("countryid");     
        	region = extras.getString("region");   
        	number = extras.getString("number");   
        	taggy = extras.getString("tag"); 
        	search = extras.getString("keyword"); 
        }
        
        if(!taggy.equals("search"))        
        	setTitle(region);
        else if(taggy.equals("search")) 
        	setTitle(search);
        
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        user = db.getUserDetails();
        
        new GetFeed().execute();
        
 		//quick action implementation
        
        ActionItem map 	= new ActionItem(ID_LOCATOR, "Navigate To", getResources().getDrawable(R.drawable.ic_map));        
        ActionItem share 		= new ActionItem(ID_SHARE, "Share", getResources().getDrawable(R.drawable.menu_share));
        
        //create QuickAction. Use QuickAction.VERTICAL or QuickAction.HORIZONTAL param to define layout 
        //orientation
  	    quickAction = new QuickAction(this, QuickAction.HORIZONTAL);
  	    
  	    quickAction.addActionItem(map);
        quickAction.addActionItem(share);
      
        quickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {			
  			@Override
  			public void onItemClick(QuickAction source, int pos, int actionId) {				
  				ActionItem actionItem = quickAction.getActionItem(pos);
                 
  				//here we can filter which action item was clicked with pos or actionId parameter
  				if (actionId == ID_LOCATOR) {
  					Intent locator = new Intent(getApplicationContext(), MapNavigateActivity.class);						
  					Bundle extras = new Bundle();				
  				    extras.putString("medicalservice",emsName.get(currentPic));
  				    extras.putString("countryid", countryID);	
  			        extras.putString("region", region);	
  			        extras.putString("tag","service");	
  			        extras.putString("lat",emsLat.get(currentPic));
  			        extras.putString("lon",emsLon.get(currentPic));  			        
  			        locator.putExtras(extras);
  			        startActivityForResult(locator, 1);	
  					//finish();
  				} else if (actionId == ID_SHARE) {
  					Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
  					sharingIntent.setType("text/plain");
  					String shareBody = emsName.get(currentPic)+"\nAddress: "+emsAddress.get(currentPic)+"\n"+emsTown.get(currentPic)+","+emsTown.get(currentPic)+"\n\nShared by Doc-ES for Android";
  					sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Emergency Medical Service");
  					sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
  					startActivity(Intent.createChooser(sharingIntent, "Share via"));
  				}
  			}
  		});
        
        list=(ListView)findViewById(R.id.qnlist);
		
        // Click event for single list row
        list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
   				currentPic = position;
           		quickAction.show(view);
           		
           	}
   		});	        
          

           //set long click listener for each gallery thumbnail item
           list.setOnItemLongClickListener(new OnItemLongClickListener() {
           	//handle long clicks
           	public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
           		currentPic = position;
           		quickAction.show(v);      	
           		return true;
           	}
           });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	getSupportMenuInflater().inflate(R.menu.map_topmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.pageinfo:
				new AlertDialog.Builder(this)
		        .setTitle("Tip")
		        .setMessage("Long press medical sercice to view menu")
		        .setIcon(R.drawable.icon_tip)
		        .show();
				break;
			case R.id.viewmap:
				Intent storelocator = new Intent(getApplicationContext(), MapFragmentActivity.class);						
				Bundle extras = new Bundle();				
			    extras.putString("medicalserviceID","all");
			    extras.putString("countryid", countryID);	
		        extras.putString("region", region);	
		        extras.putString("tag","region");	
		        //extras.putString("itemID",itemID.get(currentPic));
		        storelocator.putExtras(extras);
		        startActivityForResult(storelocator, 1);	
				//finish();
				break;
			case R.id.pagerefresh:
				Intent i = new Intent(getApplicationContext(), ViewMedicalServicesActivity.class);
		        Bundle extras2 = new Bundle();
		        extras2.putString("countryid", countryID);	
		        extras2.putString("region", region);	
		        extras2.putString("number", number);				        
		        i.putExtras(extras2);
		        startActivityForResult(i, 1);				
                finish();
				break; 
            
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

   
    /**
     * Background Async Task
     * */
    class GetFeed extends AsyncTask<String, String, String> {
	    protected void onPreExecute() {
	    	super.onPreExecute();
	    	pDialog = new ProgressDialog(ViewMedicalServicesActivity.this);
            pDialog.setMessage("Loading Health Services...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
	    }

	    protected String doInBackground(String... params1) {
	    	
			//DatabaseHandler db = new DatabaseHandler(getApplicationContext());
	    	//db = new DatabaseHandler(getApplicationContext());
	    	List<NameValuePair> params = new ArrayList<NameValuePair>();
	    	medicServicesList = new ArrayList<HashMap<String, String>>();
                        
            // post album id as GET parameter
            params.add(new BasicNameValuePair("country_id", countryID));
            params.add(new BasicNameValuePair("region", region));
            params.add(new BasicNameValuePair("tag", "fetch_healthservices"));
            params.add(new BasicNameValuePair("action", "region"));
            // getting JSON string from URL
            String json = jsonParser.makeHttpRequest(URL, "POST",params);
 
            // Check your log cat for JSON reponse
            Log.d("JSON: ", json);
            
            try {
                JSONObject jObj = new JSONObject(json);
                if (jObj != null) {                   
                    success = jObj.getString(KEY_SUCCESS);
                    error = jObj.getString(KEY_ERROR);
                    //country = jObj.getString(KEY_COUNTRY);
                    data = jObj.getJSONArray(KEY_DATA);
                    Log.d("Success: ", success);
                    if (data != null && success.equals("1")) {
                    	Log.d("JSON: xxxx", Integer.toString(data.length()));
                    	 for (int i = 0; i < data.length(); i++) {
                            JSONObject c = data.getJSONObject(i);
                             
                            HashMap<String, String> map = new HashMap<String, String>();
             	 			
             	 			// adding each child node to HashMap key => value
             	 			map.put(KEY_ID, c.getString(KEY_ID));
             	 			map.put(KEY_NAME, c.getString(KEY_NAME));
             	 			map.put(KEY_LAT, c.getString(KEY_LAT));
             	 			map.put(KEY_LON, c.getString(KEY_LON));
             	 			map.put(KEY_ADDRESS, c.getString(KEY_ADDRESS));
             	 			map.put(KEY_TOWN, c.getString(KEY_TOWN));
             	 			map.put(KEY_CITY, c.getString(KEY_CITY));
             	 			map.put(KEY_TYPE, c.getString(KEY_TYPE));
             	 			             	 			
             	 			emsID.add(c.getString(KEY_ID));             	 		
             	 			emsName.add(c.getString(KEY_NAME));
             	 			emsAddress.add(c.getString(KEY_ADDRESS));             	 		
             	 			emsTown.add(c.getString(KEY_TOWN));
             	 			emsCity.add(c.getString(KEY_CITY));             	 		
             	 			emsLat.add(c.getString(KEY_LAT));
             	 			emsLon.add(c.getString(KEY_LON));             	 		
             	 			emsType.add(c.getString(KEY_TYPE));
             	 			
             	 			medicServicesList.add(map);             	 			           			 
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


			list=(ListView)findViewById(R.id.qnlist);
			
			// Getting adapter by passing xml data ArrayList
	        adapter=new MedicalServicesAdapter(ViewMedicalServicesActivity.this, medicServicesList);        
	        list.setAdapter(adapter);
	        

	        // Click event for single list row
	        list.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					//Intent i = new Intent(getApplicationContext(), ViewMedicalServicesActivity.class);
			        //Bundle extras = new Bundle();
			        //extras.putString("countryid", country);	
			        //extras.putString("region", region.get(position));	
			        //extras.putString("number", number.get(position));				        
			        //i.putExtras(extras);
			        //startActivityForResult(i, 1);			
					currentPic = position;
	           		quickAction.show(view);
				}
			});	
	    	pDialog.dismiss();
	    }
	}

}
