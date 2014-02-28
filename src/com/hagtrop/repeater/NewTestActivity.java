package com.hagtrop.repeater;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class NewTestActivity extends Activity implements OnClickListener, LoaderCallbacks<Cursor>{
	EditText testNameET;
	ListView groupsLV;
	Button saveBtn;
	CursorAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity8_new_test);
		
		testNameET = (EditText) findViewById(R.id.a8_testNameET);
		saveBtn = (Button) findViewById(R.id.a8_nextBtn);
		saveBtn.setOnClickListener(this);
		groupsLV = (ListView) findViewById(R.id.a8_groupsLV);
		
		//Заполняем ListView списком непустых групп
		getLoaderManager().initLoader(4, null, this);
		int layout = android.R.layout.simple_list_item_multiple_choice;
		String[] from = {"title"};
		int[] to = {android.R.id.text1};
		int flags = Adapter.NO_SELECTION;
		groupsLV.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		adapter = new SimpleCursorAdapter(this, layout, null, from, to, flags);
		groupsLV.setAdapter(adapter);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.a8_nextBtn:
			String testName = testNameET.getText().toString().trim();
			if(TextUtils.isEmpty(testName)){
				//Выводим сообщение, если не задано название теста
				Toast.makeText(this, R.string.a8_titleMissedMsg, Toast.LENGTH_SHORT).show();
				break;
			}
			//Получаем список id отмеченных групп
			long[] checkedGroups = groupsLV.getCheckedItemIds();
			if(checkedGroups.length == 0){
				//Если ни одной группы не выбрано выводим сообщение
				Toast.makeText(this, R.string.a8_nothingSelectedMsg, Toast.LENGTH_SHORT).show();
				break;
			}
			ContentValues cv = new ContentValues();
			cv.put("title", testName);
			//Добавляем название нового теста в таблицу тестов
			//DataBaseContentProvider автоматически создаст таблицу состояния для нового теста
			Uri createdTestUri = getContentResolver().insert(DataBaseContentProvider.TESTS_URI, cv);
			//Получаем ID нового теста из URI, возвращённого методом insert
			String createdTestId = createdTestUri.getLastPathSegment();
			//Получаем название новой таблицы
			Cursor cursor = getContentResolver().query(DataBaseContentProvider.TESTS_URI, new String[]{"table_title"}, "_id="+createdTestId, null, null);
			cursor.moveToFirst();
			String testTableName = cursor.getString(0);
			cursor.close();
			ContentValues[] groupsId = new ContentValues[checkedGroups.length];
			for(int i=0; i<groupsId.length; i++){
				groupsId[i] = new ContentValues();
				groupsId[i].put("groupId", checkedGroups[i]);
				groupsId[i].put("tableName", testTableName);
			}
			//Отправляем в ContentResolver ID отмеченных групп и имя таблицы теста
			getContentResolver().bulkInsert(DataBaseContentProvider.ONE_TEST_URI, groupsId);
			
			Intent iToModeActivity = new Intent(this, ModeActivity.class);
			iToModeActivity.putExtra("testId", String.valueOf(createdTestId));
			startActivityForResult(iToModeActivity,1);
			break;
		default: break;
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(this, DataBaseContentProvider.NOT_EMPTY_GROUPS_URI, null, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
		adapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		adapter.swapCursor(null);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		finish();
	}
}
