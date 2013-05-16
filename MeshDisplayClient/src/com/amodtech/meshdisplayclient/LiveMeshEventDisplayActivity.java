package com.amodtech.meshdisplayclient;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LiveMeshEventDisplayActivity extends Activity {
	/*
	 * This class is is an Activity that implements the client display when part of a 
	 * live event.
	 */
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_live_mesh_event_display);
	
        //Set the back button listener
        final Button backButton = (Button) findViewById(R.id.liveMeshDisplayBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	finish();
            }
        });
		
	}

}
