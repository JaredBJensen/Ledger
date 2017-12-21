package edu.ucsb.cs.cs184.jaredbjensen.ledger;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String SIMPLE_DATE_FORMAT = "MM/dd/yyyy";

    SummaryFragment summaryFragment;
    TransactionsFragment transactionsFragment;
    SetupFragment setupFragment;

    public ArrayList<String> categories;

    TransactionDatabaseHelper dbHelper;
    public SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        TransactionDatabaseHelper.Initialize(this);
        dbHelper = TransactionDatabaseHelper.GetInstance();

        preferences = getSharedPreferences("ledger", Context.MODE_PRIVATE);
        if (preferences.getAll().isEmpty()) runFirstTimeSetup();
        else {
            categories = new ArrayList<>();
            Map<String, ?> entries = preferences.getAll();
            for (Map.Entry<String, ?> entry : entries.entrySet()) {
                categories.add(entry.getValue().toString());
            }

            summaryFragment = new SummaryFragment();
            addFragment(summaryFragment);
        }
    }

    private void runFirstTimeSetup() {
        categories = new ArrayList<>();
        categories.add("Food");
        categories.add("Utilities");
        categories.add("Entertainment");
        categories.add("Misc");

        for (String cat : categories) {
            preferences.edit().putString(cat, cat).apply();
        }

        setupFragment = new SetupFragment();
        addFragment(setupFragment);
    }

    public void addCategory(String category) {
        preferences.edit().putString(category, category).apply();
    }

    public void removeCategory(String category) {
        preferences.edit().remove(category).apply();
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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.settings_clear) {
//            clearDB();
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_summary) {
            summaryFragment = new SummaryFragment();
            addFragment(summaryFragment);
        } else if (id == R.id.nav_transactions) {
            transactionsFragment = new TransactionsFragment();
            Bundle args = new Bundle();
            args.putStringArrayList("categories", categories);
            transactionsFragment.setArguments(args);
            addFragment(transactionsFragment);
        } else if (id == R.id.nav_setup) {
            setupFragment = new SetupFragment();
            addFragment(setupFragment);
        }

        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void addFragment(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().add(R.id.layout_content_main, fragment).addToBackStack(null).commit();
    }

    public void addTransaction(View view) {
        TransactionDialogFragment dialog = new TransactionDialogFragment();
        Bundle args = new Bundle();
        args.putStringArrayList("categories", categories);
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), "");
    }

    public void onTransactionResult(String type, String date, float amount, String description, String category) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Integer.valueOf(date.substring(6)), Integer.valueOf(date.substring(0, 2)) - 1, Integer.valueOf(date.substring(3, 5)));
        long timestamp = calendar.getTimeInMillis();

        ContentValues values = new ContentValues();
        values.put("type", type);
        values.put("date", timestamp);
        values.put("amount", amount);
        values.put("description", description);
        values.put("category", category);
        dbHelper.insert("Transactions", null, values);
    }

    public void resetApp() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        Intent i = getBaseContext().getPackageManager()
                                .getLaunchIntentForPackage( getBaseContext().getPackageName() );
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        finish();
                        startActivity(i);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Clear all app data and start from scratch?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();


    }
}
