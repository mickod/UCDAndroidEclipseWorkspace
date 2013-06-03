package com.amodtech.meshdisplaycontroller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LiveMeshEventControllerActivity extends Activity implements View.OnTouchListener  {
	/*
	 * This class represents the activity that controls a live event
	 */
	
	private PollServerForClients serverPollTask;
	private ViewGroup eventDisplayArea;
	private TextView clientText;
	private HashMap<String, TextView> clientDisplayMap = new HashMap<String, TextView>();
	private int _xDelta;
	private int _yDelta;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_live_mesh_event_controller);
		
		//Text client display
		eventDisplayArea = (ViewGroup)findViewById(R.id.mainEventDisplayArea);

		clientText = new TextView(this);
		clientText.setText("New Client...");

	    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(150, 50);
	    layoutParams.leftMargin = 50;
	    layoutParams.topMargin = 50;
	    layoutParams.bottomMargin = -250;
	    layoutParams.rightMargin = -250;
	    clientText.setLayoutParams(layoutParams);

	    clientText.setOnTouchListener(this);
	    eventDisplayArea.addView(clientText);
	    
		//Create an Asynch task to poll the server for client updates. Note a Service
		//is not used here as this task is tightly coupled to this activity - we will stop
		//it when we navigate away from the activity.
		serverPollTask = new PollServerForClients();
		serverPollTask.execute();
	
        //Set the back button listener
        final Button backButton = (Button) findViewById(R.id.liveMeshEventBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	finish();
            }
        });
		
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		//This method allows a view be moved around in the parent view. This solution
		//is based on the answer at: http://stackoverflow.com/questions/9398057/
		//android-move-a-view-on-touch-move-action-move
	    final int X = (int) event.getRawX();
	    final int Y = (int) event.getRawY();
	    switch (event.getAction() & MotionEvent.ACTION_MASK) {
	        case MotionEvent.ACTION_DOWN:
	            RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
	            _xDelta = X - lParams.leftMargin;
	            _yDelta = Y - lParams.topMargin;
	            break;
	        case MotionEvent.ACTION_UP:
	            break;
	        case MotionEvent.ACTION_POINTER_DOWN:
	            break;
	        case MotionEvent.ACTION_POINTER_UP:
	            break;
	        case MotionEvent.ACTION_MOVE:
	            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
	            layoutParams.leftMargin = X - _xDelta;
	            layoutParams.topMargin = Y - _yDelta;
	            layoutParams.rightMargin = -250;
	            layoutParams.bottomMargin = -250;
	            view.setLayoutParams(layoutParams);
	            break;
	    }
	    eventDisplayArea.invalidate();
	    return true;
	}
	
	private void displayNewClient(MeshDisplayClient clientToDisplay) {
		//This method add a new client to the event Display
		eventDisplayArea = (ViewGroup)findViewById(R.id.mainEventDisplayArea);

		TextView clientTextView = new TextView(this);
		clientTextView.setText(clientToDisplay.id);

	    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(150, 50);
	    layoutParams.leftMargin = 50;
	    layoutParams.topMargin = 50;
	    layoutParams.bottomMargin = -250;
	    layoutParams.rightMargin = -250;
	    clientTextView.setLayoutParams(layoutParams);

	    clientText.setOnTouchListener(this);
	    eventDisplayArea.addView(clientTextView);
	    clientDisplayMap.put(clientToDisplay.id, clientTextView);
	}
	
	private void removeClientFromDisplay(String clientIDToRemove) {
		//This method removes a client from the event Display
		eventDisplayArea = (ViewGroup)findViewById(R.id.mainEventDisplayArea);

		TextView clientTextViewToRemove = clientDisplayMap.get(clientIDToRemove);
		eventDisplayArea.removeView(clientTextViewToRemove);
		clientDisplayMap.remove(clientIDToRemove);
	}
	
	@Override
	protected void onPause() {
		Log.d("LiveMeshEventControllerActivity","onPause");
		
		//Stop the server polling
		if (serverPollTask != null) {
			serverPollTask.cancel(true);
			serverPollTask = null;
		}

		super.onPause();
	}
	
	@Override
	protected void onResume() {
		Log.d("LiveMeshEventControllerActivity","onResume");
		
		super.onResume();
		
		//Restart the server polling - it should not exist but check first
		if (serverPollTask != null) {
			serverPollTask.cancel(true);
		}
		serverPollTask = new PollServerForClients();
		serverPollTask.execute();
	}

	private class PollServerForClients extends AsyncTask<String, ResponseInfo, Void> {
		/*
		 * Private AsynchTask to poll the server to get the text to display for this device
		 */
       	//Get the meshDisplayEngine object
    	MeshDisplayControllerApplictaion appObject = (MeshDisplayControllerApplictaion)LiveMeshEventControllerActivity.this.getApplicationContext();
    	MeshDisplayControllerEngine meshDisplayEngine = appObject.getAppMeshDisplayControllerEngine();
    	
        @Override
        protected Void doInBackground(String... args) {
            //Poll the server and send the POST request to the HTTP server and check that an OK is received
        	
        	//Continually pole the server until the task is cancelled - include the current location longitude
        	//and latitude data in the request 
        	while(true) {
	        	InputStream is = null;
	        	int response = 0;
	            try {
	                URL url = new URL(meshDisplayEngine.getServerBaseURL() + "/device_list_for_event/event_id/" 
	                				+ meshDisplayEngine.eventID);
	                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	                conn.setReadTimeout(10000 /* milliseconds */);
	                conn.setConnectTimeout(15000 /* milliseconds */);
	                conn.setRequestMethod("GET");
	                conn.setRequestProperty("accept","application/json");
	                conn.setDoInput(true);
	                
	                // Starts the query
	                conn.connect();
	                
	                //Check the response code and decode the message sent by the server
	                response = conn.getResponseCode();
	                is = conn.getInputStream();
	                BufferedReader reader = new BufferedReader(new InputStreamReader(is), 8192);
	                String line = "";
	                StringBuffer receivedMessage = new StringBuffer();
	                while ((line = reader.readLine()) != null) {
	                	receivedMessage = receivedMessage.append(line);
	                }
	                reader.close();
	                Log.d("LiveMeshEventControllerActivity PollServerForClients", "receivedMessage: " + receivedMessage);
	                JSONObject jsonResponseObject = new JSONObject(receivedMessage.toString());
	                JSONArray clientList = jsonResponseObject.getJSONArray("client_text");
	                publishProgress(new ResponseInfo(response, clientList));
	            } catch (IOException e) {
					//Some IO problem occurred - dump stack and inform caller
	            	Log.d("LiveMeshEventControllerActivity PollServerForClients", "exception posting join event request - response code: " + response);
					e.printStackTrace();
				} catch (JSONException e) {
					//An Error occurred decoding the JSON
					Log.d("LiveMeshEventControllerActivity PollServerForClients", "exception parsing JSON response");
					e.printStackTrace();
				} finally {
		            // Makes sure that the InputStream is closed after the app is
		            // finished using it.
	                if (is != null) {
	                	try {
	                		is.close();
	                	} catch (IOException e) {
	        				//Some IO problem occurred while closing is - just to a stack dump in this case
	                		Log.d("LiveMeshEventControllerActivity PollServerForClients", "exception closing is file");
	        				e.printStackTrace();
	                	}
	                } 
	            }
	            
	            //Sleep until the next poll
	            try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					//Interrupted while sleeping - simply log this
					Log.d("LiveMeshEventControllerActivity PollServerForClients", "interupted while sleeping");
					return null;
				}
        	}
        }
              
        @Override
        protected void onProgressUpdate(ResponseInfo... responseInfo) {
        	//The next text sent from the server will be reported in the respnse Info
        	JSONArray clientList = responseInfo[0].clientList;
        	
            //Check the result and update the event display if the response code was OK
        	if (responseInfo[0].responseCode == 200 | responseInfo[0].responseCode == 201) {
        		//Update the event display - first check for any new devices added
            	MeshDisplayControllerApplictaion appObject = (MeshDisplayControllerApplictaion)LiveMeshEventControllerActivity.this.getApplicationContext();
            	MeshDisplayControllerEngine meshDisplayEngine = appObject.getAppMeshDisplayControllerEngine();
        		try {
        			//First read the JSON response into a new HashMap
        			//For large displays this is not particularly efficient and the server should respond with 
        			//added and deleted clients only, with a full refresh only done periodically. For this proof 
        			//of concept this is fine.
        			HashMap<String, MeshDisplayClient> receivedClientsMap = new HashMap<String, MeshDisplayClient>();
        			for (int i = 0; i< clientList.length(); i++) {
        				MeshDisplayClient receivedClient = new MeshDisplayClient();
        				receivedClient.id = clientList.getJSONObject(i).getString("client_id").toString();
        				receivedClient.textToDisplay = clientList.getJSONObject(i).getString("client_text").toString();
        				receivedClientsMap.put(receivedClient.id, receivedClient);
        			}
        			
        			//Check for anything that is in the Map from the server but not in the engine Map here - this is a 
        			//new client added
        			Set <String> addedClients = new HashSet<String>(receivedClientsMap.keySet());
        			addedClients.removeAll(meshDisplayEngine.clientsMap.keySet());
        			
        			for (String clientIDToAdd : addedClients) {
        				//Add this client from the current engine client Map
        				MeshDisplayClient newClient = receivedClientsMap.get(clientIDToAdd);
        				meshDisplayEngine.clientsMap.put(clientIDToAdd, receivedClientsMap.get(clientIDToAdd));
        				
        				//Add this client to the display
        				displayNewClient(newClient);
        			}
        			
        			//Now check for anything that is in the current engine clientMap but not the received clientMap
        			Set <String> deletedClients = new HashSet<String>(meshDisplayEngine.clientsMap.keySet());
        			deletedClients.removeAll(receivedClientsMap.keySet());
        			
        			for (String clientIDToRemove : deletedClients) {
        				//Remove this client from the current engine client Map
        				meshDisplayEngine.clientsMap.remove(clientIDToRemove);
        				
        				//Remove this client from the display
        				removeClientFromDisplay(clientIDToRemove);
        			}
	        		
        		} catch (JSONException e) {
					//An Error occurred decoding the JSON
					Log.d("LiveMeshEventControllerActivity PollServerForClients", "exception parsing JSON response");
					e.printStackTrace();
        		}

        	} else {
        		//Log an issue
        		Log.d("LiveMeshEventControllerActivity PollServerForClients","-> onProgressUpdate: unexpecetd resposne code: " + responseInfo[0].responseCode);
        	}
        }
       
	}
	
	private class ResponseInfo {
		//Simple class to contain response codes
		public int responseCode;
		public JSONArray clientList;
		
		public ResponseInfo(int resCode, JSONArray cList) {
			this.responseCode = resCode;
			this.clientList = cList;
			
		}
	}







}