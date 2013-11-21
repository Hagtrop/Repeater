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
import android.util.Log;
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
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity8_new_test);
		
		testNameET = (EditText) findViewById(R.id.a8_testNameET);
		saveBtn = (Button) findViewById(R.id.a8_nextBtn);
		saveBtn.setOnClickListener(this);
		groupsLV = (ListView) findViewById(R.id.a8_groupsLV);
		
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
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.a8_nextBtn:
			String testName = testNameET.getText().toString().trim();
			if(TextUtils.isEmpty(testName)){
				Toast.makeText(this, "Введите название теста", Toast.LENGTH_SHORT).show();
				break;
			}
			long[] checkedGroups = groupsLV.getCheckedItemIds();
			if(checkedGroups.length == 0){
				Toast.makeText(this, "Вы не выбрали ни одной группы вопросов", Toast.LENGTH_SHORT).show();
				break;
			}
			ContentValues cv = new ContentValues();
			cv.put("title", testName);
			Uri createdTestUri = getContentResolver().insert(DataBaseContentProvider.TESTS_URI, cv);
			String createdTestId = createdTestUri.getLastPathSegment();
			Cursor cursor = getContentResolver().query(DataBaseContentProvider.TESTS_URI, new String[]{"table_title"}, "_id="+createdTestId, null, null);
			cursor.moveToFirst();
			String testTableName = cursor.getString(0);
			cursor.close();
			Log.d("mLog", "Добавлен тест id=" + createdTestId);
			Log.d("mLog", "Создана таблица " + testTableName);
			ContentValues[] groupsId = new ContentValues[checkedGroups.length];
			for(int i=0; i<groupsId.length; i++){
				groupsId[i] = new ContentValues();
				groupsId[i].put("groupId", checkedGroups[i]);
				groupsId[i].put("tableName", testTableName);
			}
			getContentResolver().bulkInsert(DataBaseContentProvider.ONE_TEST_URI, groupsId);
			
			Intent iModeActivity = new Intent(this, ModeActivity.class);
			iModeActivity.putExtra("testId", String.valueOf(createdTestId));
			startActivityForResult(iModeActivity,1);
			break;
		default: break;
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// TODO Auto-generated method stub
		//return new CursorLoader(this, Uri.parse("content://com.hagtrop.repeater.DataBase/groups"), new String[]{"_id","title"}, null, null, null);
		return new CursorLoader(this, DataBaseContentProvider.NOT_EMPTY_GROUPS_URI, null, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
		// TODO Auto-generated method stub
		adapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		adapter.swapCursor(null);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		finish();
	}
}
