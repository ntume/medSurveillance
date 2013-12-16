package com.dataware.medsurveillance.includes;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

import android.util.Log;

public class Navigator {
	
	private JSONParserCat jsonParser;
		
	public final static String MODE_DRIVING = "driving";
	public final static String MODE_WALKING = "walking";
	
	String TotalDistance;
	String TotalDuration;
	String EndAddress;
	String BeginAddress;
	
	ArrayList<String> stepDistance = new ArrayList<String>();
	ArrayList<String> stepDuration = new ArrayList<String>(); 
	ArrayList<String> stepBeginLocation = new ArrayList<String>(); 
	ArrayList<String> stepEndLocation = new ArrayList<String>(); 
	ArrayList<String> stepInstructions = new ArrayList<String>(); 
	
	ArrayList<LatLng> listGeopoints = new ArrayList<LatLng>();
	
	public Navigator() { 
		jsonParser = new JSONParserCat();
	}
	
	public ArrayList<LatLng> fetchJsonDirections(LatLng start, LatLng end){
		String url = "http://maps.googleapis.com/maps/api/directions/json";
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("origin", start.latitude + "," + start.longitude));
        params.add(new BasicNameValuePair("destination", end.latitude + "," + end.longitude));
        params.add(new BasicNameValuePair("sensor", "false"));
        params.add(new BasicNameValuePair("units", "metric"));
        params.add(new BasicNameValuePair("mode", "driving"));
        
        Log.d("url: ", url);
        Log.d("params: ", params.toString());
        
        // getting JSON string from URL
        String json = jsonParser.makeHttpRequest(url, "GET",params);

        // Check your log cat for JSON reponse        
        Log.d("JSON: ", json);
        
