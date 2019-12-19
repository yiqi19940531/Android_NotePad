package com.notepad.namespace;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	private String tableName = "record";
	private Context mContext = null;
	private String sql = "create table if not exists " + tableName +
			"(_id integer primary key autoincrement, " +
			"title varchar," +
			"content text," +
			"time varchar)";

	public DatabaseHelper(Context context, String name, CursorFactory factory,
						  int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub

		//创建表

		db.execSQL(sql);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
