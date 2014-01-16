package me.williamhester.Quantum;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class TransactionDataSource {

	private Context mContext;
	private SQLiteDatabase mDatabase;
	private TransactionSQLHelper mHelper;
	private String[] allColumns = new String[12];
	private String mDatabaseName;

	public TransactionDataSource(Context context, long id) {
		mContext = context;
        mDatabaseName = "history_" + id;

		mHelper = new TransactionSQLHelper(context, mDatabaseName);

		allColumns[0] = TransactionSQLHelper.COLUMN_ID;
		allColumns[1] = TransactionSQLHelper.COLUMN_VALUE;
		allColumns[2] = TransactionSQLHelper.COLUMN_MONTH;
        allColumns[3] = TransactionSQLHelper.COLUMN_DAY;
        allColumns[4] = TransactionSQLHelper.COLUMN_YEAR;
        allColumns[5] = TransactionSQLHelper.COLUMN_WEEK_OF_YEAR;
        allColumns[6] = TransactionSQLHelper.COLUMN_HOUR;
        allColumns[7] = TransactionSQLHelper.COLUMN_MINUTE;
        allColumns[8] = TransactionSQLHelper.COLUMN_DAY_OF_YEAR;
        allColumns[9] = TransactionSQLHelper.COLUMN_TYPE;
        allColumns[10] = TransactionSQLHelper.COLUMN_LOCATION_NAME;
        allColumns[11] = TransactionSQLHelper.COLUMN_MEMO;

	}

	public void open() throws SQLException {
		mDatabase = mHelper.getWritableDatabase();
	}

	public void close() {
		mHelper.close();
	}

	public void delete() {
		mContext.deleteDatabase(mDatabaseName);
	}

	public Transaction createTransaction(Transaction t) {
		ContentValues values = new ContentValues();
		values.put(TransactionSQLHelper.COLUMN_VALUE, t.getDollars());
		values.put(TransactionSQLHelper.COLUMN_MONTH, t.getMonth());
		values.put(TransactionSQLHelper.COLUMN_DAY, t.getDayOfMonth());
        values.put(TransactionSQLHelper.COLUMN_YEAR, t.getYear());
        values.put(TransactionSQLHelper.COLUMN_WEEK_OF_YEAR, t.getWeekOfYear());
        values.put(TransactionSQLHelper.COLUMN_HOUR, t.getHour());
        values.put(TransactionSQLHelper.COLUMN_MINUTE, t.getMinutes());
        values.put(TransactionSQLHelper.COLUMN_DAY_OF_YEAR, t.getDayOfYear());
        values.put(TransactionSQLHelper.COLUMN_TYPE, t.getType());
        values.put(TransactionSQLHelper.COLUMN_LOCATION_NAME, t.getLocationName());
        values.put(TransactionSQLHelper.COLUMN_MEMO, t.getMemo());

		long insertId = mDatabase.insert(mDatabaseName, null, values);

		Cursor cursor = mDatabase.query(mDatabaseName,
				allColumns, BudgetSQLHelper.COLUMN_ID + " = " + insertId,
				null, null, null, null);
		cursor.moveToFirst();
	    Transaction newTransaction = new Transaction(cursor);
	    cursor.close();
		return newTransaction;
	}

	public void deleteTransaction(Transaction t) {
		mDatabase.delete(mDatabaseName, TransactionSQLHelper.COLUMN_ID + " = " + t.getId(), null);
	}

    public void editTransactionValue(int value, long id) {
        ContentValues values = new ContentValues();
        values.put(TransactionSQLHelper.COLUMN_VALUE, value);

        String[] sId = { "" + id };
        String where = TransactionSQLHelper.COLUMN_ID + "=?";
        mDatabase.update(mDatabaseName, values, where, sId);
    }

    public void editTransactionLocation(String location, long id) {
        ContentValues values = new ContentValues();
        values.put(TransactionSQLHelper.COLUMN_LOCATION_NAME, location);

        String[] sId = { "" + id };
        String where = TransactionSQLHelper.COLUMN_ID + "=?";
        mDatabase.update(mDatabaseName, values, where, sId);
    }

    public void editTransactionMemo(String memo, long id) {
        ContentValues values = new ContentValues();
        values.put(TransactionSQLHelper.COLUMN_MEMO, memo);

        String[] sId = { "" + id };
        String where = TransactionSQLHelper.COLUMN_ID + "=?";
        mDatabase.update(mDatabaseName, values, where, sId);
    }
	
	public List<Transaction> getAllTransactionsReverse() {
		List<Transaction> transactions = new ArrayList<Transaction>();
		Cursor cursor = mDatabase.query(mHelper.getTableName(), 
				allColumns, null, null, null, null, null);
		
		cursor.moveToLast();
		while(!cursor.isBeforeFirst()) {
			Transaction transaction = new Transaction(cursor);
			transactions.add(transaction);
			cursor.moveToPrevious();
		}
		
		cursor.close();
		return transactions;
	}

    public Transaction getTransaction(long id) {
        Cursor cursor = mDatabase.query(mHelper.getTableName(),
                allColumns, "_id=" + id, null, null, null, null);
        cursor.moveToFirst();
        return new Transaction(cursor);
    }
}
