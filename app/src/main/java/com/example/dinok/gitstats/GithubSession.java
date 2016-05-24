package com.example.dinok.gitstats;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Manage access token and user name. Uses shared preferences to store access
 * token and user name.
 * 
 * @author Thiago Locatelli <thiago.locatelli@gmail.com>
 * @author Lorensius W. L T <lorenz@londatiga.net>
 * 
 */
public class GithubSession {

	private SharedPreferences sharedPref;
	private Editor editor;

	private static final String SHARED = "Github_Preferences";
	private static final String API_USERNAME = "username";
	private static final String API_ID = "id";
	private static final String API_ACCESS_TOKEN = "access_token";
	private static final String REPO = "repo";

	public GithubSession(Context context) {
		sharedPref = context.getSharedPreferences(SHARED, Context.MODE_PRIVATE);
		editor = sharedPref.edit();
	}

	/**
	 * 
	 * @param accessToken
	 * @param expireToken
	 * @param expiresIn
	 * @param username
	 */
	public void storeAccessToken(String accessToken, String id, String username, String repo) {
		editor.putString(API_ID, id);
		editor.putString(API_ACCESS_TOKEN, accessToken);
		editor.putString(API_USERNAME, username);
		editor.putString(REPO, repo);
		editor.commit();
	}

	public void storeAccessToken(String accessToken) {
		editor.putString(API_ACCESS_TOKEN, accessToken);
		editor.commit();
	}

	/**
	 * Reset access token and user name
	 */
	public void resetAccessToken() {
		editor.putString(API_ID, null);
		editor.putString(API_ACCESS_TOKEN, null);
		editor.putString(API_USERNAME, null);
		editor.putString(REPO, null);
		editor.commit();
	}

	/**
	 * Get user name
	 * 
	 * @return User name
	 */
	public String getUsername() {
		return sharedPref.getString(API_USERNAME, null);
	}

	/**
	 * Get access token
	 * 
	 * @return Access token
	 */
	public String getAccessToken() {
		return sharedPref.getString(API_ACCESS_TOKEN, null);
	}

	public String getRepo() {
		return sharedPref.getString(REPO, null);
	}
}