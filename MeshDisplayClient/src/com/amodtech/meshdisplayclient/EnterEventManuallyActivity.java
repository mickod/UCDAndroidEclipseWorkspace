package com.amodtech.meshdisplayclient;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EnterEventManuallyActivity extends Activity {
	/*
	 * This class is the Activity which enables a user to enter data about the event
	 * and the client id and then to join an event.
	 */
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_enter_event_manually);
		
        //Hide the soft keyboard
        getWindow().setSoftInputMode(
        	      WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        //Set the join event button listener
        final Button joinButton = (Button) findViewById(R.id.entermanuallyJoinButton);
        joinButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	//Get the entered eventID and clientID
            	EditText eventEditText = (EditText) findViewById(R.id.eventIdEditText);
            	String eneteredEventID = eventEditText.getText().toString();
            	EditText clientEditText = (EditText) findViewById(R.id.clientIdEditText);
            	String eneteredClientID = clientEditText.getText().toString();
            	
            	//Join the event using an AsynchTask so the UI thread is not paused
            	new MeshDisplayJoinTask().execute(eneteredEventID, eneteredClientID);
            }
        });
        
        //Set the back button listener
        final Button backButton = (Button) findViewById(R.id.enterEventManuallyBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	finish();
            }
        });
	}
	
	private class MeshDisplayJoinTask extends AsyncTask<String, Void, Integer> {
		/*
		 * Private AsynchTask to message server to join an event
		 */
        @Override
        protected Integer doInBackground(String... ids) {
           	//Get the meshDisplayEngine object and set the event and client IDs
        	MeshDisplayApplictaion appObject = (MeshDisplayApplictaion)EnterEventManuallyActivity.this.getApplicationContext();
        	MeshDisplayClientEngine meshDisplayEngine = appObject.getAppMeshDisplayEngine();
        	meshDisplayEngine.eventID = ids[0];
        	meshDisplayEngine.clintID = ids[1];
              
            //Send the POST request to the HTTP server and check that an OK is received
        	InputStream is = null;
        	int response = 0;
            try {
                URL url = new URL(meshDisplayEngine.getServerBaseURL() + "/event_client");
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
                String paramString = URLEncodedUtils.format(params, "utf-8");
                writer.write(paramString);
                writer.close();
                os.close();
                
                // Starts the query
                conn.connect();
                
                //For this case just check the response code - we are not interested in the response itself
                response = conn.getResponseCode();
                is = conn.getInputStream();
                return response;
                
            } catch (IOException e) {
				//Some IO problem occurred - dump stack and inform caller
            	Log.d("EnterEventManuallyActivity MeshDisplayJoinTask", "exception posting join event request - response code: " + response);
				e.printStackTrace();
				return 0;
			} finally {
	            // Makes sure that the InputStream is closed after the app is
	            // finished using it.
                if (is != null) {
                	try {
                		is.close();
                	} catch (IOException e) {
        				//Some IO problem occurred while closing is - just to a stack dump in this case
                		Log.d("EnterEventManuallyActivity MeshDisplayJoinTask", "exception closing is file");
        				e.printStackTrace();
                	}
                } 
            }
        }
        
        @Override
        protected void onPostExecute(Integer responseCode) {
        	// onPostExecute displays the results of the AsyncTask.
        	
            //Check the result and move to new Activity if appropriate
        	if (responseCode == 200) {
        		//We have joined the event successfully - move to the live event activity
                Intent intent = new Intent(getApplicationContext(), LiveMeshEventDisplayActivity.class);
                startActivity(intent);
        	} else {
        		//Inform the user there was a problem
        		Toast.makeText(getApplicationContext(), R.string.connection_problem, Toast.LENGTH_SHORT).show();
        	}
       }
    }
}
