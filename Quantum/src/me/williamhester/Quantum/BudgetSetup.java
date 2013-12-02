package me.williamhester.Quantum;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;

public class BudgetSetup extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        Typeface slabBold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/RobotoSlab-Bold.ttf");
        Typeface slabReg = Typeface.createFromAsset(getActivity().getAssets(), "fonts/RobotoSlab-Regular.ttf");

		View v = inflater.inflate(R.layout.budget_creator, null);

        TextView createFirst = (TextView) v.findViewById(R.id.create_first);
        createFirst.setTypeface(slabBold);
        TextView titleBudget = (TextView) v.findViewById(R.id.title_budget);
        titleBudget.setTypeface(slabReg);
        TextView howMuch = (TextView) v.findViewById(R.id.how_much);
        howMuch.setTypeface(slabReg);
        TextView howOften = (TextView) v.findViewById(R.id.how_often);
        howOften.setTypeface(slabReg);

		final TextView requirement = (TextView) v.findViewById(R.id.enter_all_fields);
		requirement.setVisibility(TextView.INVISIBLE);
        requirement.setTypeface(slabBold);

        final TextView intervalText = (TextView) v.findViewById(R.id.interval_text);
        intervalText.setVisibility(View.INVISIBLE);
        intervalText.setTypeface(slabReg);
		
		final Spinner intervalSelector = (Spinner) v.findViewById(R.id.budget_interval);

		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				getActivity(), R.array.budget_type,
				R.layout.spinner_text_color);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		intervalSelector.setAdapter(adapter);

        final Spinner subIntervalSelector = (Spinner) v.findViewById(R.id.sub_interval_spinner);

        intervalSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position == 1) {
                    ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(
                            getActivity(), R.array.days_of_week,
                            R.layout.spinner_text_color);

                    adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    subIntervalSelector.setAdapter(adapter1);
                    intervalText.setText(R.string.sub_interval_day);
                    subIntervalSelector.setVisibility(View.VISIBLE);
                    intervalText.setVisibility(View.VISIBLE);
                } else if (position == 2) {
                    ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(
                            getActivity(), R.array.days,
                            R.layout.spinner_text_color);
                    adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    subIntervalSelector.setAdapter(adapter1);
                    intervalText.setText(R.string.sub_interval_month);
                    subIntervalSelector.setVisibility(View.VISIBLE);
                    intervalText.setVisibility(View.VISIBLE);
                } else {
                    subIntervalSelector.setVisibility(View.INVISIBLE);
                    intervalText.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

		Button save = (Button) v.findViewById(R.id.save);
        save.setTypeface(slabReg);
		final EditText budgetName = (EditText) v.findViewById(R.id.budget_title);
        budgetName.setTypeface(slabReg);
		final EditText budgetValue = (EditText) v.findViewById(R.id.budget_value);
        budgetValue.setTypeface(slabReg);

		save.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				final String name = budgetName.getText().toString().trim();
				final String value = budgetValue.getText().toString();
				
				if (!name.isEmpty() && !value.isEmpty()
						&& numberIsValid(value)) {
					final int budget = (int) Double.parseDouble(budgetValue.getText()
							.toString()) * 100;

                    int subInterval = subIntervalSelector.getSelectedItemPosition();

                    final int interval;
                    String intervalString = intervalSelector.getSelectedItem().toString();
                    if (intervalString.equals("Weekly")) {
                        interval = Budget.WEEKLY;
                        subInterval += Calendar.SUNDAY;
                    } else if (intervalString.equals("Monthly")) {
                        interval = Budget.MONTHLY;
                        subInterval++;
                    } else if (intervalString.equals("Yearly")) {
                        interval = Budget.YEARLY;
                    } else {
                        interval = Budget.NEVER;
                    }


					BudgetDataSource data = new BudgetDataSource(getActivity());
					data.open();
					data.createBudget(name, budget, budget, 0, interval,
							Budget.getCurrentInterval(interval), subInterval);
					data.close();
					MainActivity.notFirstTime();
					Intent i = new Intent(getActivity(), MainActivity.class);
					i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i);
				} else {
					requirement.setVisibility(TextView.VISIBLE);
				}
			}
		});

		return v;
	}
	
	private boolean numberIsValid(String number) {
		int decIndex = number.indexOf(".");
		if (decIndex == -1)
			return true;
		if (decIndex < number.length() - 3)
			return false;
		return true;
	}
}
