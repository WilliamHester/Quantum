package me.williamhester.Quantum.ui.dialogs;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import me.williamhester.Quantum.ui.dialogs.MoneyPickerDialogFragment.MoneyPickerDialogHandler;
import me.williamhester.Quantum.R;
import me.williamhester.Quantum.datasources.BudgetDataSource;
import me.williamhester.Quantum.datasources.TransactionDataSource;

/**
 * Created by William on 7/1/13.
 */
public class TransactionEditorDialogFragment extends DialogFragment {

    public static final String BUDGET_ID = "budget_id";
    public static final String TRANSACTION_ID = "transaction_id";
    public static final String TRANSACTION_VALUE = "transaction_value";
    public static final String POSITION = "position";

    private final TransactionEditorDialogFragment mThis = this;
    private long mBudgetId, mTransactionId;
    private int mTransactionValue, mPosition;
    private TransactionEditorListener mCallback;

    public TransactionEditorDialogFragment(TransactionEditorListener callback) {
        mCallback = callback;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(STYLE_NORMAL, android.R.style.Theme_Holo_Light_Dialog);

        Bundle args = getArguments();
        mBudgetId = args.getLong(BUDGET_ID);
        mTransactionId = args.getLong(TRANSACTION_ID);
        mTransactionValue = args.getInt(TRANSACTION_VALUE);
        mPosition = args.getInt(POSITION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        getDialog().setTitle(R.string.edit_transaction);

        View v = inflater.inflate(R.layout.transaction_editor_dialog, null);
        Button done = (Button) v.findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dismiss();
            }
        });
        Button cancel = (Button) v.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dismiss();
            }
        });

        Button modifyTransaction = (Button) v.findViewById(R.id.modify_transaction_amount);
        modifyTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                MoneyPickerDialogFragment moneyPicker = new MoneyPickerDialogFragment(true, mEditCallback);
                moneyPicker.setTargetFragment(mThis, 0);
                moneyPicker.show(fm, "fragment_money_picker");
            }
        });

        Button delete = (Button) v.findViewById(R.id.delete_transaction);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onDeleteTransaction(mPosition);
                dismiss();
            }
        });
        return v;
    }

    public MoneyPickerDialogHandler mEditCallback = new MoneyPickerDialogHandler() {
        @Override
        public void onValueSet(int value) {
            TransactionDataSource data = new TransactionDataSource(getActivity(), mBudgetId);
            data.open();
//            data.editTransactionValue(mTransactionValue, value * posNeg, mTransactionId);
            data.close();

            BudgetDataSource dataSource = new BudgetDataSource(getActivity());
            dataSource.open();
            dataSource.setCurrentBudget(mBudgetId, dataSource.getCurrentBudgetFromId(mBudgetId)
                    - value);
            dataSource.close();
        }
    };

    public interface TransactionEditorListener {
        public void onDeleteTransaction(int position);
    }

}
