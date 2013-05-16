package com.amodtech.meshdisplayclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EnterEventManuallyActivity extends Activity {
	/*
	 * This class is the Activity which enables a user to enter data about the event
	 * and the client id and then to join an event.
	 */
	
	private MyHandler myhandler = new MyHandler();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_enter_event_manually);
        
        //Set the join event button listener
        final Button joinButton = (Button) findViewById(R.id.entermanuallyJoinButton);
        joinButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	//Get the meshDisplayEngine object
            	MeshDisplayApplictaion appObject = (MeshDisplayApplictaion)EnterEventManuallyActivity.this.getApplicationContext();
            	MeshDisplayClientEngine meshDisplayEngine = appObject.getAppMeshDisplayEngine();
            	
            	//Get the entered eventID and clientID
            	EditText eventEditText = (EditText) findViewById(R.id.eventIdEditText);
            	String eneteredEventID = eventEditText.getText().toString();
            	EditText clientEditText = (EditText) findViewById(R.id.clientIdEditText);
            	String eneteredClientID = clientEditText.getText().toString();
            	
            	//Join the event, passing in the handler for this activity so we can be
            	//notified when we are in the event
            	meshDisplayEngine.joinEvent(eneteredEventID, eneteredClientID, myhandler);
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
	
	static private class MyHandler extends Handler {
		/*
		 * This private class is a Handler and handles messages from the MeshDisplay Engine
		 */
		
		public void handleMessage(Message msg) {
			//Do something
			Log.d("In my handler!!!","xxx");
		}
	}
	


}
