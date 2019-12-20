package com.karimapps.hipodriver.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.karimapps.hipodriver.R;
import com.karimapps.hipodriver.fragments.LocationFragmentAws;
import com.karimapps.hipodriver.fragments.LoginFragment;
import com.karimapps.hipodriver.fragments.QuestionairFragment;
import com.karimapps.hipodriver.fragments.SettingsFragment;

import universal.UniversalConstants;
import universal.Utils;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{
    private Context context;
    private FragmentTransaction ft;
    private Fragment fragment;

    public static FloatingActionButton fab;
    public static Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        context = MainActivity.this;

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle(getString(R.string.app_name));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        ft = getSupportFragmentManager().beginTransaction();
        fragment = new QuestionairFragment();
        ft.replace(R.id.container, fragment);
        ft.commit();


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        /*getMenuInflater().inflate(R.menu.main, menu);*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/
        if (id == R.id.action_settings)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Are you sure you want to delete?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id)
                        {
                            Utils.savePreferencesBool(UniversalConstants.LOGIN, false, context);
                            FragmentManager fm = getSupportFragmentManager();
                            fm.popBackStack();
                            FragmentTransaction transaction = fm.beginTransaction();
                            transaction.replace(R.id.container, new LoginFragment());
                            dialog.cancel();
                            transaction.commit();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {

            Fragment fragment = new LocationFragmentAws();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, fragment);
            transaction.commit();
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_settings)
        {
            FragmentManager fm = getSupportFragmentManager();
            fm.popBackStack();
            FragmentTransaction t = fm.beginTransaction();
            t.replace(R.id.container, new SettingsFragment());
            t.addToBackStack(null);
            t.commit();

        }
        else if (id == R.id.nav_exit)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(getString(R.string.Are_you_sure_you_want_to_exit))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.txt_yes), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id)
                        {
                            finish();
                            System.exit(0);
                        }
                    })
                    .setNegativeButton(getString(R.string.txt_no), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

        else if (id == R.id.nav_logout)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(getString(R.string.Are_you_sure_you_want_to_logout))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.txt_yes), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id)
                        {
                            Utils.savePreferences(UniversalConstants.LOGGED_IN, "false", context);
                            Utils.savePreferencesBool(UniversalConstants.LOGIN, false, context);
                            startActivity(new Intent(context, LoginActivity.class));
                            finish();
                        }
                    })
                    .setNegativeButton(getString(R.string.txt_no), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Fragment fragment = new LocFragment(context);
        fragment.onActivityResult(requestCode, resultCode, data);
    }*/
}
