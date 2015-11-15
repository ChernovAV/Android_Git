package com.chernov.android.android_git.DataBase;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;

/**
 * Created by Android on 14.11.2015.
 */
public class HelperFactory {

    private static TimeDatabase databaseHelper;

    public static TimeDatabase getHelper(){
        return databaseHelper;
    }

    public static void setHelper(Context context){
        databaseHelper = OpenHelperManager.getHelper(context, TimeDatabase.class);
    }

    public static void releaseHelper(){
        OpenHelperManager.releaseHelper();
        databaseHelper = null;
    }
}
