package tfg.shuttlego.activities.route;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import tfg.shuttlego.R;
import tfg.shuttlego.model.session.Session;
import tfg.shuttlego.model.transfer.person.Person;

@SuppressLint("Registered")
public class RouteListPassenger extends RouteList implements NavigationView.OnNavigationItemSelectedListener {

    Person user = Session.getInstance(getApplicationContext()).getUser();

    @Override
    protected void setMenuDrawer() {

        routeListNavigation.setNavigationItemSelectedListener(this);
        Toolbar toolbar = findViewById(R.id.route_list_toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, routeListDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        routeListDrawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    protected void setCredencials() {

        View hView =  routeListNavigation.getHeaderView(0);

        TextView nav_name_text = hView.findViewById(R.id.menu_nav_header_name);
        TextView nav_email_text = hView.findViewById(R.id.menu_nav_header_email);

        String complete_name = user.getName() + " " + user.getSurname();
        nav_name_text.setText(complete_name);
        nav_email_text.setText(user.getEmail());
    }

    @Override
    protected void throwEventGetAllRoutes() {
    }

    @Override
    protected void createListView() {
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }

    @Override
    public void onBackPressed() {
    }
}
