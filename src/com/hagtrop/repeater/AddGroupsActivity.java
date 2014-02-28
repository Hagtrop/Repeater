package com.hagtrop.repeater;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddGroupsActivity extends Activity implements OnClickListener{
	EditText nameET;
	Button saveBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity3_add_groups);
		
		nameET = (EditText) findViewById(R.id.a3_nameET);
		saveBtn = (Button) findViewById(R.id.a3_saveBtn);
		saveBtn.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View view) {
		ContentValues cv = new ContentValues();
		String newName = nameET.getText().toString().trim();
		Toast toast;
		//Проверяем, что пользователь не оставил поле имени пустым
		if(!TextUtils.isEmpty(newName)){
			//Добавляем имя новой группы в БД и выводим соответствующее сообщение
			cv.put("title", newName);
			getContentResolver().insert(DataBaseContentProvider.GROUPS_URI, cv);
			toast = Toast.makeText(this, R.string.a3_groupCreatedMsg, Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
			toast.show();
			//Возвращаемся к списку групп
			finish();
			
		}
		else{
			//Если имя не задано, выводим сообщение
			toast = Toast.makeText(this, R.string.a3_groupNameMissedMsg, Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
			toast.show();
		}
	}
}
