package com.gmail.gogobebe2.thedayahead;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.ConnectException;

public class TimetableFragment extends TheDayAheadFragment implements View.OnClickListener {
    private RelativeLayout relativeLayout;
    private Document kmarDocument = null;
    private SharedPreferences.Editor loginPrefEditor;

    public TimetableFragment() { /* Required empty public constructor */}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // So keyboard doesnt popup on startup:
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        relativeLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_timetable, parent, false);

        initKmarLoginConnection();

        Button loginButton = (Button) relativeLayout.findViewById(R.id.login_button);
        loginButton.setOnClickListener(this);

        EditText usernameField = (EditText) relativeLayout.findViewById(R.id.editText_username);
        EditText passwordField = (EditText) relativeLayout.findViewById(R.id.editText_password);
        CheckBox rememberMeCheckBox = (CheckBox) relativeLayout.findViewById(R.id.checkBox_rememberMe);

        SharedPreferences loginPreferences = getActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        loginPrefEditor = loginPreferences.edit();

        boolean saveLogin = loginPreferences.getBoolean("saveLogin", false);
        if (saveLogin) {
            usernameField.setText(loginPreferences.getString("username", ""));
            passwordField.setText(loginPreferences.getString("password", ""));
            rememberMeCheckBox.setChecked(true);
        }

        return relativeLayout;
    }

    @NonNull
    @Override
    String getTitle() {
        return "Timetable";
    }

    @Override
    public String getLoggingTag() {
        return Utils.createTagName(TimetableFragment.class);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initKmarLoginConnection() {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    kmarDocument = Jsoup.connect(getString(R.string.kmar_login_url)).get();
                    if (kmarDocument == null) throw new NullPointerException();
                } catch (IOException e) {
                    if (e instanceof ConnectException) Log.w(getLoggingTag(),
                            "ConnectException, Kmar Portal down or internet down.");
                    else {
                        Log.w(getLoggingTag(), "Failed to connect to Kmar Portal.");
                        e.printStackTrace();
                    }
                    return false;
                }
                return true;
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
            Log.d(instance.getLoggingTag(), "Using clearCookies code for API >=" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            Log.d(instance.getLoggingTag(), "Using clearCookies code for API <" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(instance.getContext());
            cookieSyncManager.startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncManager.stopSync();
            cookieSyncManager.sync();
        }
    }


    @SuppressLint({"AddJavascriptInterface", "SetJavaScriptEnabled"})
    @Override
    public void onClick(View view) {
        EditText usernameEditText = (EditText) getActivity().findViewById(R.id.editText_username);
        EditText passwordEditText = (EditText) getActivity().findViewById(R.id.editText_password);
        if (view.getId() == R.id.checkBox_rememberMe) {
            CheckBox checkBox = (CheckBox) view;

            // So it doesnt show soft input:
            ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(usernameEditText.getWindowToken(), 0);

            String usernameString = usernameEditText.getText().toString();
            String passwordString = passwordEditText.getText().toString();

            if (checkBox.isChecked()) {
                loginPrefEditor.putBoolean("saveLogin", true);
                loginPrefEditor.putString("username", usernameString);
                loginPrefEditor.putString("password", passwordString);
                loginPrefEditor.commit();
            }
            else {
                loginPrefEditor.clear();
                loginPrefEditor.commit();
            }
        }
        else if (view.getId() == R.id.login_button) {
            WebView webView = new WebView(getContext());

            webView.setVisibility(View.INVISIBLE);

            webView.clearCache(true);
            webView.clearHistory();
            clearCookies(this);

            class HTMLRetrieverJavaScriptInterface {
                @JavascriptInterface
                void showHTML(String html) {
                    // TODO make this method create a TimetableParser instance using this html and
                    // store it in a instance variable of the TimetableFragment class.
                    Toast.makeText(getContext(), html, Toast.LENGTH_LONG).show();
                }
            }

            webView.addJavascriptInterface(new HTMLRetrieverJavaScriptInterface(), "HtmlRetriever");

            webView.setWebViewClient(new WebViewClient() {
                private boolean showWebViewNext = false;

                @Override
                public void onLoadResource(WebView webView, String destinationUrl) {
                    toggleLoadingVisual(true);
                    super.onLoadResource(webView, destinationUrl);
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView webView, String destinationUrl) {
                    if (showWebViewNext) {
                        webView.setVisibility(View.VISIBLE); // remove and reformat timetable.
                        showWebViewNext = false;
                    }
                    return super.shouldOverrideUrlLoading(webView, destinationUrl);
                }

                @Override
                public void onPageFinished(WebView webView, String urlLoaded) {
                    toggleLoadingVisual(false);

                    final String LOGIN_JAVASCRIPT = "javascript:document.getElementById(\"loginSubmit\").click()";
                    final String HTML_RETRIEVER_JAVASCRIPT = "javascript:window.HtmlRetriever.showHTML" +
                            "('<html>' + document.getElementsByTagName('html')[0].innerHTML + '</html>');";
                    if (urlLoaded.equals(getString(R.string.kmar_timetable_url))) {
                        webView.loadUrl(HTML_RETRIEVER_JAVASCRIPT);
                    } else if (!urlLoaded.equals(LOGIN_JAVASCRIPT)) {
                        showWebViewNext = true;
                        webView.loadUrl(LOGIN_JAVASCRIPT);
                    }
                }
            });

            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);

            relativeLayout.addView(webView);

            Element loginUsernameElement = kmarDocument.select("input#loginUsername").first();
            Element loginPasswordElement = kmarDocument.select("input#loginPassword").first();

            loginUsernameElement.attr("value", usernameEditText.getText().toString());
            loginPasswordElement.attr("value", passwordEditText.getText().toString());

            webView.loadData(kmarDocument.html(), "text/html", "UTF-8");

            // I then call the click() function on the loginSubmit button when the page is finished
            // loading in the overrided onPageFinished(WebView webView, String url) method.
        }
    }

    private void toggleLoadingVisual(boolean on) {
        final CheckBox rememberMeCheckbox = (CheckBox) relativeLayout.findViewById(R.id.checkBox_rememberMe);
        final ProgressBar progressBar = (ProgressBar) relativeLayout.findViewById(R.id.progressBar);
        if (on) {
            progressBar.setVisibility(View.VISIBLE);
            rememberMeCheckbox.setVisibility(View.INVISIBLE);
        }
        else {
            progressBar.setVisibility(View.INVISIBLE);
            rememberMeCheckbox.setVisibility(View.VISIBLE);
        }

    }
}
