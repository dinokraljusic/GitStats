package com.example.dinok.gitstats;

/**
 * Created by dinok on 5/19/2016.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


/**
 * @author Thiago Locatelli <thiago.locatelli@gmail.com>
 * @author Lorensius W. L T <lorenz@londatiga.net>
 */
public class GithubApp {
    private GithubSession mSession;
    private GithubDialog mDialog;
    private OAuthAuthenticationListener mListener;
    private ProgressDialog mProgress;
    private String mAuthUrl;
    private String mTokenUrl;
    private String mAccessToken;
    private String mRepo;
    private Repository repository;

    /**
     * Callback url, as set in 'Manage OAuth Costumers' page
     * (https://developer.github.com/)
     */

    public static String mCallbackUrl = "";
    private static final String AUTH_URL = "https://github.com/login/oauth/authorize?";
    private static final String TOKEN_URL = "https://github.com/login/oauth/access_token?";
    private static final String API_URL = "https://api.github.com";

    private static final String TAG = "GitHubAPI";

    public GithubApp(Context context, String clientId, String clientSecret,
                     String callbackUrl) {
        mSession = new GithubSession(context);
        mAccessToken = mSession.getAccessToken();
        mCallbackUrl = callbackUrl;
        mTokenUrl = TOKEN_URL + "client_id=" + clientId + "&client_secret="
                + clientSecret + "&redirect_uri=" + mCallbackUrl;
        mAuthUrl = AUTH_URL + "client_id=" + clientId + "&redirect_uri="
                + mCallbackUrl + "&scope=" + Constants.SCOPE /*+ "&username=" + "vk15248" + "&password=" + "1812sKapi"*/;

        GithubDialog.OAuthDialogListener listener = new GithubDialog.OAuthDialogListener() {
            @Override
            public void onComplete(String code) {
                getAccessToken(code);
            }

            @Override
            public void onError(String error) {
                mListener.onFail("Authorization failed");
            }
        };

        mDialog = new GithubDialog(context, mAuthUrl, listener);
        mProgress = new ProgressDialog(context);
        mProgress.setCancelable(false);
    }

