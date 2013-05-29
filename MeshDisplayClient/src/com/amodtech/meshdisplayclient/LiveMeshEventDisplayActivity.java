package com.amodtech.meshdisplayclient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LiveMeshEventDisplayActivity extends Activity implements LocationListener{
	/*
	 * This class is is an Activity that implements the client display when the client is part of a 
	 * live event.
	 */
	
	private GetTextFromServerTask pollTask;
	private LocationManager locationManager;
	private String loctaionProvider = null;
	private Criteria loctaionCriteria = new Criteria();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_live_mesh_event_display);
		
		//Set up the location provider - we set fine accuracy as that is all that is of interest to us
		//for mesh display. If we can only get coarse accuracy it is of little use - in fact even
		//fine accuracy is likely to not meet our needs but this will allow us experiment and test it
		//in different environments.
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		//loctaionCriteria.setAccuracy(Criteria.ACCURACY_FINE);
		loctaionProvider = locationManager.getBestProvider(loctaionCriteria, false);
		Log.d("LiveMeshEventDisplayActivity onCreate","locationProvider: " + loctaionProvider);
		Location lastKnowLoctation = locationManager.getLastKnownLocation(loctaionProvider);
		if (lastKnowLoctation != null) {
			Log.d("LiveMeshEventDisplayActivity onCreate","lastKnowLoctation: " + lastKnowLoctation.toString());
		} else {
			Log.d("LiveMeshEventDisplayActivity onCreate","lastKnowLoctation is null");
		}
		
		//Create an Asynch task to poll the server for the text to display. Note a Service
		//is not used here as this task is tightly coupled to this activity - we will stop
		//it when we navigate away from the activity.
		pollTask = new GetTextFromServerTask();
		pollTask.execute();
	
        //Set the back button listener
        final Button backButton = (Button) findViewById(R.id.liveMeshDisplayBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	//Create an AsynchTask to message the server to leave the event
            	//When the server responds the activity will finish
            	LeaveEventTask leaveEventTask = new LeaveEventTask();
            	leaveEventTask.execute();
            }
        });
        
        //Set the info button listener
        final Button infoButton = (Button) findViewById(R.id.liveMeshDisplayEventInfoButton);
        infoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	//Start the event info activity
                Intent intent = new Intent(v.getContext(), EventInfoActivity.class);
                startActivity(intent);
            }
        });
		
	}
	
	@Override
	protected void onPause() {
		Log.d("LiveMeshEventDisplayActivity","onPause");
		
		//Stop the server polling
		if (pollTask != null) {
			pollTask.cancel(true);
			pollTask = null;
		}
		
		//Stop location updates
		locationManager.removeUpdates(this);
		
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		Log.d("LiveMeshEventDisplayActivity","onResume");
		
		super.onResume();
		
		//Request location updates
		//loctaionCriteria.setAccuracy(Criteria.ACCURACY_FINE);
		loctaionProvider = locationManager.getBestProvider(loctaionCriteria, false);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 1, this);
		
		//Restart the server polling - it should not exist but check first
		if (pollTask != null) {
			pollTask.cancel(true);
		}
		pollTask = new GetTextFromServerTask();
		pollTask.execute();
	}
	
	@Override
	protected void onStop() {
		Log.d("LiveMeshEventDisplayActivity","onStop");
		
		//Stop the server polling
		if (pollTask != null) {
			pollTask.cancel(true);
			pollTask = null;
		}
		super.onStop();
	}
	
	@Override
	public void onLocationChanged(Location location) {
		//This is the listener method for loctaion updates - it will be called when the users location changes.
		//We update the MeshEventEngine with the new location details and they will then be reported to the server
		//during the regular poll. This is experimental functionality at this time to see how useful the location
		//data is for showing the relative positions of phones in a display - it is expecyed that loctaion dta by itelf 
		//will not be enough for the granularity we require.
    	MeshDisplayApplictaion appObject = (MeshDisplayApplictaion)LiveMeshEventDisplayActivity.this.getApplicationContext();
    	MeshDisplayClientEngine meshDisplayEngine = appObject.getAppMeshDisplayEngine();
    	meshDisplayEngine.deviceLatitude = (int) (location.getLatitude());
		meshDisplayEngine.deviceLongitude = (int) (location.getLongitude());
	}
	
	@Override
	public void onProviderDisabled(String arg0) {
		//Ignore for now	
	}

	@Override
	public void onProviderEnabled(String arg0) {
		//Ignore for now
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		//Ignore for now
	}
	
	private class GetTextFromServerTask extends AsyncTask<String, ResponseInfo, Void> {
		/*
		 * Private AsynchTask to poll the server to get the text to display for this device
		 */
       	//Get the meshDisplayEngine object and set the event and client IDs
    	MeshDisplayApplictaion appObject = (MeshDisplayApplictaion)LiveMeshEventDisplayActivity.this.getApplicationContext();
    	MeshDisplayClientEngine meshDisplayEngine = appObject.getAppMeshDisplayEngine();
    	
        @Override
        protected Void doInBackground(String... args) {
            //Poll the server and send the POST request to the HTTP server and check that an OK is received
        	
        	//Continually pole the server until the task is cancelled - include the current location longitude
        	//and latitude data in the request 
        	while(true) {
	        	InputStream is = null;
	        	int response = 0;
	            try {
	                URL url = new URL(meshDisplayEngine.getServerBaseURL() + "/text_for_client/event_id/" 
	                				+ meshDisplayEngine.eventID 
	                				+ "/client_id/" + meshDisplayEngine.clintID 
	                				+ "/lat/" + meshDisplayEngine.deviceLatitude
	                				+ "/long/" + meshDisplayEngine.deviceLongitude);
	                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	                conn.setReadTimeout(10000 /* milliseconds */);
	                conn.setConnectTimeout(15000 /* milliseconds */);
	                conn.setRequestMethod("GET");
	                conn.setRequestProperty("accept","application/json");
	                conn.setDoInput(true);
	                
	                // Starts the query
	                conn.connect();
	                
	                //Check the response code and decode the text sent by the server
	                response = conn.getResponseCode();
	                is = conn.getInputStream();
	                BufferedReader reader = new BufferedReader(new InputStreamReader(is), 8192);
	                String line = "";
	                StringBuffer receivedMessage = new StringBuffer();
	                while ((line = reader.readLine()) != null) {
	                	receivedMessage = receivedMessage.append(line);
	                }
	                reader.close();
	                Log.d("LiveMeshEventDisplayActivity GetTextFromServerTask", "receivedMessage: " + receivedMessage);
	                JSONObject jsonResponseObject = new JSONObject(receivedMessage.toString());
	                String textToDisplay = jsonResponseObject.getString("client_text");
	                publishProgress(new ResponseInfo(response, textToDisplay));
	            } catch (IOException e) {
					//Some IO problem occurred - dump stack and inform caller
	            	Log.d("LiveMeshEventDisplayActivity GetTextFromServerTask", "exception posting join event request - response code: " + response);
					e.printStackTrace();
				} catch (JSONException e) {
					//An Error occurred decoding the JSON
					Log.d("LiveMeshEventDisplayActivity GetTextFromServerTask", "exception parsing JSON response");
					e.printStackTrace();
				} finally {
		            // Makes sure that the InputStream is closed after the app is
		            // finished using it.
	                if (is != null) {
	                	try {
	                		is.close();
	                	} catch (IOException e) {
	        				//Some IO problem occurred while closing is - just to a stack dump in this case
	                		Log.d("LiveMeshEventDisplayActivity GetTextFromServerTask", "exception closing is file");
	        				e.printStackTrace();
	                	}
	                } 
	            }
	            
	            //Sleep until the next poll
	            try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					//Interrupted while sleeping - simply log this
					Log.d("LiveMeshEventDisplayActivity GetTextFromServerTask", "interupted while sleeping");
					return null;
				}
        	}
        }
              
        @Override
        protected void onProgressUpdate(ResponseInfo... responseInfo) {
        	//The next text sent from the server will be reported in the respnse Info
        	
            //Check the result and update the text to display if the response code was OK
        	if (responseInfo[0].responseCode == 200 | responseInfo[0].responseCode == 201) {
        		//Update the text to display
        		TextView textDisplay = (TextView) findViewById(R.id.liveMeshDisplayText);
        		textDisplay.setText(responseInfo[0].textToDisplay);
        		Log.d("LiveMeshEventDisplayActivity GetTextFromServerTask","-> onPostExecute: text: " + responseInfo[0].textToDisplay);
        	} else {
        		//Log an issue
        		Log.d("LiveMeshEventDisplayActivity GetTextFromServerTask","-> onPostExecute: unexpecetd resposne code: " + responseInfo[0].responseCode);
        	}
        }
       
	}
	
	private class ResponseInfo {
		//Simple class to contain response codes
		public int responseCode;
		public String textToDisplay;
		
		public ResponseInfo(int resCode, String dispText) {
			this.responseCode = resCode;
			this.textToDisplay = dispText;
			
		}
	}
	
	private class LeaveEventTask extends AsyncTask<String, Void, Integer> {
		/*
		 * Private AsynchTask to message server to join an event
		 */
        @Override
        protected Integer doInBackground(String... args) {
        	
           	//Get the meshDisplayEngine object
        	MeshDisplayApplictaion appObject = (MeshDisplayApplictaion)LiveMeshEventDisplayActivity.this.getApplicationContext();
        	MeshDisplayClientEngine meshDisplayEngine = appObject.getAppMeshDisplayEngine();
              
            //Send the POST request to the HTTP server and check that an OK is received
        	InputStream is = null;
        	int response = 0;
            try {
                URL url = new URL(meshDisplayEngine.getServerBaseURL() + "/event_client_remove");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                
                //Add the POST parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("event_id", meshDisplayEngine.eventID));
                params.add(new BasicNameValuePair("client_id", meshDisplayEngine.clintID));
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"), 8192);
                writer.write(getQuery(params));
                writer.close();
                os.close();
                
                // Starts the query
                conn.connect();
                
                //For this case just check the response code - we are not interested in the response itself
                response = conn.getResponseCode();
                is = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is), 8192);
                String line = "";
                StringBuffer receivedMessage = new StringBuffer();
                while ((line = reader.readLine()) != null) {
                	receivedMessage = receivedMessage.append(line);
                }
                Log.d("LiveMeshEventDisplayActivity LeaveEventTask", "response message: " + receivedMessage);
                reader.close();
                
            } catch (IOException e) {
				//Some IO problem occurred - dump stack and inform caller
            	Log.d("LiveMeshEventDisplayActivity LeaveEventTask", "exception posting leave event request - response code: " + response);
				e.printStackTrace();
				response = 0;;
			} finally {
	            // Makes sure that the InputStream is closed after the app is
	            // finished using it.
                if (is != null) {
                	try {
                		is.close();
                	} catch (IOException e) {
        				//Some IO problem occurred while closing is - just to a stack dump in this case
                		Log.d("LiveMeshEventDisplayActivity LeaveEventTask", "exception closing is file");
        				e.printStackTrace();
                	}
                } 
            }
            
            //Set the client and event id to null
        	meshDisplayEngine.eventID = null;
        	meshDisplayEngine.clintID = null;
        	
        	return response;
        }
        
        @Override
        protected void onPostExecute(Integer responseCode) {
        	//onPostExecute simply checks the response code and then finishes the Activity
        	
            //Check the result and move to new Activity if appropriate
        	if (responseCode != 200 & responseCode != 201) {
        		//Log the problem leaving the event
        		Log.d("LiveMeshEventDisplayActivity LeaveEventTask", "Unexpected response code for " +
        				"leave event request - response code: " + responseCode);
        	}
        	
        	//Leave the activity
        	finish();
       }
        
        private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException {
        	//utility method to help build the post method - see: http://stackoverflow.com/a/13486223/334402
            StringBuilder result = new StringBuilder();
            boolean first = true;

            for (NameValuePair pair : params)
            {
                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
            }

            return result.toString();
        }
    }
}
