package me.williamhester.Quantum.ui.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import me.williamhester.Quantum.datasources.BudgetDataSource;
import me.williamhester.Quantum.BudgetViewer;
import me.williamhester.Quantum.DrawerToggle;
import me.williamhester.Quantum.ui.dialogs.MoneyPickerDialogFragment;
import me.williamhester.Quantum.ui.dialogs.MoneyPickerDialogFragment.MoneyPickerDialogHandler;
import me.williamhester.Quantum.R;
import me.williamhester.Quantum.ui.views.TabView;
import me.williamhester.Quantum.models.Transaction;
import me.williamhester.Quantum.ui.adapters.TransactionArrayAdapter;
import me.williamhester.Quantum.TransactionCreatedListener;
import me.williamhester.Quantum.ui.dialogs.TransactionCreatorDialogFragment;
import me.williamhester.Quantum.datasources.TransactionDataSource;
import me.williamhester.Quantum.models.Budget;
import me.williamhester.Quantum.ui.activities.SettingsContainerActivity;

public class BudgetFragment extends Fragment {

    public final static String BUDGET_POSITION_IN_LIST = "budgetPositionInList";

    private int mBudgetPosition;
    private Budget mBudget;
    private BudgetViewer mBudgetViewer;
    private Context mContext;
    private DrawerToggle mDrawerToggle;
    private List<Transaction> mTransactions;
    private ListView mList;
    private TransactionArrayAdapter mTransactionAdapter;
	private TextView mDollarsView, mCentsView;
    private View mNumbersHeader;
    private View mButtonsHeader;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mDrawerToggle = (DrawerToggle) activity;
        } catch (ClassCastException e) {
            Log.e("ClassCastException", "BudgetFragment's hosting activity does not implement"
                    + " DrawerToggle");
        }
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (savedInstanceState != null) {
            mBudgetPosition = savedInstanceState.getInt(BUDGET_POSITION_IN_LIST, 0);
        } else if (args != null) {
            mBudgetPosition = args.getInt(BUDGET_POSITION_IN_LIST);
        } else {
            mBudgetPosition = 0;
        }

        BudgetDataSource budgetDataSource = new BudgetDataSource(getActivity());
        budgetDataSource.open();
        List<Budget> budgetList = budgetDataSource.getAllBudgets();
        budgetDataSource.close();
        mBudget = budgetList.get(mBudgetPosition);

		TransactionDataSource trans = new TransactionDataSource(getActivity(), mBudget.getId());
		trans.open();
		mTransactions = trans.getAllTransactionsReverse();
		trans.close();

        if (getActivity() != null && getActivity().getActionBar() != null) {
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
            setActionBarTitle(mBudget.toString());
        }

        mBudgetViewer = new BudgetViewer(mBudget);

        mContext = getActivity();

