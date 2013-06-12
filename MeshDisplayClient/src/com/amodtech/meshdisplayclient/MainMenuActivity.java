package com.amodtech.meshdisplayclient;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainMenuActivity extends Activity {
	/*
	 * This class is the main menu Activity for the applictaion
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
		
		//Display the Main Menu image
        ImageView mainMenuImageView;
        mainMenuImageView = (ImageView) findViewById(R.id.mainMenuPicture);
        int imageResID = getResources().getIdentifier("main_menu_logo", "drawable",  getPackageName());
        if (imageResID != 0) {
        	mainMenuImageView.setImageResource(imageResID);
        }
        
        //Set the enter manually button listener
        final Button menuManualEnterButton = (Button) findViewById(R.id.enterEventButton);
        menuManualEnterButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), EnterEventManuallyActivity.class);
                startActivity(intent);
            }
        });
        
        //Set the preferences button listener
        final Button menuScanButton = (Button) findViewById(R.id.preferencesButton);
        menuScanButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MeshDisplayPreferencesActivity.class);
                startActivity(intent);
            }
        });
        
        //Set the about button listener
        final Button menuAboutButton = (Button) findViewById(R.id.aboutButton);
        menuAboutButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AboutMeshDisplayActivity.class);
                startActivity(intent);
            }
        });
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

}
