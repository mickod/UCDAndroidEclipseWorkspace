package com.amodtech.meshdisplayclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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
            	finish();
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
	
	private class GetTextFromServerTask extends AsyncTask<String, Void, responseInfo> {
		/*
		 * Private AsynchTask to poll the server to get the text to display for this device
		 */
       	//Get the meshDisplayEngine object and set the event and client IDs
    	MeshDisplayApplictaion appObject = (MeshDisplayApplictaion)LiveMeshEventDisplayActivity.this.getApplicationContext();
    	MeshDisplayClientEngine meshDisplayEngine = appObject.getAppMeshDisplayEngine();
    	
        @Override
        protected responseInfo doInBackground(String args[]) {
            //Send the POST request to the HTTP server and check that an OK is received
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
                return new responseInfo(response, textToDisplay);
                publishProgress(new responseInfo(response, textToDisplay));
            } catch (IOException e) {
				//Some IO problem occurred - dump stack and inform caller
            	Log.d("LiveMeshEventDisplayActivity GetTextFromServerTask", "exception posting join event request - response code: " + response);
				e.printStackTrace();
				return new responseInfo(0, "");
			} catch (JSONException e) {
				//An Error occurred decoding the JSON
				Log.d("LiveMeshEventDisplayActivity GetTextFromServerTask", "exception parsing JSON response");
				e.printStackTrace();
				return new responseInfo(0, "");
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
        }
        
        @Override
        protected void onPostExecute(responseInfo responseInfo) {
        	// onPostExecute displays the results of the AsyncTask.
        	
            //Check the result and update the text to display if the response code was OK
        	if (responseInfo.responseCode == 200) {
        		//Update the text to display
        		meshDisplayEngine.textToDisplay = responseInfo.textToDisplay;
        	} else {
        		//Log an issue
        		Log.d("LiveMeshEventDisplayActivity GetTextFromServerTask","-> onPostExecute: unexpecetd resposne code: " + responseInfo.responseCode);
        	}
       }
	}
	
	private class responseInfo {
		//Simple class to contain response codes
		public int responseCode;
		public String textToDisplay;
		
		public responseInfo(int resCode, String dispText) {
			this.responseCode = resCode;
			this.textToDisplay = dispText;
			
		}
	}
}
