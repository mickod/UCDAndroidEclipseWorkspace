package ie.ucd.onetwotwofive.practical3;

import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import ie.ucd.onetwotwofive.practical3.R;

public class FruitDetailsActivity extends Activity implements OnInitListener {
	
	private MediaPlayer mPlayer;
	private TextToSpeech textToSpeechEngine;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Create the text to speech instance
		textToSpeechEngine = new TextToSpeech(this, this);
		
		//Set the content view
		setContentView(R.layout.fruit_details_layout);
		
        //Get the name from the intent
        Intent intent = getIntent();
        final String fruitName = intent.getStringExtra(FruitListActivity.FRUIT_NAME);
        int localizedFruitName = getResources().getIdentifier(fruitName, "string",  getPackageName());
        
        //Display the information
        TextView fruitNameTextView = (TextView) findViewById(R.id.fruitName);
        fruitNameTextView.setText(localizedFruitName);
        ImageView fruitImageView;
        fruitImageView = (ImageView) findViewById(R.id.fruitPicture);
        int fruitResID = getResources().getIdentifier(fruitName, "drawable",  getPackageName());
        if (fruitResID != 0) {
        	fruitImageView.setImageResource(fruitResID);
        }
		
        //Set the back button listener
        final Button linearButton = (Button) findViewById(R.id.backButton);
        linearButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	finish();
            }
        });
        
        //Set the Speech button listener if there is a speech button
        final Button speechButton = (Button) findViewById(R.id.speechButton);
        if (speechButton != null) {
	        speechButton.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	textToSpeechEngine.speak(fruitName, TextToSpeech.QUEUE_FLUSH, null);
	            }
	        });
        }
	}
	
	public void onDestroy() {
		//Stop the Media player if it exits
		if (mPlayer != null) {
			mPlayer.stop();
		}
		super.onDestroy();

	}
	
	public void onInit(int initStatus) {
	    if (initStatus == TextToSpeech.SUCCESS) {
	    	textToSpeechEngine.setLanguage(Locale.US);
	    } else if (initStatus == TextToSpeech.ERROR) {
	    	//Hide the speech button if the text to speech engine does not initialise
	        Toast.makeText(this, "Sorry! Text To Speech failed to initialise", Toast.LENGTH_LONG).show();
	        Button speechButt = (Button) findViewById(R.id.speechButton);
	        if (speechButt != null) {
	        	speechButt.setVisibility(View.GONE);
	        }
	    }
	}

}
