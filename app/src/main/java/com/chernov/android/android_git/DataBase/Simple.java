package com.chernov.android.android_git.DataBase;

import com.j256.ormlite.field.DatabaseField;

/**
 *  Объект, который мы создаем и сохранение в базе данных.
 */
public class Simple {

	@DatabaseField
	String username;
	@DatabaseField
	int followers;
	@DatabaseField
	int following;

	Simple() {
		// needed by ormlite
	}

	public Simple(String username, int followers, int following) {
		this.username = username;
		this.followers = followers;
		this.following = following;
	}

	public String getUsername() {
		return username;
	}

	public int getFollowers() {
		return followers;
	}

	public int getFollowing() {
		return following;
	}

	@Override
	public String toString() {
		return username + followers + following;
	}
}
