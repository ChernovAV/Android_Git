package com.chernov.android.android_git;

import java.util.List;

public class GitUserInformation {

    private static GitUserInformation instance;
    private static String link_avatar;
    private static String link_profile;
    private static int count_folowing;
    private static int count_folowers;
    private static String user_name;
    private static String company_name;
    private static List<Item> items;

     public static synchronized GitUserInformation getInstance(
             String avatar, String profile, String name, String comp, int folowing, int folowers,
             List<Item> news) {
        if (instance == null) {
           instance = new GitUserInformation();
        }
         user_name = name;
         company_name = comp;
         link_avatar = avatar;
         link_profile = profile;
         count_folowing = folowing;
         count_folowers = folowers;
         items = news;
            return instance;
        }

    public static String get_Avatar() {
        return link_avatar;
    }

    public static String get_Profile() {
        return link_profile;
    }

    public static int get_Folowing() {
        return count_folowing;
    }

    public static int get_Folowers() {
        return count_folowers;
    }

    public static String get_User() {
        return user_name;
    }

    public static String get_Company() {
        return company_name;
    }

    public static List<Item> get_List() {
        return items;
    }


}
