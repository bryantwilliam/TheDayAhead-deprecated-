package com.gmail.gogobebe2.thedayahead;

import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
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

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, TimetableFragment.OnFragmentInteractionListener {
    private final static String TAG = "TheDayAhead";

    private DrawerLayout drawer;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupDrawer(toolbar);

        drawer.openDrawer(GravityCompat.START); // Opened this on first run so they know what's in it.

        // TODO Change this later to a home page and remove goToTimetablePage();
        // goToTimetablePage();
        goToNavFragment((MenuItem) findViewById(R.id.nav_timetable));
    }

    private void setupDrawer(Toolbar toolbar) {
        this.drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        this.drawerToggle = new ActionBarDrawerToggle(
                this, this.drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        this.drawer.setDrawerListener(this.drawerToggle);
        this.drawerToggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    // To make sure we synchronize the state whenever the screen is restored:
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    // To make sure we synchronize the state whenever configuration change (i.e screen rotation):
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START);
        else super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent e) {
        // If the hardware options button is pressed on some devices, open drawer/close drawer.
        if (keycode == KeyEvent.KEYCODE_MENU) {
            // Toggle drawer:
            if (drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START);
            else drawer.openDrawer(GravityCompat.START);
            return true;
        }

        return super.onKeyDown(keycode, e);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        // Handle navigation view item clicks here.

        goToNavFragment(menuItem);
        return true;
    }

    private void goToNavFragment(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass;

        switch (menuItem.getItemId()) {
            case R.id.nav_slideshow: // TODO fragmentClass = SlideshowFragment.class; break;
            case R.id.nav_share: // TODO fragmentClass = ShareFragment.class; break;
            case R.id.nav_timetable:
                fragmentClass = TimetableFragment.class;
                // goToTimetablePage();
                break;
            default:
                fragmentClass = TimetableFragment.class;
                // TODO Change this later to a home page:
                // TODO fragmentClass = HomeFragment.class; Remove "goToTimetablePage();"
        }


        //noinspection TryWithIdenticalCatches
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        drawer.closeDrawer(GravityCompat.START);
    }

    private void goToTimetablePage() {
        FrameLayout flContent = (FrameLayout) findViewById(R.id.flContent);

        flContent.removeAllViews();

        final WebView kmarLogin = new WebView(this);

        kmarLogin.clearCache(true);
        kmarLogin.clearHistory();
        clearCookies(this);

        final ImageView kmarLoginLoadingImage = new ImageView(this);
        kmarLoginLoadingImage.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                R.drawable.ic_menu_share, null));

        kmarLogin.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!url.equals(getString(R.string.kmar_login_url))) view.loadUrl(getString(R.string.kmar_timetable_url));
                else {
                    Toast.makeText(MainActivity.this,
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
                    if (e instanceof ConnectException) Log.w(TAG,
                            "ConnectException, Kmar Portal down or internet down.");
                    else {
                        Log.w(TAG, "Failed to connect to Kmar Portal.");
                        e.printStackTrace();
                    }
                    this.cancel(true);
                    return null;
                }
            }


            @Override
            protected void onProgressUpdate(Boolean... success) {
                if (!success[0]) Toast.makeText(MainActivity.this,
                        "Failed to connect to the Kmar Portal.", Toast.LENGTH_LONG).show();
                else Toast.makeText(MainActivity.this, "Successfully connected to the Kmar Portal.",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void onPostExecute(Document doc) {
                Element loginElement = doc != null ? doc.select("#wrapper").first() : null;
                if (doc != null) doc.select("input#loginSubmit").attr("value", "Login");

                if (loginElement != null) kmarLogin.loadData(loginElement.html(), "text/html", "UTF-8");
                else {
                    // Do this if I can't crop the html. (This shouldn't ever happen).
                    kmarLogin.loadUrl(getString(R.string.kmar_login_url));
                    Log.e(TAG, "Can't find #wrapper html element! Trying to use whole page instead!");
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

    private static void clearCookies(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Log.d(TAG, "Using clearCookies code for API >=" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            Log.d(TAG, "Using clearCookies code for API <" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
