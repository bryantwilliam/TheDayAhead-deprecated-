package com.gmail.gogobebe2.thedayahead.timetable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.gmail.gogobebe2.thedayahead.MainActivity;
import com.gmail.gogobebe2.thedayahead.R;
import com.gmail.gogobebe2.thedayahead.TheDayAheadFragment;
import com.gmail.gogobebe2.thedayahead.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.ConnectException;

public class TimetableFragment extends TheDayAheadFragment implements View.OnClickListener {

    private RelativeLayout loginRelativeLayout;
    private LinearLayout linearLayout;


    private Document kmarDocument = null;
    private final String KMAR_LOGIN_URL = "https://portal.sanctamaria.school.nz/student/index.php/login";
    private final String KMAR_TIMETABLE_URL = "https://portal.sanctamaria.school.nz/student/index.php/timetable";

    private String kmarTimetableHTML;

    public TimetableFragment() { /* Required empty public constructor */}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        linearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_timetable, parent, false);
        loginRelativeLayout = (RelativeLayout) linearLayout.findViewById(R.id.relativelayout_timetablelogin);

        if (kmarTimetableHTML == null) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            initKmarLoginConnection();

            Button loginButton = (Button) loginRelativeLayout.findViewById(R.id.login_button);
            loginButton.setOnClickListener(this);

            EditText usernameField = (EditText) loginRelativeLayout.findViewById(R.id.editText_username);
            EditText passwordField = (EditText) loginRelativeLayout.findViewById(R.id.editText_password);
            CheckBox rememberMeCheckBox = (CheckBox) loginRelativeLayout.findViewById(R.id.checkBox_rememberMe);

            SharedPreferences loginPreferences = getLoginPreferencesInstance();

            boolean saveLogin = loginPreferences.getBoolean("saveLogin", false);
            if (saveLogin) {
                usernameField.setText(loginPreferences.getString("username", ""));
                passwordField.setText(loginPreferences.getString("password", ""));
                rememberMeCheckBox.setChecked(true);
            }
        }

        return linearLayout;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (kmarTimetableHTML != null) {
            MainActivity.timetable = new Timetable(kmarTimetableHTML, this);
        }
    }

    private SharedPreferences getLoginPreferencesInstance() {
        return getActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    protected String getTitle() {
        return "Timetable";
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initKmarLoginConnection() {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    kmarDocument = Jsoup.connect(KMAR_LOGIN_URL).get();
                } catch (IOException e) {
                    if (e instanceof ConnectException) Log.w(Utils.getTagName(this),
                            "ConnectException, Kmar Portal down or internet down.");
                    else {
                        Log.w(Utils.getTagName(this), "Failed to connect to Kmar Portal.");
                        e.printStackTrace();
                    }
                    return false;
                }
                return kmarDocument != null;
            }

            @Override
            protected void onPostExecute(Boolean succesful) {
                if (succesful)
                    Toast.makeText(getContext(), "Successfully connected to the Kmar Portal.",
                            Toast.LENGTH_SHORT).show();
                else Toast.makeText(getContext(),
                        "Failed to connect to the Kmar Portal.", Toast.LENGTH_LONG).show();
            }
        }.execute();

    }

    @SuppressWarnings("deprecation")
    private static void clearCookies(TimetableFragment instance) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Log.d(Utils.getTagName(instance), "Using clearCookies code for API >=" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            Log.d(Utils.getTagName(instance), "Using clearCookies code for API <" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(instance.getContext());
            cookieSyncManager.startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncManager.stopSync();
            cookieSyncManager.sync();
        }
    }

    private void updateLoginPreferences(CheckBox rememberMeCheckBox, EditText usernameEditText,
                                        EditText passwordEditText) {
        String usernameString = usernameEditText.getText().toString();
        String passwordString = passwordEditText.getText().toString();

        SharedPreferences.Editor loginPrefEditor = getLoginPreferencesInstance().edit();
        if (rememberMeCheckBox.isChecked()) {
            loginPrefEditor.putBoolean("saveLogin", true);
            loginPrefEditor.putString("username", usernameString);
            loginPrefEditor.putString("password", passwordString);
            loginPrefEditor.apply();
        } else {
            loginPrefEditor.clear();
            loginPrefEditor.apply();
        }
    }

    @Keep
    public class HTMLRetrieverJavaScriptInterface {
        TimetableFragment timetableFragment;

        HTMLRetrieverJavaScriptInterface(TimetableFragment timetableFragment) {
            super();
            this.timetableFragment = timetableFragment;
        }

        @JavascriptInterface
        void showHTML(String html) {
            kmarTimetableHTML = html;
            MainActivity.timetable = new Timetable(kmarTimetableHTML, timetableFragment);
        }
    }

    @SuppressLint({"AddJavascriptInterface", "SetJavaScriptEnabled"})
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.login_button) {
            if (kmarDocument == null) initKmarLoginConnection();
            else {
                EditText usernameEditText = (EditText) loginRelativeLayout.findViewById(R.id.editText_username);
                EditText passwordEditText = (EditText) loginRelativeLayout.findViewById(R.id.editText_password);

                updateLoginPreferences((CheckBox) loginRelativeLayout.findViewById(R.id.checkBox_rememberMe),
                        usernameEditText, passwordEditText);

                WebView webView = new WebView(getContext());

                webView.setVisibility(View.INVISIBLE);

                webView.clearCache(true);
                webView.clearHistory();
                clearCookies(this);

                webView.addJavascriptInterface(new HTMLRetrieverJavaScriptInterface(this), "HtmlRetriever");

                webView.setWebViewClient(new WebViewClient() {

                    @Override
                    public void onLoadResource(WebView webView, String destinationUrl) {
                        CheckBox rememberMeCheckbox = (CheckBox) loginRelativeLayout.findViewById(R.id.checkBox_rememberMe);
                        ProgressBar progressBar = (ProgressBar) loginRelativeLayout.findViewById(R.id.progressBar);
                        progressBar.setVisibility(View.VISIBLE);
                        rememberMeCheckbox.setVisibility(View.INVISIBLE);
                        super.onLoadResource(webView, destinationUrl);
                    }

                    @Override
                    public void onPageFinished(WebView webView, String urlLoaded) {
                        final String LOGIN_JAVASCRIPT = "javascript:document.getElementById(\"loginSubmit\").click()";
                        final String HTML_RETRIEVER_JAVASCRIPT = "javascript:window.HtmlRetriever.showHTML" +
                                "('<html>' + document.getElementsByTagName('html')[0].innerHTML + '</html>');";
                        if (urlLoaded.equals(KMAR_TIMETABLE_URL)) {
                            // debug: webView.setVisibility(View.VISIBLE);
                            webView.loadUrl(HTML_RETRIEVER_JAVASCRIPT);
                        } else if (!urlLoaded.equals(LOGIN_JAVASCRIPT)) {
                            webView.loadUrl(LOGIN_JAVASCRIPT);
                        }
                    }
                });

                WebSettings webSettings = webView.getSettings();
                webSettings.setJavaScriptEnabled(true);

                loginRelativeLayout.addView(webView);

                Element loginUsernameElement = kmarDocument.getElementById("loginUsername");
                Element loginPasswordElement = kmarDocument.getElementById("loginPassword");

                loginUsernameElement.attr("value", usernameEditText.getText().toString());
                loginPasswordElement.attr("value", passwordEditText.getText().toString());

                webView.loadData(kmarDocument.html(), "text/html", "UTF-8");

                // I then call the click() function on the loginSubmit button when the page is finished
                // loading in the overridden onPageFinished(WebView webView, String url) method.
            }
        }
    }

    public RelativeLayout getLoginRelativeLayout() {
        return this.loginRelativeLayout;
    }

    public LinearLayout getTimetableLinearLayout() {
        return this.linearLayout;
    }
}
