package me.williamhester.Quantum.models;

import android.database.Cursor;

import java.util.Calendar;

public class Transaction {
	
	private int mDayOfMonth, mMonth, mYear, mWeekOfYear, mHour, mMinutes, mValue, mDayOfYear;
    private String mType, mLocationName, mMemo;
    private long mId;

    public Transaction(int dollars, String locationName) {
        this(dollars, locationName, null, null);
    }

    public Transaction(int dollars, String locationName, String memo, String type) {
        mValue = dollars;

        Calendar c = Calendar.getInstance();
        mDayOfMonth = c.get(Calendar.DAY_OF_MONTH);
        mMonth = c.get(Calendar.MONTH);
        mYear = c.get(Calendar.YEAR);
        mWeekOfYear = c.get(Calendar.WEEK_OF_YEAR);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinutes = c.get(Calendar.MINUTE);
        mDayOfYear = c.get(Calendar.DAY_OF_YEAR);
        mType = type;
        mLocationName = locationName == null ? "NULL" : locationName;
        mMemo = memo;
    }
	
	public Transaction(Cursor cursor) {
		mId = cursor.getLong(0);
		mValue = cursor.getInt(1);
		mMonth = cursor.getInt(2);
		mDayOfMonth = cursor.getInt(3);
        mYear = cursor.getInt(4);
        mWeekOfYear = cursor.getInt(5);
        mHour = cursor.getInt(6);
        mMinutes = cursor.getInt(7);
        mDayOfYear = cursor.getInt(8);
        mType = cursor.getString(9);
        mLocationName = cursor.getString(10);
        mMemo = cursor.getString(11);
	}
	
	public long getId() {
		return mId;
	}
	
	public int getValue() {
		return mValue;
	}

    public int getMonth() {
        return mMonth;
    }

    public int getDayOfMonth() {
        return mDayOfMonth;
    }

    public int getYear() {
        return mYear;
    }

    public int getWeekOfYear() {
        return mWeekOfYear;
    }

    public int getHour() {
        return mHour;
    }

    public int getMinutes() {
        return mMinutes;
    }

    public int getDayOfYear() {
        return mDayOfYear;
    }

    public String getType() {
        return mType;
    }

    public String getLocationName() {
        return mLocationName;
    }

    public String getMemo() {
        return mMemo;
    }

	public String getDateString() {
		return (mMonth + 1) + "/" + mDayOfMonth + "/" + mYear;
	}
	
	public String getTimeString() {
        if (mHour == 0)
            return 12 + ":" + minutesString();
		return (mHour <= 12 ? mHour : (mHour - 12)) + ":" + minutesString();
	}
	
	private String minutesString() {
		return (mMinutes > 9 ? mMinutes + "" : "0" + mMinutes) + (mHour >= 12 ? "pm" : "am");
	}
	
	public String getTopLineText() {
		return "" + ((double) mValue) / 100.0;
	}
	
	public String getBottomLineText() {
		return getDateString() + " at " + getTimeString();
	}

    public void setId(long id) {
        mId = id;
    }

    public void setValue(int value) {
        mValue = value;
    }

    public void setLocation(String location) {
        mLocationName = location;
    }

    public void setMemo(String memo) {
        mMemo = memo;
    }

    @Override
    public boolean equals(Object o) {
        return ((Transaction) o).mId == mId;
    }
}
