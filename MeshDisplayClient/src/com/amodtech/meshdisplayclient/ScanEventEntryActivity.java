package com.amodtech.meshdisplayclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ScanEventEntryActivity extends Activity {
	/*
	 * This class is an Activity that allows a user to scan event and client data to join
	 * an event
	 */
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scan_event_entry);
		
        //Set the scan button listener
        final Button scanDetailsButton = (Button) findViewById(R.id.scanEventEntryScanButton);
        scanDetailsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), JoinEventWithScannedDetailsActivity.class);
                startActivity(intent);
            }
        });
	
        //Set the back button listener
        final Button backButton = (Button) findViewById(R.id.scanEventEntryBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	finish();
            }
        });
		
	}

}
