package com.hagtrop.repeater;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;

public class GroupsActivity extends Activity implements LoaderCallbacks<Cursor>, OnClickListener, OnItemClickListener{
	Button createBtn;
	TextView noGroupsTV;
	ListView groupsLV;
	CursorAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity2_groups);
		
		//Получаем доступ к элементам формы
		noGroupsTV = (TextView) findViewById(R.id.a2_noGroupsFoundTV);
		createBtn = (Button) findViewById(R.id.a2_createGroupBtn);
		createBtn.setOnClickListener(this);
		groupsLV = (ListView) findViewById(R.id.a2_groupsLV);
		//Добавляем контекстное меню к списку групп
		registerForContextMenu(groupsLV);
		groupsLV.setOnItemClickListener(this);
		
		//Асинхронно подгружаем в ListView названия групп вопросов
		getLoaderManager().initLoader(1, null, this);
		int layout = android.R.layout.simple_list_item_1;
		String[] from = {"title"};
		int[] to = {android.R.id.text1};
		int flags = Adapter.NO_SELECTION;
		adapter = new SimpleCursorAdapter(this, layout, null, from, to, flags);
		groupsLV.setAdapter(adapter);
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.a2_createGroupBtn:
			//Переходим к созданию новой группы
			Intent iToGroupCreate = new Intent(this, AddGroupsActivity.class);
			startActivity(iToGroupCreate);
			break;
		default: break;
		}
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(this, DataBaseContentProvider.GROUPS_URI, new String[]{"_id","title"}, null, null, null);
	}
	
	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
		adapter.swapCursor(cursor);
		//Если в базе нет групп с вопросами, выводим подсказку в TextView
		if(adapter.getCount() == 0){
			noGroupsTV.setVisibility(View.VISIBLE);
		}
		else{
			noGroupsTV.setVisibility(View.GONE);
		}
	}
	
	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		adapter.swapCursor(null);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		if(v.getId() == R.id.a2_groupsLV){
			//Создаём контекстное меню на основе XML-файла
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.group_context_menu, menu);
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch(item.getItemId()){
		case R.id.delete_group:
			//Удаляем группу и все связанные с ней вопросы
			ContentResolver contentResolver = getContentResolver();
			contentResolver.delete(DataBaseContentProvider.GROUPS_URI, "_id="+info.id, null);
			contentResolver.delete(DataBaseContentProvider.QUESTIONS_URI, "group_id="+info.id, null);
			break;
		}
		return super.onContextItemSelected(item);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		//Переходим к списку вопросов группы
		Intent iToQuestionsList = new Intent(this, GroupQuestionsActivity.class);
		iToQuestionsList.putExtra("id", id);
		startActivity(iToQuestionsList);
	}
	
	
}
