package me.williamhester.Quantum;

import android.app.Fragment;

/**
 * Created by William Hester on 8/29/13.
 */
public class FragmentStack {

    private FragmentNode top;

    public void push(Fragment f) {
        if (top == null) {
            top = new FragmentNode(f, null);
        } else {
            top = new FragmentNode(f, top);
        }
    }

    public Fragment pop() {
        Fragment f = top.fragment;
        top = top.below;
        return f;
    }

    private class FragmentNode {
        Fragment fragment;
        FragmentNode below;

        public FragmentNode(Fragment f, FragmentNode below) {
            fragment = f;
            this.below = below;
        }
    }

}
