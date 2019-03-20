package tfg.shuttlego.activities.route;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.view.MenuItem;

import tfg.shuttlego.R;
import tfg.shuttlego.activities.person.passenger.PassengerMain;

@SuppressLint("Registered")
public class RouteListPassenger extends RouteList implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void listeners() {

        this.routeListNavigation.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {

            case R.id.driver_drawer_home:
                startActivity(new Intent(RouteListPassenger.this, PassengerMain.class));
                finish();
                break;
        }

        this.routeListDrawer.closeDrawer(GravityCompat.START);

        return true;
    }
}
