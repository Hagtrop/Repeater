package com.hagtrop.repeater;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

public class SimpleMessageDialog extends DialogFragment implements OnClickListener{
	TextView messageTV;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		return dialog;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		//getDialog().setTitle("Операция недоступна");
		View view = inflater.inflate(R.layout.simple_message_dialog, null);
		messageTV = (TextView) view.findViewById(R.id.a1_d1_message);
		messageTV.setText("В базе данных нет вопросов для создания теста.\nСначала необходимо наполнить базу данных в разделе \"Вопросы\"");
		view.findViewById(R.id.d1_backBtn).setOnClickListener(this);
		return view;
	}

	@Override
	public void onClick(View v) {
		dismiss();
	}

}
