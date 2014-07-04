package me.williamhester.Quantum.ui.dialogs;


import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;

import me.williamhester.Quantum.R;

public class NumberPickerDialog extends DialogFragment implements DialogInterface.OnClickListener {
	
	private NumberPicker numberPicker;
	private final OnNumbersSetListener mCallBack;
    private int mTitle;
    private int mMinimum, mMaximum, mCurrent;
	
	// Constants
	public static final int MAX_PICKER_NUMBER = 9999;
	public static final int MIN_PICKER_NUMBER = 0;
	public static final int DEFAULT_PICKER_NUMBER = 0;
	
	public interface OnNumbersSetListener {

        /**
         * @param number The number that was set.
         */
        void onNumbersSet(int number);
    }
	
	public NumberPickerDialog(Context context,
			int theme,
			OnNumbersSetListener callBack,
			int title,
            int min,
            int max,
            int current) {
        mCallBack = callBack;
        mTitle = title;
        mMinimum = min;
        mMaximum = max;
        mCurrent = current;
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle(mTitle);

        View v = inflater.inflate(R.layout.number_picker_dialog, null);

        // The number picker that selects the dollars
        numberPicker = (NumberPicker) v.findViewById(R.id.number_picker);
        // initialize state
        numberPicker.setMinValue(mMinimum);
        numberPicker.setMaxValue(mMaximum);
        numberPicker.setValue(mCurrent);
        numberPicker.setOnLongPressUpdateInterval(100);
        numberPicker.setWrapSelectorWheel(true);

        Button cancel = (Button) v.findViewById(R.id.cancel);
        Button save = (Button) v.findViewById(R.id.save);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCallBack != null) {
                    numberPicker.clearFocus();
                    mCallBack.onNumbersSet(numberPicker.getValue());
                    dismiss();
                }
            }
        });
        return v;
    }
		
    public void onClick(DialogInterface dialog, int which) {
        if (mCallBack != null) {
            numberPicker.clearFocus();
            mCallBack.onNumbersSet(numberPicker.getValue());
            dialog.dismiss();
        }
    }

}
