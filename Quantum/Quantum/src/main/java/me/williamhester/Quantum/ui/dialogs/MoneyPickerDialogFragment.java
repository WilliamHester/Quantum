package me.williamhester.Quantum.ui.dialogs;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import me.williamhester.Quantum.ui.views.MoneyPickerView;
import me.williamhester.Quantum.R;

@SuppressLint("Override")
public class MoneyPickerDialogFragment extends DialogFragment {

	private MoneyPickerDialogHandler mCallback;
    private boolean mShowAddSub;

    public MoneyPickerDialogFragment() { }

    public MoneyPickerDialogFragment(boolean showAddSub, MoneyPickerDialogHandler callback) {
        mShowAddSub = showAddSub;
        mCallback = callback;
    }

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View v = inflater.inflate(R.layout.money_picker_dialog, null);
		Button confirm = (Button) v.findViewById(R.id.confirm);
		Button cancel = (Button) v.findViewById(R.id.cancel);
		cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dismiss();
            }
        });
		final MoneyPickerView picker = (MoneyPickerView) v.findViewById(R.id.money_picker_view);
        if (!mShowAddSub) {
            picker.hideAddSubKeys();
        }
		confirm.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                mCallback.onValueSet(picker.getValue());
                dismiss();
            }
        });
		return v;
	}
	
	interface MoneyPickerDialogHandler {
        void onValueSet(int value);
    }
}
