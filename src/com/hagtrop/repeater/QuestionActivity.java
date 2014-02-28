package com.hagtrop.repeater;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.TextView;

public class QuestionActivity extends Activity implements LoaderCallbacks<Cursor>, OnLongClickListener{
	TextView titleTV, questionTV, answerTV;
	long questionId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity6_question);
		
		titleTV = (TextView) findViewById(R.id.a6_titleTV);
		titleTV.setOnLongClickListener(this);
		questionTV = (TextView) findViewById(R.id.a6_questionBodyTV);
		questionTV.setOnLongClickListener(this);
		answerTV = (TextView) findViewById(R.id.a6_answerBodyTV);
		answerTV.setOnLongClickListener(this);
		questionId = getIntent().getExtras().getLong("id");
		//«агружаем данные вопроса, использу€ Loader
		//ѕри редактировании пол€ будут обновл€тьс€ автоматически
		getLoaderManager().initLoader(3, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String[] columns = {"_id", "title", "question", "answer"};
		String selection = "_id=" + questionId;
		return new CursorLoader(this, DataBaseContentProvider.QUESTIONS_URI, columns, selection, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		int titleI, questionI, answerI;
		titleI = cursor.getColumnIndex("title");
		questionI = cursor.getColumnIndex("question");
		answerI = cursor.getColumnIndex("answer");
		cursor.moveToFirst();
		titleTV.setText(cursor.getString(titleI));
		questionTV.setText(cursor.getString(questionI));
		answerTV.setText(cursor.getString(answerI));
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {}

	@Override
	public boolean onLongClick(View v) {
		String field, text;
		field = text = "";
		switch(v.getId()){
		case R.id.a6_titleTV:
			field = "title";
			text = titleTV.getText().toString();
			break;
		case R.id.a6_questionBodyTV:
			field = "question";
			text = questionTV.getText().toString();
			break;
		case R.id.a6_answerBodyTV:
			field = "answer";
			text = answerTV.getText().toString();
			break;
		default: return false;
		}
		//ѕереходим к редактированию выбранного пол€
		Intent iToTextEditor = new Intent(this, TextEditorActivity.class);
		iToTextEditor.putExtra("id", questionId);
		iToTextEditor.putExtra("field", field);
		iToTextEditor.putExtra("text", text);
		startActivity(iToTextEditor);
		return true;
	}
}
