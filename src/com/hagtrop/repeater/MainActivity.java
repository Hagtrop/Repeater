package com.hagtrop.repeater;

import android.os.Bundle;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;

public class MainActivity extends Activity implements OnClickListener{
	Button newTestBtn, continueTestBtn, questionsBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity1_main);
		
		//Назначаем обработчики кнопкам формы
		newTestBtn = (Button) findViewById(R.id.a1_newTestBtn);
		newTestBtn.setOnClickListener(this);
		continueTestBtn = (Button) findViewById(R.id.a1_continueTestBtn);
		continueTestBtn.setOnClickListener(this);
		questionsBtn = (Button) findViewById(R.id.a1_questionsBtn);
		questionsBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.a1_newTestBtn:
			if(getQuestionsCount() == 0){
				//Если в базе нет вопросов, выводим сообщение
				(new NoQuestionsDialog()).show(getFragmentManager(), "NoQuestionsDialog");
			}
			else{
				//Переходим к созданию нового теста
				startActivity(new Intent(this, NewTestActivity.class));
			}
			break;
		case R.id.a1_continueTestBtn:
			//Переходим к списку сохранённых тестов
			startActivity(new Intent(this, SelectTestActivity.class));
			break;
		case R.id.a1_questionsBtn:
			//Переходим к наполнению базы данных
			startActivity(new Intent(this, GroupsActivity.class));
			break;
		default: break;
		}
	}
	
	int getQuestionsCount(){
		//Метод возвращает общее количество вопросов в базе
		ContentResolver resolver = getContentResolver();
		Cursor cursor = resolver.query(DataBaseContentProvider.QUESTIONS_URI, new String[]{"COUNT(*)"}, null, null, null);
		cursor.moveToFirst();
		int qCount = cursor.getInt(0);
		cursor.close();
		return qCount;
	}
}
