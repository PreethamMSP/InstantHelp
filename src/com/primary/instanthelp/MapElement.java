package com.primary.instanthelp;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.telephony.SmsManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.os.Build;
import android.provider.ContactsContract;


public class MapElement extends FragmentActivity{

	@SuppressLint("NewApi")
	public void onCreate(Bundle savedInstanceState) {
		
		GoogleMap map;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_mapelement);
		Bundle bundle = getIntent().getExtras();

		if(bundle != null)
		{
			String src = bundle.getString("Source");
			String dest = bundle.getString("Destination");
			String PhoneNo = bundle.getString("PhoneNo");
			Toast.makeText(getApplicationContext(),
					src+" "+dest+" "+PhoneNo,
					Toast.LENGTH_LONG).show();
			try
			{
				//geo coding example
//				String src="1226, West Adams Boulevards,Los Angeles,California 90007";
//				String dest="6740, Kings Harbour Drive, Rancho Palos Verdes,California";

				
				HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
				GenericUrl srcURL=new GenericUrl("https://maps.googleapis.com/maps/api/geocode/json");
				GenericUrl destURL=new GenericUrl("https://maps.googleapis.com/maps/api/geocode/json");

				HttpRequest srcRequest = HTTP_TRANSPORT.createRequestFactory().buildGetRequest(srcURL); 
				srcRequest.getUrl().put("sensor","false");
				srcRequest.getUrl().put("address",src);

				HttpResponse srcRes=srcRequest.execute();

				//System.out.println("src json otp:"+srcRes.parseAsString());

				JSONObject srcObject=new JSONObject(srcRes.parseAsString());
				JSONArray resultsArray=(JSONArray)srcObject.get("results");

				boolean geometryObjectFound=false;
				JSONObject obj=null,geoObj = null;
				for(int i=0;i<resultsArray.length();i++)
				{
					obj=(JSONObject) resultsArray.get(i);
					if(obj.has("geometry"))
					{ 
						geometryObjectFound=true;
						geoObj=(JSONObject)obj.get("geometry");
						break;
					}
				}


				JSONObject locObj=null;

				if(geoObj.has("location"))
				{
					locObj=(JSONObject)geoObj.get("location");
				}

				double srcLat=locObj.getDouble("lat");
				double srcLng=locObj.getDouble("lng");

				System.out.println("srcLat:"+srcLat+",srcLng:"+srcLng);

				HttpRequest destRequest = HTTP_TRANSPORT.createRequestFactory().buildGetRequest(destURL);

				destRequest.getUrl().put("sensor","false");
				destRequest.getUrl().put("address",dest);

				HttpResponse destRes=destRequest.execute();

				JSONObject destObject=new JSONObject(destRes.parseAsString());
				JSONArray destResultsArray=(JSONArray)destObject.get("results");

				boolean destGeometryObjectFound=false;
				JSONObject destObj=null,destGeoObj = null;

				for(int i=0;i<destResultsArray.length();i++)
				{
					destObj=(JSONObject) destResultsArray.get(i);
					if(destObj.has("geometry"))
					{ 
						destGeometryObjectFound=true;
						destGeoObj=(JSONObject)destObj.get("geometry");
						break;
					}
				}

				JSONObject destLocObj=null;

				if(destGeoObj.has("location"))
				{
					destLocObj=(JSONObject)destGeoObj.get("location");
				}

				double destLat=destLocObj.getDouble("lat");
				double destLng=destLocObj.getDouble("lng");

				System.out.println("destLat:"+destLat+",destLng:"+destLng);


				GenericUrl hosturl=new GenericUrl("https://maps.googleapis.com/maps/api/directions/json?key=AIzaSyDLodOX1JRB1YglSK2hA-lBVAObo8N84vc");


				//String url = "origin=37.2345487+ ,-121.5840723&destination=37.236064,-121.961595&sensor=false";
				HttpRequest request = HTTP_TRANSPORT.createRequestFactory().buildGetRequest(hosturl);

				//request.getUrl().put("origin", 37.2345487 + "," + -121.5840723);
				request.getUrl().put("origin", srcLat + "," + srcLng);
				//request.getUrl().put("destination", 37.236064 + "," + -121.961595);
				request.getUrl().put("destination", destLat + "," + destLng);
				request.getUrl().put("sensor", "false");
				HttpResponse res=request.execute();

				JSONObject dirObject=new JSONObject(res.parseAsString());
				JSONArray routesArray=null,legsArray=null,stepsArray=null;
				JSONObject routeObject=null,legsObject=null;
				List<String> directions=new ArrayList<String>();

				if(dirObject.has("routes"))
					routesArray=(JSONArray)dirObject.getJSONArray("routes");
				JSONObject object=null;
				JSONArray array=null;

				for(int i=0;i<routesArray.length();i++)
				{
					Object singleObj=routesArray.get(i);

					if(singleObj instanceof JSONObject)
					{
						object=(JSONObject)singleObj;

						if(object.has("legs"))
						{
							legsArray=(JSONArray)object.getJSONArray("legs");

						}

						legsObject=(JSONObject)legsArray.get(0);

						if(legsObject.has("steps"))
						{
							stepsArray=(JSONArray)legsObject.getJSONArray("steps");
						}
					}
				}



				JSONObject stepObject=null;
				JSONObject coordinatesArray=null;
				double lat,lng;
				List<LatLng> coordinatesList=new ArrayList<LatLng>();

				for(int i=0;i<stepsArray.length();i++)
				{
					stepObject=(JSONObject) stepsArray.get(i);
					if(stepObject.has("html_instructions"))
					{
						String singleDirectionDetail=new String(stepObject.getString("html_instructions").getBytes(),Charset.forName("UTF-8"));
						directions.add(singleDirectionDetail);
					}

					if(stepObject.has("start_location"))
					{
						coordinatesArray=stepObject.getJSONObject("start_location");

						lat=coordinatesArray.getDouble("lat");
						lng=coordinatesArray.getDouble("lng");
						LatLng coordinates=new LatLng(lat,lng);
						coordinatesList.add(coordinates);
					}
				}

				String direction=null;

				for(int i=0;i<directions.size();i++)
				{
					direction=directions.get(i);
					System.out.println("Direction"+(i+1)+":"+direction);
				}
				//display on the map, get google map object using map fragment and add to its polylines

				/*map = ((SupportMapFragment)getFragmentManager().findFragmentById(R.id.map))
				        .getMap(); */
				map=((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMap();

				PolylineOptions line=new PolylineOptions();
				
				for(LatLng latlng:coordinatesList)
				{
					line.add(latlng);
				}
				
				map.addPolyline(line); 
			} 
			catch (Exception ex) 
			{
				ex.printStackTrace();
				System.out.println("Message:"+ex.getMessage());
			}
		}

		
	}
}
