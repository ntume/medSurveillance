package com.dataware.medsurveillance.includes;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.dataware.medsurveillance.MapNavigateActivity;
import com.dataware.medsurveillance.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class AbstractMapDirectionActivity extends SherlockFragmentActivity {
	  protected static final String TAG_ERROR_DIALOG_FRAGMENT="errorDialog";
	  ArrayList<String> stepInstructions = new ArrayList<String>();

	  @Override
	  public boolean onCreateOptionsMenu(Menu menu) {
	    getSupportMenuInflater().inflate(R.menu.menu_navigate, menu);

	    return(super.onCreateOptionsMenu(menu));
	  }

	  @Override
	  public boolean onOptionsItemSelected(MenuItem item) {
	    if (item.getItemId() == R.id.directionList) {
	    	String directions="";
	    	for(int x = 0; x < MapNavigateActivity.stepInstructions.size(); x++){
	    		directions = directions + MapNavigateActivity.stepInstructions.get(x)+"\n";
	    	}
	    	
	    	directions = Html.fromHtml(directions).toString();
	    	
	    	new AlertDialog.Builder(AbstractMapDirectionActivity.this)
		    .setTitle("Directions")
		    .setMessage(directions)	  
		    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int whichButton) {
		            
		            
		        }
		    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int whichButton) {
		        		
		        }
		    }).show();
	      return(true);
	    }
	    
	    if (item.getItemId() == R.id.navigation) {
	    	Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" +MapNavigateActivity.toPosition.latitude+","+MapNavigateActivity.toPosition.longitude));
	    	intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    	startActivity(intent);
	    	
	    	//Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q="+MapNavigateActivity.toPosition.latitude+","+MapNavigateActivity.toPosition.longitude)); startActivity(i);
	    	
	    	return(true);
	    }
	    
	    if (item.getItemId() == R.id.instrustions) {
	    	
	    	new AlertDialog.Builder(AbstractMapDirectionActivity.this)
		    .setTitle("Tips!")
		    .setMessage("Click on marker to see the name of the medical service\n\nClick the balloon (name of medical service) to share that location with a friend through email, sms, whatsapp...")	  
		    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int whichButton) {
		            
		            
		        }
		    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int whichButton) {
		        		
		        }
		    }).show();
	      return(true);
	    }    

	    return super.onOptionsItemSelected(item);
	  }

	  protected boolean readyToGo() {
	    int status=
	        GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

	    if (status == ConnectionResult.SUCCESS) {
	      return(true);
	    }
	    else if (GooglePlayServicesUtil.isUserRecoverableError(status)) {
	      ErrorDialogFragment.newInstance(status)
	                         .show(getSupportFragmentManager(),
	                               TAG_ERROR_DIALOG_FRAGMENT);
	    }
	    else {
	      Toast.makeText(this, R.string.no_maps, Toast.LENGTH_LONG).show();
	      finish();
	    }
	    
	    return(false);
	  }

	  public static class ErrorDialogFragment extends DialogFragment {
	    static final String ARG_STATUS="status";

	    static ErrorDialogFragment newInstance(int status) {
	      Bundle args=new Bundle();

	      args.putInt(ARG_STATUS, status);

	      ErrorDialogFragment result=new ErrorDialogFragment();

	      result.setArguments(args);

	      return(result);
	    }

	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	      Bundle args=getArguments();

	      return GooglePlayServicesUtil.getErrorDialog(args.getInt(ARG_STATUS),
	                                                   getActivity(), 0);
	    }

	    @Override
	    public void onDismiss(DialogInterface dlg) {
	      if (getActivity() != null) {
	        getActivity().finish();
	      }
	      Uri marketUri = Uri.parse("market://details?id=com.google.android.gms");
	      Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
	      startActivity(marketIntent);
	    }
	  }
	}
