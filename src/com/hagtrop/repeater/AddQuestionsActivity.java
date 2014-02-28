package com.hagtrop.repeater;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
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
		switch(v.getId()){
		case R.id.a5_saveBtn:
			String title, question, answer;
			title = titleET.getText().toString().trim();
			question = questionET.getText().toString().trim();
			answer = answerET.getText().toString().trim();
			Toast toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
			//Проверяем что все поля формы были заполнены, иначе выводим сообщение
			if(TextUtils.isEmpty(title)){
				toast.setText(R.string.a5_headerMissedMsg);
			}
			else if(TextUtils.isEmpty(question)){
				toast.setText(R.string.a5_questionMissedMsg);
			}
			else if(TextUtils.isEmpty(answer)){
				toast.setText(R.string.a5_answerMissedMsg);
			}
			else{
				//Добавляем вопрос в базу данных и выводим сообщение
				ContentValues cv = new ContentValues();
				cv.put("title", title);
				cv.put("question", question);
				cv.put("answer", answer);
				cv.put("group_id", groupId);
				getContentResolver().insert(DataBaseContentProvider.QUESTIONS_URI, cv);
				toast.setText(R.string.a5_questionSavedMsg);
				titleET.getText().clear();
				questionET.getText().clear();
				answerET.getText().clear();
			}
			toast.show();
			break;
		default: break;
		}
	}
}
