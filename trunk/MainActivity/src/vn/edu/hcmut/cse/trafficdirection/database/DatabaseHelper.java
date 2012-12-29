package vn.edu.hcmut.cse.trafficdirection.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "MapDatabase";
	public static final String NODE_TABLE_NAME = "NodeTable";
	public static final String STREET_TABLE_NAME = "StreetTable";

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("CREATE TABLE "
				+ NODE_TABLE_NAME
				+ " (ID INTEGER PRIMARY KEY AUTOINCREMENT, NodeID INTEGER, Lat REAL, Lon REAL, StreetID Integer, Speed TEXT, Density TEXT, TimeStamp TEXT)");
		/*
		 * db.execSQL("CREATE TABLE " + NODE_TABLE_NAME +
		 * " (ID INTEGER PRIMARY KEY AUTOINCREMENT, NodeID INTEGER, Lat REAL, Lon REAL, StreetID Integer, Speed TEXT, Density TEXT, TimeStamp TEXT"
		 * + "FOREIGN KEY(StreetID) REFERENCES STREET_TABLE_NAME(StreetID))");
		 */

		db.execSQL("CREATE TABLE " + STREET_TABLE_NAME
				+ " (StreetID INTEGER PRIMARY KEY, Label TEXT, Type TEXT)");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS " + NODE_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + STREET_TABLE_NAME);
		onCreate(db);
	}
}
