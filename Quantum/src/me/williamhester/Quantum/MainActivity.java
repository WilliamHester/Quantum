package me.williamhester.Quantum;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

@SuppressLint("WorldReadableFiles")
public class MainActivity extends FragmentActivity implements NewBudgetDialog.OnCreateNewBudget {

    public final static String BUDGET_ID = "position number";
	public final static String FIRST_TIME = "First time";
    public final static String POSITION_NUMBER = "position number";
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

    private int mBudget; // Index of the budget in the list.

    private ActionBarDrawerToggle mDrawerToggle;
    private ActionBar mAction;
    private BudgetViewer mBudgetViewer;
    private CharSequence mTitle;
    private DrawerLayout mDrawerLayout;
    private List<Budget> mBudgets;
    private List<Transaction> mTransactions;
    private ListView mDrawerList;
	private static SharedPreferences prefs;
    private ListView mList;
    private TextView mDollarsView, mCentsView;
    private View mNumbersHeader;
    private View mButtonsHeader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.container);

        checkForRollover();

        prefs = getSharedPreferences("preferences", MODE_PRIVATE);
        boolean firstTime = prefs.getBoolean(FIRST_TIME, true);
        
        if (firstTime) {
    		Intent i = new Intent(this, Welcome.class);
    		startActivity(i);
        }

        loadBudgets();
        loadBudget(savedInstanceState);

        mList = (ListView) findViewById(R.id.transactions);
        mNumbersHeader = findViewById(R.id.budget_amount);
        mButtonsHeader = findViewById(R.id.buttons_row);

        mDollarsView = (TextView) mNumbersHeader.findViewById(R.id.dollars_view);
        mCentsView = (TextView) mNumbersHeader.findViewById(R.id.cents_view);
        setUpList();
        setOnClicks();
        setFonts();

        mTitle = mBudgets.get(mBudget).getName();
        
        mAction = getActionBar();
		if (mAction != null) {
            mAction.setDisplayShowTitleEnabled(true);
            mAction.setDisplayHomeAsUpEnabled(false);
//            mAction.setDisplayShowHomeEnabled(false); // Toggles the Quantum Icon
        }

