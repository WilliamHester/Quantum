package me.williamhester.Quantum.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import me.williamhester.Quantum.R;
import me.williamhester.Quantum.models.Statistics;
import me.williamhester.Quantum.models.Transaction;
import me.williamhester.Quantum.models.Budget;

/**
 * Created by William Hester on 7/6/13.
 */
public class StatisticsFragment extends Fragment {

    private Budget mBudget;
    private List<Transaction> mTransactions;
    private Statistics mStatistics;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        BudgetDataSource data = new BudgetDataSource(getActivity());
//        data.open();
//        mBudget = data.getAllBudgets().get(id);
//        data.close();

//        TransactionDataSource trans = new TransactionDataSource(getActivity(), mBudget.getId());
//        trans.open();
//        mTransactions = trans.getAllTransactionsReverse();
//        data.close();

//        mStatistics = new Statistics(mTransactions);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.statistics_fragment, container, false);
        if (v != null) {
//            TextView averageDaily = (TextView) v.findViewById(R.id.average_spent_daily);
//            TextView averageWeekly = (TextView) v.findViewById(R.id.average_spent_weekly);
//            averageDaily.setText("$" + -1 * mStatistics.getAverageSpentPerDay());
//            averageWeekly.setText("$" + -1 * mStatistics.getAverageSpentPerWeek());
        }
        return v;
    }

}
