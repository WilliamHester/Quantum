package me.williamhester.Quantum.ui.fragments;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import me.williamhester.Quantum.datasources.BudgetDataSource;
import me.williamhester.Quantum.ui.views.MoneyPickerView;
import me.williamhester.Quantum.R;
import me.williamhester.Quantum.models.Transaction;
import me.williamhester.Quantum.datasources.TransactionDataSource;

/**
 * Created by William Hester on 9/4/13.
 */
public class TransactionCreatorFragment extends Fragment {

    private long mBudgetId;

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
     */
    public TransactionCreatorFragment(long budgetId) {
        mBudgetId = budgetId;
        mInitialDollars = 0;
        mInitialMemo = "";
        mInitialLocation = "";
    }

    public TransactionCreatorFragment(long budgetId, Transaction t) {
        mBudgetId = budgetId;
        mTransaction = t;
        mInitialDollars = t.getValue();
        mInitialMemo = t.getMemo();
        mInitialLocation = t.getLocationName();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mTransaction == null) {
            mTransaction = new Transaction(0, "", "", "");
            mTransaction.setId(-1);
        }
        try {
            getActivity().getActionBar().setTitle(R.string.new_transaction);
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {
            Log.e("NPE", "Could not find either activity or ActionBar");
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Typeface slabReg = Typeface.createFromAsset(getActivity().getAssets(), "fonts/RobotoSlab-Regular.ttf");

        View v = inflater.inflate(R.layout.transaction_creator_fragment, null);
        mLocation = (EditText) v.findViewById(R.id.location);
        mLocation.setTypeface(slabReg);
        mLocation.setMaxLines(1);
        mMemo = (EditText) v.findViewById(R.id.memo);
        mMemo.setTypeface(slabReg);
        mMemo.setMaxLines(2);
        mMoneyPicker = (MoneyPickerView) v.findViewById(R.id.money_picker_view);

        mLocation.setText(mTransaction.getLocationName());
        mMemo.setText(mTransaction.getMemo());
        mMoneyPicker.setValue(mTransaction.getValue());
        mMoneyPicker.updateView();

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.new_transaction_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_new_transaction:
                saveTransaction();
                updateBudget();
                try {
//                    ((OnFragmentOpenedListener) getActivity()).onFragmentClosed();
                } catch (ClassCastException e) {

                } catch (NullPointerException e) {

                }
                return true;
            case R.id.delete_transaction:
                if (mTransaction.getId() != -1) {
                    BudgetDataSource dataSource = new BudgetDataSource(getActivity());
                    dataSource.open();
                    dataSource.setCurrentBudget(mBudgetId, dataSource.getCurrentBudgetFromId(mBudgetId)
                            + mMoneyPicker.getValue());
                    dataSource.close();
                    TransactionDataSource transData = new TransactionDataSource(getActivity(), mBudgetId);
                    transData.open();
                    transData.deleteTransaction(mTransaction);
                    transData.close();
                    Toast.makeText(getActivity(), "Should exit now?", Toast.LENGTH_LONG);
                }
//                ((OnFragmentOpenedListener) getActivity()).onFragmentClosed();
                return true;
            case android.R.id.home:
                try{
//                    ((OnFragmentOpenedListener) getActivity()).onFragmentClosed();
                } catch (ClassCastException e) {

                } catch (NullPointerException e) {

                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean saveTransaction() {
        final TransactionDataSource transData = new TransactionDataSource(getActivity(), mBudgetId);
        transData.open();
        if (mTransaction.getId() == -1) {
            if (mMoneyPicker.getValue() != 0) {
                mTransaction = new Transaction(mMoneyPicker.getValue(),
                        mLocation.getText().toString(), mMemo.getText().toString(), "");
                transData.createTransaction(mTransaction);
            }
        } else {
            if (mMoneyPicker.getValue() != mInitialDollars && mMoneyPicker.getValue() != 0) {
                transData.editTransactionValue(mMoneyPicker.getValue(), mTransaction.getId());
            }
            if (!mLocation.getText().toString().equals(mInitialLocation)) {
                transData.editTransactionLocation(mLocation.getText().toString(), mTransaction.getId());
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
