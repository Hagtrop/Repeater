package com.hagtrop.repeater;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

class BaseHelper extends SQLiteOpenHelper {
	final String LOG_TAG = "mLog";
	public BaseHelper(Context context){
		super(context, "repeaterDB", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String query1, query2, query3, query4, query5;
		query1 = "CREATE TABLE groups(_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT)";
		query2 = "CREATE TABLE questions(_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, question TEXT, answer TEXT, group_id INTEGER)";
		query3 = "CREATE TABLE tests(_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, table_title TEXT, mode INTEGER DEFAULT 0, duration INTEGER DEFAULT 0, round INTEGER DEFAULT 0, step INTEGER DEFAULT 0, completed INTEGER DEFAULT 0)";
		query4 = "CREATE TRIGGER test_name_trigger AFTER INSERT ON tests BEGIN UPDATE tests SET table_title='test_' || CAST(_id AS TEXT), step=0; END";
		query5 = "CREATE TABLE log(_id INTEGER PRIMARY KEY AUTOINCREMENT, question_id INTEGER, test_id INTEGER, round INTEGER, step INTEGER, result INTEGER)";
		db.execSQL(query1);
		db.execSQL(query2);
		db.execSQL(query3);
		db.execSQL(query4);
		db.execSQL(query5);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}
	
	public int getQuestionsCount(){
		int count = 0;
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try{
			db = getReadableDatabase();
			cursor = db.rawQuery("SELECT COUNT(*) FROM questions", null);
			cursor.moveToFirst();
			count = cursor.getInt(0);
		}
		catch(Exception e){
			Log.e(LOG_TAG, e.toString());
		}
		closeCursor(cursor);
		closeDB(db);
		Log.d(LOG_TAG, "getQuestionsCount = " + count);
		return count;
	}
	public int getGroupsCount(){
		int count = 0;
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try{
			db = getReadableDatabase();
			cursor = db.rawQuery("SELECT COUNT(*) FROM groups", null);
			cursor.moveToFirst();
			count = cursor.getInt(0);
		}
		catch(Exception e){
			Log.e(LOG_TAG, e.toString());
		}
		closeCursor(cursor);
		closeDB(db);
		Log.d(LOG_TAG, "getGroupsCount = " + count);
		return count;
	}
	private void closeCursor(Cursor c){
		if(c != null && !c.isClosed()){
			try{c.close();}
			catch(Exception e){Log.e(LOG_TAG, e.toString());}
		}
	}
	private void closeDB(SQLiteDatabase db){
		if(db != null && db.isOpen()){
			try{db.close();}
			catch(Exception e){Log.e(LOG_TAG, e.toString());}
		}
	}

}
