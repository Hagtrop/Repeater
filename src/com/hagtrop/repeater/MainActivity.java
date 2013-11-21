package com.hagtrop.repeater;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.view.View.OnClickListener;

public class MainActivity extends Activity implements OnClickListener{
	Button newTestBtn, continueTestBtn, questionsBtn;
	BaseHelper baseHelper;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity1_main);
		
		newTestBtn = (Button) findViewById(R.id.a1_newTestBtn);
		newTestBtn.setOnClickListener(this);
		continueTestBtn = (Button) findViewById(R.id.a1_continueTestBtn);
		continueTestBtn.setOnClickListener(this);
		questionsBtn = (Button) findViewById(R.id.a1_questionsBtn);
		questionsBtn.setOnClickListener(this);
		baseHelper = new BaseHelper(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.a1_newTestBtn:
			int qcount = baseHelper.getQuestionsCount();
			if(qcount == 0){
				Toast.makeText(this, "В базе данных нет вопросов", Toast.LENGTH_LONG).show();
			}
			else{
				startActivity(new Intent(this, NewTestActivity.class));
			}
			break;
		case R.id.a1_continueTestBtn:
			startActivity(new Intent(this, SelectTestActivity.class));
			break;
		case R.id.a1_questionsBtn:
			startActivity(new Intent(this, GroupsActivity.class));
			break;
		default: break;
		}
		baseHelper.close();
	}

}
