package ie.ucd.onetwotwofive.practical4;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class CoursesPreferencesActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        addPreferencesFromResource(R.xml.practical4_preferences);
    }
	
}