        try {
			JSONObject jObj = new JSONObject(json);
			JSONArray jObjRoute = jObj.getJSONArray("routes");
			String route = jObjRoute.getString(0);
			
			JSONObject jObjRoutes = new JSONObject(route);
			
			/*
			 * To get Legs xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
			 */
			
			JSONArray jarrLegs = jObjRoutes.getJSONArray("legs");
			String leg = jarrLegs.getString(0);
			
			JSONObject jObjLegs = new JSONObject(leg);
			
			/*
			 * to get distance Text------------------------------------------------
			 */
			
			String Dist = jObjLegs.getString("distance");
			JSONObject jObjDist = new JSONObject(Dist);
			
			TotalDistance = jObjDist.getString("text");
								
			Log.d("JSON Distancexxxxxx: ", TotalDistance);
			
			/*
			 * To get duration------------------------------------------------
			 */
			
			String Duration = jObjLegs.getString("duration");
			JSONObject jObjDuration = new JSONObject(Duration);
			TotalDuration = jObjDuration.getString("text");
			
			Log.d("JSON DUrationxxxxxx: ", TotalDuration);
			
			/*
			 * To get end address------------------------------------------------
			 */
			
			EndAddress = jObjLegs.getString("end_address");
			Log.d("JSON Endxxxxxx: ", EndAddress);
			
			/*
			 * To get Begin Address------------------------------------------------
			 */
			
			BeginAddress = jObjLegs.getString("start_address");
			Log.d("JSON Beginxxxxxx: ", BeginAddress);
			
			/*
			 * To get driving instructions xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
			 */
			
								
			JSONArray jarrSteps = jObjLegs.getJSONArray("steps");
			
			for (int x = 0; x < jarrSteps.length();x++){
				
				Log.d("Stepsxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx: ", x+"");
						
				String steps = jarrSteps.getString(x);
				
				JSONObject jObjSteps = new JSONObject(steps);
				
				
				/*
				 * to get Step distance Text------------------------------------------------
				 */
				
				String StepDist = jObjSteps.getString("distance");
				JSONObject jObjStepDist = new JSONObject(StepDist);
				
				String textStepDist = jObjStepDist.getString("text");
				stepDistance.add(textStepDist);
				Log.d("JSON StepDistxxxxxx: ", textStepDist);
				
				/*
				 * to get Step Duration Text------------------------------------------------
				 */
				
				String StepDur = jObjSteps.getString("duration");
				JSONObject jObjStepDur = new JSONObject(StepDur);
				
				String textStepDur = jObjStepDur.getString("text");
				stepDuration.add(textStepDur);
				Log.d("JSON StepDurationxxxxxx: ", textStepDur);
				
				
				
				/*
				 * To get Direction instructions------------------------------------------------
				 */
				
				String instructions = jObjSteps.getString("html_instructions");
				stepInstructions.add(instructions);
				Log.d("JSON instructionsxxxxxx: ", instructions);
				
				/*
				 * to get Start Location Point------------------------------------------------
				 */
				
				String StepStartLoc = jObjSteps.getString("start_location");
				JSONObject jObjStepStartLoc = new JSONObject(StepStartLoc);
				
				String textStepStartLoc = jObjStepStartLoc.getString("lat")+","+jObjStepStartLoc.getString("lng");
				stepBeginLocation.add(textStepStartLoc);
				listGeopoints.add(new LatLng(Double.parseDouble(jObjStepStartLoc.getString("lat")), Double.parseDouble(jObjStepStartLoc.getString("lng"))));
				Log.d("JSON StepStart Locationxxxxxx: ", textStepStartLoc);	
				
				/*
				 * to get polyline------------------------------------------------
				 */
				
				String Steppoly = jObjSteps.getString("polyline");
				JSONObject jObjStepPoly = new JSONObject(Steppoly);
				String textStepPoly = jObjStepPoly.getString("points");
				ArrayList<LatLng> arr = decodePoly(textStepPoly);
				for(int j = 0 ; j < arr.size() ; j++) {
                	listGeopoints.add(new LatLng(arr.get(j).latitude, arr.get(j).longitude));
                }
				
				/*
				 * to get end Location Point------------------------------------------------
				 */
				
				String StepEndLoc = jObjSteps.getString("end_location");
				JSONObject jObjStepEndLoc = new JSONObject(StepEndLoc);
				
				String textStepEndLoc = jObjStepEndLoc.getString("lat")+","+jObjStepEndLoc.getString("lng");
				stepEndLocation.add(textStepEndLoc);
				listGeopoints.add(new LatLng(Double.parseDouble(jObjStepEndLoc.getString("lat")), Double.parseDouble(jObjStepEndLoc.getString("lng"))));
				Log.d("JSON StepEnd Locationxxxxxx: ", textStepEndLoc);
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return listGeopoints;
	}
	
	private ArrayList<LatLng> decodePoly(String encoded) {
		ArrayList<LatLng> poly = new ArrayList<LatLng>();
		int index = 0, len = encoded.length();
		int lat = 0, lng = 0;
		while (index < len) {
			int b, shift = 0, result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lat += dlat;
			shift = 0;
			result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lng += dlng;
			
			LatLng position = new LatLng((double) lat / 1E5, (double) lng / 1E5);
			poly.add(position);
		}
		return poly;
	}
	
	public String getTotalDistance(){
        return this.TotalDistance;
    }
	
	public String getTotalDuration(){
        return this.TotalDuration;
    }
	
	public String getEndAddress(){
        return this.EndAddress;
    }
	
	public String getBeginAddress(){
        return this.BeginAddress;
    }
	
	public ArrayList<String> getStepDistance(){
        return this.stepDistance;
    }
	
	public ArrayList<String> getStepDuration(){
        return this.stepDuration;
    }
	
	public ArrayList<String> getStepInstruction(){
        return this.stepInstructions;
    }
	
	public ArrayList<String> getStepBeginLocationn(){
        return this.stepBeginLocation;
    }
	
	public ArrayList<String> getStepEndLocation(){
        return this.stepEndLocation;
    }
}
