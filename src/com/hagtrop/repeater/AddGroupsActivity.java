package com.hagtrop.repeater;

import android.app.Activity;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddGroupsActivity extends Activity implements OnClickListener{
	EditText nameET;
	Button saveBtn;
	
	final Uri GROUPS_URI = Uri.parse("content://com.hagtrop.repeater.DataBase/groups");
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity3_add_groups);
		
		nameET = (EditText) findViewById(R.id.a3_nameET);
		saveBtn = (Button) findViewById(R.id.a3_saveBtn);
		saveBtn.setOnClickListener(this);
	}
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		ContentValues cv = new ContentValues();
		String newName = nameET.getText().toString().trim();
		if(!TextUtils.isEmpty(newName)){
			cv.put("title", newName);
			getContentResolver().insert(DataBaseContentProvider.GROUPS_URI, cv);
			Toast.makeText(this, "Группа создана", Toast.LENGTH_SHORT).show();
		}
		else{
			Toast.makeText(this, "Имя группы не задано", Toast.LENGTH_SHORT).show();
		}
		nameET.getText().clear();
	}
}
