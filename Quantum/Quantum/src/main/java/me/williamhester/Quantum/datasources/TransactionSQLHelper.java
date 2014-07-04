package me.williamhester.Quantum.datasources;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TransactionSQLHelper extends SQLiteOpenHelper {

	public final String TABLE_TRANSACTIONS;
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_VALUE = "value";
    public static final String COLUMN_MONTH = "month";
    public static final String COLUMN_DAY = "day";
    public static final String COLUMN_YEAR = "year";
    public static final String COLUMN_WEEK_OF_YEAR = "week_of_year";
    public static final String COLUMN_HOUR = "hour";
    public static final String COLUMN_MINUTE = "minute";
    public static final String COLUMN_DAY_OF_YEAR = "day_of_year";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_LOCATION_NAME = "location_name";
    public static final String COLUMN_MEMO = "memo";
	
	public static final int DATABASE_VERSION = 1;
	
	private final String DATABASE_CREATE;

	public TransactionSQLHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, DATABASE_VERSION);
		TABLE_TRANSACTIONS = name;
		
		DATABASE_CREATE = "CREATE TABLE "
				+ TABLE_TRANSACTIONS + "(" + COLUMN_ID
				+ " integer primary key autoincrement, " + COLUMN_VALUE + " integer not null, " 
				+ COLUMN_MONTH + " integer not null, " + COLUMN_DAY + " integer not null, "
                + COLUMN_YEAR + " integer not null, " + COLUMN_WEEK_OF_YEAR + " integer not null, "
                + COLUMN_HOUR + " integer not null, " + COLUMN_MINUTE + " integer not null, "
                + COLUMN_DAY_OF_YEAR + " integer not null, " + COLUMN_TYPE + " text not null, "
                + COLUMN_LOCATION_NAME + " text not null, "+ COLUMN_MEMO + " text not null);";
	}

	public TransactionSQLHelper(Context context, String name) {
		this(context, name, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(BudgetSQLHelper.class.getName(),
		        "Upgrading database from version " + oldVersion + " to "
		            + newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
	    onCreate(db);
	}
	
	public String getTableName() {
		return TABLE_TRANSACTIONS;
	}
}
