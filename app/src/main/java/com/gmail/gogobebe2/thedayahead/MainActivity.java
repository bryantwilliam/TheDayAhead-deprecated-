package com.gmail.gogobebe2.thedayahead;

import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, TimetableFragment.OnFragmentInteractionListener, Loggable {
    private static MainActivity instance;

    private DrawerLayout drawer;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupDrawer(toolbar);

        // TODO Change this later to a home page fragment class.
        showFragmentInFrameLayout(TimetableFragment.class);
        drawer.openDrawer(GravityCompat.START); // Opened this on first run so they know what's in it.

        Log.i(getLoggingTag(), "onCreate has finished.");
    }

    private void setupDrawer(Toolbar toolbar) {
        this.drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        this.drawerToggle = new ActionBarDrawerToggle(
                this, this.drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        this.drawer.setDrawerListener(this.drawerToggle);
        this.drawerToggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
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
        assert drawer != null;
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
        Class<? extends TheDayAheadFragment> fragmentClass;

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

        showFragmentInFrameLayout(fragmentClass);
        menuItem.setChecked(true);
        return true;
    }

    private void showFragmentInFrameLayout(Class<? extends TheDayAheadFragment> fragmentClass) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        TheDayAheadFragment fragment = null;

        //noinspection TryWithIdenticalCatches
        try {
            fragment = fragmentClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        // Begin the transaction
        FragmentTransaction fragmentTransactiont = getSupportFragmentManager().beginTransaction();

        // Replace the contents of the container with the new fragment
        fragmentTransactiont.replace(R.id.flContent, fragment);

        // Complete the changes added above
        fragmentTransactiont.commit();

        assert fragment != null;
        setTitle(fragment.getTitle());
        drawer.closeDrawer(GravityCompat.START);

        // This just makes the menu get recreated and thus no need to do menuItem.setChecked(false):
        invalidateOptionsMenu();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public String getLoggingTag() {
        return Utils.createTagName(MainActivity.class);
    }

    static MainActivity getInstance() {
        return instance;
    }
}
