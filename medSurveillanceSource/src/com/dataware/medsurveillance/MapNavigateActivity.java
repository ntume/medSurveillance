package com.dataware.medsurveillance;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.dataware.medsurveillance.includes.AbstractMapDirectionActivity;
import com.dataware.medsurveillance.includes.GPSTracker;
import com.dataware.medsurveillance.includes.Navigator;
import com.dataware.medsurveillance.includes.PopupShareAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
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
import com.google.android.gms.maps.model.PolylineOptions;

public class MapNavigateActivity extends AbstractMapDirectionActivity implements LocationSource, LocationListener, OnInfoWindowClickListener, OnNavigationListener {
	 
	
	final int RQS_GooglePlayServices = 1;
	GoogleMap myMap;	
    
   // GPSTracker class
    GPSTracker gps;
    String medicalservice;
    String json;
   
    double lat,lon;
    double latitude,longitude;    
       
	LatLng fromPosition; 
	public static LatLng toPosition; 
	
	Double toLat,toLon;
	
	private LocationManager locMgr=null;
	
	// Progress Dialog
    private ProgressDialog pDialog;
    
    PolylineOptions rectLine;
    
    String start_address, end_address;
    
    Navigator navigator;
    
    String TotalDistance;
	String TotalDuration;
	String EndAddress;
	String BeginAddress;
	
