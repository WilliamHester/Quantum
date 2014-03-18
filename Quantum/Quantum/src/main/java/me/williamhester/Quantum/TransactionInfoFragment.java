package me.williamhester.Quantum;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Calendar;

/**
 * Created by William Hester on 7/13/13.
 */
public class TransactionInfoFragment extends Fragment {

    public static final String BUDGET_ID = "budget_id";
    public static final String TRANSACTION_ID = "transaction_id";

    private long mBudgetId;
    private Transaction mTransaction;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mBudgetId = args.getLong(BUDGET_ID);
        long transactionId = args.getLong(TRANSACTION_ID);

        TransactionDataSource trans = new TransactionDataSource(getActivity(), mBudgetId);
        trans.open();
        mTransaction = trans.getTransaction(transactionId);
        trans.close();

        setHasOptionsMenu(true);
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        getActivity().getActionBar().setTitle(R.string.transaction_info);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.transaction_info_fragment, null);

        Typeface slabReg = Typeface.createFromAsset(getActivity().getAssets(), "fonts/RobotoSlab-Regular.ttf");

        TextView value = (TextView) v.findViewById(R.id.value);
        TextView time = (TextView) v.findViewById(R.id.time);
        TextView location = (TextView) v.findViewById(R.id.location);
        TextView memo = (TextView) v.findViewById(R.id.memo);

        value.setText(generateValueString());
        time.setText("Spent at " + mTransaction.getTimeString()
                + " on " + mTransaction.getDateString());
        location.setText(mTransaction.getLocationName());
        memo.setText(mTransaction.getMemo());

        value.setTypeface(slabReg);
        time.setTypeface(slabReg);
        location.setTypeface(slabReg);
        memo.setTypeface(slabReg);

        if (mTransaction.getValue() > 0) {
            value.setTextColor(getResources().getColor(R.color.red));
        } else {
            value.setTextColor(getResources().getColor(R.color.dark_green));
        }

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.transaction_info_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                BudgetFragment fragment = new BudgetFragment();
                Bundle args = new Bundle();
                args.putLong(BudgetFragment.BUDGET_POSITION_IN_LIST, mBudgetId);
                fragment.setArguments(args);
//                getFragmentManager().beginTransaction()
//                        .replace(R.id.frame_container, fragment)
//                        .commit();
                return true;
            case R.id.edit_transaction:
                TransactionCreatorFragment tranCreator
                        = new TransactionCreatorFragment(mBudgetId, mTransaction);
//                getFragmentManager().beginTransaction()
//                        .replace(R.id.frame_container, tranCreator)
//                        .commit();
                return true;
            case R.id.delete_transaction:

            default:
                return false;
        }
    }

    private String generateValueString() {
        DecimalFormat format = new DecimalFormat("#,##0.00");
        if (mTransaction.getValue() > 0) {
           return "-" + format.format(mTransaction.getValue() / 100.0);
        }
        return "+" + format.format(mTransaction.getValue() / 100.0);
    }

    private String generateText() {
        String s;
        s = "You spent " + getMoneyString() + " on " + getMonth() + " " + mTransaction.getDayOfMonth()
            + getDateSuffix() + ", " + mTransaction.getYear() + " at " + mTransaction.getTimeString();
        if (!mTransaction.getLocationName().equals("NULL")) {
            s += " at " + mTransaction.getLocationName();
        }
        if (!mTransaction.getMemo().equals("NULL")) {
            s += " for " + mTransaction.getMemo();
        }
        s += ".";
        return s;
    }

    private String getMonth() {
        switch (mTransaction.getMonth()) {
            case Calendar.JANUARY:
                return getString(R.string.january);
            case Calendar.FEBRUARY:
                return getString(R.string.february);
            case Calendar.MARCH:
                return getString(R.string.march);
            case Calendar.APRIL:
                return getString(R.string.april);
            case Calendar.MAY:
                return getString(R.string.may);
            case Calendar.JUNE:
                return getString(R.string.june);
            case Calendar.JULY:
                return getString(R.string.july);
            case Calendar.AUGUST:
                return getString(R.string.august);
            case Calendar.SEPTEMBER:
                return getString(R.string.september);
            case Calendar.OCTOBER:
                return getString(R.string.october);
            case Calendar.NOVEMBER:
                return getString(R.string.november);
            case Calendar.DECEMBER:
                return getString(R.string.december);
            default:
                return "unknown";
        }
    }

    private String getDateSuffix() {
        if (mTransaction.getDayOfMonth() == 1 | mTransaction.getDayOfMonth() == 21
                | mTransaction.getDayOfMonth() == 31)
            return "st";
        else if (mTransaction.getDayOfMonth() == 2 | mTransaction.getDayOfMonth() == 22)
            return "nd";
        else if (mTransaction.getDayOfMonth() == 3 | mTransaction.getDayOfMonth() == 23)
            return "rd";
        else
            return "th";
    }

    private String getMoneyString() {
        DecimalFormat format = new DecimalFormat("###,##0.00");
        return format.format((double) mTransaction.getValue() / 100);
    }

}