//        setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.budget_fragment_2, container, false);

        mList = (ListView) view.findViewById(R.id.transactions);
        mNumbersHeader = view.findViewById(R.id.budget_amount);
        mButtonsHeader = view.findViewById(R.id.buttons_row);

        mDollarsView = (TextView) mNumbersHeader.findViewById(R.id.dollars_view);
        mCentsView = (TextView) mNumbersHeader.findViewById(R.id.cents_view);
        mTransactionAdapter = new TransactionArrayAdapter(mContext, mTransactions);
        setOnClicks();
        setFonts();
		return view;
	}

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.budget_fragment_menu, menu);
//        super.onCreateOptionsMenu(menu, inflater);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerToggle.toggle();
                return true;
            case R.id.budget_management:
                Bundle b = new Bundle();
                b.putLong(SettingsContainerActivity.BUDGET_ID, mBudget.getId());
                Intent i = new Intent(getActivity(), SettingsContainerActivity.class);
                i.putExtras(b);
                getActivity().startActivity(i);
                return true;
            case R.id.add_new_budget:
                BudgetCreatorDialogFragment bc = new BudgetCreatorDialogFragment();
                bc.show(getFragmentManager(), "BudgetCreator");
                return true;
            default:
                return false;
        }
    }

    @Override
	public void onResume() {
		super.onResume();
		updateDisplayedBudget();
        loadList();
	}

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(BUDGET_POSITION_IN_LIST, mBudgetPosition);
    }

    private MoneyPickerDialogHandler mMoneySpentListener = new MoneyPickerDialogHandler() {
        @Override
        public void onValueSet(int value) {
            if (value != 0) {
                mBudget.budgetEdit(value);
                BudgetDataSource data = new BudgetDataSource(getActivity());
                data.open();
                data.setCurrentBudget(mBudget.getId(), mBudget.getCurrentBudget());
                data.close();
                updateDisplayedBudget();
                addTransaction(value, "");
            }
            if (value == 528491) {
                Toast toast;
                toast = Toast.makeText(getActivity(), "We have to go deeper.",
                        Toast.LENGTH_SHORT);
                toast.show();

                StatisticsFragment fragment = new StatisticsFragment();
//                getFragmentManager().beginTransaction().addToBackStack("BudgetFragment")
//                        .replace(R.id.container, fragment).commit();
            }
        }
    };

    public void loadList() {
        TransactionDataSource trans = new TransactionDataSource(getActivity(), mBudget.getId());
        trans.open();
        mTransactions = trans.getAllTransactionsReverse();
        trans.close();
    }

	public void editBudget() {
    	FragmentManager fm = getFragmentManager();
        MoneyPickerDialogFragment moneyPicker =
                new MoneyPickerDialogFragment(true, mMoneySpentListener);
        moneyPicker.show(fm, "fragment_money_picker");
    }

    private void addTransaction(int amount, String location) {
        Transaction tran = new Transaction(amount, location, "", "");
        TransactionDataSource tranSource = new TransactionDataSource(mContext, mBudget.getId());
        tranSource.open();
        mTransactions.add(0, tranSource.createTransaction(tran));
        tranSource.close();
        mTransactionAdapter.notifyDataSetChanged();
    }

    private void setOnClicks() {
    	mDollarsView.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				editBudget();
			}
            	});
    	mCentsView.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                editBudget();
            }
        });

        Button newTransaction = (Button) mButtonsHeader.findViewById(R.id.spend_money);
        newTransaction.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                TransactionCreatorDialogFragment fragment =
                        new TransactionCreatorDialogFragment(mBudget.getId(), transactionCallback);
                fragment.show(getFragmentManager(), "TransactionCreatorDialogFragment");
            }
        });
        Button quickSpend = (Button) mButtonsHeader.findViewById(R.id.quick_spend_button);
        quickSpend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                editBudget();
            }
        });
        mList.setClickable(true);
        mList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TransactionCreatorDialogFragment tranCreator
                        = new TransactionCreatorDialogFragment(mBudget.getId(),
                        (Transaction) adapterView.getItemAtPosition(i), transactionCallback);
                tranCreator.show(getFragmentManager(), "TransactionEditor");
            }
        });
    }

    private void setFonts() {
        Typeface slabBold = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/RobotoSlab-Bold.ttf");
        Typeface slabThin = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/RobotoSlab-Thin.ttf");
        Typeface slabReg = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/RobotoSlab-Regular.ttf");

        TextView viewDollars = (TextView) mNumbersHeader.findViewById(R.id.dollars_view);
        viewDollars.setTypeface(slabBold);

        TextView viewCents = (TextView) mNumbersHeader.findViewById(R.id.cents_view);
        viewCents.setTypeface(slabThin);

        TextView viewDollarsRed = (TextView) mNumbersHeader.findViewById(R.id.dollars_view_red);
        viewDollarsRed.setTypeface(slabBold);

        TextView viewCentsRed = (TextView) mNumbersHeader.findViewById(R.id.cents_view_red);
        viewCentsRed.setTypeface(slabThin);

        Button newTransaction = (Button) mButtonsHeader.findViewById(R.id.spend_money);
        newTransaction.setTypeface(slabReg);

        Button quickSpend = (Button) mButtonsHeader.findViewById(R.id.quick_spend_button);
        quickSpend.setTypeface(slabReg);
    }
	
	private void updateDisplayedBudget() {
        float opacity = mBudgetViewer.calculateOpacity();
        mBudgetViewer.processBudgetViewer();

        TextView viewDollars = (TextView) mNumbersHeader.findViewById(R.id.dollars_view);
        TextView viewDollarsRed = (TextView) mNumbersHeader.findViewById(R.id.dollars_view_red);
        TextView viewCents = (TextView) mNumbersHeader.findViewById(R.id.cents_view);
        TextView viewCentsRed = (TextView) mNumbersHeader.findViewById(R.id.cents_view_red);

        viewDollars.setText(mBudgetViewer.getDollarsString());
        viewDollarsRed.setText(mBudgetViewer.getDollarsString());
        viewCents.setText(mBudgetViewer.getCentsString());
        viewCentsRed.setText(mBudgetViewer.getCentsString());

        viewDollars.setAlpha(opacity);
        viewCents.setAlpha(opacity);
    }

    private TransactionCreatedListener transactionCallback = new TransactionCreatedListener() {

        @Override
        public void onCreateTransaction(Transaction transaction) {
            mBudget.budgetEdit(transaction.getValue());
            updateDisplayedBudget();
            mTransactions.add(transaction);
            mTransactionAdapter.notifyDataSetChanged();
        }

        @Override
        public void onDeleteTransaction(Transaction transaction) {
            mBudget.budgetEdit(transaction.getValue() * -1);
            updateDisplayedBudget();
            mTransactions.remove(transaction);
            mTransactionAdapter.notifyDataSetChanged();
        }

        @Override
        public void onEditTransaction(Transaction transaction, int changed, Object changedObject) {
            switch (changed) {
                case TRANSACTION_VALUE:
                    mBudget.budgetEdit(-1 * (Integer) changedObject);
                    updateDisplayedBudget();
                    mTransactions.get(mTransactions.indexOf(transaction))
                            .setValue(transaction.getValue());
                    mTransactionAdapter.notifyDataSetChanged();
                    break;
                case TRANSACTION_LOCATION:
                    mTransactions.get(mTransactions.indexOf(transaction))
                            .setLocation((String) changedObject);
                    mTransactionAdapter.notifyDataSetChanged();
                    break;
                case TRANSACTION_MEMO:
                    mTransactions.get(mTransactions.indexOf(transaction))
                            .setMemo((String) changedObject);
                    mTransactionAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    private void setActionBarTitle(String title) {
        // Temporary disabling
        if (false) {
        if (getActivity() != null) {
            LayoutInflater inflater = (LayoutInflater)
                    getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.titleview, null);
//            TextView titleView = (TextView) v.findViewById(R.id.title);
//            Typeface slabReg =
//                    Typeface.createFromAsset(getActivity().getAssets(), "fonts/RobotoSlab-Regular.ttf");
//            titleView.setTypeface(slabReg);
//            titleView.setText(title);
            TabView tabs = (TabView) v.findViewById(R.id.tabs);
            if (getActivity().getActionBar() != null) {
                getActivity().getActionBar().setCustomView(v);
            }
        }
        }
    }
}
