package com.amodtech.meshdisplayclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class JoinEventWithScannedDetailsActivity extends Activity {
	/*
	 * This class is an Activity that allows a user to enter an event with scanned details.
	 * **** This is not in use in this version of the applictaion ****
	 */
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_join_with_scanned_details);
        
        //Set the join event button listener
        final Button joinButton = (Button) findViewById(R.id.scannedJoinButton);
        joinButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), LiveMeshEventDisplayActivity.class);
                startActivity(intent);
            }
        });
        
        //Set the back button listener
        final Button backButton = (Button) findViewById(R.id.scannedBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	finish();
            }
        });
		
	}

}
