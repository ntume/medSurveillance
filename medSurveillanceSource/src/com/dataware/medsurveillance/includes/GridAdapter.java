package com.dataware.medsurveillance.includes;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dataware.medsurveillance.R;

public class GridAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<HashMap<String, String>> data;

	public GridAdapter(Context context, ArrayList<HashMap<String, String>> d) {
		this.context = context;
		this.data = d;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View gridView;

		//if (convertView == null) {

			gridView = new View(context);
			
			HashMap<String, String> datareceived = new HashMap<String, String>();
			datareceived = data.get(position);

			// get layout from mobile.xml
			gridView = inflater.inflate(R.layout.row_gridcard, null);

			// set value into textview
			TextView title = (TextView) gridView.findViewById(R.id.title);
			title.setText(datareceived.get("title"));

			TextView subtitle = (TextView)gridView.findViewById(R.id.subtitle);
			subtitle.setText(datareceived.get("subtitle"));
			
			TextView semititle = (TextView)gridView.findViewById(R.id.semititle);
			semititle.setText(datareceived.get("semititle"));
			
			ImageView imageView = (ImageView) gridView.findViewById(R.id.list_image);	
			
			
			String sync = datareceived.get("sync");
			String typelist = datareceived.get("typelist");
			
			if(typelist.equals("family")){
				imageView.setImageResource(R.drawable.icon_familyhome);
			}
			else if(typelist.equals("person")){
				if(datareceived.get("gender").equals("Male"))
					imageView.setImageResource(R.drawable.icon_personmale);
				else
					imageView.setImageResource(R.drawable.icon_personfemale);
			}
			
			ImageView imageViewsync = (ImageView) gridView.findViewById(R.id.sync_image);
			
			/*f(sync.equals("true")){
				imageViewsync.setImageResource(R.drawable.synced);				
			}
			else if(sync.equals("false")){
				imageViewsync.setImageResource(R.drawable.notsynced);				
			}*/

		//} else {
		//	gridView = (View) convertView;
		//}

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
