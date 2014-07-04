package me.williamhester.Quantum.ui.views;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import me.williamhester.Quantum.R;

public class MoneyPickerView extends LinearLayout implements Button.OnClickListener,
	Button.OnLongClickListener {

    protected final int POSITIVE = 1;
    protected final int NEGATIVE = -1;
    protected final int DEFAULT_TEXT_SIZE = 48;

	protected int mInputSize = 6;
    protected int mTextSize;
	
	protected int mAddSub = POSITIVE;
	protected int mInputPointer = -1;
	protected int[] mInput = new int[mInputSize];
	protected Context mContext;
	protected Button[] mNumbers = new Button[10];
	protected Button mLeft, mRight;
	protected ImageButton mDelete;
	protected MoneyView mEnteredMoney;
	
    public MoneyPickerView(Context context) {
    	this(context, null);
    }

    public MoneyPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        if (attrs != null) {
            String packageName = "http://schemas.android.com/apk/res-auto";
            mTextSize = attrs.getAttributeIntValue(packageName, "textSize", DEFAULT_TEXT_SIZE);
        } else {
            mTextSize = DEFAULT_TEXT_SIZE;
        }
        LayoutInflater layoutInflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(getLayoutId(), this);
    }

    protected int getLayoutId() {
        return R.layout.money_view_layout;
    }
	
	@Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        View v1 = findViewById(R.id.first);
        View v2 = findViewById(R.id.second);
        View v3 = findViewById(R.id.third);
        View v4 = findViewById(R.id.fourth);
        mEnteredMoney = (MoneyView)findViewById(R.id.money_text);
        mEnteredMoney.setTextSize(mTextSize);
        mDelete = (ImageButton)findViewById(R.id.delete);
        mDelete.requestLayout();
        mDelete.setOnClickListener(this);
        mDelete.setOnLongClickListener(this);
        
        mNumbers[1] = (Button)v1.findViewById(R.id.key_left);
        mNumbers[2] = (Button)v1.findViewById(R.id.key_middle);
        mNumbers[3] = (Button)v1.findViewById(R.id.key_right);

        mNumbers[4] = (Button)v2.findViewById(R.id.key_left);
        mNumbers[5] = (Button)v2.findViewById(R.id.key_middle);
        mNumbers[6] = (Button)v2.findViewById(R.id.key_right);

        mNumbers[7] = (Button)v3.findViewById(R.id.key_left);
        mNumbers[8] = (Button)v3.findViewById(R.id.key_middle);
        mNumbers[9] = (Button)v3.findViewById(R.id.key_right);

        mLeft = (Button)v4.findViewById(R.id.key_left);
        mNumbers[0] = (Button)v4.findViewById(R.id.key_middle);
        mRight = (Button)v4.findViewById(R.id.key_right);
        
        mLeft.setOnClickListener(this);
        mRight.setOnClickListener(this);

        Typeface slabThin = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/RobotoSlab-Light.ttf");

        for (int i = 0; i < 10; i++) {
            mNumbers[i].setOnClickListener(this);
            mNumbers[i].setText(String.format("%d",i));
            mNumbers[i].setTag(R.id.numbers_key, Integer.valueOf(i));
            mNumbers[i].setTextColor(getResources().getColor(R.color.dark_gray));
            mNumbers[i].setTypeface(slabThin);
            mNumbers[i].setTextSize(mTextSize);
        }

		mLeft.setText(R.string.add);
		mLeft.setTextSize(15);
        mLeft.setTextColor(getResources().getColor(R.color.dark_gray));
        mLeft.setTypeface(slabThin);
		mRight.setText(R.string.subtract);
		mRight.setTextSize(15);
        mRight.setTextColor(getResources().getColor(R.color.dark_gray));
        mRight.setTypeface(slabThin);
		
		updateDeleteButton();
		updateAddSub();
	}
	
	public boolean onLongClick(View v) {
        if (v == mDelete) {
        	mAddSub = POSITIVE;
            updateAddSub();
            updateNumKeys();
            reset();
            updateDeleteButton();
            restoreNumKeys();
        	return true;
        }
        return false;
    }

	public void onClick(View v) {
		doOnClick(v);
		updateAddSub();
		updateNumKeys();
	}
	

	protected void doOnClick(View v) {
		
		Integer val = (Integer)v.getTag(R.id.numbers_key);
		
		if (val != null) {
			if (mInputPointer == -1 && val == 0) {
				return;
			}
			else if (mInputPointer < mInputSize - 1) {
				for (int i = mInputPointer; i >= 0; i--) {
					mInput[i + 1] = mInput[i];
				}
				mInputPointer++;
				if (mInputPointer == 4) {
					hideNumKeys();
				}
				mInput[0] = val;
				updateView();
				updateDeleteButton();
			}
			return;
		}
		else if (v == mDelete) {
			for (int i = 0; i < mInputPointer; i++) {
				mInput[i] = mInput[i + 1];
			}
			mInput[mInputPointer] = 0;
			mInputPointer--;
			updateView();
			updateDeleteButton();
			if (mInputPointer == 3) {
				restoreNumKeys();
			}
		}
		else if (v == mLeft) {
			mAddSub = NEGATIVE;
			updateAddSub();
		}
		else if (v == mRight) {
			mAddSub = POSITIVE;
			updateAddSub();
		}
		updateDeleteButton();
	}
	
	protected void reset() {
		for (int i = 0; i < mInputSize; i++) {
			mInput[i] = 0;
		}
		mInputPointer = -1;
		updateView();
	}
	
	public int getValue() {
		return (mInput[5] * 100000 + mInput[4] * 10000 + mInput[3] * 1000
				+ mInput[2] * 100 + mInput[1] * 10 + mInput[0]) * mAddSub;
	}
	
	protected void updateAddSub() {
		mLeft.setEnabled(mAddSub == POSITIVE);
		mRight.setEnabled(mAddSub == NEGATIVE);
        if (mAddSub == POSITIVE) {
            mLeft.setTextColor(getResources().getColor(R.color.dark_gray));
            mRight.setTextColor(getResources().getColor(R.color.mid_gray));
        } else {
            mRight.setTextColor(getResources().getColor(R.color.dark_gray));
            mLeft.setTextColor(getResources().getColor(R.color.mid_gray));
        }
	}	
	
	protected void updateDeleteButton() {
		if (mDelete != null) {
			mDelete.setEnabled(mInputPointer != -1);
		}
	}
	
	protected void updateNumKeys() {
		for (int i = 0; i < 10; i++) {
			mNumbers[i].setEnabled(mInputPointer != mInput.length);
		}
	}
	
	public void updateView() {
		mEnteredMoney.setMoney(mInput[5], mInput[4], mInput[3], mInput[2], mInput[1], mInput[0]);
	}
	
	private void hideNumKeys() {
		for (int i = 0; i < mNumbers.length; i++) {
			mNumbers[i].setEnabled(false);
		}
	}

    public void hideAddSubKeys() {
        mLeft.setVisibility(View.INVISIBLE);
        mRight.setVisibility(View.INVISIBLE);
    }
	
	private void restoreNumKeys() {
		for (int i = 0; i < mNumbers.length; i++) {
			mNumbers[i].setEnabled(true);
		}
	}

    public void setValue(int value) {
        if (value < 0) {
            mAddSub = NEGATIVE;
            updateAddSub();
        }
        if (value != 0) {
            value = Math.abs(value);
            int size = ((int) Math.log10(value)) + 1;
            int constructor = 0;
            mInputPointer = size - 1;
            for (int i = size - 1; i >= 0; i--) {
                mInput[i] = value / e(i) - constructor;
                constructor *= 10;
                constructor += mInput[i] * 10;
            }
            updateDeleteButton();
        }
    }

    private int e(int power) {
        int number = 1;
        for (int i = 0; i < power; i++) {
            number *= 10;
        }
        return number;
    }
	
	public void saveEntryState(Bundle outState, String key) {
        outState.putIntArray(key, mInput);
    }
	
	public void restoreEntryState(Bundle inState, String key) {
        int[] input = inState.getIntArray(key);
        if (input != null && mInputSize == input.length) {
            for (int i = 0; i < mInputSize; i++) {
                mInput[i] = input[i];
                if (mInput[i] != 0) {
                    mInputPointer = i;
                }
            }
            updateView();
        }
    }
}