    private void getAccessToken(final String code) {
        mProgress.setMessage("Getting access token ...");
        mProgress.show();

        new Thread() {
            @Override
            public void run() {
                Log.i(TAG, "Getting access token");
                int what = 0;

                try {
                    URL url = new URL(mTokenUrl + "&code=" + code);
                    Log.i(TAG, "Opening URL " + url.toString());
                    HttpURLConnection urlConnection = (HttpURLConnection) url
                            .openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);
                    urlConnection.connect();
                    String response = streamToString(urlConnection
                            .getInputStream());
                    Log.i(TAG, "response " + response);
                    mAccessToken = response.substring(
                            response.indexOf("access_token=") + 13,
                            response.indexOf("&scope"));
                    Log.i(TAG, "Got access token: " + mAccessToken);
                    mSession.storeAccessToken(mAccessToken, "", "", mRepo);
                } catch (Exception ex) {
                    what = 1;
                    ex.printStackTrace();
                }

                mHandler.sendMessage(mHandler.obtainMessage(what, 1, 0));
            }
        }.start();
    }

    private void fetchUserName() {
        mProgress.setMessage("Finalizing ...");

        new Thread() {
            @Override
            public void run() {
                Log.i(TAG, "Fetching user info");
                int what = 0;

                try {
                    URL url = new URL(API_URL + "/user?access_token="
                            + mAccessToken);

                    Log.d(TAG, "Opening URL " + url.toString());
                    HttpURLConnection urlConnection = (HttpURLConnection) url
                            .openConnection();
                   /* urlConnection.setRequestMethod("GET");
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);
                    urlConnection.setRequestProperty("User-Agent", "Git-Stats");
                    urlConnection.setRequestProperty("Content-Type", "application/json");
                    urlConnection.setRequestProperty("Accept", "application/json");*/
                    urlConnection.connect();
                    String response = streamToString(urlConnection
                            .getInputStream());

                    System.out.println(response);
                    JSONObject jsonObj = (JSONObject) new JSONTokener(response)
                            .nextValue();
                    String id = jsonObj.getString("id");
                    String login = jsonObj.getString("login");
                    Log.i(TAG, "Got user name: " + login);
                    mSession.storeAccessToken(mAccessToken, id, login, mRepo);
                } catch (Exception ex) {
                    what = 1;
                    ex.printStackTrace();
                }

                mHandler.sendMessage(mHandler.obtainMessage(what, 2, 0));
            }
        }.start();
    }

    public Repository getRepoData() {
        Repository repository = new Repository();
        String repoUrl = mSession.getRepo();
        if (repoUrl != null && !repoUrl.equals("")) {
            try {
                String actualRepo = repoUrl.substring(repoUrl.indexOf("com/") + 4);
                URL url = new URL(API_URL + "/repos/" + actualRepo + "?access_token=" + mAccessToken);
                //  URL url = new URL("https://api.github.com/repos/vk15248/Memory?access_token=" + mAccessToken);

                Log.d(TAG, "Opening URL " + url.toString());
                HttpURLConnection urlConnection = (HttpURLConnection) url
                        .openConnection();
                       /* urlConnection.setRequestMethod("GET");
                        String authString = Base64.encodeToString(("OAuth2 " + mAccessToken).getBytes(), Base64.DEFAULT);
                        //urlConnection.setRequestProperty("Authorization", "token " + mAccessToken);
                        urlConnection.setRequestProperty("User-Agent", "Git-Stats");
                        urlConnection.setDoInput(true);
                        urlConnection.setDoOutput(true);*/
                urlConnection.connect();
                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    String response = streamToString(urlConnection.getInputStream());
                    JSONObject jsonObj = (JSONObject) new JSONTokener(response)
                            .nextValue();
                    repository.fromJson(jsonObj);
                } else
                    repository = null;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return repository;
    }

    public void getReadMe(final Repository repo) {

        String repoUrl = mSession.getRepo();
        if (repoUrl != null && !repoUrl.equals("")) {
            try {
                String actualRepo = repoUrl.substring(repoUrl.indexOf("com/") + 4);
                URL url = new URL(API_URL + "/repos/" + actualRepo + "/readme" + "?access_token=" + mAccessToken);

                Log.d(TAG, "Opening URL " + url.toString());
                HttpURLConnection urlConnection = (HttpURLConnection) url
                        .openConnection();
                urlConnection.connect();
                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    String response = streamToString(urlConnection.getInputStream());
                    JSONObject jsonObj = (JSONObject) new JSONTokener(response)
                            .nextValue();
                    URL downloadUrl = new URL(jsonObj.getString("download_url"));
                    HttpURLConnection downloadUrlConnection = (HttpURLConnection) downloadUrl
                            .openConnection();
                    downloadUrlConnection.connect();
                    if (downloadUrlConnection.getResponseCode() == HttpURLConnection.HTTP_OK)
                        repo.setReadme(streamToString(downloadUrlConnection.getInputStream()));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void getTotalData(final Repository repo) {
        String repoUrl = mSession.getRepo();
        if (repoUrl != null && !repoUrl.equals("")) {
            try {
                String actualRepo = repoUrl.substring(repoUrl.indexOf("com/") + 4);
                URL url = new URL(API_URL + "/repos/" + actualRepo + "/stats/commit_activity" + "?access_token=" + mAccessToken);

                Log.d(TAG, "Opening URL " + url.toString());
                HttpURLConnection urlConnection = (HttpURLConnection) url
                        .openConnection();
                urlConnection.connect();
                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    String response = streamToString(urlConnection.getInputStream());
                    JSONArray jsonArray = (JSONArray) new JSONTokener(response)
                            .nextValue();
                    repo.setTotals(jsonArray);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void getDayCommits(final Repository repo) {
        String repoUrl = mSession.getRepo();
        if (repoUrl != null && !repoUrl.equals("")) {
            try {
                String actualRepo = "";
                if (repoUrl.contains("com/"))
                    actualRepo = repoUrl.substring(repoUrl.indexOf("com/") + 4);
                else
                    actualRepo = repoUrl;

                TimeZone tz = TimeZone.getTimeZone("UTC");
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
                df.setTimeZone(tz);
                String nowAsISO = df.format(new Date());

                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DATE, -2);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                String sinceAsIso = df.format(calendar.getTime());


                URL url = new URL(API_URL + "/repos/" + actualRepo + "/commits" + "?access_token=" + mAccessToken + "&since=" + sinceAsIso + "&until=" + nowAsISO);

                Log.d(TAG, "Opening URL " + url.toString());
                HttpURLConnection urlConnection = (HttpURLConnection) url
                        .openConnection();
                urlConnection.connect();
                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    String response = streamToString(urlConnection.getInputStream());
                    JSONArray jsonArray = (JSONArray) new JSONTokener(response)
                            .nextValue();
                    repo.setGraphData(jsonArray);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.arg1 == 1) {
                if (msg.what == 0) {
                    fetchUserName();
                } else {
                    mProgress.dismiss();
                    mListener.onFail("Failed to get access token");
                }
            } else {
                mProgress.dismiss();
                mListener.onSuccess();
            }
        }
    };

    public boolean hasAccessToken() {
        return (mAccessToken == null) ? false : true;
    }

    public void setListener(OAuthAuthenticationListener listener) {
        mListener = listener;
    }

    public String getUserName() {
        return mSession.getUsername();
    }

    public void authorize() {
        mDialog.show();
    }

    private String streamToString(InputStream is) throws IOException {
        String str = "";

        if (is != null) {
            StringBuilder sb = new StringBuilder();
            String line;

            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is));

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                reader.close();
            } finally {
                is.close();
            }

            str = sb.toString();
        }

        return str;
    }

    public void resetAccessToken() {
        if (mAccessToken != null) {
            mSession.resetAccessToken();
            mAccessToken = null;
        }
    }

    public void storeRepoId(Long id) {
        mSession.storeRepoInternalId(id);
    }

    public Long getInternalRepoId() {
        return mSession.getRepoInternalId();
    }


    public String getRepo() {
        return mRepo;
    }

    public void setRepo(String mRepo) {
        this.mRepo = mRepo;
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public void resetStoredRepoId() {
        mSession.resetStoredRepoId();
    }

    public interface OAuthAuthenticationListener {
        public abstract void onSuccess();

        public abstract void onFail(String error);
    }
}