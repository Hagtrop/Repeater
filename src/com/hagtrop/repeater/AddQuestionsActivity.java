package com.hagtrop.repeater;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddQuestionsActivity extends Activity implements OnClickListener{
	EditText titleET, questionET, answerET;
	Button saveBtn;
	long groupId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity5_add_questions);
		
		groupId = getIntent().getExtras().getLong("id");
		titleET = (EditText) findViewById(R.id.a5_titleET);
		questionET = (EditText) findViewById(R.id.a5_questionET);
		answerET = (EditText) findViewById(R.id.a5_answerET);
		saveBtn = (Button) findViewById(R.id.a5_saveBtn);
		saveBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.a5_saveBtn:
			String title, question, answer;
			title = titleET.getText().toString().trim();
			question = questionET.getText().toString().trim();
			answer = answerET.getText().toString().trim();
			if(TextUtils.isEmpty(title)){
				Toast.makeText(this, "Укажите заголовок", Toast.LENGTH_SHORT).show();
			}
			else if(TextUtils.isEmpty(question)){
				Toast.makeText(this, "Введите вопрос", Toast.LENGTH_SHORT).show();
			}
			else if(TextUtils.isEmpty(answer)){
				Toast.makeText(this, "Введите ответ", Toast.LENGTH_SHORT).show();
			}
			else{
				ContentValues cv = new ContentValues();
				cv.put("title", title);
				cv.put("question", question);
				cv.put("answer", answer);
				cv.put("group_id", groupId);
				getContentResolver().insert(DataBaseContentProvider.QUESTIONS_URI, cv);
				Toast.makeText(this, "Вопрос сохранён", Toast.LENGTH_SHORT).show();
				titleET.getText().clear();
				questionET.getText().clear();
				answerET.getText().clear();
			}
			break;
		default: break;
		}
	}
}
