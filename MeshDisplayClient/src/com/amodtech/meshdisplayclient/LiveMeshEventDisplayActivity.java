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
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class LiveMeshEventDisplayActivity extends Activity {
	/*
	 * This class is is an Activity that implements the client display when the client is part of a 
	 * live event.
	 */
	
	private GetTextFromServerTask pollTask;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_live_mesh_event_display);
		
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
		
	}
	
	@Override
	protected void onPause() {
		
		//Stop the server polling
		if (pollTask != null) {
			pollTask.cancel(true);
			pollTask = null;
		}
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		
		super.onResume();
		
		//Restart the server polling - it should not exist but check first
		if (pollTask != null) {
			pollTask.cancel(true);
		}
		pollTask = new GetTextFromServerTask();
		pollTask.execute();
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
        	
        	//Continually pole the server until the task is cancelled
        	while(true) {
	        	InputStream is = null;
	        	int response = 0;
	            try {
	                URL url = new URL(meshDisplayEngine.getServerBaseURL() + "/text_for_client/event_id" 
	                				+ meshDisplayEngine.eventID + "/client_id/" + meshDisplayEngine.clintID);
	                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	                conn.setReadTimeout(10000 /* milliseconds */);
	                conn.setConnectTimeout(15000 /* milliseconds */);
	                conn.setRequestMethod("GET");
	                conn.setDoInput(true);
	                
	                // Starts the query
	                conn.connect();
	                
	                //Check the response code and decode the text sent by the server
	                response = conn.getResponseCode();
	                is = conn.getInputStream();
	                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	                String line = "";
	                StringBuffer receivedMessage = new StringBuffer();
	                while ((line = reader.readLine()) != null) {
	                	receivedMessage = receivedMessage.append(line);
	                }
	                reader.close();
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
				}
        	}
        }
       
        
        @Override
        protected void onProgressUpdate(ResponseInfo... responseInfo) {
        	//The next text sent from the server will be reported in the respnse Info
        	
            //Check the result and update the text to display if the response code was OK
        	if (responseInfo[0].responseCode == 200) {
        		//Update the text to display
        		meshDisplayEngine.textToDisplay = responseInfo[0].textToDisplay;
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
                URL url = new URL(meshDisplayEngine.getServerBaseURL() + "/event_client_delete");
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
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getQuery(params));
                writer.close();
                os.close();
                
                // Starts the query
                conn.connect();
                
                //For this case just check the response code - we are not interested in the response itself
                response = conn.getResponseCode();
                is = conn.getInputStream();
                
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
