package com.hagtrop.repeater;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class DisplayAnswerActivity extends Activity {
	TextView answerTV;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity13_display_answer);
		
		answerTV = (TextView) findViewById(R.id.a13_answerTV);
		answerTV.setText(getIntent().getStringExtra("answer"));
	}
}
