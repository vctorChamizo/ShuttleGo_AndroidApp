package tfg.shuttlego.activities.route;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.view.MenuItem;

@SuppressLint("Registered")
public class RouteListPassenger extends RouteList implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void throwEventGetAllRoutes() {
    }

    @Override
    protected void createListView() {

    }

    @Override
    protected void listeners() {
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }

    @Override
    public void onBackPressed() {
    }
}
