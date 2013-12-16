package com.dataware.medsurveillance;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.dataware.medsurveillance.includes.AbstractMapActivity;
import com.dataware.medsurveillance.includes.AlertDialogManager;
import com.dataware.medsurveillance.includes.DatabaseHandler;
import com.dataware.medsurveillance.includes.GPSTracker;
import com.dataware.medsurveillance.includes.JSONParserCat;
import com.dataware.medsurveillance.includes.PopupAdapter;
import com.dataware.medsurveillance.includes.QuickAction;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class MapFragmentActivity extends AbstractMapActivity implements LocationSource, LocationListener, OnInfoWindowClickListener, OnNavigationListener {
	
	private static final String STATE_NAV="nav";
	private static final int[] MAP_TYPE_NAMES= { R.string.normal,R.string.hybrid, R.string.satellite, R.string.terrain };
	private static final int[] MAP_TYPES= { GoogleMap.MAP_TYPE_NORMAL,GoogleMap.MAP_TYPE_HYBRID, GoogleMap.MAP_TYPE_SATELLITE,GoogleMap.MAP_TYPE_TERRAIN };
	private GoogleMap map=null;
	private OnLocationChangedListener mapLocationListener=null;
	private LocationManager locMgr=null;
	private Criteria crit=new Criteria();
		
	final int RQS_GooglePlayServices = 1;
	
	// Progress Dialog
    private ProgressDialog pDialog;

    // JSON parser class
    JSONParserCat jsonParser = new JSONParserCat();
   
    static final String URL = "http://ntumedev.me/medsurv/apiRequests.php";
    
    //static final String URL = "http://ntume.byethost7.com/doc/mobile/fetchServicesCordinates";
    //static final String URLdistance = "http://ntume.byethost7.com/doc/mobile/findClosestMedicalService";
	
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
  	final ArrayList<String> emsDist = new ArrayList<String>();
  	
    // JSONArray
    JSONArray data = null;
   
    AlertDialogManager alert = new AlertDialogManager();
    
    boolean dataFound;
   
    JSONArray stores = null;
   // GPSTracker class
    GPSTracker gps;
    String medicalserviceID,countryid,region,success,error,country,tag,service,distance,search;
    String json;
   
    double lat,lon;
    double latitude,longitude;
    
    private static final int ID_LOCATOR = 3;
    private static final int ID_SHARE     = 6;
    QuickAction quickAction;
    
    String quickLat,quickLon,quickMedicalservice;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (readyToGo()) {
			setContentView(R.layout.activity_mapfragment);
			
	        Bundle extras = getIntent().getExtras();
	        
	        if (extras!=null)
	        {
	        	
	        	medicalserviceID = extras.getString("medicalserviceID");
	        	countryid = extras.getString("countryid");
	        	region = extras.getString("region");	        	
	        	//page = extras.getString("page");
	        	tag = extras.getString("tag");
	        	service = extras.getString("service");
	        	distance  = extras.getString("distance");
	        	search = extras.getString("keyword");
	        }

			SupportMapFragment mapFrag =	(SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);

			mapFrag.setRetainInstance(true);
			initListNav();

			map=mapFrag.getMap();

			map.setInfoWindowAdapter(new PopupAdapter(getLayoutInflater()));
		    map.setOnInfoWindowClickListener(this);

		    locMgr=(LocationManager)getSystemService(LOCATION_SERVICE);
		    crit.setAccuracy(Criteria.ACCURACY_FINE);
		    
		    // Getting the name of the best provider
            String provider = locMgr.getBestProvider(crit, true);
            
            locMgr.requestLocationUpdates(provider, 20000000, 10, this);
            // Getting Current Location
            Location location = locMgr.getLastKnownLocation(provider);

            if(location!=null){
               onLocationChanged(location);
            }
            
            gps = new GPSTracker(MapFragmentActivity.this);
			 
            // check if GPS enabled
            if(gps.canGetLocation()){
                latitude = gps.getLatitude();
                longitude = gps.getLongitude();                
                Log.d("xxxxxxxxxxxxx", "Your Location is - \nLat: " + latitude + "\nLong: " + longitude);               
            }else{              
                gps.showSettingsAlert();
            }
            
	        lat = gps.getLatitude();
	        lon = gps.getLongitude();

		    map.setMyLocationEnabled(true);
		    map.getUiSettings().setMyLocationButtonEnabled(false);
		    map.getUiSettings().setAllGesturesEnabled(true);
		    map.getUiSettings().setZoomControlsEnabled(true);
		    map.getUiSettings().setCompassEnabled(true);
		    map.getUiSettings().setMyLocationButtonEnabled(true);
		    
		    if (savedInstanceState == null) {
				CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(lat,lon));
				CameraUpdate zoom=CameraUpdateFactory.zoomTo(8);

				map.moveCamera(center);
				map.animateCamera(zoom);			
			}
		    
		    new GetHealthServices().execute();  
	        
		}
		
		 
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);

		savedInstanceState.putInt(STATE_NAV,getSupportActionBar().getSelectedNavigationIndex());
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		getSupportActionBar().setSelectedNavigationItem(savedInstanceState.getInt(STATE_NAV));
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		Toast.makeText(this, marker.getTitle(), Toast.LENGTH_LONG).show();
		Intent locator = new Intent(getApplicationContext(), MapNavigateActivity.class);						
		Bundle extras = new Bundle();				
	    extras.putString("medicalservice",marker.getTitle());	    	
        extras.putString("lat",marker.getPosition().latitude+"");
        extras.putString("lon",marker.getPosition().longitude+"");  			        
        locator.putExtras(extras);
        startActivityForResult(locator, 1);	
	}

	
	private void initListNav() {
		ArrayList<String> items=new ArrayList<String>();
		ArrayAdapter<String> nav=null;
		ActionBar bar=getSupportActionBar();

		for (int type : MAP_TYPE_NAMES) {
			items.add(getString(type));
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			nav=new ArrayAdapter<String>(bar.getThemedContext(),android.R.layout.simple_spinner_item,items);
		}
		else {
			nav=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,items);
		}

		nav.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		bar.setListNavigationCallbacks(nav, this);
	}

	private void addMarker(GoogleMap map, double lat, double lon,int title, int snippet) {
		map.addMarker(new MarkerOptions().position(new LatLng(lat, lon))
                                 .title(getString(title))
                                 .snippet(getString(snippet))
                                 .draggable(true));
	}

	 
	@Override
	  public void onResume() {
	    super.onResume();

	    //locMgr.requestLocationUpdates(0L, 0.0f, crit, this, null);
	   // map.setLocationSource(this);
	  }

	  @Override
	  public void onPause() {
	    //map.setLocationSource(null);
	    //locMgr.removeUpdates(this);

	    super.onPause();
	  }

	  @Override
	  public boolean onNavigationItemSelected(int itemPosition, long itemId) {
	    map.setMapType(MAP_TYPES[itemPosition]);

	    return(true);
	  }

	@Override
	public void onLocationChanged(Location location) {
		  //TextView tvLocation = (TextView) findViewById(R.id.locinfo);
		
	       // Getting latitude of the current location
	       double latitude = location.getLatitude();
	       lat = latitude;
	       // Getting longitude of the current location
	       double longitude = location.getLongitude();
	       lon = longitude;
	       // Creating a LatLng object for the current location
	       LatLng latLng = new LatLng(latitude, longitude);
	
	       // Showing the current location in Google Map
	      // map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
	
	       // Zoom in the Google Map
	      // myMap.animateCamera(CameraUpdateFactory.zoomTo(15));
	
	       BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.map_user);
       
	       
	       Marker newMarker = map.addMarker(new MarkerOptions()
	       .position(latLng)
	       .icon(bitmapDescriptor)
	       .snippet("My Location"));
	 
	       newMarker.setTitle("My Location");	
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void activate(OnLocationChangedListener listener) {
	    this.mapLocationListener=listener;
	}

	@Override
	public void deactivate() {
	    this.mapLocationListener=null;
	}
	
	/**
     * Background Async Task to Get xml url details
     * */
    class GetHealthServices extends AsyncTask<String, String, String> {
 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MapFragmentActivity.this);
            pDialog.setMessage("Loading health services. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
 
        /**
         * Getting url in background thread
         * */
        protected String doInBackground(String... params1) {
        	// Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            DatabaseHandler db = new DatabaseHandler(getApplicationContext());
            
 
            // post album id as GET parameter
            params.add(new BasicNameValuePair("medicalserviceID", medicalserviceID));
            params.add(new BasicNameValuePair("country_id", countryid));
            params.add(new BasicNameValuePair("region", region));
            params.add(new BasicNameValuePair("lat", String.valueOf(lat)));
            params.add(new BasicNameValuePair("lon", String.valueOf(lon)));
            params.add(new BasicNameValuePair("service", service));
            params.add(new BasicNameValuePair("radius", distance));
            params.add(new BasicNameValuePair("tag", "view_map"));
            params.add(new BasicNameValuePair("action", tag));
            params.add(new BasicNameValuePair("keyword", search));
            
            Log.d("zzzzzzzzzzzzzzzz  params: ", params.toString());
            // getting JSON string from URL
            
            if(!tag.equals("nearest"))
            	json = jsonParser.makeHttpRequest(URL, "POST",params);
            else if(tag.equals("nearest"))
            	json = jsonParser.makeHttpRequest(URL, "POST",params);
 
            // Check your log cat for JSON reponse
            Log.d("store List JSON: ", json);
            
            try {
                JSONObject jObj = new JSONObject(json);
                if (jObj != null) {                   
                    success = jObj.getString(KEY_SUCCESS);
                    error = jObj.getString(KEY_ERROR);
                    //country = jObj.getString(KEY_COUNTRY);
                    data = jObj.getJSONArray(KEY_DATA);
                    Log.d("Success: ", success);
                    if (data != null && success.equals("1")) {
                    	dataFound = true;
                    	Log.d("JSON: xxxx", Integer.toString(data.length()));
                    	 for (int i = 0; i < data.length(); i++) {
                            JSONObject c = data.getJSONObject(i);
                                       	 			             	 			
             	 			emsID.add(c.getString(KEY_ID));             	 		
             	 			emsName.add(c.getString(KEY_NAME));
             	 			emsAddress.add(c.getString(KEY_ADDRESS));             	 		
             	 			emsTown.add(c.getString(KEY_TOWN));
             	 			emsCity.add(c.getString(KEY_CITY));             	 		
             	 			emsLat.add(c.getString(KEY_LAT));
             	 			emsLon.add(c.getString(KEY_LON));             	 		
             	 			emsType.add(c.getString(KEY_TYPE));
             	 			emsDist.add(c.getString("distance"));
             	 			
             	 			//medicServicesList.add(map);             	 			           			 
                    	 }
                     }
                    
                    else if(error.equals("1")){
                    	dataFound = false;
                    	//alert.showAlertDialog(DashboardActivity.this, "Retail Groups Error","Sorry no retail groups at the moment, please check again later", false);
                    }
                }
                
            } catch (JSONException e) {
                e.printStackTrace();
            }            
             return null;
        }
 
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
        	// dismiss the dialog after getting all tracks
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                	if(dataFound){
		                	  BitmapDescriptor bitmapDescriptoruser = BitmapDescriptorFactory.fromResource(R.drawable.map_user);
		                	  LatLng latLng = new LatLng(Double.parseDouble(emsLat.get(0)),Double.parseDouble(emsLon.get(0)));	                	  
	
			           	       // Showing the current location in Google Map
			           	       map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
			           	
			           	       // Zoom in the Google Map
			           	       map.animateCamera(CameraUpdateFactory.zoomTo(5));	           	       
		           	       
			         	       /*Marker newMarker = map.addMarker(new MarkerOptions()
			         	       .position(latLng)
			         	       .icon(bitmapDescriptoruser)
			         	       .snippet("Marker to show current location "));	         	 
			         	       newMarker.setTitle("My Location");*/
			         	       
			         	       
		                	for(int x=0;x<emsID.size();x++){
		                	   BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.map_hospital);
			                   LatLng latLng2 = new LatLng(Double.parseDouble(emsLat.get(x)), Double.parseDouble(emsLon.get(x)));
			                   String type = emsType.get(x);
			                   if(type.equals("Government Hospital"))
			                	   bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.map_hospital2);
			                   else if(type.equals("Clinic"))
			                	   bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.map_clinic);
			                   else if(type.equals("Pharmacy"))
			                	   bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.map_pharmacy);
			                   else if(type.equals("Private Hospital"))
			                	   bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.map_hospital);
			                   
			         	       Marker newMarker2 = map.addMarker(new MarkerOptions()
			         	       .position(latLng2)
			         	       .icon(bitmapDescriptor)
			         	       .snippet(emsAddress.get(x)+"("+emsDist.get(x)+")"));	         	 
			         	       newMarker2.setTitle(emsName.get(x) + " - " +emsType.get(x));
		                	}
                	}
                	else if(!dataFound){
                		  BitmapDescriptor bitmapDescriptoruser = BitmapDescriptorFactory.fromResource(R.drawable.map_user);
	                	  LatLng latLng = new LatLng(lat,lon);	                	  

		           	       // Showing the current location in Google Map
	                	   map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
		           	
		           	       // Zoom in the Google Map
	                	   map.animateCamera(CameraUpdateFactory.zoomTo(10));	           	       
	           	       
		         	       Marker newMarker = map.addMarker(new MarkerOptions()		         	      
		         	       .position(latLng)
		         	       .icon(bitmapDescriptoruser)
		         	       .snippet("Marker to show current location "));	         	 
		         	       newMarker.setTitle("My Location");
                		   alert.showAlertDialog(MapFragmentActivity.this, "Health Services","Sorry no health services found", false);
                	}
                }
            });
            
        }
    }
}
