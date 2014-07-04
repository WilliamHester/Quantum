package me.williamhester.Quantum.ui.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import me.williamhester.Quantum.models.Budget;
import me.williamhester.Quantum.ui.adapters.BudgetArrayAdapter;
import me.williamhester.Quantum.ui.fragments.BudgetCreatorDialogFragment;
import me.williamhester.Quantum.datasources.BudgetDataSource;
import me.williamhester.Quantum.ui.fragments.BudgetFragment;
import me.williamhester.Quantum.DrawerToggle;
import me.williamhester.Quantum.R;
import me.williamhester.Quantum.ui.fragments.StatisticsFragment;
import me.williamhester.Quantum.ui.views.TabView;
import me.williamhester.Quantum.datasources.TransactionDataSource;

/**
 * Created by William on 1/26/14.
 */
public class BudgetActivity2 extends Activity implements
        BudgetCreatorDialogFragment.OnCreateNewBudget, DrawerToggle {

    public final static String FIRST_TIME = "First time";
    public final static String BUDGET_INDEX = "Budget Index";
    public final static String VIEW_PAGER_INDEX = "View pager index";

    private ActionBarDrawerToggle mDrawerToggle;
    private ActionBar mAction;
    private DrawerLayout mDrawerLayout;
    private List<Budget> mBudgets;
    private ListView mDrawerList;
    private String mBudgetName;
    private TabView mTabView;
    private ViewPager mViewPager;

    private int mBudgetIndex;
    private int mViewPagerIndex;

    private static SharedPreferences mPrefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mBudgetIndex = savedInstanceState.getInt(BUDGET_INDEX, 0);
            mViewPagerIndex = savedInstanceState.getInt(VIEW_PAGER_INDEX, 0);
        } else {
            mBudgetIndex = 0;
            mViewPagerIndex = 0;
        }

        setContentView(R.layout.budget_fragment_activity);

        mPrefs = getSharedPreferences("preferences", MODE_PRIVATE);
        if (mPrefs.getBoolean(FIRST_TIME, true)) {
            Intent i = new Intent(this, WelcomeActivity.class);
            startActivity(i);
        } else {
            setActionBarCustomView();
            setUpBudgetDrawer();
        }

        mAction = getActionBar();
        if (mAction != null) {
            mAction.setDisplayShowTitleEnabled(false);
            mAction.setDisplayHomeAsUpEnabled(false);
            mAction.setDisplayShowCustomEnabled(true);
            mAction.setHomeButtonEnabled(true);
        }

        mViewPager = (ViewPager) findViewById(R.id.view_pager);
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
                BudgetFragment fragment = new BudgetFragment();
                Bundle args = new Bundle();
                args.putInt(BudgetFragment.BUDGET_POSITION_IN_LIST, mBudgetIndex);
                fragment.setArguments(args);
                // Todo uncomment what is below
//                getFragmentManager().beginTransaction()
//                        .replace(R.id.container, fragment)
//                        .commit();
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
                setActionBarCustomView();
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                setActionBarTitle(R.string.budgets);
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
        if (mDrawerToggle != null) {
            mDrawerToggle.syncState();
        }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.budget_fragment_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                toggle();
                return true;
            case R.id.budget_management:
                Bundle b = new Bundle();
                b.putLong(SettingsContainerActivity.BUDGET_ID, mBudgets.get(mBudgetIndex).getId());
                Intent i = new Intent(this, SettingsContainerActivity.class);
                i.putExtras(b);
                startActivity(i);
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(BUDGET_INDEX, mBudgetIndex);
        outState.putInt(VIEW_PAGER_INDEX, mViewPagerIndex);
    }

    private void loadBudgets() {
        BudgetDataSource data = new BudgetDataSource(this);
        data.open();
        mBudgets = data.getAllBudgets();
        data.close();
        mDrawerList.setAdapter(new BudgetArrayAdapter(mBudgets, this));
    }

    /**
     * Reloads all of the data and the fragment.
     */
    private void loadData() {
        BudgetFragment fragment = new BudgetFragment();
        Bundle args = new Bundle();
        args.putInt(BudgetFragment.BUDGET_POSITION_IN_LIST, mBudgetIndex);
        fragment.setArguments(args);
        // Todo: uncomment below
//        getFragmentManager().beginTransaction()
//                .replace(R.id.container, fragment)
//                .commit();
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
        BudgetFragment fragment = new BudgetFragment();
        Bundle args = new Bundle();
        args.putInt(BudgetFragment.BUDGET_POSITION_IN_LIST, mBudgetIndex);
        fragment.setArguments(args);
        // Todo uncomment below
//        getFragmentManager().beginTransaction()
//                .replace(R.id.container, fragment)
//                .commit();
        mDrawerList.setAdapter(new BudgetArrayAdapter(mBudgets, this));
        mDrawerLayout.closeDrawer(Gravity.LEFT);
    }

    private void setActionBarTitle(int string) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.titleview, null);
        TextView title = (TextView) v.findViewById(R.id.title);
        title.setText(string);
        Typeface slabReg = Typeface.createFromAsset(getAssets(), "fonts/RobotoSlab-Regular.ttf");
        title.setTypeface(slabReg);
        mAction.setCustomView(v);
    }

    private void setActionBarCustomView() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        Typeface slabReg = Typeface.createFromAsset(getAssets(), "fonts/RobotoSlab-Regular.ttf");
        View v = inflater.inflate(R.layout.titleview2, null);
        mTabView = (TabView) v.findViewById(R.id.tabs);
        mTabView.attachViewPager(mViewPager);
        Bundle args = new Bundle();
        args.putInt(BudgetFragment.BUDGET_POSITION_IN_LIST, mBudgetIndex);
        TextView tv1 = new TextView(this);
        tv1.setText("Tiger Card");
        tv1.setTextSize(20);
        tv1.setTextColor(getResources().getColor(R.color.ghostwhite));
        tv1.setTypeface(slabReg);
        ImageView iv = new ImageView(this);
        ImageView iv2 = new ImageView(this);
        iv.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher2));
        iv2.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher2));
        mTabView.addTab(BudgetFragment.class, args, TabView.TAB_TYPE_MAIN, tv1);
        mTabView.addTab(StatisticsFragment.class, args, TabView.TAB_TYPE_MINOR, iv);
        mTabView.addTab(StatisticsFragment.class, args, TabView.TAB_TYPE_MINOR, iv2);
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
}
