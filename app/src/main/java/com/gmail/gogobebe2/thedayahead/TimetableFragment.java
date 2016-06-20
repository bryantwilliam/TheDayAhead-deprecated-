package com.gmail.gogobebe2.thedayahead;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.ConnectException;

public class TimetableFragment extends TheDayAheadFragment implements View.OnClickListener, TextView.OnEditorActionListener {
    private RelativeLayout relativeLayout;
    private Document kmarDocument = null;

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


    @SuppressLint("AddJavascriptInterface")
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.login_button) {
            WebView webView = new WebView(getContext());

            webView.setVisibility(View.INVISIBLE);

            webView.clearCache(true);
            webView.clearHistory();
            clearCookies(this);

            final ProgressBar progressBar = (ProgressBar) relativeLayout.findViewById(R.id.progressBar);

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
                    progressBar.setVisibility(View.VISIBLE);
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
                    progressBar.setVisibility(View.INVISIBLE);

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

            EditText usernameEditText = (EditText) getActivity().findViewById(R.id.editText_username);
            EditText passwordEditText = (EditText) getActivity().findViewById(R.id.editText_password);

            loginUsernameElement.attr("value", usernameEditText.getText().toString());
            loginPasswordElement.attr("value", passwordEditText.getText().toString());

            webView.loadData(kmarDocument.html(), "text/html", "UTF-8");

            // I then call the click() function on the loginSubmit button when the page is finished
            // loading in the overrided onPageFinished(WebView webView, String url) method.
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (v.getId() == R.id.editText_password && ((event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) || actionId == EditorInfo.IME_ACTION_DONE)) {
            Log.i(getLoggingTag(), "Enter pressed on password edittext");
            relativeLayout.findViewById(R.id.login_button).callOnClick();
            return true;
        }
        return false;
    }
}
