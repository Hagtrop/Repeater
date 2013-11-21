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
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity4_group_questions);
		
		groupId = getIntent().getExtras().getLong("id");
		noQuestionsTV = (TextView) findViewById(R.id.a4_noQuestionsFoundTV);
		questionsLV = (ListView) findViewById(R.id.a4_questionsLV);
		createBtn = (Button) findViewById(R.id.a4_createBtn);
		createBtn.setOnClickListener(this);
		
		getLoaderManager().initLoader(1, null, this);
		int layout = android.R.layout.simple_list_item_1;
		String[] from = {"title"};
		int[] to = {android.R.id.text1};
		int flags = Adapter.NO_SELECTION;
		adapter = new SimpleCursorAdapter(this, layout, null, from, to, flags);
		questionsLV.setAdapter(adapter);
		registerForContextMenu(questionsLV);
		questionsLV.setOnItemClickListener(this);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch(view.getId()){
		case R.id.a4_createBtn:
			Intent questionCreateInt = new Intent(this, AddQuestionsActivity.class);
			questionCreateInt.putExtra("id", groupId);
			startActivity(questionCreateInt);
			break;
		default: break;
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// TODO Auto-generated method stub
		return new CursorLoader(this, DataBaseContentProvider.QUESTIONS_URI, new String[]{"_id","title","group_id"}, "group_id="+groupId, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
		// TODO Auto-generated method stub
		adapter.swapCursor(cursor);
		if(adapter.getCount() == 0){
			noQuestionsTV.setVisibility(View.VISIBLE);
		}
		else{
			noQuestionsTV.setVisibility(View.GONE);
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
		
		if(v.getId() == R.id.a4_questionsLV){
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.question_context_menu, menu);
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch(item.getItemId()){
		case R.id.delete_question:
			getContentResolver().delete(DataBaseContentProvider.QUESTIONS_URI, "_id="+info.id, null);
			break;
		default: break;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, QuestionActivity.class);
		intent.putExtra("id", id);
		startActivity(intent);
	}
}
