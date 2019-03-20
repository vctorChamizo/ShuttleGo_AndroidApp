package tfg.shuttlego.activities.route;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import java.util.ArrayList;
import java.util.HashMap;
import tfg.shuttlego.R;
import tfg.shuttlego.activities.person.driver.DriverMain;
import tfg.shuttlego.model.adapter.RecyclerViewAdapterRoute;
import tfg.shuttlego.model.event.Event;
import tfg.shuttlego.model.event.EventDispatcher;
import tfg.shuttlego.model.transfer.route.Route;

@SuppressLint("Registered")
public class RouteListDriver extends RouteList implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void throwEventGetAllRoutes() {

        EventDispatcher.getInstance(getApplicationContext())
        .dispatchEvent(Event.GETALLROUTESBYUSER, this.getRoutes)
        .addOnCompleteListener(task -> {

            if (!task.isSuccessful() || task.getResult() == null) {

                throwToast(R.string.errConexion);
                startActivity(new Intent(RouteListDriver.this, DriverMain.class));
            }
            else if (task.getResult().containsKey("error")) {
                throwToast(R.string.errServer);
                startActivity(new Intent(RouteListDriver.this, DriverMain.class));
            }
            else {

                HashMap<?, ?> result = task.getResult();
                ArrayList<HashMap<?, ?>> list = (ArrayList<HashMap<?, ?>>) result.get("routes");
                this.listRoutes = new ArrayList<>();

                assert list != null;
                for (int i = 0; i < list.size(); ++i) {

                    Route route = new Route();

                    route.setId((String) list.get(i).get("id"));
                    route.setOrigin((String) list.get(i).get("origin"));
                    route.setDestination(Integer.parseInt(String.valueOf(list.get(i).get("destination"))));
                    route.setHour((String) list.get(i).get("hour"));
                    route.setPassengersNumber(Integer.parseInt(String.valueOf(list.get(i).get("passengersNumber"))));
                    route.setMax(Integer.parseInt(String.valueOf(list.get(i).get("max"))));

                    this.listRoutes.add(route);
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
        RecyclerView.Adapter<RecyclerViewAdapterRoute.RouteViewHolder> adapter = new RecyclerViewAdapterRoute(this.listRoutes);
        routeListRecycler.setAdapter(adapter);
    }

    @Override
    protected void listeners() {

        routeListNavigation.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {

            case R.id.driver_drawer_home:
                startActivity(new Intent(RouteListDriver.this, DriverMain.class));
                finish();
                break;
        }

        routeListDrawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onBackPressed() {

        if (routeListDrawer.isDrawerOpen(GravityCompat.START)) routeListDrawer.closeDrawer(GravityCompat.START);
        else finish();
    }
}
