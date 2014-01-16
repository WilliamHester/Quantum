package me.williamhester.Quantum;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by William Hester on 9/2/13.
 */
public class EditTextDialog extends DialogFragment {

    private int mHint;

    private EditTextListener mCallback;
    private String mPreviousValue;

    /**
     * Creates a new EditTextDialog.
     *
     * @param previousValue the String value that should be put in place of the EditText view
     *                      use null if it should be blank
     * @param hintId the id of the hint that should be used in the EditText
     * @param callback the EditTextListener that will return the String that was created
     */
    public EditTextDialog(String previousValue, int hintId, EditTextListener callback) {
        if (previousValue == null)
            mPreviousValue = "";
        else
            mPreviousValue = previousValue;
        mHint = hintId;
        mCallback = callback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle(R.string.pref_change_name);

        View v = inflater.inflate(R.layout.edit_text_dialog, null);
        Button confirm = (Button) v.findViewById(R.id.confirm);
        Button cancel = (Button) v.findViewById(R.id.cancel);
        final EditText text = (EditText) v.findViewById(R.id.editor);
        text.setHint(mHint);
        text.setText(mPreviousValue);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onTextEdited(text.getText().toString());
                dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return v;
    }

    public interface EditTextListener {
        public void onTextEdited(String text);
    }
}
