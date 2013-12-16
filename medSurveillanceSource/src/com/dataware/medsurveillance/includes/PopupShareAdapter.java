package com.dataware.medsurveillance.includes;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.dataware.medsurveillance.R;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;

public class PopupShareAdapter implements InfoWindowAdapter {
	  LayoutInflater inflater=null;

	  public PopupShareAdapter(LayoutInflater inflater) {
	    this.inflater=inflater;
	  }

	  @Override
	  public View getInfoWindow(Marker marker) {
	    return(null);
	  }

	  @Override
	  public View getInfoContents(Marker marker) {
	    View popup=inflater.inflate(R.layout.popupshare, null);

	    TextView tv=(TextView)popup.findViewById(R.id.title);

	    tv.setText(marker.getTitle());
	    tv=(TextView)popup.findViewById(R.id.snippet);
	    tv.setText(marker.getSnippet());

	    return(popup);
	  }
	}