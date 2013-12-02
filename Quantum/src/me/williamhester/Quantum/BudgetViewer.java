package me.williamhester.Quantum;

public class BudgetViewer {

	public final static int RED = -1, NORMAL = 1;

	private String mCents;
	private Budget mBudget;

	public BudgetViewer(Budget budget) {
		mBudget = budget;
		processBudgetViewer();
	}
	
	public void processBudgetViewer() {
		int currentBudget = mBudget.getCurrentBudget();
		String stringBudget = "" + currentBudget;
		try{
			mCents = "." + stringBudget.substring(stringBudget.length() - 2);
		}
		catch (StringIndexOutOfBoundsException e) {
			if (stringBudget.length() == 1) {
				mCents = ".0" + stringBudget;
			}
			else {
				mCents = ".00";
			}
		}
		mCents = mCents.replace('-', '0');
	}
	
	public int getColor() {
		return mBudget.getCurrentBudget() >= 0 ? NORMAL : RED;
	}
	
	public String getDollarsString() {
		int dollars = mBudget.getCurrentBudget();
		if (mBudget.getCurrentBudget() < 0) {
			return "" + mBudget.getCurrentBudget() * -1 / 100;
		}
		return dollars / 100 + "";
	}
	
	public String getCentsString() {
		return mCents;
	}

    public float calculateOpacity() {
        float ratio = (float) mBudget.getCurrentBudget()
                / (float) mBudget.getBudget();
        if (ratio > 1) {
            return 1;
        } else if (ratio < 0) {
            return 0;
        } return ratio;
    }
	
}
