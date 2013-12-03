package me.williamhester.Quantum;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;

/**
 * Created by William on 12/2/13.
 */
public class SettingsContainerActivity extends Activity {

    public static String BUDGET_ID = "BUDGET ID";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.container_activity);
        Bundle extras = getIntent().getExtras();
        long id = extras.getLong(BUDGET_ID);
        BudgetPreferenceFragment prefs = new BudgetPreferenceFragment(id);
        getFragmentManager().beginTransaction()
                .replace(R.id.container, prefs)
                .commit();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (getFragmentManager().getBackStackEntryCount() == 0)
                finish();
            getFragmentManager().popBackStack();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

}
