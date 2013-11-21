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
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity9_select_test);
		
		noTestsTV = (TextView) findViewById(R.id.a9_noTestsTV);
		testsLV = (ListView) findViewById(R.id.a9_testsLV);
		testsLV.setOnItemClickListener(this);
		registerForContextMenu(testsLV);
		
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
		// TODO Auto-generated method stub
		return new CursorLoader(this, DataBaseContentProvider.TESTS_URI, new String[]{"_id","title"}, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		// TODO Auto-generated method stub
		adapter.swapCursor(cursor);
		if(adapter.getCount() == 0){
			noTestsTV.setVisibility(View.VISIBLE);
		}
		else{
			noTestsTV.setVisibility(View.GONE);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// TODO Auto-generated method stub
		adapter.swapCursor(null);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
		// TODO Auto-generated method stub
		selectedTestId = id;
		PopupMenu popupMenu = new PopupMenu(this, v);
		popupMenu.getMenuInflater().inflate(R.menu.a9_popup_menu, popupMenu.getMenu());
		popupMenu.setOnMenuItemClickListener(this);
		popupMenu.show();
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()){
		case R.id.a9_continueTestMenuItem:
			Cursor cursor = getContentResolver().query(DataBaseContentProvider.TESTS_URI, new String[]{"completed"}, "_id=" + selectedTestId, null, null);
			cursor.moveToFirst();
			int completed = cursor.getInt(0);
			cursor.close();
			if(completed == 1){
				Toast.makeText(this, "Тест выполнен", Toast.LENGTH_SHORT).show();
				return true;
			}
			break;
		case R.id.a9_restartTestMenuItem:
			Test.resetTest(selectedTestId, getContentResolver());
			break;
		}
		Intent iTestActivity = new Intent(this, TestActivity.class);
		iTestActivity.putExtra("testId", selectedTestId);
		startActivityForResult(iTestActivity,1);
		return true;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.a9_context_menu, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch(item.getItemId()){
		case R.id.a9_deleteTestMenuItem:
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
