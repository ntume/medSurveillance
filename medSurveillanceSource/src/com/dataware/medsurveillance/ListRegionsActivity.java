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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.dataware.medsurveillance.includes.AlertDialogManager;
import com.dataware.medsurveillance.includes.DatabaseHandler;
import com.dataware.medsurveillance.includes.JSONParserCat;
import com.dataware.medsurveillance.includes.RegionAdapter;

public class ListRegionsActivity extends SherlockFragmentActivity  {

    private ImageView icon;
    
    
    static final String URL = "http://ntumedev.me/medsurv/apiRequests.php";
    //static final String URL = "http://ntume.byethost7.com/doc/mobile/fetchCountryRegion";
	// json node keys
    public static final String KEY_SUCCESS = "success";
    public static final String KEY_ERROR = "error";
    public static final String KEY_REGION = "region";
    public static final String KEY_COUNT = "number";
    public static final String KEY_COUNTRY = "countryID";			
    public static final String KEY_DATA = "data";
    
    final ArrayList<String> region = new ArrayList<String>(); 
  	final ArrayList<String> countryid = new ArrayList<String>(); 
  	final ArrayList<String> number = new ArrayList<String>(); 

	
    JSONParserCat jsonParser = new JSONParserCat();
	
	// JSONArray
    JSONArray data = null;
    String success,error,country;
	
    ListView list;
    RegionAdapter adapter;
    ArrayList<HashMap<String, String>> regionList;
    
	private ProgressDialog pDialog;
	String tag;
	
	public HashMap<String, String> user;
	
	String countryID,countryname;
	
	boolean datafound = false;
	AlertDialogManager alert = new AlertDialogManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list);
        icon = (ImageView) findViewById(android.R.id.icon);
        
        Bundle extras = getIntent().getExtras();
        
        if (extras!=null)
        {        	       	
        	countryID = extras.getString("countryid");
        	countryname = extras.getString("country");
        }
        
        setTitle(countryname);
        
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        user = db.getUserDetails();
        
        new GetFeed().execute();
        
     
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
		        .setMessage("Click region to view medical services in that region.\n\nClick the map icon above to view Medical Health Services")
		        .setIcon(R.drawable.icon_tip)
		        .show();
				break;
			case R.id.viewmap:
				Intent emslocator = new Intent(getApplicationContext(), MapFragmentActivity.class);						
				Bundle extras = new Bundle();				
			    extras.putString("tag","country");
			    extras.putString("countryid", countryID);	
			    emslocator.putExtras(extras);
		        startActivityForResult(emslocator, 1);		
				
				break;
			case R.id.pagerefresh:
				Intent i = new Intent(getApplicationContext(), ListRegionsActivity.class);
		        Bundle extras2 = new Bundle();
		        extras2.putString("country",countryname);	
		        extras2.putString("countryid",countryID);	
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
	    	pDialog = new ProgressDialog(ListRegionsActivity.this);
            pDialog.setMessage("Loading Regions...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
	    }

	    protected String doInBackground(String... params1) {
	    	
			//DatabaseHandler db = new DatabaseHandler(getApplicationContext());
	    	//db = new DatabaseHandler(getApplicationContext());
	    	List<NameValuePair> params = new ArrayList<NameValuePair>();
	    	regionList = new ArrayList<HashMap<String, String>>();
                        
            // post album id as GET parameter
            params.add(new BasicNameValuePair("id", countryID));
            params.add(new BasicNameValuePair("tag", "fetch_regions"));
            
 
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
                    	datafound = true;
                    	 for (int i = 0; i < data.length(); i++) {
                            JSONObject c = data.getJSONObject(i);
                             
                            HashMap<String, String> map = new HashMap<String, String>();
             	 			
             	 			// adding each child node to HashMap key => value
             	 			map.put(KEY_REGION, c.getString(KEY_REGION));
             	 			map.put(KEY_COUNT, c.getString(KEY_COUNT));
             	 			             	 			
             	 			region.add(c.getString(KEY_REGION));
             	 			number.add(c.getString(KEY_COUNT));
             	 			
             	 			regionList.add(map);             	 			           			 
                    	 }
                     }
                    
                    else if(error.equals("1")){
                    	datafound = false;
                    	//alert.showAlertDialog(DashboardActivity.this, "Retail Groups Error","Sorry no retail groups at the moment, please check again later", false);
                    }
                }
                
            } catch (JSONException e) {
                e.printStackTrace();
            }            
            
	      return null;
	    }

	    protected void onPostExecute(String aResult) {
	    	pDialog.dismiss();
			if(datafound){
				list=(ListView)findViewById(R.id.qnlist);
				
				// Getting adapter by passing xml data ArrayList
		        adapter=new RegionAdapter(ListRegionsActivity.this, regionList);        
		        list.setAdapter(adapter);
		        
	
		        // Click event for single list row
		        list.setOnItemClickListener(new OnItemClickListener() {
	
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						Intent i = new Intent(getApplicationContext(), ViewMedicalServicesActivity.class);
				        Bundle extras = new Bundle();
				        extras.putString("countryid", country);	
				        extras.putString("region", region.get(position));	
				        extras.putString("number", number.get(position));	
				        extras.putString("tag","region");
				        i.putExtras(extras);
				        startActivityForResult(i, 1);						            
					}
				});	
				    	
			}
			else if(!datafound){
				Toast.makeText(getApplicationContext(), "Sorry no regions for "+countryname+" at the moment, please check again later", Toast.LENGTH_SHORT).show();
				alert.showAlertDialog(ListRegionsActivity.this, countryname,"Sorry no regions at the moment, please check again later", false);
				startActivity (new Intent(getApplicationContext(), HomeActivity.class));
			}
	    }
	}

}
