package com.amodtech.meshdisplaycontroller;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class EntryScreenAvtivity extends Activity {

	/*
	 * This class is the main menu Activity for the application
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_entry_screen);
		
		//Display the Main Menu image
        ImageView mainMenuImageView;
        mainMenuImageView = (ImageView) findViewById(R.id.mainMenuPicture);
        int imageResID = getResources().getIdentifier("controller_menu_image", "drawable",  getPackageName());
        if (imageResID != 0) {
        	mainMenuImageView.setImageResource(imageResID);
        }
        
        //Set the enter manually button listener
        final Button menuManualEnterButton = (Button) findViewById(R.id.enterEventButton);
        menuManualEnterButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });
        
        //Set the about button listener
        final Button menuAboutButton = (Button) findViewById(R.id.aboutButton);
        menuAboutButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AboutMeshControllerActivity.class);
                startActivity(intent);
            }
        });	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.entry_screen_avtivity, menu);
		return true;
	}

}
