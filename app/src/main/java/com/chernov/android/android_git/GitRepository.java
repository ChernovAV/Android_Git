package com.chernov.android.android_git;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chernov.android.android_git.DataBase.HelperFactory;
import com.chernov.android.android_git.DataBase.Simple;
import com.chernov.android.android_git.DataBase.SimpleDao;
import com.chernov.android.android_git.DataBase.TimeDatabase;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.squareup.picasso.Picasso;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.List;

public class GitRepository extends ListFragment implements View.OnClickListener {

    Adapter adapter;
    ImageView logo;
    List<Item> items = GitUserInformation.get_List();
    TextView user, followers, following;
    ImageButton btnSave, btnWeb;
    // share button
    private ShareButton btnSend;
    // link for facebook
    ShareLinkContent content;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // при смене ориентации экрана фрагмент сохраняет свое состояние. onDestroy не вызывается
        setRetainInstance(true);
        progressbarInvisible();
        // инициализируем facebook
        FacebookSdk.sdkInitialize(getActivity());
        if(items!=null) {
            // создаем адаптер
            adapter = new Adapter(getActivity(), items);
            // setAdapter
            setListAdapter(adapter);
        }
        // put link for facebook
        content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse(GitUserInformation.get_Profile().toString()))
                .build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle instance) {
        View view = inflater.inflate(R.layout.git_repository, container, false);
        // регистрируем компоненты
        setComponents(view);

        Picasso.with(getActivity())
                .load(GitUserInformation.get_Avatar())
                .resize(150, 150)
                .error(R.mipmap.github)
                .into(logo);

        if(instance == null) {
            user.setText(GitUserInformation.get_User() + "/" + GitUserInformation.get_Company());
            followers.setText(Integer.toString(GitUserInformation.get_Folowers()) + "\n"
                    + getString(R.string.followers));
            following.setText(Integer.toString(GitUserInformation.get_Folowing()) + "\n"
                    + getString(R.string.following));

        }
        // This is how, DatabaseHelper can be initialized for future use
        HelperFactory.setHelper(getActivity());

        btnSend = (ShareButton)view.findViewById(R.id.divo);
        btnSend.setShareContent(content);
        return view;
    }

    private void setComponents(View view) {
        followers = (TextView)view.findViewById(R.id.followers);
        following = (TextView)view.findViewById(R.id.following);
        btnSave = (ImageButton)view.findViewById(R.id.save);
        btnWeb = (ImageButton)view.findViewById(R.id.internet);
        btnSave.setOnClickListener(this);
        btnWeb.setOnClickListener(this);
        logo = (ImageView)view.findViewById(R.id.photo);
        user = (TextView)view.findViewById(R.id.username);
    }

    private void progressbarInvisible() {
        ((SingleFragmentActivity) getActivity())
                .getSupportActionBar()
                .getCustomView()
                .findViewById(R.id.myProgressBar)
                .setVisibility(View.INVISIBLE);
    }

    // get hashcode in log
    private void getHascode() {
        // Add code to print out the key hash
        try {
            PackageInfo info = getActivity().getPackageManager().getPackageInfo(
                    "com.chernov.android.android_git", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
        } catch (NoSuchAlgorithmException e) {}
    }

    private void addNewItem() {

        Simple simple = new Simple(
                GitUserInformation.get_User(),
                GitUserInformation.get_Folowers(),
                GitUserInformation.get_Folowing());

        // store it in the database
        try {
            HelperFactory.getHelper().getSimpleDao().create(simple);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String massage = getString(R.string.saved) + "\n" +
                getString(R.string.Username)  + " " + GitUserInformation.get_User()     + "\n" +
                getString(R.string.Followers) + " " + GitUserInformation.get_Folowers() + "\n" +
                getString(R.string.Following) + " " + GitUserInformation.get_Folowing();

        Toast toast = Toast.makeText(getActivity(), massage, Toast.LENGTH_SHORT);
        toast.show();

        //ReadDB();
    }

    private void ReadDB() {
        try {
            SimpleDao simpleDao = HelperFactory.getHelper().getSimpleDao();

            List<Simple> simpleList = simpleDao.getAllSimple();

            for(int i = 0; i<simpleList.size(); i++) {
                Log.i("myLog", "User " + String.valueOf(simpleList.get(i).getUsername()));
                Log.i("myLog", "Followers " + String.valueOf(simpleList.get(i).getFollowers()));
                Log.i("myLog", "Following " + String.valueOf(simpleList.get(i).getFollowing()));
            }

        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(savedInstanceState!=null) {
            user.setText(savedInstanceState.getString(getString(R.string.restoreName)));
            followers.setText(savedInstanceState.getString(getString(R.string.restoreFollowers)));
            following.setText(savedInstanceState.getString(getString(R.string.restoreFollowing)));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(getActivity());
    }

    @Override
    public void onPause() {
        super.onPause();
        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(getActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.internet:
                openProfile();
                break;
            case R.id.save:
                addNewItem();
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(getString(R.string.restoreName), user.getText().toString());
        outState.putString(getString(R.string.restoreFollowers), followers.getText().toString());
        outState.putString(getString(R.string.restoreFollowing), following.getText().toString());
        super.onSaveInstanceState(outState);
    }

    public void openProfile() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(GitUserInformation.get_Profile()));
        startActivity(browserIntent);
    }
}