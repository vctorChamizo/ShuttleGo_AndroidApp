package tfg.shuttlego.activities.route;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import tfg.shuttlego.R;
import tfg.shuttlego.activities.person.admin.AdminMain;
import tfg.shuttlego.model.adapter.RecyclerViewAdapterOrigin;
import tfg.shuttlego.model.adapter.RecyclerViewAdapterRoute;
import tfg.shuttlego.model.event.Event;
import tfg.shuttlego.model.event.EventDispatcher;
import tfg.shuttlego.model.session.Session;
import tfg.shuttlego.model.transfer.origin.Origin;
import tfg.shuttlego.model.transfer.person.Person;
import tfg.shuttlego.model.transfer.route.Route;

@SuppressLint("Registered")
public class RouteListDriver extends RouteList implements NavigationView.OnNavigationItemSelectedListener {

    Person user = Session.getInstance(getApplicationContext()).getUser();
    private ArrayList<Route> routeListArray;

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

        EventDispatcher.getInstance(getApplicationContext())
        .dispatchEvent(Event.GETORIGINS, null)
        .addOnCompleteListener(task -> {

            if (!task.isSuccessful() || task.getResult() == null) {

                throwToast(R.string.errConexion);
                startActivity(new Intent(RouteListDriver.this, AdminMain.class));
            }
            else if (task.getResult().containsKey("error")) {
                throwToast(R.string.errServer);
                startActivity(new Intent(RouteListDriver.this, AdminMain.class));
            }
            else {

                HashMap<?, ?> result = task.getResult();
                ArrayList<HashMap<?, ?>> list = (ArrayList<HashMap<?, ?>>) result.get("routes");
                listRoutes = new ArrayList<>();

                assert list != null;
                for (int i = 0; i < list.size(); ++i) {
                    Route route = new Route();

                    // Introducir los atributos que ean necesarios.

                    listRoutes.add(route);
                }

                createListView();
                removeProgressBar();
            }
        });

    }

    @Override
    protected void createListView() {

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        routeListRecycler.setLayoutManager(layoutManager);
        RecyclerView.Adapter<RecyclerViewAdapterRoute.RouteViewHolder> adapter = new RecyclerViewAdapterRoute(routeListArray);
        routeListRecycler.setAdapter(adapter);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }

    @Override
    public void onBackPressed() {
    }
}
