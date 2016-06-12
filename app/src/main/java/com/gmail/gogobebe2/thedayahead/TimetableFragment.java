package com.gmail.gogobebe2.thedayahead;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
        final WebView webView = new WebView(getContext());

        webView.setVisibility(View.INVISIBLE);

        webView.clearCache(true);
        webView.clearHistory();
        clearCookies(this);

        final ProgressBar progressBar = (ProgressBar) relativeLayout.findViewById(R.id.progressBar);

        webView.setWebViewClient(new WebViewClient()  {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String destinationUrl) {
                final String currentUrl = view.getUrl();
                final String kmarLoginUrl = getString(R.string.kmar_login_url);
                final String kmarMainUrl = getString(R.string.kmar_url);
                final String kmarTimetableUrl = getString(R.string.kmar_timetable_url);

                if (destinationUrl.equals(kmarLoginUrl)) {
                    // It goes to the login url if details incorrect.
                    // If it does, make an error message for user.
                    Toast.makeText(getContext(),
                            "Error logging in! Maybe the password or username is incorrect.",
                            Toast.LENGTH_LONG).show();
                }
                else if ((currentUrl.equals(kmarMainUrl) || currentUrl.equals(kmarLoginUrl))
                        && destinationUrl.equals(kmarMainUrl)) {
                    // If the the main url isn't the 1st url loaded, it means the user is logged in.
                    // (Since if he wasnt logged in, then it would've gone to the login url)
                    // Once logged in, redirect to the timetable page:
                    view.loadUrl(kmarTimetableUrl);
                }
                else Log.w(getLoggingTag(), "Tried loading unexpected url: " + destinationUrl);

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

        new AsyncTask<Void, Boolean, Document>() {
            // Void: No params.
            // Boolean: true if connection was successful, false otherwise.
            // Document: The kmar page's html document using Jsoup.
            @Override
            protected Document doInBackground(Void... params) {
                try {
                    Document kmarDocument = Jsoup.connect(getString(R.string.kmar_url)).get();
                    this.publishProgress(true);
                    return kmarDocument;
                } catch (IOException e) {
                    this.publishProgress(false);
                    if (e instanceof ConnectException) Log.w(getLoggingTag(),
                            "ConnectException, Kmar Portal down or internet down.");
                    else {
                        Log.w(getLoggingTag(), "Failed to connect to Kmar Portal.");
                        e.printStackTrace();
                    }
                    this.cancel(true);
                    return null;
                }
            }


            @Override
            protected void onProgressUpdate(Boolean... success) {
                if (!success[0]) Toast.makeText(getContext(),
                        "Failed to connect to the Kmar Portal.", Toast.LENGTH_LONG).show();
                else Toast.makeText(getContext(), "Successfully connected to the Kmar Portal.",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void onPostExecute(Document doc) {
                // TODO make username/password/login buttons all associate with the html ones.
                Element loginElement = doc != null ? doc.select("#wrapper").first() : null;
                if (doc != null) doc.select("input#loginSubmit").attr("value", "Login");

                if (loginElement != null) webView.loadData(loginElement.html(), "text/html", "UTF-8");
                else {
                    // Do this if I can't crop the html. (This shouldn't ever happen)
                    // It will only happen if the school decides to change the html setup of kmar.
                    webView.loadUrl(getString(R.string.kmar_login_url));
                    Toast.makeText(getContext(), "Can't find login section of kmar, now trying " +
                            "to load whole page as a last resort...", Toast.LENGTH_LONG).show();
                    Log.e(getLoggingTag(), "Can't find #wrapper html element! Trying to use whole page instead!");
                }
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
}
