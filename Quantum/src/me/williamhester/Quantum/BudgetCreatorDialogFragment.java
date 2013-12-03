package me.williamhester.Quantum;

import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
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
import android.widget.Toast;

import java.util.Calendar;

public class BudgetCreatorDialogFragment extends DialogFragment {

    private Fragment mLauncher;

    public BudgetCreatorDialogFragment() {
        this(null);
    }

    public BudgetCreatorDialogFragment(Fragment launcher) {
        mLauncher = launcher;
    }
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(STYLE_NORMAL, android.R.style.Theme_Holo_Light_Dialog);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View v = inflater.inflate(R.layout.budget_creator_dialog, null);

        final TextView intervalText = (TextView) v.findViewById(R.id.interval_text);
        intervalText.setVisibility(View.INVISIBLE);
		
		getDialog().setTitle(R.string.new_budget);

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
                    subIntervalSelector.setVisibility(View.GONE);
                    intervalText.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> a) {}
        });
		
		final EditText budgetName = (EditText)v.findViewById(R.id.budget_title);
		final EditText budgetValue = (EditText)v.findViewById(R.id.budget_value);
		
		Button cancel = (Button) v.findViewById(R.id.cancel);
		cancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				dismiss();
			}
		});
		Button save = (Button) v.findViewById(R.id.save);
		save.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				// Retrieve all of the info from the input fields
				final String name = budgetName.getText().toString().trim();
				final String value = budgetValue.getText().toString();
				
				if (!name.isEmpty() && !value.isEmpty()
						&& numberIsValid(value)) {
					// Retrieve the input, because it has now been validated
					final int budget = (int) (Double.parseDouble(budgetValue.getText()
							.toString()) * 100);

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

					// Save the input
					BudgetDataSource data = new BudgetDataSource(getActivity());
					data.open();
                    // The budget being created here is merely to create a placeholder for the
                    // budget to be modified by the subsequent dialog, where interval information
                    // will be stored
                    Budget b = data.createBudget(name, budget, budget, 0, interval,
                            Budget.getCurrentInterval(interval), subInterval);
					data.close();
					
					// Callback to Main
					((OnCreateNewBudget) getActivity()).onCreateNewBudget(b.getId());
//                    ((OnFragmentOpenedListener) getActivity()).onFragmentOpened(mLauncher);

					dismiss();

				} else {
					Context context = getActivity().getApplicationContext();
					CharSequence text = "Please fill out all fields.";
					int duration = Toast.LENGTH_SHORT;

					Toast toast = Toast.makeText(context, text, duration);
					toast.show();
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
	
	public interface OnCreateNewBudget {
		void onCreateNewBudget(long id);
	}
}
