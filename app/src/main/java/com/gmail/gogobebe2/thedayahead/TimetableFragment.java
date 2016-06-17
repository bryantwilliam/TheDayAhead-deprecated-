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

public class TimetableFragment extends TheDayAheadFragment implements View.OnClickListener {
    private RelativeLayout relativeLayout;
    private Document kmarDocument = null;

    public TimetableFragment() { /* Required empty public constructor */}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        relativeLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_timetable, parent, false);
        Button loginButton = (Button) relativeLayout.findViewById(R.id.login_button);
        loginButton.setOnClickListener(this);
        initKmarLoginConnection();
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

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.login_button) {
            WebView webView = new WebView(getContext());

            webView.setVisibility(View.INVISIBLE);

            webView.clearCache(true);
            webView.clearHistory();
            clearCookies(this);

            final ProgressBar progressBar = (ProgressBar) relativeLayout.findViewById(R.id.progressBar);

            webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String destinationUrl) {
                    // TODO make it work with updated code.

                    progressBar.setVisibility(View.VISIBLE);
                    super.shouldOverrideUrlLoading(view, destinationUrl);
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

            Element loginUsernameElement = kmarDocument.select("input#loginUsername").first();
            Element loginPasswordElement = kmarDocument.select("input#loginPassword").first();

            EditText usernameEditText = (EditText) getActivity().findViewById(R.id.editText_username);
            EditText passwordEditText = (EditText) getActivity().findViewById(R.id.editText_password);

            loginUsernameElement.attr("value", usernameEditText.getText().toString());
            loginPasswordElement.attr("value", passwordEditText.getText().toString());

            Element head = kmarDocument.select("head").first();

            head.append(
                    "<script>\n " +
                        "function loginHack() { \n" +
                            "var form = document.getElementById(\"loginForm\");\n" +
                            "form.submit();\n" +
                        "} \n" +
                    "</script>\n");

            webView.setVisibility(View.VISIBLE); // TODO: remove and reformat

            webView.loadData(kmarDocument.html(), "text/html", "UTF-8");
        }
    }
}
