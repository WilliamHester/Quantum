package me.williamhester.Quantum;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

@SuppressLint("WorldReadableFiles")
public class BudgetActivity extends FragmentActivity implements NewBudgetDialog.OnCreateNewBudget {

	public final static String FIRST_TIME = "First time";
    public final static String POSITION_NUMBER = "position number";

    private ActionBarDrawerToggle mDrawerToggle;
    private ActionBar mAction;
    private DrawerLayout mDrawerLayout;
    private List<Budget> mBudgets;
    private ListView mDrawerList;
	private static SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.container);

        prefs = getSharedPreferences("preferences", MODE_PRIVATE);
        boolean firstTime = prefs.getBoolean(FIRST_TIME, true);
        
        if (firstTime) {
    		Intent i = new Intent(this, Welcome.class);
    		startActivity(i);
        }

        loadBudgets();
        checkForRollover();

        mAction = getActionBar();
		if (mAction != null) {
            mAction.setDisplayShowTitleEnabled(true);
            mAction.setDisplayHomeAsUpEnabled(false);
        }

        Bundle b = new Bundle();
        b.putLong(BudgetFragment.BUDGET_ID, mBudgets.get(0).getId());
        BudgetFragment fragment = new BudgetFragment();
        fragment.setArguments(b);
        getFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new BudgetArrayAdapter(mBudgets, this));
        
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_navigation_drawer, R.string.drawer_open, R.string.drawer_close) {
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                // TODO: find a _good_ way to get the fragment's budget title and set it here
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
            try {
                getFragmentManager().popBackStack();
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
        // TODO save the position of the selected budget
	}

    private void loadBudgets() {
        BudgetDataSource data = new BudgetDataSource(this);
        data.open();
        mBudgets = data.getAllBudgets();
        data.close();
    }
    
    public static void notFirstTime() {
    	SharedPreferences.Editor edit = prefs.edit();
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
        BudgetFragment fragment = new BudgetFragment();
        Bundle args = new Bundle();
        args.putLong(BudgetFragment.BUDGET_ID, id);
        fragment.setArguments(args);
        getFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
	}
}