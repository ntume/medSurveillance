package com.dataware.medsurveillance;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class HomeActivity extends SherlockActivity {
	String userID;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
			    
		setContentView(R.layout.activity_home);
		
		Bundle extras = getIntent().getExtras();
        if (extras!=null)
        {
        	userID = extras.getString("userID");
        }
		
	}
	
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	
        return super.onCreateOptionsMenu(menu);
    }

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		return super.onOptionsItemSelected(item);
	}
	
	public void onClickFeature (View v)
	{
	    int id = v.getId ();
	    switch (id) {
	      case R.id.home_btn_feature1 :	           
	    	   Intent demographic = new Intent(getApplicationContext(), DemographicActivity.class);
		       Bundle extras = new Bundle();		        
		       extras.putString("userID", userID);
		       demographic.putExtras(extras);
		       startActivity(demographic);
	           break;
	      case R.id.home_btn_feature2 :
	           startActivity (new Intent(getApplicationContext(), HomeCoordinatesActivity.class));
	           break;	      
	      case R.id.home_btn_feature5 :
	    	  Intent demographiclist = new Intent(getApplicationContext(), ListDemographicActivity.class);
		       Bundle extraslist = new Bundle();		        
		       extraslist.putString("userID", userID);
		       demographiclist.putExtras(extraslist);
		       startActivity(demographiclist);
	           break;
	      case R.id.home_btn_feature6 :
	    	   Intent regionlist = new Intent(getApplicationContext(), ListRegionsActivity.class);
		       Bundle extrasregion = new Bundle();		        
		       extrasregion.putString("userID", userID);
		       regionlist.putExtras(extrasregion);
		       startActivity(regionlist);
	           break;
	      default: 
	    	   break;
	    }
	}

}
