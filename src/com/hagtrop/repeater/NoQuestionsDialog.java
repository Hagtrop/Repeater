package com.hagtrop.repeater;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;

public class NoQuestionsDialog extends DialogFragment implements OnClickListener{
	Button yesBtn, noBtn;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		return dialog;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.a1_dialog_no_questions_in_base, null);
		yesBtn = (Button) view.findViewById(R.id.a1_d1_yesBtn);
		yesBtn.setOnClickListener(this);
		noBtn = (Button) view.findViewById(R.id.a1_d1_noBtn);
		noBtn.setOnClickListener(this);
		return view;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.a1_d1_yesBtn:
			startActivity(new Intent(getActivity(), GroupsActivity.class));
			dismiss();
			break;
		case R.id.a1_d1_noBtn:
			dismiss();
			break;
		default: break;
		}
	}
}
