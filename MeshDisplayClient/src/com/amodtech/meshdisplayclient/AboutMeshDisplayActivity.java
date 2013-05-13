package com.amodtech.meshdisplayclient;

import com.amodtech.meshdisplayclient.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class AboutMeshDisplayActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_mesh_display);
	
        //Set the back button listener
        final Button backButton = (Button) findViewById(R.id.aboutMeshDisplayBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	finish();
            }
        });
		
	}

}
