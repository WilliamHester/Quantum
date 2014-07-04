package me.williamhester.Quantum.ui.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;

import me.williamhester.Quantum.ui.fragments.WelcomeFragment;

public class WelcomeActivity extends Activity {

	@Override
	public void onStart() {
		super.onStart();
		ActionBar action = getActionBar();
        if (action != null)
		    action.hide();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getFragmentManager().beginTransaction()
        .replace(android.R.id.content, new WelcomeFragment())
        .commit();
	}
	
	@Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	System.exit(0);  
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	this.finish();  
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }
	
}
