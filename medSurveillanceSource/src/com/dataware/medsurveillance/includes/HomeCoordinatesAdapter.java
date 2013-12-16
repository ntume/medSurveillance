package com.dataware.medsurveillance.includes;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dataware.medsurveillance.R;

public class HomeCoordinatesAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<HashMap<String, String>> data;

	public HomeCoordinatesAdapter(Context context, ArrayList<HashMap<String, String>> d) {
		this.context = context;
		this.data = d;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View gridView;
		
		gridView = new View(context);
		
		HashMap<String, String> datareceived = new HashMap<String, String>();
		datareceived = data.get(position);

		// get layout from mobile.xml
		gridView = inflater.inflate(R.layout.row_homecoordinates, null);

		// set value into textview
		TextView homenumber = (TextView) gridView.findViewById(R.id.homenumber);
		homenumber.setText(datareceived.get("homenumber"));	
		
		TextView address = (TextView) gridView.findViewById(R.id.address);
		address.setText(datareceived.get("address"));

		return gridView;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

}
