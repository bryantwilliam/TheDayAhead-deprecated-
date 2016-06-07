package com.gmail.gogobebe2.thedayahead;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.ConnectException;

public class TimetableFragment extends TheDayAheadFragment {

    private OnFragmentInteractionListener listener;

    public TimetableFragment() {
        // Required empty public constructorg
    }

    // This event fires 2nd, before views are created for the fragment
    // The onCreate method is called when the Fragment instance is being created, or re-created.
    // Use onCreate for any standard setup that does not require the activity to be fully created
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        goToTimetablePage();

        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_timetable, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // onViewCreated() is only called if the view returned from onCreateView() is non-null.
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);
        super.onViewCreated(view, savedInstanceState);
    }

    // This event fires 1st, before creation of fragment or any views
    // The onAttach method is called when the Fragment instance is associated with an Activity.
    // This does not mean the Activity is fully initialized.
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) listener = (OnFragmentInteractionListener) context;
        else throw new RuntimeException(context.toString()
                + " must implement OnFragmentInteractionListener");
    }

    // This method is called after the parent Activity's onCreate() method has completed.
    // Accessing the view hierarchy of the parent activity must be done in the onActivityCreated.
    // At this point, it is safe to search for activity View objects by their ID, for example.
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @SuppressLint("SetJavaScriptEnabled")
    protected void goToTimetablePage() {
        FrameLayout flContent = (FrameLayout) MainActivity.getInstance().findViewById(R.id.flContent);

        assert flContent != null;
        flContent.removeAllViews();

        final WebView kmarLogin = new WebView(getContext());

        kmarLogin.clearCache(true);
        kmarLogin.clearHistory();
        clearCookies(this);

        final ImageView kmarLoginLoadingImage = new ImageView(getContext());
        kmarLoginLoadingImage.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                R.drawable.ic_menu_share, null));

        kmarLogin.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!url.equals(getString(R.string.kmar_login_url))) view.loadUrl(getString(R.string.kmar_timetable_url));
                else {
                    Toast.makeText(getContext(),
                            "Error logging in! Maybe the password or username is incorrect.",
                            Toast.LENGTH_LONG).show();
                }
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                hideWebviewLoadingImage(kmarLoginLoadingImage, kmarLogin);
            }
        });

        WebSettings webSettings = kmarLogin.getSettings();
        webSettings.setJavaScriptEnabled(true);

        showWebviewLoadingImage(kmarLoginLoadingImage, kmarLogin);

        flContent.addView(kmarLoginLoadingImage);
        flContent.addView(kmarLogin);

        new AsyncTask<Void, Boolean, Document>() {
            // Void: No params.
            // Boolean: true if connection was successful, false otherwise.
            // Document: The kmar page's html document using Jsoup.
            @Override
            protected Document doInBackground(Void... params) {
                try {
                    Document kmarDocument = Jsoup.connect(getString(R.string.kmar_login_url)).get();
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
                Element loginElement = doc != null ? doc.select("#wrapper").first() : null;
                if (doc != null) doc.select("input#loginSubmit").attr("value", "Login");

                if (loginElement != null) kmarLogin.loadData(loginElement.html(), "text/html", "UTF-8");
                else {
                    // Do this if I can't crop the html. (This shouldn't ever happen)
                    // It will only happen if the school decides to change the html setup of kmar.
                    kmarLogin.loadUrl(getString(R.string.kmar_login_url));
                    Toast.makeText(getContext(), "Can't find login section of kmar, now trying " +
                            "to load whole page as a last resort...", Toast.LENGTH_LONG).show();
                    Log.e(getLoggingTag(), "Can't find #wrapper html element! Trying to use whole page instead!");
                }

                hideWebviewLoadingImage(kmarLoginLoadingImage, kmarLogin);
            }
        }.execute();
    }

    private void showWebviewLoadingImage(ImageView kmarLoginLoadingImage, WebView kmarLogin) {
        kmarLoginLoadingImage.setVisibility(View.VISIBLE);
        kmarLogin.setVisibility(View.GONE);
    }

    private void hideWebviewLoadingImage(ImageView kmarLoginLoadingImage, WebView kmarLogin) {
        kmarLoginLoadingImage.setVisibility(View.GONE);
        kmarLogin.setVisibility(View.VISIBLE);
    }

    @SuppressWarnings("deprecation")
    private static void clearCookies(TimetableFragment instance) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Log.d(instance.getLoggingTag(), "Using clearCookies code for API >=" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            Log.d(instance.getLoggingTag(), "Using clearCookies code for API <" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(instance.getContext());
            cookieSyncMngr.startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }

}