//        Potentially old code, as the MainActivity is becoming the content. (possibly)
//
//        Bundle b = new Bundle();
//        b.putLong(BudgetFragment.BUDGET_ID, mBudgets.get(0).getId());
//        BudgetFragment fragment = new BudgetFragment();
//        fragment.setArguments(b);
//        getFragmentManager().beginTransaction()
//                .replace(R.id.main_layout, fragment)
//                .commit();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new BudgetArrayAdapter(mBudgets, this));
        
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_navigation_drawer, R.string.drawer_open, R.string.drawer_close) {
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(getString(R.string.budgets));
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mAction.setDisplayHomeAsUpEnabled(true);
        mAction.setHomeButtonEnabled(true);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }


    @Override
    public void onResume() {
        super.onResume();
        mAction.show();
        updateDisplayedBudget();
        loadList();
        setUpList();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
//        menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
//        menu.
        return super.onPrepareOptionsMenu(menu);
    }
    @Override
     public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            try {
//                getFragmentManager().beginTransaction()
//                        .replace(R.id.main_layout, mBackStack.pop())
//                        .commit();
            } catch (NullPointerException e) {
                finish();
            } finally {
                return true;
            }
        }
        return super.onKeyUp(keyCode, event);
    }

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current dropdown position.
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, mAction
				.getSelectedNavigationIndex());
	}

    private void loadBudgets() {
        BudgetDataSource data = new BudgetDataSource(this);
        data.open();
        mBudgets = data.getAllBudgets();
        data.close();
    }

    private void loadBudget(Bundle bundle) {
        long id = 0;
        try {
            id = bundle.getLong(BUDGET_ID);
        } catch (NullPointerException e) {
            // Need to find something to do if a budget is not selected.
        }

        mBudget = 0;
        int i = 0;
        while(i < mBudgets.size() && mBudgets.get(i).getId() != id) {
            i++;
        }
        if (i < mBudgets.size()) {
            mBudget = i;
        }

        TransactionDataSource trans = new TransactionDataSource(this, mBudgets.get(mBudget).getId());
        trans.open();
        mTransactions = trans.getAllTransactionsReverse();
        trans.close();
        mBudgetViewer = new BudgetViewer(mBudgets.get(mBudget));
    }
    
    public static void notFirstTime() {
    	SharedPreferences.Editor edit = prefs.edit();
    	edit.putBoolean(FIRST_TIME, false);
    	edit.commit();
    }
    
    public void checkForRollover() {
    	BudgetDataSource data = new BudgetDataSource(this);
    	data.open();
    	List<Budget> budgets = data.getAllBudgets();
    	for (Budget b : budgets) {
            if (b.isNextInterval()) {
                b.rollover(data);
                Toast.makeText(this, b.getName() + " rolling over", Toast.LENGTH_SHORT).show();
            }
    	}
    	data.close();
    }

	public void onCreateNewBudget(long id) {
//        BudgetFragment fragment = new BudgetFragment();
//        Bundle args = new Bundle();
//        args.putLong(BudgetFragment.BUDGET_ID, id);
//        fragment.setArguments(args);
//        getFragmentManager().beginTransaction()
//                .replace(R.id.main_layout, fragment)
//                .commit();

	}

    public TransactionEditorDialogFragment.TransactionEditorListener mTransactionDeletedCallback
            = new TransactionEditorDialogFragment.TransactionEditorListener() {
        @Override
        public void onDeleteTransaction(int position) {
            TransactionDataSource tranSource = new TransactionDataSource(getApplicationContext(), mBudgets.get(mBudget).getId());
            tranSource.open();
            tranSource.deleteTransaction(mTransactions.get(position));
            tranSource.close();
            mBudgets.get(mBudget).budgetEdit(mTransactions.get(position).getDollars() * -1);
            mTransactions.remove(position);
            updateDisplayedBudget();
            setUpList();
        }
    };

    private MoneyPickerDialogFragment.MoneyPickerDialogHandler mMoneySpentListener = new MoneyPickerDialogFragment.MoneyPickerDialogHandler() {
        @Override
        public void onValueSet(int value) {
            if (value != 0) {
                mBudgets.get(mBudget).budgetEdit(value);
                updateDisplayedBudget();
                addTransaction(value, "");
            }
            if (value == 528491) {
//                StatisticsFragment fragment = new StatisticsFragment();
//                Bundle args = new Bundle();
//                args.putInt(MainActivity.POSITION_NUMBER,
//                        getArguments().getInt(MainActivity.POSITION_NUMBER));
//                fragment.setArguments(args);
//                getFragmentManager().beginTransaction()
//                        .replace(R.id.main_layout, fragment)
//                        .commit();
            }
        }
    };

    public void loadList() {
        TransactionDataSource trans = new TransactionDataSource(this, mBudgets.get(mBudget).getId());
        trans.open();
        mTransactions = trans.getAllTransactionsReverse();
        trans.close();
    }

    public void editBudget() {
        FragmentManager fm = getFragmentManager();
        MoneyPickerDialogFragment moneyPicker = new MoneyPickerDialogFragment(true, mMoneySpentListener);
        moneyPicker.show(fm, "fragment_money_picker");
    }

    private void addTransaction(int amount, String location) {
        Transaction tran = new Transaction(amount, location, "", "");
        TransactionDataSource tranSource = new TransactionDataSource(this, mBudgets.get(mBudget).getId());
        tranSource.open();
        mTransactions.add(0, tranSource.createTransaction(tran));
        tranSource.close();
        setUpList();
    }

    private void setOnClicks() {
        mDollarsView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                editBudget();
            }
        });
        mCentsView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                editBudget();
            }
        });

        Button newTransaction = (Button) mButtonsHeader.findViewById(R.id.spend_money);
        newTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransactionCreatorFragment fragment =
                        new TransactionCreatorFragment(mBudgets.get(mBudget).getId());
                FragmentManager fm = getFragmentManager();
                fm.beginTransaction().replace(R.id.main_layout, fragment).commit();
            }
        });
        Button quickSpend = (Button) mButtonsHeader.findViewById(R.id.quick_spend_button);
        quickSpend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editBudget();
            }
        });

        mList.setLongClickable(true);
//        mList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
//                FragmentManager fm = getFragmentManager();
////                TransactionEditorDialogFragment editor
////                        = new TransactionEditorDialogFragment(mTransactionDeletedCallback);
//                Bundle args = new Bundle();
//                args.putLong(TransactionEditorDialogFragment.BUDGET_ID, mBudgets.get(mBudget).getId());
//                args.putLong(TransactionEditorDialogFragment.TRANSACTION_ID,
//                        ((Transaction) adapterView.getItemAtPosition(i)).getId());
//                args.putInt(TransactionEditorDialogFragment.TRANSACTION_VALUE,
//                        ((Transaction) adapterView.getItemAtPosition(i)).getDollars());
//                args.putInt(TransactionEditorDialogFragment.POSITION, i);
////                editor.setArguments(args);
////                editor.setTargetFragment(mThis, 0);
////                editor.show(fm, "fragment_transaction_editor");
//                return true;
//            }
//        });
        mList.setClickable(true);
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TransactionCreatorFragment tranCreator
                        = new TransactionCreatorFragment(mBudgets.get(mBudget).getId(),
                        (Transaction) adapterView.getItemAtPosition(i));
                getFragmentManager().beginTransaction()
                        .replace(R.id.main_layout, tranCreator)
                        .commit();
            }
        });
    }

    private void setFonts() {
        Typeface slabBold = Typeface.createFromAsset(getAssets(), "fonts/RobotoSlab-Bold.ttf");
        Typeface slabThin = Typeface.createFromAsset(getAssets(), "fonts/RobotoSlab-Thin.ttf");
        Typeface slabReg = Typeface.createFromAsset(getAssets(), "fonts/RobotoSlab-Regular.ttf");

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
        TransactionArrayAdapter adapter = new TransactionArrayAdapter(this, mTransactions);
        mList.setAdapter(adapter);
    }

    private void updateDisplayedBudget() {
        BudgetDataSource data = new BudgetDataSource(this);
        data.open();
        data.setCurrentBudget(mBudgets.get(mBudget).getId(),
                mBudgets.get(mBudget).getCurrentBudget());
        data.close();

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
}
