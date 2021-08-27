package com.example.geoponics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.muddzdev.styleabletoast.StyleableToast;

public class Home extends AppCompatActivity {

    ChipNavigationBar chipNavigationBar;
    Fragment fragment;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        chipNavigationBar = findViewById(R.id.bottomnav);
        chipNavigationBar.setItemSelected(R.id.home, true);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new BuyFragment()).commit();

        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                switch (i) {
                    case R.id.home:
                        fragment = new BuyFragment();
                        break;
                    case R.id.sell_menu:
                        fragment = new SellFragment();
                        break;
                    case R.id.profile_menu:
                        fragment = new ProfileFragment();
                        break;
                    case R.id.wallet_menu:
                        fragment = new WalletFragment();
                        break;
                    case R.id.plant_rec:
                        fragment = new PlantHealthFragment();
                        break;

                }
                if (fragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        switch (item.getItemId()) {
            case R.id.infotab:
                fragment = new InfoFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
                chipNavigationBar.setItemSelected(R.id.home, false);
                break;

            case R.id.logout:
                new AlertDialog.Builder(this)
                        .setTitle("Logout")
                        .setMessage("Are You Sure Want To Logout ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Home.this);
                                editor = sharedPreferences.edit();
                                editor.putString(getString(R.string.rmbox), "false");
                                editor.commit();
                                editor.putString(getString(R.string.uemail), "");
                                editor.commit();
                                editor.putString(getString(R.string.upwd), "");
                                editor.commit();
                                StyleableToast.makeText(Home.this, "Logged Out..", Toast.LENGTH_SHORT,R.style.errortoast).show();
                                startActivity(new Intent(Home.this, Login.class));
                                finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create().show();

                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

}