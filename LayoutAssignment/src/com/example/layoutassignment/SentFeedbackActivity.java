package com.example.layoutassignment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SentFeedbackActivity extends Activity {
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sent_feedback_layout);
        
        //Get the feedback from the intent
        Intent intent = getIntent();
        String feedbackMessage = intent.getStringExtra(MainActivity.FEEDBACK);
        String feedbackName = intent.getStringExtra(MainActivity.NAME);
        String feedbackEmail = intent.getStringExtra(MainActivity.EMAIL);
        
        //Set the feedback TextView with the fedback message
        TextView feedbackTextView = (TextView) findViewById(R.id.textViewFeedback);
        feedbackTextView.setText(feedbackMessage);
        TextView nameTextView = (TextView) findViewById(R.id.textViewName);
        nameTextView.setText(feedbackName);
        TextView emailTextView = (TextView) findViewById(R.id.textViewEmail);
        emailTextView.setText(feedbackEmail);
        
        final Button linearButton = (Button) findViewById(R.id.SentFeedbackBackButton);
        linearButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	finish();
            }
        });
    }

}
