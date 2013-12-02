package me.williamhester.Quantum;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BudgetDataSource {

	private SQLiteDatabase mDatabase;
	private BudgetSQLHelper mHelper;
	private String[] allColumns = { BudgetSQLHelper.COLUMN_ID, 
			BudgetSQLHelper.COLUMN_BUDGET_NAME , BudgetSQLHelper.COLUMN_BUDGET_VALUE,
            BudgetSQLHelper.COLUMN_CURRENT_BUDGET_VALUE, BudgetSQLHelper.COLUMN_SURPLUS,
            BudgetSQLHelper.COLUMN_BUDGET_ITERATION_TYPE, BudgetSQLHelper.COLUMN_LAST_INTERVAL,
            BudgetSQLHelper.COLUMN_ITERATE_ON};
	
	public BudgetDataSource(Context context) {
		this(new BudgetSQLHelper(context));
	}

    public BudgetDataSource(BudgetSQLHelper helper) {
        mHelper = helper;
    }
	
	public void open() throws SQLException {
		mDatabase = mHelper.getWritableDatabase();
	}
	
	public void close() {
		mHelper.close();
	}
	
	public Budget createBudget(String name, int budget,
			int currentBudget, int surplus, int iterationType,
            int lastInterval, int iterateOn) {
		ContentValues values = new ContentValues();
		values.put(BudgetSQLHelper.COLUMN_BUDGET_NAME, name);
		values.put(BudgetSQLHelper.COLUMN_BUDGET_VALUE, budget);
		values.put(BudgetSQLHelper.COLUMN_CURRENT_BUDGET_VALUE, currentBudget);
		values.put(BudgetSQLHelper.COLUMN_SURPLUS, surplus);
        values.put(BudgetSQLHelper.COLUMN_BUDGET_ITERATION_TYPE, iterationType);
        values.put(BudgetSQLHelper.COLUMN_LAST_INTERVAL, lastInterval);
        values.put(BudgetSQLHelper.COLUMN_ITERATE_ON, iterateOn);
		long insertId = mDatabase.insert(BudgetSQLHelper.TABLE_BUDGETS, null, values);
		Cursor cursor = mDatabase.query(BudgetSQLHelper.TABLE_BUDGETS,
				allColumns, BudgetSQLHelper.COLUMN_ID + " = " + insertId,
				null, null, null, null);
		cursor.moveToFirst();
	    Budget newBudget = new Budget(cursor);
	    cursor.close();
		return newBudget;
	}

    public void addBudget(Budget budget) {
        ContentValues values = new ContentValues();
        values.put(BudgetSQLHelper.COLUMN_BUDGET_NAME, budget.getName());
        values.put(BudgetSQLHelper.COLUMN_BUDGET_VALUE, budget.getBudget());
        values.put(BudgetSQLHelper.COLUMN_CURRENT_BUDGET_VALUE, budget.getCurrentBudget());
        values.put(BudgetSQLHelper.COLUMN_SURPLUS, budget.getSurplus());
        values.put(BudgetSQLHelper.COLUMN_BUDGET_ITERATION_TYPE, budget.getIterationType());
        values.put(BudgetSQLHelper.COLUMN_LAST_INTERVAL, budget.getLastInterval());
        values.put(BudgetSQLHelper.COLUMN_ITERATE_ON, budget.getIterateOn());
        mDatabase.insert(BudgetSQLHelper.TABLE_BUDGETS, null, values);
    }

	public void setCurrentBudget(long id, int newCurrentBudget) {
		ContentValues values = new ContentValues();
		values.put(BudgetSQLHelper.COLUMN_CURRENT_BUDGET_VALUE, newCurrentBudget);

		String[] sId = { "" + id };
		String where = BudgetSQLHelper.COLUMN_ID + "=?";
		open();
		mDatabase.update(BudgetSQLHelper.TABLE_BUDGETS, values, where, sId);
	}

    public void setBudget(long id, int newBudget) {
        ContentValues values = new ContentValues();
        values.put(BudgetSQLHelper.COLUMN_BUDGET_VALUE, newBudget);

        String[] sId = { "" + id };
        String where = BudgetSQLHelper.COLUMN_ID + "=?";
        open();
        mDatabase.update(BudgetSQLHelper.TABLE_BUDGETS, values, where, sId);
    }

    public void setSurplus(long id, int surplus) {
        ContentValues values = new ContentValues();
        values.put(BudgetSQLHelper.COLUMN_SURPLUS, surplus);

        String[] sId = { "" + id };
        String where = BudgetSQLHelper.COLUMN_ID + "=?";
        mDatabase.update(BudgetSQLHelper.TABLE_BUDGETS, values, where, sId);
    }

    public void setIterateOn(long id, int iterateOn) {
        ContentValues values = new ContentValues();
        values.put(BudgetSQLHelper.COLUMN_ITERATE_ON, iterateOn);

        String[] sId = { "" + id };
        String where = BudgetSQLHelper.COLUMN_ID + "=?";
        mDatabase.update(BudgetSQLHelper.TABLE_BUDGETS, values, where, sId);
    }

    public void setIntervalType(long id, int intervalType) {
        ContentValues values = new ContentValues();
        values.put(BudgetSQLHelper.COLUMN_BUDGET_ITERATION_TYPE, intervalType);

        String[] sId = { "" + id };
        String where = BudgetSQLHelper.COLUMN_ID + "=?";
        mDatabase.update(BudgetSQLHelper.TABLE_BUDGETS, values, where, sId);
    }

    public void setLastInterval(long id, int interval) {
        ContentValues values = new ContentValues();
        values.put(BudgetSQLHelper.COLUMN_LAST_INTERVAL, interval);

        String[] sId = { "" + id };
        String where = BudgetSQLHelper.COLUMN_ID + "=?";
        mDatabase.update(BudgetSQLHelper.TABLE_BUDGETS, values, where, sId);
    }

    public void setBudgetName(long id, String name) {
        ContentValues values = new ContentValues();
        values.put(BudgetSQLHelper.COLUMN_BUDGET_NAME, name);

        String[] sId = { "" + id };
        String where = BudgetSQLHelper.COLUMN_ID + "=?";
        mDatabase.update(BudgetSQLHelper.TABLE_BUDGETS, values, where, sId);
    }

    public Budget getBudgetFromId(long id) {
        Cursor c = mDatabase.query(BudgetSQLHelper.TABLE_BUDGETS,
                allColumns, BudgetSQLHelper.COLUMN_ID + "=" + id, null, null, null, null);
        c.moveToFirst();
        return new Budget(c);
    }

    public int getCurrentBudgetFromId(long id) {
        Cursor c = mDatabase.rawQuery("SELECT " + BudgetSQLHelper.COLUMN_CURRENT_BUDGET_VALUE
                + " FROM " + BudgetSQLHelper.TABLE_BUDGETS + " WHERE _id = '" + id + "'", null);
        c.moveToFirst();
        return c.getInt(0);
    }

    public void deleteBudget(long id) {
        mDatabase.delete(BudgetSQLHelper.TABLE_BUDGETS, BudgetSQLHelper.COLUMN_ID
                + " = " + id, null);
    }

	public void updateLastInterval(long id, int currentInterval) {
		ContentValues values = new ContentValues();
		values.put(BudgetSQLHelper.COLUMN_LAST_INTERVAL, currentInterval);

		String[] sId = { "" + id };
		String where = BudgetSQLHelper.COLUMN_ID + "=?";

		mDatabase.update(BudgetSQLHelper.TABLE_BUDGETS, values, where, sId);
	}

	public String[] getAllBudgetTitles() {
		List<Budget> list = getAllBudgets();
		Iterator<Budget> i = list.iterator();
		String[] array = new String[list.size()];
		int j = 0;
		while (i.hasNext()) {
			array[j] = i.next().toString();
			j++;
		}
		return array;
	}

	public List<Budget> getAllBudgets() {
		List<Budget> budgets = new ArrayList<Budget>();
		Cursor cursor = mDatabase.query(BudgetSQLHelper.TABLE_BUDGETS,
				allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while(!cursor.isAfterLast()) {
			Budget budget = new Budget(cursor);
			budgets.add(budget);
			cursor.moveToNext();
		}
		
		cursor.close();
		return budgets;
	}

}
