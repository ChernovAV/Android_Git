package com.chernov.android.android_git;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.chernov.android.android_git.DataBase.HelperFactory;

/**
 * Created by Android on 11.11.2015.
 */
public abstract class SingleFragmentActivity extends AppCompatActivity {

    protected abstract Fragment createFragment();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.git_action);
        setContentView(R.layout.git_main);

        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.fragmentContainer);

        if (fragment == null) {
            fragment = createFragment();
            manager.beginTransaction()
                    .add(R.id.fragmentContainer, fragment)
                    .commit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // предотвратим утечку памяти из-за незакрытого соединения с БД
        HelperFactory.releaseHelper();
        // если сервис не остановлен, останавливаем его
        if(!GitService.isStopped) {
            stopService(new Intent(this, GitService.class));
        }

        System.gc();
    }
}
