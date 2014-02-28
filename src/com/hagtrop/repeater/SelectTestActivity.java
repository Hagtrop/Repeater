package com.hagtrop.repeater;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;

public class SelectTestActivity extends Activity implements OnItemClickListener, LoaderCallbacks<Cursor>, OnMenuItemClickListener{
	TextView noTestsTV;
	ListView testsLV;
	CursorAdapter adapter;
	private long selectedTestId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity9_select_test);
		
		noTestsTV = (TextView) findViewById(R.id.a9_noTestsTV);
		testsLV = (ListView) findViewById(R.id.a9_testsLV);
		testsLV.setOnItemClickListener(this);
		//Добавляем контекстное меню к списку тестов
		registerForContextMenu(testsLV);
		
		//Загружаем в ListView список созданных тестов
		getLoaderManager().initLoader(0, null, this);
		int layout = android.R.layout.simple_list_item_1;
		String[] from = {"title"};
		int[] to = {android.R.id.text1};
		int flags = Adapter.NO_SELECTION;
		adapter = new SimpleCursorAdapter(this, layout, null, from, to, flags);
		testsLV.setAdapter(adapter);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(this, DataBaseContentProvider.TESTS_URI, new String[]{"_id","title"}, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		adapter.swapCursor(cursor);
		//Если список тестов пуст, отображаем сообщение в TextView
		if(adapter.getCount() == 0){
			noTestsTV.setVisibility(View.VISIBLE);
		}
		else{
			noTestsTV.setVisibility(View.GONE);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.swapCursor(null);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
		selectedTestId = id;
		//Создаём меню на основе xml, вызываемое по клику на пункте списка
		PopupMenu popupMenu = new PopupMenu(this, v);
		popupMenu.getMenuInflater().inflate(R.menu.a9_popup_menu, popupMenu.getMenu());
		popupMenu.setOnMenuItemClickListener(this);
		popupMenu.show();
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		//Обрабатываем выбор пункта меню
		switch(item.getItemId()){
		case R.id.a9_continueTestMenuItem:
			//Получаем состояние выбранного теста
			Cursor cursor = getContentResolver().query(DataBaseContentProvider.TESTS_URI, new String[]{"completed"}, "_id=" + selectedTestId, null, null);
			cursor.moveToFirst();
			int completed = cursor.getInt(0);
			cursor.close();
			//Если тест пройден до конца, выводим сообщение
			if(completed == 1){
				Toast.makeText(this, R.string.a9_testCompletedMsg, Toast.LENGTH_SHORT).show();
				return true;
			}
			break;
		case R.id.a9_restartTestMenuItem:
			Test.resetTest(selectedTestId, getContentResolver());
			break;
		}
		//Проверяем, все ли вопросы теста присутствуют в БД и удаляем несуществующие
		getContentResolver().delete(DataBaseContentProvider.TEST_REFRESH_URI, "_id=?", new String[]{String.valueOf(selectedTestId)});
		//Продолжаем выбранный тест
		Intent iTestActivity = new Intent(this, TestActivity.class);
		iTestActivity.putExtra("testId", selectedTestId);
		startActivityForResult(iTestActivity,1);
		return true;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.a9_context_menu, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		//Обрабатываем события контекстного меню
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch(item.getItemId()){
		case R.id.a9_deleteTestMenuItem:
			//Удаяем выбранный тест
			getContentResolver().delete(DataBaseContentProvider.TESTS_URI, "_id=?", new String[]{String.valueOf(info.id)});
			break;
		default: break;
		}
		return super.onContextItemSelected(item);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		finish();
	}
}
