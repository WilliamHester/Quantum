package me.williamhester.Quantum;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.MenuItem;

/**
 * Created by William Hester on 8/4/13.
 */
public class BudgetPreferenceFragment extends PreferenceFragment {

    private Preference mChangeName;
    private Preference mClearHistory;
    private Preference mManualReset;
    private Preference mClearSurplus;
    private Preference mChangeAmount;
    private ListPreference mChangeType;
    private ListPreference mChangeDayOfWeek;
    private Preference mChangeDayOfMonth;
    private Budget mBudget;

    public BudgetPreferenceFragment(Budget budget) {
        mBudget = budget;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActivity().getActionBar() != null) {
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
            getActivity().getActionBar().setTitle(mBudget.toString() + " Settings");
        }

        loadPrefs();
        setPrefsEnabled();
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
//                ((OnFragmentOpenedListener) getActivity()).onFragmentClosed();
                return true;
            default:
                return true;
        }
    }

    private void loadPrefs() {
        addPreferencesFromResource(R.xml.preferences);

        mChangeName = findPreference("pref_change_name");
        mClearHistory = findPreference("pref_clear_history");
        mManualReset = findPreference("pref_manual_reset");
        mClearSurplus = findPreference("pref_clear_surplus");
        mChangeAmount = findPreference("pref_change_budget_amount");
        mChangeType = (ListPreference) findPreference("pref_change_reset_type");
        mChangeDayOfWeek = (ListPreference) findPreference("pref_change_reset_date");
        mChangeDayOfMonth = findPreference("pref_change_day_of_month");

        mChangeType.setValueIndex(1 + mBudget.getIterationType());

        // Set onPreferenceChanged listeners so that certain prefs are enabled/disabled
        mChangeType.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                mBudget.setIntervalType(Integer.parseInt((String) newValue));
                mBudget.setIterateOn(Integer.parseInt((String) newValue) == Budget.NEVER ? -1 : 1);
                BudgetDataSource data = new BudgetDataSource(getActivity());
                data.open();
                data.setIterateOn(mBudget.getId(), mBudget.getIterateOn());
                data.setLastInterval(mBudget.getId(), mBudget.getCurrentInterval());
                data.setIntervalType(mBudget.getId(), mBudget.getIterationType());
                data.close();
                setPrefsEnabled();
                return true;
            }
        });
    }

    private void setPrefsEnabled() {
        mManualReset.setEnabled(mBudget.getIterationType() == Budget.NEVER);
        mChangeDayOfMonth.setEnabled(mBudget.getIterationType() == Budget.MONTHLY);
        mChangeDayOfWeek.setEnabled(mBudget.getIterationType() == Budget.WEEKLY);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
                                         Preference preference) {
        if (preference == mChangeName) {
            EditTextDialog.EditTextListener callback = new EditTextDialog.EditTextListener() {
                @Override
                public void onTextEdited(String text) {
                    BudgetDataSource data = new BudgetDataSource(getActivity());
                    data.open();
                    data.setBudgetName(mBudget.getId(), text.trim());
                    data.close();
                    try {
                        getActivity().getActionBar().setTitle(text.trim() + " Settings");
                    } catch (NullPointerException e) {
                        Log.e("NPE", "Could not find activity or ActionBar");
                    }
                }
            };
            EditTextDialog dialog = new EditTextDialog(mBudget.getName(), R.string.budget_hint, callback);
            dialog.show(getFragmentManager(), "fragment_new_budget");
        } else if (preference == mClearHistory) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setMessage(R.string.are_you_sure);
            dialog.setTitle(R.string.pref_clear_history);
            dialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    BudgetDataSource data = new BudgetDataSource(getActivity());
                    data.open();
                    data.setCurrentBudget(mBudget.getId(), mBudget.getBudget());
                    data.setSurplus(mBudget.getId(), 0);
                    data.close();
                    TransactionDataSource trans = new TransactionDataSource(getActivity(),
                            mBudget.getId());
                    trans.delete();
                    mBudget.setCurrentBudget(mBudget.getBudget());
                    mBudget.setSurplus(0);
                }
            });
            dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            dialog.create().show();
        } else if (preference == mManualReset) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setMessage(R.string.are_you_sure);
            dialog.setTitle(R.string.rollover);
            dialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    BudgetDataSource data = new BudgetDataSource(getActivity());
                    data.open();
                    mBudget.rollover(data);
                    data.close();
                }
            });
            dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            }
            );
            dialog.create().show();
        } else if (preference == mClearSurplus) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setMessage(R.string.are_you_sure);
            dialog.setTitle(R.string.pref_clear_surplus);
            dialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    BudgetDataSource data = new BudgetDataSource(getActivity());
                    data.open();
                    data.setSurplus(mBudget.getId(), 0);
                    data.close();
                    mBudget.setSurplus(0);
                }
            });
            dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            }
            );
            dialog.create().show();
        } else if (preference == mChangeAmount) {
            FragmentManager fm = getFragmentManager();
            MoneyPickerDialogFragment moneyPicker = new MoneyPickerDialogFragment(false, mChangeBudgetValueCallback);
            moneyPicker.setTargetFragment(this, 0);
            moneyPicker.show(fm, "fragment_money_picker");
        } else if (preference == mChangeDayOfMonth) {
            NumberPickerDialog fragment = new NumberPickerDialog(getActivity(),
                    android.R.style.Theme_Holo_Light,
                    mChangeDateCallback,
                    R.string.pref_change_reset_date,
                    1, 31, mBudget.getIterateOn());
            FragmentManager fm = getFragmentManager();
            fragment.show(fm, "quick_spend_fragment");
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    private NumberPickerDialog.OnNumbersSetListener mChangeDateCallback = new NumberPickerDialog.OnNumbersSetListener() {
        @Override
        public void onNumbersSet(int number) {
            mBudget.setIterateOn(number);
            BudgetDataSource data = new BudgetDataSource(getActivity());
            data.open();
            data.setIterateOn(mBudget.getId(), mBudget.getIterateOn());
            data.close();
        }
    };

    private MoneyPickerDialogFragment.MoneyPickerDialogHandler mChangeBudgetValueCallback
            = new MoneyPickerDialogFragment.MoneyPickerDialogHandler() {
        @Override
        public void onValueSet(int value) {
            mBudget.setBudget(value);
            mBudget.setCurrentBudget(value);
            BudgetDataSource data = new BudgetDataSource(getActivity());
            data.open();
            data.setBudget(mBudget.getId(), mBudget.getBudget());
            data.setCurrentBudget(mBudget.getId(), mBudget.getBudget());
            data.close();
        }
    };
}
