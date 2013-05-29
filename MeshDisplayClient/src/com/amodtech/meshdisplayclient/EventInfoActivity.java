package com.amodtech.meshdisplayclient;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class EventInfoActivity extends Activity {
	/*
	 * This class displays the current event info including the
	 * current location information. 
	 */
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_info_display);
		
		//Set the event info from the MeshEngine
    	MeshDisplayApplictaion appObject = (MeshDisplayApplictaion)EventInfoActivity.this.getApplicationContext();
    	MeshDisplayClientEngine meshDisplayEngine = appObject.getAppMeshDisplayEngine();
    	TextView eventIDTextView = (TextView) findViewById(R.id.eventID);
    	eventIDTextView.setText(meshDisplayEngine.eventID);
    	TextView clientIDTextView = (TextView) findViewById(R.id.clientID);
    	clientIDTextView.setText(meshDisplayEngine.clintID);
    	TextView latitudeTextView = (TextView) findViewById(R.id.latitude);
    	latitudeTextView.setText(Integer.toString(meshDisplayEngine.deviceLatitude));
    	TextView longitudeTextView = (TextView) findViewById(R.id.longitude);
    	longitudeTextView.setText(Integer.toString(meshDisplayEngine.deviceLongitude));
	
        //Set the back button listener
        final Button backButton = (Button) findViewById(R.id.eventInfoBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	finish();
            }
        });
		
	}

}
