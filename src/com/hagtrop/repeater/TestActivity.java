package com.hagtrop.repeater;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class TestActivity extends Activity implements OnClickListener{
	TextView stepTV, questionTV;
	private long testId;
	Button yesBtn, noBtn;
	Test test;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity10_test);
		
		testId = getIntent().getExtras().getLong("testId");
		stepTV = (TextView) findViewById(R.id.a10_stepTV);
		questionTV = (TextView) findViewById(R.id.a10_questionTV);
		yesBtn = (Button) findViewById(R.id.a10_yesBtn);
		yesBtn.setOnClickListener(this);
		noBtn = (Button) findViewById(R.id.a10_noBtn);
		noBtn.setOnClickListener(this);
		test = new Test(testId, getContentResolver());
		if(test.moveToNext()){
			displayQuestion();
			test.printTestData();
		}
		else endTest();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		test.saveProgress();
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.a10_yesBtn:
			test.applyAnswer(true);
			break;
		case R.id.a10_noBtn:
			test.applyAnswer(false);
			break;
		default: break;
		}
		if(test.moveToNext()){
			displayQuestion();
			test.printTestData();
		}
		else endTest();
	}
	
	private void endTest(){
		Toast.makeText(this, "Тест выполнен", Toast.LENGTH_SHORT).show();
		test.setCompleted(true);
		finish();
	}
	
	private void displayQuestion(){
		stepTV.setText("Round: " + test.getRound() + " Step: " + test.getStep());
		questionTV.setText(test.getQuestion());
	}
}

class Test{
	private long testId;
	private long[] quesId;
	private ArrayList<Long> testQuesId;
	private int round;
	private int step;
	private String tableName;
	private int mode;
	private int duration;
	private String question;
	private String answer;
	private int quNumber;
	private long quId;
	private Random random;
	private ContentResolver resolver;
	private int prevRound;
	private int prevStep;
	private HashMap<Long, Integer[]> quesStatus;
	private int completed;
	
	static void resetTest(long id, ContentResolver resolver){
		ContentValues cv = new ContentValues();
		cv.put("round", 0);
		cv.put("step", 0);
		cv.put("completed", 0);
		resolver.update(DataBaseContentProvider.TESTS_URI, cv, "_id=" + id, null);
		cv.clear();
		cv.put("hits", 0);
		cv.put("answered", 0);
		Uri uri = ContentUris.withAppendedId(DataBaseContentProvider.TESTS_URI, id);
		resolver.update(uri, cv, null, null);
	}
	
	Test(long testId, ContentResolver resolver){
		this.testId = testId;
		this.resolver = resolver;		
		quNumber = -1;
		random = new Random();
		loadDataFromDB();
		if(mode == 0) ArrayShuffle.reshuffle(testQuesId);
	}
	
	void applyAnswer(boolean correct){
		int result = correct? 1 : 0;
		if(correct && mode == 1){
			testQuesId.remove(quNumber);
		}
		prevRound = round;
		prevStep = step;
		Integer[] status = {quesStatus.get(quId)[0]+1, result};
		quesStatus.put(quId, status);
		ContentValues cv = new ContentValues();
		
		cv.put("question_id", quId);
		cv.put("test_id", testId);
		cv.put("round", round);
		cv.put("step", step);
		cv.put("result", result);
		resolver.insert(DataBaseContentProvider.LOG_URI, cv);
		cv.clear();
		
		Log.d("mLog", "--------Сохранено в базу--------");
		Log.d("mLog", "round=" + round + " step=" + step + " quId=" + quId + " answer=" + result);
		Log.d("mLog", "--------------------------------");
	}
	
	void saveProgress(){
		ContentValues cv = new ContentValues();
		cv.put("round", prevRound);
		cv.put("step", prevStep);
		cv.put("completed", completed);
		resolver.update(DataBaseContentProvider.TESTS_URI, cv, "_id=?", new String[]{String.valueOf(testId)});
		cv.clear();
		
		Set<Map.Entry<Long, Integer[]>> set = quesStatus.entrySet();
		for(Map.Entry<Long, Integer[]> entry : set){
			cv.put("hits", entry.getValue()[0]);
			cv.put("answered", entry.getValue()[1]);
			Uri uri = ContentUris.withAppendedId(DataBaseContentProvider.TESTS_URI, testId);
			resolver.update(uri, cv, "_id=?", new String[]{String.valueOf(entry.getKey())});
			cv.clear();
		}
		Log.d("mLog", "saveProgress()");
	}
	
	int getRound(){
		return round;
	}
	
	int getStep(){
		return step;
	}
	
	String getQuestion(){
		return question;
	}
	
	void setCompleted(boolean compl){
		completed = compl? 1 : 0;
	}
	
