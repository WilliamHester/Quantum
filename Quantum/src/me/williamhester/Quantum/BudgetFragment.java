package me.williamhester.Quantum;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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

import me.williamhester.Quantum.MoneyPickerDialogFragment.MoneyPickerDialogHandler;

class BudgetFragment extends Fragment {

    public final static String BUDGET_ID = "position number";

    private Budget mBudget;
    private BudgetViewer mBudgetViewer;
    private Context mContext;
    private List<Transaction> mTransactions;
    private ListView mList;
	private TextView mDollarsView, mCentsView;
    private View mNumbersHeader;
    private View mButtonsHeader;

    @Deprecated
    public BudgetFragment() {
        this(new Budget());
    }

    public BudgetFragment(Budget b) {
        mBudget = b;
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

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

        setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		View view;
        view = inflater.inflate(R.layout.budget_fragment_2, container, false);

        mList = (ListView) view.findViewById(R.id.transactions);
        mNumbersHeader = view.findViewById(R.id.budget_amount);
        mButtonsHeader = view.findViewById(R.id.buttons_row);

        mDollarsView = (TextView) mNumbersHeader.findViewById(R.id.dollars_view);
        mCentsView = (TextView) mNumbersHeader.findViewById(R.id.cents_view);
        setUpList();
        setOnClicks();
        setFonts();
		return view;
	}

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.budget_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ((DrawerToggle) getActivity()).toggle();
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
            default:
                return false;
        }
    }

    @Override
	public void onResume() {
		super.onResume();
		updateDisplayedBudget();
        loadList();
        setUpList();
	}

    public TransactionEditorDialogFragment.TransactionEditorListener mTransactionDeletedCallback
            = new TransactionEditorDialogFragment.TransactionEditorListener() {
        @Override
        public void onDeleteTransaction(int position) {
            TransactionDataSource tranSource = new TransactionDataSource(mContext, mBudget.getId());
            tranSource.open();
            tranSource.deleteTransaction(mTransactions.get(position));
            tranSource.close();
            mBudget.budgetEdit(mTransactions.get(position).getDollars() * -1);
            mTransactions.remove(position);
            updateDisplayedBudget();
            setUpList();
        }
    };

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
                getFragmentManager().beginTransaction().addToBackStack("BudgetFragment")
                        .replace(R.id.container, fragment).commit();
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
        setUpList();
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

//        mList.setLongClickable(true);
//        mList.setOnItemLongClickListener(new OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
//                FragmentManager fm = getFragmentManager();
//                TransactionEditorDialogFragment editor
//                        = new TransactionEditorDialogFragment(mTransactionDeletedCallback);
//                Bundle args = new Bundle();
//                args.putLong(TransactionEditorDialogFragment.BUDGET_ID, mBudget.getId());
//                args.putLong(TransactionEditorDialogFragment.TRANSACTION_ID,
//                        ((Transaction) adapterView.getItemAtPosition(i)).getId());
//                args.putInt(TransactionEditorDialogFragment.TRANSACTION_VALUE,
//                        ((Transaction) adapterView.getItemAtPosition(i)).getDollars());
//                args.putInt(TransactionEditorDialogFragment.POSITION, i);
//                editor.setArguments(args);
//                editor.setTargetFragment(mThis, 0);
//                editor.show(fm, "fragment_transaction_editor");
//                return true;
//            }
//        });
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

    private void setUpList() {
        TransactionArrayAdapter adapter = new TransactionArrayAdapter(mContext, mTransactions);
    	mList.setAdapter(adapter);
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

    private GenericCallback transactionCallback = new GenericCallback() {
        @Override
        public void callback() {
            BudgetDataSource data = new BudgetDataSource(getActivity());
            data.open();
            mBudget.setCurrentBudget(data.getCurrentBudgetFromId(mBudget.getId()));
            data.close();
            updateDisplayedBudget();
            loadList();
            setUpList();
        }
    };

    private void setActionBarTitle(String title) {
        LayoutInflater inflater = (LayoutInflater)
                getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.titleview, null);
        TextView titleView = (TextView) v.findViewById(R.id.title);
        Typeface slabReg =
                Typeface.createFromAsset(getActivity().getAssets(), "fonts/RobotoSlab-Regular.ttf");
        titleView.setTypeface(slabReg);
        titleView.setText(title);
        getActivity().getActionBar().setCustomView(v);
    }
}
