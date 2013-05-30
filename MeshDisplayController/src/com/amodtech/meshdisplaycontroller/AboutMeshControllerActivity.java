package com.amodtech.meshdisplaycontroller;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class AboutMeshControllerActivity extends Activity {
		/*
		 * This class is provides user help for the application
		 */
		
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_about_mesh_controller);
			
			//Display the About image
	        ImageView mainMenuImageView;
	        mainMenuImageView = (ImageView) findViewById(R.id.aboutMeshControllerImage);
	        int imageResID = getResources().getIdentifier("controller_menu_image", "drawable",  getPackageName());
	        if (imageResID != 0) {
	        	mainMenuImageView.setImageResource(imageResID);
	        }
		
	        //Set the back button listener
	        final Button backButton = (Button) findViewById(R.id.aboutMeshDisplayBackButton);
	        backButton.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	finish();
	            }
	        });
			
		}

	}
