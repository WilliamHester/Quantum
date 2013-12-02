package me.williamhester.Quantum;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Creates a Fragment that displays all budgets and specific data
 * about each one.
 *
 * Created by William Hester on 5/20/13.
 */
public class BudgetLauncherFragment extends Fragment {

    private Context mContext;
    private List<Budget> mBudgets;
    private ListView mList;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity();

        BudgetDataSource data = new BudgetDataSource(getActivity());
        data.open();
        mBudgets = data.getAllBudgets();
        data.close();

        getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.budget_launcher, container, false);

        if (view != null) {
            mList = (ListView) view.findViewById(R.id.budget_list);
            mList.setAdapter(new BudgetArrayAdapter());
        }

        setOnClicks();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.budget_launcher_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_budget:
                FragmentManager fm = getFragmentManager();
                NewBudgetDialog fragment = new NewBudgetDialog(this);
                fragment.show(fm, "fragment_new_budget");
                return true;
            default:
                return false;
        }
    }

    private void setOnClicks() {
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                BudgetFragment fragment = new BudgetFragment();
                Bundle args = new Bundle();
                args.putLong(BudgetFragment.BUDGET_ID, ((Budget) adapterView.getItemAtPosition(position)).getId());
                fragment.setArguments(args);
                try {
//                    getFragmentManager().beginTransaction()
//                            .replace(R.id.frame_container, fragment)
//                            .commit();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }

        });
        mList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                FragmentManager fm = getFragmentManager();
                DeleteBudgetDialog fragment = new DeleteBudgetDialog(position);
                fragment.show(fm, "fragment_delete_budget");
                return true;
            }
        });
    }

    private class BudgetArrayAdapter extends ArrayAdapter<Budget> {

        public BudgetArrayAdapter() {
            super(getActivity(), R.layout.budget_list_item, mBudgets);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getActivity()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.budget_list_item, parent, false);
            TextView name = (TextView) view.findViewById(R.id.budget_name);
            TextView value = (TextView) view.findViewById(R.id.budget_value);

            Typeface slabBold = Typeface.createFromAsset(getActivity().getAssets(),
                    "fonts/RobotoSlab-Bold.ttf");
            Typeface slabReg = Typeface.createFromAsset(getActivity().getAssets(),
                    "fonts/RobotoSlab-Regular.ttf");

            name.setTypeface(slabBold);
            value.setTypeface(slabReg);

            name.setText(mBudgets.get(position).toString());
            int budgetValue = mBudgets.get(position).getCurrentBudget();
            if (budgetValue < 0) {
                value.setTextColor(Color.RED);
            } else {
                value.setTextColor(getResources().getColor(R.color.dark_gray));
            }
            BudgetViewer viewer = new BudgetViewer(mBudgets.get(position));

            value.setText(viewer.getDollarsString() + viewer.getCentsString() + "/"
                + new DecimalFormat("#,##0.00").format(mBudgets.get(position).getBudget() / 100.0)
                + " " + getString(R.string.remaining));

            return view;
        }
    }

    private void deleteBudget(int position) {
        BudgetDataSource data = new BudgetDataSource(mContext);
        data.open();
        List<Budget> budgets = data.getAllBudgets();
        TransactionDataSource trans = new TransactionDataSource(mContext,
                budgets.get(position).getId());
        trans.delete();
        data.deleteBudget(budgets.get(position).getId());
        data.close();
    }

    @SuppressLint("ValidFragment")
    public class DeleteBudgetDialog extends DialogFragment {

        int mPosition;

        public DeleteBudgetDialog(int position) {
            super();
            mPosition = position;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setStyle(STYLE_NORMAL, android.R.style.Theme_Holo_Light_Dialog);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View v = inflater.inflate(R.layout.delete_budget_dialog, null);

            getDialog().setTitle(R.string.delete_budget_title);

            Button cancel = (Button) v.findViewById(R.id.cancel);
            cancel.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    dismiss();
                }
            });
            Button confirm = (Button) v.findViewById(R.id.confirm);
            confirm.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    deleteBudget(mPosition);
                    mBudgets.remove(mPosition);
                    mList.setAdapter(new BudgetArrayAdapter());
                    dismiss();
                }
            });

            return v;
        }
    }
}