	boolean moveToNext(){
		if(round == 0) round++;
		if(duration > 0 && round == duration && (testQuesId.isEmpty() || (quNumber == testQuesId.size()-1) && mode == 0)){
			Log.d("mLog", "duration="+duration+" round="+round+" testQuesId.isEmpty()="+testQuesId.isEmpty()+" testQuesId.size()="+testQuesId.size()+" quNumber="+quNumber);
			return false;
		}
		switch(mode){
		case 0:
			if(quNumber == testQuesId.size()-1 || step == quesId.length){
				round++;
				reloadTestArray();
				ArrayShuffle.reshuffle(testQuesId);
				resetQuesStatus();
				quNumber = 0;
				step = 1;
			}
			else{
				quNumber++;
				step++;
			}
			break;
		case 1:
			if(testQuesId.isEmpty()){
				round++;
				step = 1;
				reloadTestArray();
				resetQuesStatus();
			}
			else step++;
			quNumber = random.nextInt(testQuesId.size());
			break;
		default: break;
		}
		quId = testQuesId.get(quNumber);
		loadQuData();
		return true;
	}
	
	private void loadQuData(){
		Cursor cursor = resolver.query(DataBaseContentProvider.QUESTIONS_URI, new String[]{"question", "answer"}, "_id=?", new String[]{String.valueOf(quId)}, null);
		cursor.moveToFirst();
		question = cursor.getString(cursor.getColumnIndex("question"));
		answer = cursor.getString(cursor.getColumnIndex("answer"));
		cursor.close();
	}
	
	private void reloadTestArray(){
		testQuesId.clear();
		for(long id : quesId){
			testQuesId.add(id);
		}
	}
	
	private void resetQuesStatus(){
		Set<Map.Entry<Long, Integer[]>> set = quesStatus.entrySet();
		for(Map.Entry<Long, Integer[]> entry : set){
			entry.setValue(new Integer[]{0,0});
		}
	}
	
	private void loadDataFromDB(){
		Cursor cursor = resolver.query(DataBaseContentProvider.TESTS_URI, new String[]{"table_title", "mode", "duration", "round", "step", "completed"}, "_id="+testId, null, null);
		cursor.moveToFirst();
		tableName = cursor.getString(cursor.getColumnIndex("table_title"));
		mode = cursor.getInt(cursor.getColumnIndex("mode"));
		duration = cursor.getInt(cursor.getColumnIndex("duration"));
		round= prevRound = cursor.getInt(cursor.getColumnIndex("round"));
		step = prevStep = cursor.getInt(cursor.getColumnIndex("step"));
		completed = cursor.getInt(cursor.getColumnIndex("completed"));
		cursor.close();
		Uri uri = ContentUris.withAppendedId(DataBaseContentProvider.TESTS_URI, testId);
		cursor = resolver.query(uri, new String[]{"_id", "hits", "answered"}, null, null, null);
		int idIndex = cursor.getColumnIndex("_id");
		int hitsIndex = cursor.getColumnIndex("hits");
		int answerIndex = cursor.getColumnIndex("answered");
		cursor.moveToFirst();
		testQuesId = new ArrayList<Long>();
		quesId = new long[cursor.getCount()];
		quesStatus = new HashMap<Long, Integer[]>();
		int pos = 0;
		do{
			long key = cursor.getLong(idIndex);
			Integer[] status = {cursor.getInt(hitsIndex), cursor.getInt(answerIndex)};
			quesStatus.put(key, status);
			quesId[pos] = key;
			pos++;
			if(mode == 0 && status[0] == 0 || mode == 1 && status[1] == 0){
				testQuesId.add(key);
			}
		}
		while(cursor.moveToNext());
		cursor.close();
	}
	
	void printTestData(){
		Log.d("mLog", "");
		Log.d("mLog", "--------Test--------");
		Log.d("mLog", "tableName="+tableName+" mode="+mode+" duration="+duration+" round="+round+" step="+step);
		Log.d("mLog", "Рабочий массив:");
		String str = new String();
		for(long id : testQuesId){
			str += id + " ";
		}
		Log.d("mLog", str);
		//---------------------//
		Log.d("mLog", "Полный массив:");
		str = "";
		for(int i=0; i<quesId.length; i++){
			str += quesId[i] + " ";
		}
		Log.d("mLog", str);
		//---------------------//
		Log.d("mLog", "Результаты:");
		String str1, str2, str3;
		str1 = str2 = str3 = "";
		Set<Map.Entry<Long, Integer[]>> set = quesStatus.entrySet();
		for(Map.Entry<Long, Integer[]> entry : set){
			str1 += entry.getKey() + " ";
			str2 += entry.getValue()[1] + " ";
			str3 += entry.getValue()[0] + " ";
		}
		Log.d("mLog", str1 + " ID");
		Log.d("mLog", str2 + " Answered");
		Log.d("mLog", str3 + " Hits");
		Log.d("mLog", "--------------------");
	}
}

class ArrayShuffle{
	static void reshuffle(ArrayList<Long> array){
		Random random = new Random();
		for(int i=array.size()-1; i>0; i--){
			int j = random.nextInt(i+1);
			swap(array, i, j);
		}
	}
	private static void swap(ArrayList<Long> array, int i, int j){
		Long temp = array.get(i);
		array.set(i, array.get(j));
		array.set(j, temp);
	}
}
