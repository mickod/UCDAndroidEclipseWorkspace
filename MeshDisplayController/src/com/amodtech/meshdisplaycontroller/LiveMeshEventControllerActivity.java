package com.amodtech.meshdisplaycontroller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import com.amodtech.meshdisplaycontroller.R.id;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LiveMeshEventControllerActivity extends Activity implements View.OnTouchListener  {
	/*
	 * This class represents the activity that controls a live event
	 */
	
	private PollServerForClients serverPollTask;
	private SetTextForDeviceTask setTextTask;
	private ViewGroup eventDisplayArea;
	private HashMap<String, View> clientDisplayMap = new HashMap<String, View>();
	private int _xDelta;
	private int _yDelta;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_live_mesh_event_controller);
		
        //Hide the soft keyboard
        getWindow().setSoftInputMode(
        	      WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	    
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
		//is a modified version of an approach at: http://stackoverflow.com/questions/9398057/
		//android-move-a-view-on-touch-move-action-move
		
		int viewWidth = view.getLayoutParams().width;
		int viewHeight = view.getLayoutParams().height;
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
	            if ((layoutParams.leftMargin > X - _xDelta)) {
	            	//Moving left - check for edge
	            	int newLeftMargin = X - _xDelta;
	            	if (newLeftMargin <1) {
	            		newLeftMargin = 1;
	            	}
	            	layoutParams.leftMargin = newLeftMargin;
            		//Toast.makeText(getApplicationContext(), "layoutParams.leftMargin: " + Integer.toString(layoutParams.leftMargin) + "parentLeftMargin: " + Integer.toString(parentLeftMargin), Toast.LENGTH_SHORT).show();
	            }
	            if ((layoutParams.leftMargin < X - _xDelta)) {
	            	//Moving right - check for edge
	            	View parentView= (View)view.getParent();
	            	int parentWidth = parentView.getWidth();
	            	int newLongLeftMargin = X - _xDelta;
	            	if ( newLongLeftMargin > (parentWidth - viewWidth) ) {
	            		//we are at the right edge
	            		newLongLeftMargin = parentWidth - viewWidth;
	            	}
	            	layoutParams.leftMargin = newLongLeftMargin;
	            }
	            if ((layoutParams.topMargin > Y - _yDelta)) {
	            	//Moving up - check for top
	            	int newTopMargin = Y - _yDelta;
		            if (newTopMargin < 1 ) {
		            	//We are at the top
		            	newTopMargin = 1;
		            }
		            layoutParams.topMargin = newTopMargin;
	            }
	            if ((layoutParams.topMargin < Y - _yDelta)) {
	            	//Moving down - check for bottom
	            	View parentView= (View)view.getParent();
	            	int parentHeight = parentView.getHeight();
	            	int newTallTopMargin = Y - _yDelta;
	            	if ( newTallTopMargin > ( parentHeight - viewHeight) ) {
	            		//we are at the bottom
	            		newTallTopMargin = parentHeight - viewHeight;
	            	}
	            	layoutParams.topMargin = newTallTopMargin;
	            }
	            
	            layoutParams.rightMargin = -250;
	            layoutParams.bottomMargin = -250;
	            view.setLayoutParams(layoutParams);
	            break;
	    }
	    eventDisplayArea.invalidate();
	    return true;
	}
	
	private void displayNewClient(final MeshDisplayClient clientToDisplay) {
		//This method add a new client to the event Display
		
		//Check if this is the controller and if so do not display it
    	MeshDisplayControllerApplictaion appObject = (MeshDisplayControllerApplictaion)LiveMeshEventControllerActivity.this.getApplicationContext();
    	MeshDisplayControllerEngine meshDisplayEngine = appObject.getAppMeshDisplayControllerEngine();
		if(clientToDisplay.id.equalsIgnoreCase(meshDisplayEngine.getReservedControllerName()) ) {
			return;
		}
		eventDisplayArea = (ViewGroup)findViewById(R.id.mainEventDisplayArea);
		LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View clientPhoneView = inflater.inflate(R.layout.client_display_layout, null);
		
		//Create offset for display to stop it landing on top of the last one
		int offsetMultiplier = clientDisplayMap.size();
		int viewWidth = 200;

	    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(150, 220);
	    layoutParams.leftMargin = 50 +(viewWidth*(1+offsetMultiplier));
	    layoutParams.topMargin = 50;
	    layoutParams.bottomMargin = -250;
	    layoutParams.rightMargin = -250;
	    clientPhoneView.setLayoutParams(layoutParams);
	    
	    //Set the client id display
	    TextView clientIdTextView = (TextView)clientPhoneView.findViewById(id.clientIDTextView);
	    clientIdTextView.setText(clientToDisplay.id);
	    
	    //Set the text to display in the edit text and set the edit text listener
	    EditText displayEditText = (EditText)clientPhoneView.findViewById(id.clientDisplayEditText);
	    displayEditText.setText(clientToDisplay.textToDisplay);
	    displayEditText.addTextChangedListener(new TextWatcher(){
	        public void afterTextChanged(Editable s) {
	        	//New text entered - create a new task to send the text to the server
	        	Log.d("LiveMeshEventControllerActivity displayEditText.addTextChangedListener", "Text changed: " + s.toString());
	        	String newText = s.toString();
	        	if (TextUtils.isEmpty(newText)) {
	        		newText = "%20";
	        	}
	        	Log.d("LiveMeshEventControllerActivity displayEditText.addTextChangedListener", "newText: " + s.toString());
	        	setTextTask = new SetTextForDeviceTask();
	        	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
	        		setTextTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, clientToDisplay.id, newText);
	        	} else {
	        		setTextTask.execute(clientToDisplay.id, newText);
	        	}

	        }
	        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
	        public void onTextChanged(CharSequence s, int start, int before, int count){}
	    });

	    clientPhoneView.setOnTouchListener(this);
	    eventDisplayArea.addView(clientPhoneView);
	    clientDisplayMap.put(clientToDisplay.id, clientPhoneView);
	}
	
	private void removeClientFromDisplay(String clientIDToRemove) {
		//This method removes a client from the event Display
		eventDisplayArea = (ViewGroup)findViewById(R.id.mainEventDisplayArea);

		View clientTextViewToRemove = clientDisplayMap.get(clientIDToRemove);
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
		
		//Clear the known client list - it will be re-created when the activity starts polling the server again
		//This is better than simply remembering them as we don't know how long the activity will have been
		//paused for
    	MeshDisplayControllerApplictaion appObject = (MeshDisplayControllerApplictaion)LiveMeshEventControllerActivity.this.getApplicationContext();
    	MeshDisplayControllerEngine meshDisplayEngine = appObject.getAppMeshDisplayControllerEngine();
    	meshDisplayEngine.clientsMap.clear();
    	
    	//Clear the display map
    	this.clientDisplayMap.clear();

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
	                JSONArray clientList = new JSONArray(receivedMessage.toString());
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
        			JSONArray clientList = responseInfo[0].clientList;
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


	private class SetTextForDeviceTask extends AsyncTask<String, Void, Integer> {
		/*
		 * Private AsynchTask to message server with new text for a client
		 */
        @Override
        protected Integer doInBackground(String... args) {
        	Log.d("LiveMeshEventControllerActivity SetTextForDeviceTask", "");
           	//Get the meshDisplayEngine object and set the event ID
        	MeshDisplayControllerApplictaion appObject = (MeshDisplayControllerApplictaion)LiveMeshEventControllerActivity.this.getApplicationContext();
        	MeshDisplayControllerEngine meshDisplayEngine = appObject.getAppMeshDisplayControllerEngine();
              
            //Send the POST request to the HTTP server and check that an OK is received
        	InputStream is = null;
        	int response = 0;
            StringBuffer receivedMessage = new StringBuffer();
            try {
                URL url = new URL(meshDisplayEngine.getServerBaseURL() + "/event_client_text");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                
                //Add the POST parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("event_id", meshDisplayEngine.eventID));
                params.add(new BasicNameValuePair("client_id", args[0])); 
                params.add(new BasicNameValuePair("text", args[1]));
                Log.d("LiveMeshEventControllerActivity getQuery", "args[0]: " + args[0] + "  args[1]: " + args[1]);
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"), 8192);
                String paramString = URLEncodedUtils.format(params, "utf-8");
                Log.d("LiveMeshEventControllerActivity getQuery", "paramString: " + paramString);
                writer.write(paramString);
                writer.close();
                os.close();
                
                // Starts the query
                conn.connect();
                
                //For this case just check the response code - we are not interested in the response itself
                response = conn.getResponseCode();
                is = conn.getInputStream();
                is = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is), 8192);
                String line = "";
                while ((line = reader.readLine()) != null) {
                	receivedMessage = receivedMessage.append(line);
                }
                reader.close();
                
                return response;
                
            } catch (IOException e) {
				//Some IO problem occurred - dump stack and inform caller
            	Log.d("LiveMeshEventControllerActivity SetTextForDeviceTask", "exception posting join event request - response code: " 
            						+ response + " Response message: " + receivedMessage);
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
                		Log.d("LiveMeshEventControllerActivity SetTextForDeviceTask", "exception closing is file");
        				e.printStackTrace();
                	}
                } 
            }
        }
        
        @Override
        protected void onPostExecute(Integer responseCode) {
        	// onPostExecute displays the results of the AsyncTask.
        	
            //Check the result and move to new Activity if appropriate
        	if (responseCode != 200 & responseCode != 210) {
        		//Inform the user there was a problem
        		Toast.makeText(getApplicationContext(), R.string.connection_problem, Toast.LENGTH_SHORT).show();
        	}
       }
	}
}