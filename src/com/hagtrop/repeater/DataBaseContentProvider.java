package com.hagtrop.repeater;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class DataBaseContentProvider extends ContentProvider {
	
	BaseHelper baseHelper;
	SQLiteDatabase database;
	
	static final String AUTHORITY = "com.hagtrop.repeater.DataBase";
	
	static final String GROUPS_PATH = "groups";
	static final int GROUPS_CODE = 1;
	static final int GROUP_ID_CODE = 2;
	static final Uri GROUPS_URI = Uri.parse("content://com.hagtrop.repeater.DataBase/groups");
	
	static final String QUESTIONS_PATH = "questions";
	static final int QUESTIONS_CODE = 3;
	static final int QUESTION_ID_CODE = 4;
	static final Uri QUESTIONS_URI = Uri.parse("content://com.hagtrop.repeater.DataBase/questions");
	
	static final String TESTS_PATH = "tests";
	static final Uri TESTS_URI = Uri.parse("content://com.hagtrop.repeater.DataBase/tests");
	static final int TESTS_CODE = 5;
	static final int TEST_ID_CODE = 6;
	
	static final String ONE_TEST_PATH = "onetest";
	static final Uri ONE_TEST_URI = Uri.parse("content://com.hagtrop.repeater.DataBase/onetest");
	static final int ONE_TEST_CODE = 7;
	
	static final String NOT_EMPTY_GROUPS_PATH = "groups/not_empty";
	static final Uri NOT_EMPTY_GROUPS_URI = Uri.parse("content://com.hagtrop.repeater.DataBase/groups/not_empty");
	static final int NOT_EMPTY_GROUPS_CODE = 8;
	
	static final String LOG_PATH = "log";
	static final Uri LOG_URI = Uri.parse("content://com.hagtrop.repeater.DataBase/log");
	static final int LOG_CODE = 9;
	
	static final UriMatcher uriMatcher;
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(AUTHORITY, GROUPS_PATH, GROUPS_CODE);
		uriMatcher.addURI(AUTHORITY, QUESTIONS_PATH, QUESTIONS_CODE);
		uriMatcher.addURI(AUTHORITY, TESTS_PATH, TESTS_CODE);
		uriMatcher.addURI(AUTHORITY, TESTS_PATH + "/#", TEST_ID_CODE);
		uriMatcher.addURI(AUTHORITY, ONE_TEST_PATH, ONE_TEST_CODE);
		uriMatcher.addURI(AUTHORITY, NOT_EMPTY_GROUPS_PATH, NOT_EMPTY_GROUPS_CODE);
		uriMatcher.addURI(AUTHORITY, LOG_PATH, LOG_CODE);
	}
	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		baseHelper = new BaseHelper(getContext());
		return true;
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		database = baseHelper.getWritableDatabase();
		int count = 0;
		String table;
		
		switch(uriMatcher.match(uri)){
		case GROUPS_CODE:
			table = GROUPS_PATH;
			break;
		case QUESTIONS_CODE:
			table = QUESTIONS_PATH;
			break;
		case TESTS_CODE:
			table = TESTS_PATH;
			Cursor cursor = query(TESTS_URI, new String[]{"table_title"}, "_id="+selectionArgs[0], null, null);
			cursor.moveToFirst();
			String testTableName = cursor.getString(0);
			database.execSQL("DROP TABLE " + testTableName);
			break;
		default: throw new IllegalArgumentException("Wrong URI: " + uri);
		}
		
		count = database.delete(table, selection, selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);

		return count;
	}

	@Override
	public String getType(Uri arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues cv) {
		// TODO Auto-generated method stub
		Uri resultUri = null;
		long id;
		database = baseHelper.getWritableDatabase();
		switch(uriMatcher.match(uri)){
		case GROUPS_CODE:
			database.insert(GROUPS_PATH, null, cv);
			getContext().getContentResolver().notifyChange(GROUPS_URI, null);
			break;
		case QUESTIONS_CODE:
			id = database.insert(QUESTIONS_PATH, null, cv);
			getContext().getContentResolver().notifyChange(QUESTIONS_URI, null);
			Log.d("mLog", "Вопрос добавлен, id=" + id);
			break;
		case TESTS_CODE:
			id = database.insert(TESTS_PATH, null, cv);
			resultUri = ContentUris.withAppendedId(TESTS_URI, id);
			Cursor cursor = query(TESTS_URI, new String[]{"table_title"}, "_id="+id, null, null);
			cursor.moveToFirst();
			String name = cursor.getString(0);
			//String query = "CREATE TABLE " + name + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, group_id INTEGER, question_id INTEGER, hits INTEGER, last_answer INTEGER, good_count INTEGER, bad_count INTEGER)";
			String query = "CREATE TABLE " + name + "(_id INTEGER PRIMARY KEY, group_id INTEGER, hits INTEGER DEFAULT 0, answered INTEGER DEFAULT 0)";
			database.execSQL(query);
			break;
		case ONE_TEST_CODE:
			String tableName = cv.getAsString("tableName");
			long groupId = cv.getAsLong("groupId");
			String copyCommand = "INSERT INTO " + tableName + "(_id, group_id) SELECT questions._id, questions.group_id FROM questions WHERE questions.group_id = " + groupId;
			database.execSQL(copyCommand);
			break;
		case LOG_CODE:
			database.insert("log", null, cv);
			break;
		default: throw new IllegalArgumentException("Wrong URI: " + uri);
		}
		
		return resultUri;
	}

	@Override
	public Cursor query(Uri uri, String[] columns, String selection, String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		database = baseHelper.getReadableDatabase();
		Cursor cursor = null;
		switch(uriMatcher.match(uri)){
		case GROUPS_CODE:
			cursor = database.query(GROUPS_PATH, columns, selection, selectionArgs, null, null, sortOrder);
			cursor.setNotificationUri(getContext().getContentResolver(), GROUPS_URI);
			break;
		case NOT_EMPTY_GROUPS_CODE:
			String query = "SELECT DISTINCT groups._id, groups.title FROM groups INNER JOIN questions ON groups._id = questions.group_id";
			cursor = database.rawQuery(query, null);
			cursor.setNotificationUri(getContext().getContentResolver(), GROUPS_URI);
			break;
		case QUESTIONS_CODE:
			cursor = database.query(QUESTIONS_PATH, columns, selection, selectionArgs, null, null, sortOrder);
			cursor.setNotificationUri(getContext().getContentResolver(), QUESTIONS_URI);
			break;
		case TESTS_CODE:
			cursor = database.query(TESTS_PATH, columns, selection, selectionArgs, null, null, sortOrder);
			cursor.setNotificationUri(getContext().getContentResolver(), TESTS_URI);
			break;
		case TEST_ID_CODE:
			String tableName = getTestTableName(uri.getLastPathSegment());
			cursor = database.query(tableName, columns, selection, selectionArgs, null, null, sortOrder);
			break;
		default: throw new IllegalArgumentException("Wrong URI: " + uri);
		}
		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues cv, String where, String[] args) {
		// TODO Auto-generated method stub
		database = baseHelper.getWritableDatabase();
		String table;
		switch(uriMatcher.match(uri)){
		case QUESTIONS_CODE:
			table = QUESTIONS_PATH;
			break;
		case TESTS_CODE:
			table = TESTS_PATH;
			break;
		case TEST_ID_CODE:
			table = getTestTableName(uri.getLastPathSegment());
			break;
		default: throw new IllegalArgumentException("Wrong URI: " + uri);
		}
		int count = database.update(table, cv, where, args);
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
	
	private String getTestTableName(String id){
		Cursor cursor = database.query("tests", new String[]{"table_title"}, "_id="+id, null, null, null, null);
		cursor.moveToFirst();
		String tableName = cursor.getString(0);
		return tableName;
	}
}
