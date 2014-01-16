package me.williamhester.Quantum;

import android.app.DialogFragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by William on 12/2/13.
 */
public class TransactionCreatorDialogFragment extends DialogFragment {

    private long mBudgetId;

    private Button mCancel;
    private Button mDelete;
    private Button mSave;
    private GenericCallback mCallback;
    private MoneyPickerView mMoneyPicker;
    private EditText mLocation;
    private EditText mMemo;
    private Transaction mTransaction;
    private int mInitialDollars;
    private String mInitialMemo;
    private String mInitialLocation;

    /**
     * Creates a new fragment to make a transaction
     *
     * @param budgetId the Id of the buget that the transaction is being created for
     * @param callback the trigger to update the fragment.
     */
    public TransactionCreatorDialogFragment(long budgetId, GenericCallback callback) {
        mCallback = callback;
        mBudgetId = budgetId;
        mInitialDollars = 0;
        mInitialMemo = "";
        mInitialLocation = "";
    }

    public TransactionCreatorDialogFragment(long budgetId, Transaction t,
                                            GenericCallback callback) {
        mCallback = callback;
        mBudgetId = budgetId;
        mTransaction = t;
        mInitialDollars = t.getDollars();
        mInitialMemo = t.getMemo();
        mInitialLocation = t.getLocationName();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog);
        if (mTransaction == null) {
            mTransaction = new Transaction(0, "", "", "");
            // Set the id of the transaction to -1 in the case that we created a new
            // Transaction, so we know that in order to assign the value, we need to write
            // the transaction to a database.
            mTransaction.setId(-1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Typeface slabReg = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/RobotoSlab-Regular.ttf");

        View v = inflater.inflate(R.layout.transaction_creator_dialog_fragment, null);
        mLocation = (EditText) v.findViewById(R.id.location);
        mLocation.setTypeface(slabReg);
        mLocation.setMaxLines(1);
        mMemo = (EditText) v.findViewById(R.id.memo);
        mMemo.setTypeface(slabReg);
        mMemo.setMaxLines(2);
        mMoneyPicker = (MoneyPickerView) v.findViewById(R.id.money_picker_view);
        mCancel = (Button) v.findViewById(R.id.cancel);
        mDelete = (Button) v.findViewById(R.id.delete_button);
        mSave = (Button) v.findViewById(R.id.save);
        setUpButtons();

        mLocation.setText(mTransaction.getLocationName());
        mMemo.setText(mTransaction.getMemo());
        mMoneyPicker.setValue(mTransaction.getDollars());
        mMoneyPicker.updateView();

        return v;
    }

    private void setUpButtons() {
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTransaction.getId() != -1) {
                    BudgetDataSource dataSource = new BudgetDataSource(getActivity());
                    dataSource.open();
                    dataSource.setCurrentBudget(mBudgetId,
                            dataSource.getCurrentBudgetFromId(mBudgetId) + mInitialDollars);
                    dataSource.close();
                    TransactionDataSource transData = new TransactionDataSource(getActivity(),
                            mBudgetId);
                    transData.open();
                    transData.deleteTransaction(mTransaction);
                    transData.close();
                }
                mCallback.callback();
                dismiss();
            }
        });
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateBudget();
                saveTransaction();
                mCallback.callback();
                dismiss();
            }
        });
    }

    private boolean saveTransaction() {
        final TransactionDataSource transData = new TransactionDataSource(getActivity(), mBudgetId);
        transData.open();
        if (mTransaction.getId() == -1) {
            if (mMoneyPicker.getValue() != 0) {
                mTransaction = new Transaction(mMoneyPicker.getValue(),
                        mLocation.getText().toString(), mMemo.getText().toString(), "");
                transData.createTransaction(mTransaction);
                Toast.makeText(getActivity(), "Created Transaction", Toast.LENGTH_LONG);
            }
        } else {
            if (mMoneyPicker.getValue() != mInitialDollars && mMoneyPicker.getValue() != 0) {
                transData.editTransactionValue(mMoneyPicker.getValue(), mTransaction.getId());
            }
            if (!mLocation.getText().toString().equals(mInitialLocation)) {
                transData.editTransactionLocation(mLocation.getText().toString(),
                        mTransaction.getId());
            }
            if (!mMemo.getText().toString().equals(mInitialMemo)) {
                transData.editTransactionMemo(mMemo.getText().toString(), mTransaction.getId());
            }
            if (mMoneyPicker.getValue() == 0) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                builder.setTitle(R.string.are_you_sure);
//                builder.setMessage(R.string.saving_with_a_value_of_0);
//                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//
//                    }
//                });
//                builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
                transData.deleteTransaction(mTransaction);
//                    }
//                });
//                builder.create().show();

            }
        }
        transData.close();
        return false;
    }

    private void updateBudget() {
        if (mMoneyPicker.getValue() != mInitialDollars) {
            BudgetDataSource data = new BudgetDataSource(getActivity());
            data.open();
            data.setCurrentBudget(mBudgetId, data.getCurrentBudgetFromId(mBudgetId)
                    - (mMoneyPicker.getValue() - mInitialDollars));
            data.close();
        }
    }

}
