package me.williamhester.Quantum;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

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
//        TextView value = (TextView) view.findViewById(R.id.budget_value);
//            TextView spentPercentage = (TextView) view.findViewById(R.id.spent_percentage);

        Typeface slabBold = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/RobotoSlab-Bold.ttf");
        Typeface slabThin = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/RobotoSlab-Thin.ttf");
        Typeface slabReg = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/RobotoSlab-Regular.ttf");

        name.setTypeface(slabBold);
//        value.setTypeface(slabReg);
//            spentPercentage.setTypeface(slabReg);

        name.setText(mBudgets.get(position).toString());
//        int budgetValue = mBudgets.get(position).getCurrentBudget();
//        if (budgetValue < 0) {
//            value.setTextColor(Color.RED);
////                spentPercentage.setTextColor(Color.RED);
//        } else {
//            value.setTextColor(mContext.getResources().getColor(R.color.dark_gray));
////                spentPercentage.setTextColor(getResources().getColor(R.color.dark_gray));
//        }
//        BudgetViewer viewer = new BudgetViewer(mBudgets.get(position));
//
//        value.setText(viewer.getDollarsString() + viewer.getCentsString() + "/"
//                + new DecimalFormat("#,##0.00").format(mBudgets.get(position).getBudget() / 100.0)
//                + " " + mContext.getString(R.string.remaining));
//            spentPercentage.setText(((int) (((double) budgetValue)
//                    / (double) mBudgets.get(position).getBudget() * 100)) + "%");
        return view;
    }
}
