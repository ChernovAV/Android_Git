package com.chernov.android.android_git;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.os.ResultReceiver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class GitLogin extends Fragment {

    private EditText mUsername;
    private Button mEnter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // при смене ориентации экрана фрагмент сохраняет свое состояние. onDestroy не вызывается
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle instance) {
        View view = inflater.inflate(R.layout.git_login, container, false);

        registrationComponents(view);

        return view;
    }

    // общение между потоками через ResultReceiver
    private final ResultReceiver resultReceiver = new ResultReceiver(new Handler()) {
        @SuppressWarnings("unchecked")
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            /**
             *  если resultCode==1, принимаем List
             *  после передергиваем адаптер
             */

            switch(resultCode) {
                case 0:
                    mUsername.setError(getString(R.string.incorrect));
                    offProgressBar();
                    mEnter.setClickable(true);
                    break;
                case 1:
                    startRepositoryFragment();
                    break;
                case 2:
                    mUsername.setError(getString(R.string.nointernet));
                    offProgressBar();
                    mEnter.setClickable(true);
                    break;
            }

        };
    };

    private void offProgressBar() {
        ((SingleFragmentActivity) getActivity())
                .getSupportActionBar()
                .getCustomView()
                .findViewById(R.id.myProgressBar)
                .setVisibility(View.INVISIBLE);
    }

    private void registrationComponents(View w) {
        mUsername = (EditText) w.findViewById(R.id.login_username);
        mEnter = (Button) w.findViewById(R.id.enter);
        mEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUsername.getText().toString().isEmpty()) {
                    mUsername.setError(getString(R.string.name_is_empty));
                } else {
                    startService();
                }
            }
        });
    }

    private void startRepositoryFragment() {

        FragmentManager manager = getActivity().getSupportFragmentManager();
        Fragment fragment = new GitRepository();
        manager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit();
    }

    // запускаем сервис, парсим страницу
    private void startService() {
        mEnter.setClickable(false);
        progressbarVisible();
        Intent intent = new Intent(getActivity(), GitService.class);
        intent.putExtra(GitService.RECEIVER, resultReceiver);
        intent.putExtra(GitService.LOGIN, mUsername.getText().toString());
        getActivity().startService(intent);
    }

    private void progressbarVisible() {
        ((SingleFragmentActivity) getActivity())
                .getSupportActionBar()
                .getCustomView()
                .findViewById(R.id.myProgressBar)
                .setVisibility(View.VISIBLE);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
