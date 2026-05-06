package com.tehgan.rssreader;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.navigation.NavigationView;
import com.tehgan.rssreader.databinding.ActivityMainBinding;
import com.tehgan.rssreader.interfaces.TitleCallback;

public class MainActivity extends AppCompatActivity implements TitleCallback {
    private ActivityMainBinding binding;
    private NavController navController;

    private String channel = "";

    // Fragment labels
    private String FL_FEED;
    private String FL_FAVOURITES;
    private String FL_DETAIL;
    private String FL_SETTINGS;

    /**
     * If in a relevant Fragment, set toolbar title to that of the RSS feed.
     * If not, store the title for later use.
     * @param title The RSS feed's channel value (e.g. "BBC News")
     */
    @Override
    public void onTitleUpdate(String title) {
        channel = title;
        if (binding != null && navController != null) {
            NavDestination cd = navController.getCurrentDestination();
            if (cd != null && cd.getLabel() != null) {
                String currentFragment = cd.getLabel().toString();
                if (currentFragment.equals(FL_FEED) || currentFragment.equals(FL_DETAIL)) {
                    binding.toolbar.setTitle(channel);
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FL_FEED = getString(R.string.FL_FEED);
        FL_FAVOURITES = getString(R.string.FL_FAVOURITES);
        FL_DETAIL = getString(R.string.FL_DETAIL);
        FL_SETTINGS = getString(R.string.FL_SETTINGS);

        // Inflate and set View via ViewBinding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up toolbar with NavigationView (hamburger menu)
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> binding.getRoot().open());
        binding.navView.setNavigationItemSelectedListener(navViewListener());

        // Set up Navigation controller for navigating between Fragments (not to be confused with NavigationView)
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
        } else {
            // A catastrophic event; the application can't run without NavHostFragment.
            // Exits gracefully.
            String noHostFragmentError = getResources().getString(R.string.error_toast_base, "nav_host_fragment is null");
            Toast.makeText(this, noHostFragmentError, Toast.LENGTH_LONG).show();
            finishAndRemoveTask();
        }

        // Updates the title bar depending on which Fragment is active
        addDestinationListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    /**
     * Builds and displays the appropriate AlertDialog depending on which Fragment is active.<br><br>
     * Currently only used for the help button (<code>R.id.item_help</code>)
     * @param item The menu item that was selected.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.item_help) {
            NavDestination currentFragment = navController.getCurrentDestination();
            if (currentFragment != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                String label = (currentFragment.getLabel() != null ? currentFragment.getLabel().toString() : "");
                if (label.equals(FL_FEED)) {
                    builder.setMessage(R.string.hd_feed_desc).setTitle(R.string.hd_feed_title);
                } else if (label.equals(FL_FAVOURITES)) {
                    builder.setMessage(R.string.hd_favourites_desc).setTitle(R.string.hd_favourites_title);
                } else if (label.equals(FL_DETAIL)) {
                    builder.setMessage(R.string.hd_detail_desc).setTitle(R.string.hd_detail_title);
                } else if (label.equals(FL_SETTINGS)) {
                    builder.setMessage(R.string.hd_settings_desc).setTitle(R.string.hd_settings_title);
                } else {
                    // Return early; a dialog hasn't been built
                    return true;
                }
                // Build and display AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();

            } else {
                Log.w("onOptionsItemSelected", "currentFragment == null");
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Adds an onDestinationChangedListener to the navController,
     *  so that the toolbar's title syncs with the active Fragment.
     */
    private void addDestinationListener() {
        NavController.OnDestinationChangedListener listener =
                (c, dest, a) -> {
                    if (dest.getLabel() != null) {
                        String label = dest.getLabel().toString();
                        if (label.equals(FL_SETTINGS)) {
                            binding.toolbar.setTitle(R.string.settings);
                        } else if (label.equals(FL_FAVOURITES)) {
                            binding.toolbar.setTitle(R.string.favourites);
                        } else {
                            if (channel.isBlank()) {
                                binding.toolbar.setTitle(R.string.app_name_with_version);
                            } else {
                                binding.toolbar.setTitle(channel);
                            }
                        }
                    }
                };
        navController.addOnDestinationChangedListener(listener);
    }

    /**
     * Creates and returns a listener for navigating to each menu item's associated Fragment,
     * taking care not to re-navigate in case said Fragment is already visible.
     * @return NavigationView listener
     */
    private NavigationView.OnNavigationItemSelectedListener navViewListener() {
        return item -> {
            // Navigate to whichever Fragment the user selected.
            NavDestination currentFragment = navController.getCurrentDestination();
            if (currentFragment != null && currentFragment.getLabel() != null) {
                if (item.getItemId() == R.id.item_home) {
                    // Don't re-navigate if the selected fragment is already visible
                    if (!currentFragment.getLabel().equals(FL_FEED)) {
                        navController.navigate(R.id.action_global_to_homeFragment);
                    }
                } else if (item.getItemId() == R.id.item_settings) {
                    if (!currentFragment.getLabel().equals(FL_SETTINGS)) {
                        navController.navigate(R.id.action_global_to_settingsFragment);
                    }
                } else if (item.getItemId() == R.id.item_favourites) {
                    if (!currentFragment.getLabel().equals(FL_FAVOURITES)) {
                        navController.navigate(R.id.action_global_to_favouritesFragment);
                    }
                }
            }
            return false;
        };
    }
}