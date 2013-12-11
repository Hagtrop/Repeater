package com.hagtrop.repeater;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class CompareAnswersActivity extends Activity implements OnClickListener{
	TextView resultTV, yourAnswerTV, correctAnswerTV;
	Button correctBtn, incorrectBtn;
	String yourAnswer, correctAnswer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity12_compare_answers);
		
		resultTV = (TextView) findViewById(R.id.a12_resultTV);
		yourAnswerTV = (TextView) findViewById(R.id.a12_yourAnswerTV);
		correctAnswerTV = (TextView) findViewById(R.id.a12_rightAnswerTV);
		correctBtn = (Button) findViewById(R.id.a12_answerCorrectBtn);
		correctBtn.setOnClickListener(this);
		incorrectBtn = (Button) findViewById(R.id.a12_answerIncorrectBtn);
		incorrectBtn.setOnClickListener(this);
		Intent iFromTest = getIntent();
		yourAnswer = iFromTest.getStringExtra("your answer");
		correctAnswer = iFromTest.getStringExtra("correct answer");
		yourAnswerTV.setText(yourAnswer);
		correctAnswerTV.setText(correctAnswer);
		
		if(yourAnswer.equals(correctAnswer)){
			resultTV.setText("Вы ответили правильно!");
			resultTV.setTextColor(Color.parseColor("#00bb23"));
			correctBtn.setVisibility(View.GONE);
			incorrectBtn.setVisibility(View.GONE);
			Intent iBackToTest = new Intent();
			iBackToTest.putExtra("correct", true);
			setResult(RESULT_OK, iBackToTest);
		}
		else{
			resultTV.setText("Вы ответили неправильно!");
			resultTV.setTextColor(Color.RED);
			correctBtn.setVisibility(View.VISIBLE);
			incorrectBtn.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent iBackToTest = new Intent();
		switch(v.getId()){
		case R.id.a12_answerCorrectBtn:
			iBackToTest.putExtra("correct", true);
			break;
		case R.id.a12_answerIncorrectBtn:
			iBackToTest.putExtra("correct", false);
			break;
		default: break;
		}
		setResult(RESULT_OK, iBackToTest);
		finish();
	}

}
