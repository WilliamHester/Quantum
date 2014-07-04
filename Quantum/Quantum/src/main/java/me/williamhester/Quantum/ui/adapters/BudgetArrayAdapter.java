package me.williamhester.Quantum.ui.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import me.williamhester.Quantum.R;
import me.williamhester.Quantum.models.Budget;

/**
 * Created by William on 11/6/13.
 */
public class BudgetArrayAdapter extends ArrayAdapter<Budget> {

    Context mContext;
    List<Budget> mBudgets;

    public BudgetArrayAdapter(List<Budget> budgets, Context context) {
        super(context, R.layout.budget_list_item, budgets);
        mBudgets = budgets;
        mContext = context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)
                mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.budget_list_item, parent, false);
        TextView name = (TextView) view.findViewById(R.id.budget_name);

        Typeface slabBold = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/RobotoSlab-Bold.ttf");
        Typeface slabThin = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/RobotoSlab-Thin.ttf");
        Typeface slabReg = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/RobotoSlab-Regular.ttf");

        name.setTypeface(slabBold);

        name.setText(mBudgets.get(position).toString());
        return view;
    }
}
