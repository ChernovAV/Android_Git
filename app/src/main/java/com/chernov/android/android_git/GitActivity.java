package com.chernov.android.android_git;

import android.support.v4.app.Fragment;

public class GitActivity extends SingleFragmentActivity {
    @Override
    public Fragment createFragment() {
        return new GitLogin();
    }
}
