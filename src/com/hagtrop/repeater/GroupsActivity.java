package com.hagtrop.repeater;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
	BaseHelper baseHelper;
	ListView groupsLV;
	CursorAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity2_groups);
		
		noGroupsTV = (TextView) findViewById(R.id.a2_noGroupsFoundTV);
		createBtn = (Button) findViewById(R.id.a2_createGroupBtn);
		createBtn.setOnClickListener(this);
		baseHelper = new BaseHelper(this);
		groupsLV = (ListView) findViewById(R.id.a2_groupsLV);
		registerForContextMenu(groupsLV);
		groupsLV.setOnItemClickListener(this);
		
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
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.a2_createGroupBtn:
			Intent intent = new Intent(this, AddGroupsActivity.class);
			startActivity(intent);
			break;
		default: break;
		}
	}
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// TODO Auto-generated method stub
		return new CursorLoader(this, Uri.parse("content://com.hagtrop.repeater.DataBase/groups"), new String[]{"_id","title"}, null, null, null);
	}
	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
		// TODO Auto-generated method stub
		adapter.swapCursor(cursor);
		if(adapter.getCount() == 0){
			noGroupsTV.setVisibility(View.VISIBLE);
		}
		else{
			noGroupsTV.setVisibility(View.GONE);
		}
	}
	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		adapter.swapCursor(null);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
		if(v.getId() == R.id.a2_groupsLV){
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.group_context_menu, menu);
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch(item.getItemId()){
		case R.id.delete_group:
			Log.d("mLog", "Удалить id="+info.id);
			ContentResolver contentResolver = getContentResolver();
			contentResolver.delete(DataBaseContentProvider.GROUPS_URI, "_id="+info.id, null);
			contentResolver.delete(DataBaseContentProvider.QUESTIONS_URI, "group_id="+info.id, null);
			break;
		}
		return super.onContextItemSelected(item);
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		Log.d("mLog", "itemClick: position = " + position + ", id = " + id);
		Intent intent = new Intent(this, GroupQuestionsActivity.class);
		intent.putExtra("id", id);
		startActivity(intent);
	}
}
