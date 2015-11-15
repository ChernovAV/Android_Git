package com.chernov.android.android_git;

import android.app.IntentService;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.os.ResultReceiver;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

// https://api.github.com/users/chernovav/subscriptions
public class GitService extends IntentService {

    private static final String LINK = "https://api.github.com/users/";
    private static final String SUBSCRIPTIONS = "/subscriptions";
    public static final String RECEIVER = "receiver";
    public static final String LOGIN = "login";
    public static String USERNAME;

    public static boolean isStopped = false;
    List<Item> items;

    public GitService() {
        super("GitService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        USERNAME = intent.getStringExtra(LOGIN);

        ResultReceiver receiver = intent.getParcelableExtra(RECEIVER);

        if(parseInfo()) {
            receiver.send(1, null);
        } else {
            if(isNetworkConnected()) {
                receiver.send(0, null);
            } else receiver.send(2, null);

        }

        isStopped = true;
    }

    // получает данные по URL и возвращает их в виде массива байтов
    public byte[] getUrlBytes(String urlSpec) throws IOException {
        // создаем объект URL на базе строки urlSpec
        URL url = new URL(urlSpec);
        // создаем объект подключения к заданному URL адресу
        // url.openConnection() - возвращает URLConnection (подключение по протоколу HTTP)
        // это откроет доступ для работы с методами запросов. HttpURLConnection - предоставляет подключение
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            // создаем пустой массив байтов
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            // связь с конечной точкой
            InputStream in = connection.getInputStream();
            // если подключение с интернетом отсутствует (нет кода страницы)
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            // разбираем код по 1024 байта, пока не закончится информация
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();

            // чтение закончено, выдаем массив байтов
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    // преобразует результат (массив байтов) из getUrlBytes(String) в String
    String getUrl(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    // Парсинг полученной JSON-строки
    public boolean parseItems() {

        String jsonStr = null;
        try {
            // получаем данные по URL и возвращаем их в виде массива байтов, затем делаем строку
            jsonStr = getUrl(LINK + USERNAME + SUBSCRIPTIONS);
        } catch (IOException e) {
            e.printStackTrace();
        }
        items = new ArrayList<>();
        try {
            JSONArray jArr = new JSONArray(jsonStr);

            for (int i = 0; i < jArr.length(); i++) {
                Item item = new Item();
                JSONObject JSONRepos = jArr.getJSONObject(i);
                item.setTitle(JSONRepos.getString("name"));

                String language = JSONRepos.getString("language");
                if(language.equals("null")) {
                    item.setLanguage(getString(R.string.lang));
                } else item.setLanguage(language);

                item.setForks(JSONRepos.getString("forks_count"));
                item.setStars(JSONRepos.getString("stargazers_count"));
                items.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(items.size()>0) return true;
            else return false;
    }

    public boolean parseInfo() {

        String jsonStr = null;

        try {
            jsonStr = getUrl(LINK + USERNAME);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(jsonStr == null) return false;

        try {
            JSONObject jObj = new JSONObject(jsonStr);

            String avatar = (String) jObj.get("avatar_url");
            String profile = (String) jObj.get("html_url");
            int followers = (Integer) jObj.get("followers");
            int following = (Integer) jObj.get("following");

            String user;
            if(jObj.isNull("name")) user = getString(R.string.noname);
                else user = (String) jObj.get("name");

            String company;
            if(jObj.isNull("company")) company = getString(R.string.nocompany);
                else company = (String) jObj.get("company");

            // если есть репозитории, передаем их в синглтон
            if(parseItems()) {
                GitUserInformation.getInstance(
                        avatar, profile, user, company, followers, following, items);
            } else {
                // если нет репозиториев у пользователя
                GitUserInformation.getInstance(
                        avatar,profile, user, profile, followers, following, null);
            }

        } catch (JSONException e) {
            return false;
        }
        return true;
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(getApplication().CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }
}
