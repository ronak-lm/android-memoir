package com.momana.bhromo.memoir.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.momana.bhromo.memoir.R;
import com.momana.bhromo.memoir.adapter.NotesCalendarAdapter;
import com.momana.bhromo.memoir.fragment.AtlasFragment;
import com.momana.bhromo.memoir.fragment.CalendarFragment;
import com.momana.bhromo.memoir.fragment.NotesFragment;

import java.text.SimpleDateFormat;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String LAST_SELECTION_KEY = "last_drawer_selection";

    private int currentPosition;

    @Bind(R.id.toolbar)         Toolbar toolbar;
    @Bind(R.id.drawer_layout)   DrawerLayout drawerLayout;
    @Bind(R.id.navigation_view) NavigationView navigationView;

    // Activity Lifecycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        // Setup Navigation drawer
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // Load last selected drawer item
        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        int lastPosition = preferences.getInt(LAST_SELECTION_KEY, 0);
        setSelectedDrawerItem(lastPosition);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    // Navigation Drawer
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        drawerLayout.closeDrawers();
        int id = item.getItemId();
        switch (id) {
            case R.id.drawer_notes:
                setSelectedDrawerItem(0);
                return true;
            case R.id.drawer_calendar:
                setSelectedDrawerItem(1);
                return true;
            case R.id.drawer_atlas:
                setSelectedDrawerItem(2);
                return true;
            case R.id.drawer_settings:
                return true;
            case R.id.drawer_about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            default:
                return false;
        }
    }
    private void setSelectedDrawerItem(int position) {
        currentPosition = position;

        MenuItem item = navigationView.getMenu().getItem(position);
        item.setChecked(true);

        if (position == 0) {
            getSupportActionBar().setTitle(R.string.app_name);
        } else {
            getSupportActionBar().setTitle(item.getTitle());
        }

        Fragment fragment;
        if (position == 0) {
            fragment = new NotesFragment();
        } else if (position == 1) {
            fragment = new CalendarFragment();
        } else {
            fragment = new AtlasFragment();
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, fragment);
        transaction.commit();

        // Save position to preference
        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(LAST_SELECTION_KEY, position);
        editor.apply();
    }

    // FAB - Add Note
    @OnClick(R.id.fab_add)
    public void onAddButtonClicked() {
        Intent intent = new Intent(this, ViewActivity.class);
        intent.putExtra(ViewActivity.ACTION_TYPE_KEY, ViewActivity.ACTION_TYPE_NEW);
        // Add date as extra if in calendar view
        if (currentPosition == 1) {
            SimpleDateFormat fmt = new SimpleDateFormat("ddMMyyyy");
            intent.putExtra(ViewActivity.NOTE_DATE_KEY, fmt.format(NotesCalendarAdapter.currentDate));
        }
        startActivity(intent);
    }
}
