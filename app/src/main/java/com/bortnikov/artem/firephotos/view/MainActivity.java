package com.bortnikov.artem.firephotos.view;

import android.support.v4.app.Fragment;

public class MainActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createStartFragment() {
        return new ListFragment();
    }
}
