package com.example.layoutassignment;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
	
	public final static String FEEDBACK = "ie.ucd.12259095.FEEDBACK";
	public final static String NAME = "ie.ucd.12259095.NAME";
	public final static String EMAIL = "ie.ucd.12259095.EMAIL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        final Button linearButton = (Button) findViewById(R.id.button1_Linear);
        linearButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), LinearFeedbackActivity.class);
                startActivity(intent);
            }
        });
        
        final Button relativeButton = (Button) findViewById(R.id.button2_relative);
        relativeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), RelativeFeedbackActivity.class);
                startActivity(intent);
            }
        });
        
        final Button tableButton = (Button) findViewById(R.id.button3_table);
        tableButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), TableFeedbackActivity.class);
                startActivity(intent);
            }
        });
        
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