	ArrayList<String> stepDistance = new ArrayList<String>();
	ArrayList<String> stepDuration = new ArrayList<String>(); 
	ArrayList<String> stepBeginLocation = new ArrayList<String>(); 
	ArrayList<String> stepEndLocation = new ArrayList<String>(); 
	public static ArrayList<String> stepInstructions = new ArrayList<String>();
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_map);
	       
	        Bundle extras = getIntent().getExtras();
	        
	        if (extras!=null)
	        {
	        	medicalservice = extras.getString("medicalservice");
	        	toLat = Double.parseDouble(extras.getString("lat"));
	        	toLon = Double.parseDouble(extras.getString("lon"));
	        	
	        }
	        
	        navigator = new Navigator();
	        
	        toPosition = new LatLng(toLat,toLon);
	        
	        gps = new GPSTracker(MapNavigateActivity.this);
			 
            // check if GPS enabled
            if(gps.canGetLocation()){
               latitude = gps.getLatitude();
               longitude = gps.getLongitude();               
            }else{               
               gps.showSettingsAlert();
            }
            
            fromPosition = new LatLng(latitude,longitude);
	       
            SupportMapFragment mapFrag =	(SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);

			mapFrag.setRetainInstance(true);
			
			myMap=mapFrag.getMap();

			myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(fromPosition, 16));
			
			myMap.setInfoWindowAdapter(new PopupShareAdapter(getLayoutInflater()));
			myMap.setOnInfoWindowClickListener(this);
	       
	
	        //myMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
	        myMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
	        //myMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
	        //myMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
	       
	        myMap.getUiSettings().setZoomControlsEnabled(true);
	        myMap.getUiSettings().setCompassEnabled(true);
	        myMap.getUiSettings().setMyLocationButtonEnabled(true);
	       
	        myMap.getUiSettings().setRotateGesturesEnabled(true);
	        myMap.getUiSettings().setScrollGesturesEnabled(true);
	        myMap.getUiSettings().setTiltGesturesEnabled(true);
	        myMap.getUiSettings().setZoomGesturesEnabled(true);
	        //or myMap.getUiSettings().setAllGesturesEnabled(true);
	       
	        myMap.setTrafficEnabled(true);
	        
	        new GetDirections().execute();
		    getSupportActionBar().setTitle("Directions");
	       
	       //myMap.setOnMapLongClickListener(this);     
	       
	   }
  
	   @Override
	   public void onLocationChanged(Location location) {
	
	       // Getting latitude of the current location
	       double latitude = location.getLatitude();
	       lat = latitude;
	       // Getting longitude of the current location
	       double longitude = location.getLongitude();
	       lon = longitude;
	       // Creating a LatLng object for the current location
	       LatLng latLng = new LatLng(latitude, longitude);
	
	       // Showing the current location in Google Map
	       myMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
	
	       // Zoom in the Google Map
	      // myMap.animateCamera(CameraUpdateFactory.zoomTo(15));
	
	       BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.map_user);
          
	       
	       Marker newMarker = myMap.addMarker(new MarkerOptions()
	       .position(latLng)
	       .icon(bitmapDescriptor)
	       .snippet("My Location"));
	 
	       newMarker.setTitle("My Location");
	
	   }
	   
	     

		@Override
		protected void onResume() {
		
			super.onResume();
		 
			int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
		 
			if (resultCode == ConnectionResult.SUCCESS){
				//Toast.makeText(getApplicationContext(),"isGooglePlayServicesAvailable SUCCESS",Toast.LENGTH_LONG).show(); 
			}else{
				GooglePlayServicesUtil.getErrorDialog(resultCode, this, RQS_GooglePlayServices); 
			}
		}

			
		
	    @Override
		public void onProviderDisabled(String arg0) {
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
		
		class GetDirections extends AsyncTask<String, String, String> {
			 
	        /**
	         * Before starting background thread Show Progress Dialog
	         * */
	        @Override
	        protected void onPreExecute() {
	            super.onPreExecute();
	            pDialog = new ProgressDialog(MapNavigateActivity.this);
	            pDialog.setMessage("Loading directions. Please wait...");
	            pDialog.setIndeterminate(false);
	            pDialog.setCancelable(true);
	            pDialog.show();
	        }
	 
	        /**
	         * Getting url in background thread
	         * */
	        protected String doInBackground(String... params1) {
	        	
	        	ArrayList<LatLng> directionPoint = navigator.fetchJsonDirections(fromPosition, toPosition);
	        	TotalDistance = navigator.getTotalDistance();
	        	TotalDuration= navigator.getTotalDuration();
	        	EndAddress= navigator.getEndAddress();
	        	BeginAddress= navigator.getBeginAddress();
	                	
	        	stepDistance = navigator.getStepDistance();
	        	stepDuration = navigator.getStepDuration();
	        	stepBeginLocation = navigator.getStepBeginLocationn();
	        	stepEndLocation = navigator.getStepEndLocation();
	        	stepInstructions = navigator.getStepInstruction();
	        	
	        	
	        	rectLine = new PolylineOptions().width(10).color(Color.BLUE);
				
				for(int i = 0 ; i < directionPoint.size() ; i++) {			
					rectLine.add(directionPoint.get(i));
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
	            //myMap.addPolyline(rectLine);
	            
	            BitmapDescriptor bitmapDescriptorend = BitmapDescriptorFactory.fromResource(R.drawable.map_hospital);
				
				Marker MarkerEnd = myMap.addMarker(new MarkerOptions()
	  	        .position(toPosition)
	  	        .icon(bitmapDescriptorend)
	  	        .snippet(end_address));	         	 
				MarkerEnd.setTitle(medicalservice);
				
				BitmapDescriptor bitmapDescriptorBegin = BitmapDescriptorFactory.fromResource(R.drawable.map_user);
				
				Marker MarkerBegin = myMap.addMarker(new MarkerOptions()
	  	        .position(fromPosition)
	  	        .icon(bitmapDescriptorBegin)
	  	        .snippet(start_address));	         	 
				MarkerBegin.setTitle("My Position");
				
				            
	            myMap.addPolyline(rectLine);
	            
	            
	        }
	    }

		@Override
		public void onInfoWindowClick(Marker marker) {
			Toast.makeText(this, marker.getTitle(), Toast.LENGTH_LONG).show();
			
			
			String directions="";
	    	for(int x = 0; x < stepInstructions.size(); x++){
	    		directions = directions + stepInstructions.get(x)+"\n";
	    	}
	    	
	    	directions = Html.fromHtml(directions).toString();
			
			Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
			sharingIntent.setType("text/plain");
			String shareBody = medicalservice+"\nAddress: "+end_address+"\nLatitude:"+marker.getPosition().latitude+"\nLongitude:"+marker.getPosition().longitude+"\n\nDirections:\n\n"+directions+"\n\nShared by Doc-ES for Android";
			sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Emergency Medical Service");
			sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
			startActivity(Intent.createChooser(sharingIntent, "Share via"));
			
		}

		@Override
		public boolean onNavigationItemSelected(int itemPosition, long itemId) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void activate(OnLocationChangedListener arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void deactivate() {
			// TODO Auto-generated method stub
			
		}
  
}