package me.williamhester.Quantum.ui.fragments;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import me.williamhester.Quantum.R;

public class WelcomeFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.welcome, null);

        TextView welcome = (TextView) view.findViewById(R.id.welcome);
        TextView welcomeDescription = (TextView) view.findViewById(R.id.welcome_description);
        Button letsGo = (Button) view.findViewById(R.id.lets_go);

        Typeface slabBold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/RobotoSlab-Bold.ttf");
        Typeface slabThin = Typeface.createFromAsset(getActivity().getAssets(), "fonts/RobotoSlab-Thin.ttf");
        Typeface slabReg = Typeface.createFromAsset(getActivity().getAssets(), "fonts/RobotoSlab-Regular.ttf");

        welcome.setTypeface(slabBold);
        welcomeDescription.setTypeface(slabThin);
        letsGo.setTypeface(slabReg);

        letsGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getFragmentManager().beginTransaction()
                        .replace(android.R.id.content, new BudgetSetupFragment())
                        .commit();
            }
        });

		// setupButton(view);
		return view;
	}
	
	@SuppressWarnings("unused")
	private void setupButton(View v) {
		Button save = (Button)v.findViewById(R.id.save);
		save.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
				}
			
		});
	}
}
