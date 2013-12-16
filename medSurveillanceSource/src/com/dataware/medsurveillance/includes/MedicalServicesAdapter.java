package com.dataware.medsurveillance.includes;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dataware.medsurveillance.R;
import com.dataware.medsurveillance.ViewMedicalServicesActivity;

public class MedicalServicesAdapter extends BaseAdapter {
    
    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;
    //public ImageLoader imageLoader; 
    
    public MedicalServicesAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //imageLoader=new ImageLoader(activity.getApplicationContext());
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.medicalservices_row, null);

        TextView name = (TextView)vi.findViewById(R.id.region); // 
        TextView type = (TextView)vi.findViewById(R.id.number); // 
        TextView address = (TextView)vi.findViewById(R.id.address);      
        HashMap<String, String> song = new HashMap<String, String>();
        song = data.get(position);
        
        // Setting all values in listview
        name.setText(song.get(ViewMedicalServicesActivity.KEY_NAME));
        type.setText(song.get(ViewMedicalServicesActivity.KEY_TYPE));
        address.setText(song.get(ViewMedicalServicesActivity.KEY_ADDRESS));
        
        return vi;
    }
}