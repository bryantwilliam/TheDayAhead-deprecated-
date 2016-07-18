package com.gmail.gogobebe2.thedayahead;

import android.content.res.Configuration;
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

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private ActionBarDrawerToggle drawerToggle;

    private TimetableFragment timetableFragment;

    @SuppressWarnings("TryWithIdenticalCatches")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupDrawer(toolbar);

        /*******Default Fragment*******/
        if (timetableFragment == null) try {
            timetableFragment = TimetableFragment.class.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        // TODO Change this later to a home page fragment
        showFragmentInFrameLayout(timetableFragment);
        /******************************/


        drawer.openDrawer(GravityCompat.START); // Opened this on first run so they know what's in it.

        Log.i(Utils.getTagName(this), "onCreate has finished.");
    }

    private void setupDrawer(Toolbar toolbar) {
        this.drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        this.drawerToggle = new ActionBarDrawerToggle(
                this, this.drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        this.drawer.addDrawerListener(this.drawerToggle);
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

    /*
    Handle navigation view item clicks here.
     */
    @SuppressWarnings("TryWithIdenticalCatches")
    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        TheDayAheadFragment fragment = null;
        try {
            switch (menuItem.getItemId()) {
                case R.id.nav_slideshow: // TODO fragmentClass = SlideshowFragment.class; break;
                case R.id.nav_share: // TODO fragmentClass = ShareFragment.class; break;
                case R.id.nav_timetable:
                    if (timetableFragment == null)
                        timetableFragment = TimetableFragment.class.newInstance();
                    fragment = timetableFragment;
                    break;
            }
            showFragmentInFrameLayout(fragment);
        } catch (InstantiationException e) {
            e.printStackTrace();

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        menuItem.setChecked(true);
        return true;
    }

    private void showFragmentInFrameLayout(TheDayAheadFragment fragment) {
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
}
