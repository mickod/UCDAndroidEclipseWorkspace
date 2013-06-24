package com.amodtech.meshdisplaycontroller;

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

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class EntryScreenAvtivity extends Activity {

	/*
	 * This class is the main menu Activity for the application
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_entry_screen);
		
        // Make sure default values are applied.  
        PreferenceManager.setDefaultValues(this,
                R.xml.server_url_preference_fragment, false);
		
		//Display the Main Menu image
        ImageView mainMenuImageView;
        mainMenuImageView = (ImageView) findViewById(R.id.mainMenuPicture);
        int imageResID = getResources().getIdentifier("controller_menu_image", "drawable",  getPackageName());
        if (imageResID != 0) {
        	mainMenuImageView.setImageResource(imageResID);
        }
        
        //Hide the soft keyboard
        getWindow().setSoftInputMode(
        	      WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        //Set the enter manually button listener
        final Button menuManualEnterButton = (Button) findViewById(R.id.enterEventButton);
        menuManualEnterButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	//Get the entered eventID
            	EditText eventEditText = (EditText) findViewById(R.id.eventIdEditText);
            	String eneteredEventID = eventEditText.getText().toString();
            	
            	//Join the event using an AsynchTask so the UI thread is not paused
            	new CreateMeshDisplayEventTask().execute(eneteredEventID);

            }
        });
        
        //Set the about button listener
        final Button menuAboutButton = (Button) findViewById(R.id.aboutButton);
        menuAboutButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AboutMeshControllerActivity.class);
                startActivity(intent);
            }
        });	
        
        //Set the preferences button listener
        final Button menuPreferencesButton = (Button) findViewById(R.id.preferencesButton);
        menuPreferencesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MeshDisplayControllerPreferencesActivity.class);
                startActivity(intent);
            }
        });	
        
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.entry_screen_avtivity, menu);
		return true;
	}
	
	private class CreateMeshDisplayEventTask extends AsyncTask<String, Void, Integer> {
		/*
		 * Private AsynchTask to message server to join an event
		 */
        @Override
        protected Integer doInBackground(String... ids) {
           	//Get the meshDisplayEngine object and set the event ID
        	MeshDisplayControllerApplictaion appObject = (MeshDisplayControllerApplictaion)EntryScreenAvtivity.this.getApplicationContext();
        	MeshDisplayControllerEngine meshDisplayEngine = appObject.getAppMeshDisplayControllerEngine();
        	meshDisplayEngine.eventID = ids[0];
              
            //Send the POST request to the HTTP server and check that an OK is received
        	InputStream is = null;
        	int response = 0;
            try {
                URL url = new URL(meshDisplayEngine.getServerBaseURL() + "/event");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                
                //Add the POST parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("event_id", meshDisplayEngine.eventID));
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
                Intent intent = new Intent(getApplicationContext(), LiveMeshEventControllerActivity.class);
                startActivity(intent);
        	} else {
        		//Inform the user there was a problem
        		Toast.makeText(getApplicationContext(), R.string.connection_problem, Toast.LENGTH_SHORT).show();
        	}
       }
	}
	
}
