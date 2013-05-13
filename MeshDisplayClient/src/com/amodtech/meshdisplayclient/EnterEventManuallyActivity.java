package com.amodtech.meshdisplayclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class EnterEventManuallyActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_enter_event_manually);
        
        //Set the join event button listener
        final Button joinButton = (Button) findViewById(R.id.entermanuallyJoinButton);
        joinButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), LiveMeshEventDisplayActivity.class);
                startActivity(intent);
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

}
