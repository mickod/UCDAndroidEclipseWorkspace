package ie.ucd.onetwotwofive.practical4;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import ie.ucd.onetwotwofive.practical4.R;

public class CourseDetailsActivity extends Activity {
	
	private MediaPlayer mPlayer;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Set the content view
		setContentView(R.layout.course_details_layout);
		
        //Get the information from the intent
        Intent intent = getIntent();
        String courseNameMesssage = intent.getStringExtra(CourseListActivity.COURSE_NAME);
        int courseCredits = intent.getIntExtra(CourseListActivity.COURSE_CREDITS, 0);
        String courseDesc = intent.getStringExtra(CourseListActivity.COURSE_DESC);
        boolean playSound = intent.getBooleanExtra(CourseListActivity.PLAY_MEDIA, false);
        
        //Display the information
        TextView courseName = (TextView) findViewById(R.id.courseName);
        courseName.setText(courseNameMesssage);
        TextView courseCreditsView = (TextView) findViewById(R.id.courseCredits);
        courseCreditsView.setText(Integer.toString(courseCredits));
        TextView courseDescView = (TextView) findViewById(R.id.courseDesc);
        courseDescView.setText(courseDesc);
        
        //Set the background colour according to the preferences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String preferedBackColour = sharedPref.getString("colourListPref", "1");
        View parentView = (View) findViewById(R.id.courseDeatilsLayout);
        parentView.setBackgroundColor(Integer.parseInt(preferedBackColour));
        
        //Play the sound if requested
        if (playSound) {
	        mPlayer = MediaPlayer.create(this, R.raw.vuvuzela_horn );
	        mPlayer.start();
	        mPlayer.setLooping(true);
        }
		
        //Set the back button listener
        final Button linearButton = (Button) findViewById(R.id.backButton);
        linearButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	finish();
            }
        });
	}
	
	public void onDestroy() {
		//Stop the Media player if it exits
		if (mPlayer != null) {
			mPlayer.stop();
		}
		super.onDestroy();

	}
}
