package me.williamhester.Quantum;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

@SuppressLint("WorldReadableFiles")
public class BudgetActivity extends FragmentActivity implements
        BudgetCreatorDialogFragment.OnCreateNewBudget, DrawerToggle {

	public final static String FIRST_TIME = "First time";
    public final static String BUDGET_INDEX = "Budget Index";

    private ActionBarDrawerToggle mDrawerToggle;
    private ActionBar mAction;
    private DrawerLayout mDrawerLayout;
    private List<Budget> mBudgets;
    private ListView mDrawerList;
    private String mBudgetName;
    private int mBudgetIndex;

	private static SharedPreferences mPrefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.container);

        // TODO fix the savedInstanceState so that it restores the app correctly
//        if (savedInstanceState != null)
//            mBudgetIndex = savedInstanceState.getInt(BUDGET_INDEX, 0);

        mPrefs = getSharedPreferences("preferences", MODE_PRIVATE);
        if (mPrefs.getBoolean(FIRST_TIME, true)) {
    		Intent i = new Intent(this, Welcome.class);
    		startActivity(i);
        }

        mAction = getActionBar();
		if (mAction != null) {
            mAction.setDisplayShowTitleEnabled(false);
            mAction.setDisplayHomeAsUpEnabled(false);
            mAction.setDisplayShowCustomEnabled(true);
            mAction.setHomeButtonEnabled(true);
//            mAction.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        }

        setUpBudgetDrawer();
        setUpViewPager();
    }

    private void setUpBudgetDrawer() {
        final Context context = this;
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setLongClickable(true);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mBudgetIndex = i;
                BudgetFragment fragment =
                        new BudgetFragment(((Budget) adapterView.getItemAtPosition(i)));
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, fragment)
                        .commit();
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                mBudgetName = mBudgets.get(mBudgetIndex).getName();
            }
        });
        mDrawerList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int k = i;
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(R.string.delete_selected_budget);
                builder.setTitle(R.string.delete_budget_title);
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int j) {

                    }
                });
                builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int j) {
                        if (mBudgets.size() == 1) {
                            AlertDialog.Builder error =
                                    new AlertDialog.Builder(context);
                            error.setTitle(R.string.error);
                            error.setMessage(R.string.budget_delete_error);
                            error.setNegativeButton(R.string.okay, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                            error.create().show();
                        } else {
                            BudgetDataSource data = new BudgetDataSource(context);
                            data.open();
                            data.deleteBudget(mBudgets.get(k).getId());
                            data.close();
                            TransactionDataSource trans =
                                    new TransactionDataSource(context,
                                    mBudgets.get(k).getId());
                            trans.delete();
                            if (k == mBudgetIndex) {
                                mBudgetIndex = 0;
                            }
                            loadBudgets();
                            loadData();
                        }
                    }
                });
                builder.create().show();
                return false;
            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_navigation_drawer, R.string.drawer_open, R.string.drawer_close) {
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                setActionBarTitle(mBudgetName);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                setActionBarTitle(getString(R.string.budgets));
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
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
        loadBudgets();
        if (mBudgets.size() > 0) {
            checkForRollover();
            loadData();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        try {
            menu.findItem(R.id.budget_management).setVisible(!drawerOpen);
            menu.findItem(R.id.add_new_budget).setVisible(drawerOpen);
        } catch (NullPointerException e) {
            Log.e("NPE_TAG", "NullPointerException at onPrepareOptionsMenu");
        }
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
            if (getFragmentManager().getBackStackEntryCount() == 0)
                finish();
            getFragmentManager().popBackStack();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

	@Override
	public void onSaveInstanceState(Bundle outState) {
//        outState.putInt(BUDGET_INDEX, mBudgetIndex);
//        super.onSaveInstanceState(outState);
	}

    private void loadBudgets() {
        BudgetDataSource data = new BudgetDataSource(this);
        data.open();
        mBudgets = data.getAllBudgets();
        data.close();
    }

    /**
     * Reloads all of the data and the fragment.
     */
    private void loadData() {
        mDrawerList.setAdapter(new BudgetArrayAdapter(mBudgets, this));
        BudgetFragment fragment = new BudgetFragment(mBudgets.get(mBudgetIndex));
        getFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
        mBudgetName = mBudgets.get(mBudgetIndex).getName();
    }
    
    public static void notFirstTime() {
    	SharedPreferences.Editor edit = mPrefs.edit();
    	edit.putBoolean(FIRST_TIME, false);
    	edit.commit();
    }
    
    public void checkForRollover() {
    	BudgetDataSource data = new BudgetDataSource(this);
    	data.open();
    	for (Budget b : mBudgets) {
            if (b.isNextInterval()) {
                b.rollover(data);
                Toast.makeText(this, b.getName() + " rolling over", Toast.LENGTH_SHORT).show();
            }
    	}
    	data.close();
    }

	public void onCreateNewBudget(long id) {
        loadBudgets();
        mBudgetIndex = mBudgets.size() - 1;
        BudgetFragment fragment = new BudgetFragment(mBudgets.get(mBudgetIndex));
        mBudgetName = mBudgets.get(mBudgetIndex).getName();
        getFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
        mDrawerList.setAdapter(new BudgetArrayAdapter(mBudgets, this));
        mDrawerLayout.closeDrawer(Gravity.LEFT);
	}

    private void setActionBarTitle(String title) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.titleview, null);
        TextView titleView = (TextView) v.findViewById(R.id.title);
        Typeface slabReg = Typeface.createFromAsset(getAssets(), "fonts/RobotoSlab-Regular.ttf");
        titleView.setTypeface(slabReg);
        titleView.setText(title);
        mAction.setCustomView(v);
    }

    @Override
    public void toggle() {
        if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        } else {
            mDrawerLayout.openDrawer(Gravity.LEFT);
        }
    }

<<<<<<< HEAD
    public void setUpViewPager() {

    }

=======
>>>>>>> Gradle Fixes
}