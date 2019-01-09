package com.bortnikov.artem.firephotos.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.bortnikov.artem.firephotos.R;

public abstract class SingleFragmentActivity extends MvpAppCompatActivity {
    protected abstract Fragment createStartFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.container_fragments);
        if (fragment == null) {
            fragment = createStartFragment();
            fm.beginTransaction()
                    .add(R.id.container_fragments, fragment)
                    .commit();
        }
    }
}
