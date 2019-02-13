package tfg.shuttlego.activities.passenger;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

import tfg.shuttlego.R;
import tfg.shuttlego.model.transfers.origin.Origin;
import tfg.shuttlego.model.transfers.person.Person;

/**
 *
 */
public class PassengerMain extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView;
    private Person user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.passenger_main);

        user = (Person)Objects.requireNonNull(getIntent().getExtras()).getSerializable("user");

        setMenuDrawer();
        setCredencials();
    }

    /**
     *
     */
    private void setMenuDrawer() {
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        DrawerLayout drawer = findViewById(R.id.passenger_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }//setMenuDrawer

    /**
     *
     */
    @SuppressLint("SetTextI18n")
    private void setCredencials() {
        View hView =  navigationView.getHeaderView(0);
        //TextView nav_name_text = hView.findViewById(R.id.name_admin_text);
        //TextView nav_email_text = hView.findViewById(R.id.email_admin_text);
        //nav_name_text.setText(user.getName() + " " + user.getSurname());
        //nav_email_text.setText(user.getEmail());
    }//setCredencials

    /**
     *
     * @param msg
     */
    private void throwToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }//throwToast

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.passenger_main);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }//onBackPressed

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        /*if (id == R.id.nav_add_origin) { }
        else if (id == R.id.nav_settings_admin) { }
        else if (id == R.id.nav_signout_admin) { }*/

        DrawerLayout drawer = findViewById(R.id.passenger_main);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }//onNavigationItemSelected
}