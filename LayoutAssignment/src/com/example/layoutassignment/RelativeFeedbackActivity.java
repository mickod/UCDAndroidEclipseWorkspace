package com.example.layoutassignment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RelativeFeedbackActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rrelative_layout);
        
        final Button relativeButton = (Button) findViewById(R.id.RelativeBackButton);
        relativeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        
        final Button clearButton = (Button) findViewById(R.id.RelativeClearButton);
        clearButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText nameEditText = (EditText) findViewById(R.id.editText1);
                nameEditText.setText("");
                EditText emailEditText = (EditText) findViewById(R.id.editText2);
                emailEditText.setText("");
                EditText feedbackEditText = (EditText) findViewById(R.id.editText3);
                feedbackEditText.setText("");
            }
        });
        
        final Button sendButton = (Button) findViewById(R.id.RelativeSendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), SentFeedbackActivity.class);
                EditText feedbackText = (EditText) findViewById(R.id.editText3);
                String feedbackMessage = feedbackText.getText().toString();
                intent.putExtra(MainActivity.FEEDBACK, feedbackMessage);
                EditText nameText = (EditText) findViewById(R.id.editText2);
                String nameMessage = nameText.getText().toString();
                intent.putExtra(MainActivity.NAME, nameMessage);
                EditText emailText = (EditText) findViewById(R.id.editText1);
                String emailMessage = emailText.getText().toString();
                intent.putExtra(MainActivity.EMAIL, emailMessage);
                startActivity(intent);
            }
        });
   
    }
    
    
    
	
}
