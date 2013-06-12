package com.amodtech.meshdisplayclient;

import android.os.Bundle;
import android.preference.PreferenceActivity;


public class MeshDisplayPreferencesActivity extends PreferenceActivity {
	/*
	 * This class allows the user enter preferences
	 */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        addPreferencesFromResource(R.xml.mesh_display_preferences);
    }
	
}
