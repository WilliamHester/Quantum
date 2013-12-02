package me.williamhester.Quantum;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MoneyView extends LinearLayout {
	
	private Context mContext;
    private TextView mThousands, mHundreds, mTens, mOnes, mTenths, mHundredths;

    public MoneyView(Context context) {
        this(context, null);
    }

    public MoneyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }
    
    @Override
    public void onFinishInflate() {
    	super.onFinishInflate();
        Typeface slabLight = Typeface.createFromAsset(mContext.getAssets(), "fonts/RobotoSlab-Light.ttf");
    	
    	mThousands = (TextView)findViewById(R.id.thousands);
        mThousands.setTypeface(slabLight);
    	mHundreds = (TextView)findViewById(R.id.hundreds);
        mHundreds.setTypeface(slabLight);
    	mTens = (TextView)findViewById(R.id.tens);
        mTens.setTypeface(slabLight);
    	mOnes = (TextView)findViewById(R.id.ones);
        mOnes.setTypeface(slabLight);
        TextView decimal = (TextView) findViewById(R.id.decimal);
        decimal.setTypeface(slabLight);
    	mTenths = (TextView)findViewById(R.id.tenths);
        mTenths.setTypeface(slabLight);
    	mHundredths = (TextView)findViewById(R.id.hundredths);
        mHundredths.setTypeface(slabLight);
    	
    	setMoney(1, 1, 1, 0, 0, 0);
    	setMoney(0, 0, 0, 0, 0, 0);
    }
    
    public void setMoney(int thousands, int hundreds, int tens, int ones, int tenths, int hundredths) {
    	if (thousands == 0) {
    		mThousands.setVisibility(View.INVISIBLE);
    	} else {
    		mThousands.setText(String.format("%d", thousands));
    		mThousands.setVisibility(View.VISIBLE);
    	}
    	
    	if (hundreds == 0 && thousands == 0) {
    		mHundreds.setVisibility(View.INVISIBLE);
    	} else {
    		mHundreds.setText(String.format("%d", hundreds));
    		mHundreds.setVisibility(View.VISIBLE);
    	}
    	
    	if (tens == 0 && hundreds == 0 && thousands == 0) {
    		mTens.setVisibility(View.INVISIBLE);
     	} else {
     		mTens.setVisibility(View.VISIBLE);
     		mTens.setText(String.format("%d", tens));
     	}
    	
    	mOnes.setText(String.format("%d", ones));
    	
    	mTenths.setText(String.format("%d", tenths));
    	
    	mHundredths.setText(String.format("%d", hundredths));
    }

}
