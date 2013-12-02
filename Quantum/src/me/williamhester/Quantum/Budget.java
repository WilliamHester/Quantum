package me.williamhester.Quantum;

import android.database.Cursor;


public class Budget {

    public static final int NEVER = -1;
    public static final int WEEKLY = 0;
    public static final int MONTHLY = 1;
    public static final int YEARLY = 2;

    private int mBudget, mIntervalType, mCurrentBudget, mSurplus,
        mLastInterval, mIterateOn;
	private long mId;
	private String mName;

	public Budget(Cursor cursor) {
        mId = cursor.getLong(0);
        mName = cursor.getString(1);
        mBudget = cursor.getInt(2);
        mCurrentBudget = cursor.getInt(3);
        mSurplus = cursor.getInt(4);
        mIntervalType = cursor.getInt(5);
        mLastInterval = cursor.getInt(6);
        mIterateOn = cursor.getInt(7);
	}

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }
	
	public int getIterationType() {
		return mIntervalType;
	}
	
	public void setLastInterval(int lastInterval) {
		mLastInterval = lastInterval;
	}
	
	public void setCurrentBudget(int budgetIn) {
		mCurrentBudget = budgetIn;
	}
	
	public void setBudget(int budgetIn) {
		mBudget = budgetIn;
	}
	
	public int getBudget() {
		return mBudget;
	}
	
	public long getId() {
		return mId;
	}
	
	public int getSurplus() {
		return mSurplus;
	}
	
	public void setSurplus(int surplus) {
		mSurplus = surplus;
	}
	
	public void budgetEdit(double changedBy) {
		mCurrentBudget -= changedBy;
	}
	
	public int getCurrentBudget() {
		return mCurrentBudget;
	}

    public int getIterateOn() {
        return mIterateOn;
    }

    public void setIterateOn(int iterateOn) {
        mIterateOn = iterateOn;
    }

    public void setIntervalType(int type) {
        mIntervalType = type;
    }

    public int getLastInterval() {
        return mLastInterval;
    }

    /**
     * Checks to see if it is the next interval of time.
     *
     * @return returns true if it is the next interval
     * or false if it is still within the last
     */

    public boolean isNextInterval() {
        Date today = Date.today();
        switch(mIntervalType) {
            case WEEKLY:
                return (getCurrentInterval(WEEKLY) > mLastInterval && today.getDayOfWeek()
                        >= mIterateOn) || getCurrentInterval(WEEKLY) > mLastInterval + 1;
            case MONTHLY:
                return (getCurrentInterval(MONTHLY) > mLastInterval && today.getDayOfMonth()
                        >= mIterateOn) || getCurrentInterval(MONTHLY) > mLastInterval + 1;
            case YEARLY:
                return (getCurrentInterval(YEARLY) > mLastInterval && today.getMonth() * 100 + today.getDayOfMonth()
                        >= mIterateOn) || getCurrentInterval(YEARLY) > mLastInterval + 1;
            default:
                return false;
        }
    }

    public static int getCurrentInterval(int interval) {
        Date today = Date.today();
        switch (interval) {
            case WEEKLY:
                return today.getYear() * 100 + today.getWeekOfYear();
            case MONTHLY:
                return today.getYear() * 100 + today.getMonth();
            case YEARLY:
                return today.getYear();
            default:
                return 0;
        }
    }

    public int getCurrentInterval() {
        Date today = Date.today();
        switch (mIntervalType) {
            case WEEKLY:
                return today.getYear() * 100 + today.getWeekOfYear();
            case MONTHLY:
                return today.getYear() * 100 + today.getMonth();
            case YEARLY:
                return today.getYear();
            default:
                return 0;
        }
    }

    public void rollover(BudgetDataSource data) {
        mSurplus += mCurrentBudget;
        mCurrentBudget = mBudget;
        Date today = Date.today();

        if (mIntervalType == WEEKLY) {
            mLastInterval = today.getYear() * 100 + today.getWeekOfYear();
        } else if (mIntervalType == MONTHLY) {
            mLastInterval = today.getYear() * 100 + today.getMonth();
        } else if (mIntervalType == YEARLY) {
            mLastInterval = today.getYear();
        }

        data.updateLastInterval(mId, mLastInterval);
        data.setSurplus(mId, mSurplus);
        data.setCurrentBudget(mId, mCurrentBudget);
    }

    public String toString() {
        return mName;
    }
	
}
