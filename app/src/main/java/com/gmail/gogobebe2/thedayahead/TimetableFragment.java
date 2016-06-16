package com.gmail.gogobebe2.thedayahead;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.ConnectException;

public class TimetableFragment extends TheDayAheadFragment {
    private RelativeLayout relativeLayout;
    private Document kmarDocument = null;
    private WebView webView;
    private final String JAVASCRIPT_LOGIN_BUTTON_SCRIPT = "javascript:document.getElementById('loginSubmit').click();";

    public TimetableFragment() { /* Required empty public constructor */}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        relativeLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_timetable, parent, false);
        loadTimetable();
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
    private void loadTimetable() {
        webView = new WebView(getContext());

        webView.setVisibility(View.INVISIBLE);

        webView.clearCache(true);
        webView.clearHistory();
        clearCookies(this);

        final ProgressBar progressBar = (ProgressBar) relativeLayout.findViewById(R.id.progressBar);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String destinationUrl) {
                final String currentUrl = view.getUrl();
                final String kmarLoginUrl = getString(R.string.kmar_login_url);
                final String kmarMainUrl = getString(R.string.kmar_url);
                final String kmarTimetableUrl = getString(R.string.kmar_timetable_url);

                // TODO make it work with updated code.

                if (destinationUrl.equals(kmarLoginUrl)) {
                    // It goes to the login url if details incorrect.
                    // If it does, make an error message for user.
                    Toast.makeText(getContext(),
                            "Error logging in! Maybe the password or username is incorrect.",
                            Toast.LENGTH_LONG).show();
                } else if ((currentUrl.equals(kmarMainUrl) || currentUrl.equals(kmarLoginUrl))
                        && destinationUrl.equals(kmarMainUrl)) {
                    // If the the main url isn't the 1st url loaded, it means the user is logged in.
                    // (Since if he wasnt logged in, then it would've gone to the login url)
                    // Once logged in, redirect to the timetable page:
                    view.loadUrl(kmarTimetableUrl);
                } else Log.w(getLoggingTag(), "Tried loading unexpected url: " + destinationUrl);

                progressBar.setVisibility(View.VISIBLE);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        relativeLayout.addView(webView);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    kmarDocument = Jsoup.connect(getString(R.string.kmar_url)).get();
                    Toast.makeText(getContext(), "Successfully connected to the Kmar Portal.",
                            Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    if (e instanceof ConnectException) Log.w(getLoggingTag(),
                            "ConnectException, Kmar Portal down or internet down.");
                    else {
                        Log.w(getLoggingTag(), "Failed to connect to Kmar Portal.");
                        e.printStackTrace();
                    }
                    Toast.makeText(getContext(),
                            "Failed to connect to the Kmar Portal.", Toast.LENGTH_LONG).show();
                    this.cancel(true);
                }
                return null;
            }
        }.execute();
    }

    void onClickLoginButton(Button button) {
            Element loginUsernameElement = kmarDocument.select("#loginUsername").first();
            Element loginPasswordElement = kmarDocument.select("#loginPassword").first();

            EditText usernameEditText = (EditText) getActivity().findViewById(R.id.editText_username);
            EditText passwordEditText = (EditText) getActivity().findViewById(R.id.editText_password);

            loginUsernameElement.attr("value", usernameEditText.getText().toString());
            loginPasswordElement.attr("value", passwordEditText.getText().toString());

            webView.loadUrl(JAVASCRIPT_LOGIN_BUTTON_SCRIPT);
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
}
