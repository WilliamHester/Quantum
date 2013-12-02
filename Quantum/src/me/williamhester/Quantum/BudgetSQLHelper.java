package me.williamhester.Quantum;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.List;

public class BudgetSQLHelper extends SQLiteOpenHelper {

	public static final String TABLE_BUDGETS = "budgets";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_BUDGET_NAME = "name";
	public static final String COLUMN_BUDGET_VALUE = "value";
	public static final String COLUMN_CURRENT_BUDGET_VALUE = "current_budget";
	public static final String COLUMN_LAST_INTERVAL = "last_interval";
	public static final String COLUMN_SURPLUS = "surplus";
    public static final String COLUMN_BUDGET_ITERATION_TYPE = "type";
    public static final String COLUMN_ITERATE_ON = "iterate_on";

	public static final String DATABASE_NAME = "budgets.db";
	public static final int DATABASE_VERSION = 1;

	// Database creation sql statement
	private static final String DATABASE_CREATE = "CREATE TABLE "
			+ TABLE_BUDGETS + "(" + COLUMN_ID
			+ " integer primary key autoincrement, " + COLUMN_BUDGET_NAME + " text not null, " 
			+ COLUMN_BUDGET_VALUE + " integer not null, " + COLUMN_CURRENT_BUDGET_VALUE
            + " integer not null, "+ COLUMN_SURPLUS + " integer not null, "
            + COLUMN_BUDGET_ITERATION_TYPE + " integer not null, " + COLUMN_LAST_INTERVAL
            + " integer not null, " + COLUMN_ITERATE_ON + " integer not null);";

	public BudgetSQLHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, DATABASE_NAME, factory, DATABASE_VERSION);
	}
	
	public BudgetSQLHelper(Context context) {
		this(context, DATABASE_NAME, null, DATABASE_VERSION);
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

        // Get all of the needed data before it's destroyed
        BudgetDataSource data = new BudgetDataSource(this);
        data.open();
        List<Budget> budgetList = data.getAllBudgets();
        data.close();

		db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUDGETS);
	    onCreate(db);

        data.open();
        switch(oldVersion) {
            case 1:
                // To be added
            default:
                for (Budget b : budgetList) {
                    data.addBudget(b);
                }
                break;
        }
        data.close();
	}

}
