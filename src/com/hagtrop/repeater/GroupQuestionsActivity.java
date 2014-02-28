package com.hagtrop.repeater;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class GroupQuestionsActivity extends Activity implements LoaderCallbacks<Cursor>, OnClickListener, OnItemClickListener{
	
	TextView noQuestionsTV;
	ListView questionsLV;
	Button createBtn;
	CursorAdapter adapter;
	long groupId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity4_group_questions);
		
		groupId = getIntent().getExtras().getLong("id");
		noQuestionsTV = (TextView) findViewById(R.id.a4_noQuestionsFoundTV);
		createBtn = (Button) findViewById(R.id.a4_createBtn);
		createBtn.setOnClickListener(this);
		questionsLV = (ListView) findViewById(R.id.a4_questionsLV);
		//Добавляем контекстное меню к списку вопросов
		registerForContextMenu(questionsLV);
		questionsLV.setOnItemClickListener(this);
		
		//Асинхронно подгружаем в ListView список вопросов
		getLoaderManager().initLoader(1, null, this);
		int layout = android.R.layout.simple_list_item_1;
		String[] from = {"title"};
		int[] to = {android.R.id.text1};
		int flags = Adapter.NO_SELECTION;
		adapter = new SimpleCursorAdapter(this, layout, null, from, to, flags);
		questionsLV.setAdapter(adapter);
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()){
		case R.id.a4_createBtn:
			//Переходим к добавлению вопросов
			Intent iToQuestionsCreate = new Intent(this, AddQuestionsActivity.class);
			iToQuestionsCreate.putExtra("id", groupId);
			startActivity(iToQuestionsCreate);
			break;
		default: break;
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(this, DataBaseContentProvider.QUESTIONS_URI, new String[]{"_id","title","group_id"}, "group_id="+groupId, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
		adapter.swapCursor(cursor);
		//Если группа пуста, выводим подсказку в TextView
		if(adapter.getCount() == 0){
			noQuestionsTV.setVisibility(View.VISIBLE);
		}
		else{
			noQuestionsTV.setVisibility(View.GONE);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		adapter.swapCursor(null);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		if(v.getId() == R.id.a4_questionsLV){
			//Создаём контекстное меню на основе XML-файла
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.question_context_menu, menu);
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch(item.getItemId()){
		case R.id.delete_question:
			//Удаляем вопрос из БД
			getContentResolver().delete(DataBaseContentProvider.QUESTIONS_URI, "_id="+info.id, null);
			break;
		default: break;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		//Открываем вопрос на редактирование
		Intent iToQuestionDetails = new Intent(this, QuestionActivity.class);
		iToQuestionDetails.putExtra("id", id);
		startActivity(iToQuestionDetails);
	}
}
