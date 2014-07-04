package me.williamhester.Quantum.ui.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.RippleDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import me.williamhester.Quantum.R;
import me.williamhester.Quantum.models.Transaction;

/**
 * Created by William on 12/1/13.
 * Transaction Array Adapter
 * Displays the transaction value, date, location, and memo.
 */
public class TransactionArrayAdapter extends ArrayAdapter<Transaction> {

    private List<Transaction> mTransactions;
    private Context mContext;

    public TransactionArrayAdapter(Context context, List<Transaction> transactions) {
        super(context, R.layout.transaction_list_item, transactions);
        mTransactions = transactions;
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.transaction_list_item, parent, false);
        TextView moneySpent = (TextView) rowView.findViewById(R.id.top_text);
        TextView timeSpent = (TextView) rowView.findViewById(R.id.bottom_text);
        TextView location = (TextView) rowView.findViewById(R.id.right_text);
        Typeface slabReg = Typeface.createFromAsset(mContext.getAssets(), "fonts/RobotoSlab-Regular.ttf");
        TextView menu = (TextView) rowView.findViewById(R.id.memo_text);

        double dollars = (double) mTransactions.get(position).getValue() / 100;
        if (dollars < 0) {
            moneySpent.setTextColor(mContext.getResources().getColor(R.color.dark_green));
            String text = "+" + new DecimalFormat("#####0.00").format(-1 * dollars);
            moneySpent.setText(text);
        } else {
            moneySpent.setTextColor(mContext.getResources().getColor(R.color.red));
            String text = "-" + new DecimalFormat("#####0.00").format(dollars);
            moneySpent.setText(text);
        }
        if (!mTransactions.get(position).getLocationName().equals("NULL")) {
            location.setText(mTransactions.get(position).getLocationName());
            location.setTypeface(slabReg);
        }

        moneySpent.setTypeface(slabReg);
        timeSpent.setTypeface(slabReg);
        menu.setTypeface(slabReg);

        menu.setText(mTransactions.get(position).getMemo());
        timeSpent.setText(mTransactions.get(position).getDateString());

//        colorRipple(rowView, R.color.ghostwhite, R.color.auburn);

        rowView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                RippleDrawable background = (RippleDrawable) view.findViewById(R.id.relative_layout).getBackground();
                background.setHotspot(motionEvent.getX(), motionEvent.getY());
                return view.onTouchEvent(motionEvent);
            }
        });

        return rowView;
    }
}
