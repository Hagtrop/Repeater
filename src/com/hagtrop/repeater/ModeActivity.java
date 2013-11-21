package com.hagtrop.repeater;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class ModeActivity extends Activity implements OnClickListener, OnCheckedChangeListener{
	EditText iterationET;
	Button startBtn;
	RadioGroup modeRGrp, durationRGrp;
	String testId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity11_mode);
		
		testId = getIntent().getStringExtra("testId");
		iterationET = (EditText) findViewById(R.id.a11_iterationET);
		iterationET.setText("1");
		modeRGrp = (RadioGroup) findViewById(R.id.a11_modeRGrp);
		setListenerForBtns(modeRGrp);
		durationRGrp = (RadioGroup) findViewById(R.id.a11_durationRGrp);
		setListenerForBtns(durationRGrp);
		startBtn = (Button) findViewById(R.id.a11_startBtn);
		startBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.a11_startBtn:
			int mode, duration;
			mode = 0;
			duration = 0;
			switch(modeRGrp.getCheckedRadioButtonId()){
			case R.id.a11_modeSimpleRBtn:
				mode = 0;
				break;
			case R.id.a11_modeProgressiveRBtn:
				mode = 1;
				break;
			default: break;
			}
			
			switch(durationRGrp.getCheckedRadioButtonId()){
			case R.id.a11_durationEndlessRBtn:
				duration = 0;
				break;
			case R.id.a11_iterationRBtn:
				String value = iterationET.getText().toString().trim();
				if(value.isEmpty() || Integer.parseInt(value) < 1){
					Toast toast = Toast.makeText(this, "Число проходов\nне может быть меньше 1", Toast.LENGTH_SHORT);
					TextView tv = (TextView) toast.getView().findViewById(android.R.id.message);
					if(tv != null) tv.setGravity(Gravity.CENTER);
					toast.show();
					return;
				}
				duration = Integer.parseInt(value);
				break;
			default: break;
			}
			ContentValues cv = new ContentValues();
			cv.put("mode", mode);
			cv.put("duration", duration);
			getContentResolver().update(DataBaseContentProvider.TESTS_URI, cv, "_id=?", new String[]{testId});
			
			Intent iTestActivity = new Intent(this, TestActivity.class);
			iTestActivity.putExtra("testId", Long.parseLong(testId));
			startActivityForResult(iTestActivity,1);
			break;
		default: break;
		}
	}
	
	private void setListenerForBtns(RadioGroup group){
		int btnsCount = group.getChildCount();
		for(int i=0; i<btnsCount; i++){
			View view = group.getChildAt(i);
			if(view instanceof RadioButton){
				((RadioButton) view).setOnCheckedChangeListener(this);
			}
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		int id = buttonView.getId();
		if(isChecked){
			buttonView.setAlpha(1f);
			if(id == R.id.a11_iterationRBtn) iterationET.setEnabled(true);
		}
		else{
			buttonView.setAlpha(0.5f);
			if(id == R.id.a11_iterationRBtn) iterationET.setEnabled(false);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		finish();
	}
}
