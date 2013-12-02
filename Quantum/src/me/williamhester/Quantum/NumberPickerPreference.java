package me.williamhester.Quantum;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.NumberPicker;

public class NumberPickerPreference extends DialogPreference {

	private NumberPicker numberPicker;
	private Context mContext;
	private final int MAX_PICKER_NUMBER = 9999;
	private final int MIN_PICKER_NUMBER = 0;
	private int mSetNumber;
	
	public NumberPickerPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;

		setDialogLayoutResource(R.layout.number_picker_preference);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);
        
        SharedPreferences prefs = PreferenceManager
                 .getDefaultSharedPreferences(mContext);
        mSetNumber = 50000 / 100;
        
        setDialogIcon(null);
	}
	
	@Override
	public void onBindDialogView(View view) {
        super.onBindDialogView(view);
		
        numberPicker = (NumberPicker) view.findViewById(R.id.number_picker);
        
        numberPicker.setMinValue(MIN_PICKER_NUMBER);
        numberPicker.setMaxValue(MAX_PICKER_NUMBER);
        numberPicker.setValue(mSetNumber);
        numberPicker.setOnLongPressUpdateInterval(100);
        numberPicker.setWrapSelectorWheel(false);
	}
	
	@Override
	public void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);
		if (positiveResult) {
			numberPicker.clearFocus();
			mSetNumber = numberPicker.getValue();
			
			SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(mContext);
            SharedPreferences.Editor settings = prefs.edit();
            settings.commit();
		}
	}

}
