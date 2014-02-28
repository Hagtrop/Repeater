package com.hagtrop.repeater;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class TextEditorActivity extends Activity implements OnClickListener{
	EditText editText;
	Button saveBtn;
	Intent intent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity7_text_editor);
		
		intent = getIntent();
		editText = (EditText) findViewById(R.id.a7_ET);
		editText.setText(intent.getExtras().getString("text"));
		saveBtn = (Button) findViewById(R.id.a7_saveBtn);
		saveBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		String newText = editText.getText().toString().trim();
		if(TextUtils.isEmpty(newText)){
			//Если поле не заполнено выводим сообщение
			Toast toast = Toast.makeText(this, R.string.a7_enterTextMsg, Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
			toast.show();
		}
		else{
			//Обновляем запись в БД
			ContentValues cv = new ContentValues();
			cv.put(intent.getExtras().getString("field"), newText);
			String where = "_id=" + intent.getExtras().getLong("id");
			getContentResolver().update(DataBaseContentProvider.QUESTIONS_URI, cv, where, null);
			finish();
		}
	}
}
