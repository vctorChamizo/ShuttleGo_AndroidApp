package tfg.shuttlego.activities.route;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import tfg.shuttlego.R;
import tfg.shuttlego.model.adapter.RecyclerViewAdapterRoute;
import tfg.shuttlego.model.event.Event;
import tfg.shuttlego.model.event.EventDispatcher;
import tfg.shuttlego.model.session.Session;
import tfg.shuttlego.model.transfer.person.Person;
import tfg.shuttlego.model.transfer.person.TypePerson;
import tfg.shuttlego.model.transfer.route.Route;

public abstract class RouteList extends AppCompatActivity {

    protected LinearLayout routeListLinear;
    protected ProgressBar routeListProgress;

    protected NavigationView routeListNavigation;
    protected DrawerLayout routeListDrawer;
    protected RecyclerView routeListRecycler;

    protected ArrayList<Route> listRoutes;
    protected JSONObject getRoutes;
    protected Person user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_list);

        user = Session.getInstance(getApplicationContext()).getUser();

        inicializateView();
        setProgressBar();
        setMenuDrawer();
        setCredencials();

        buildJSON();
        throwEventGetAllRoutes();

        listeners();
    }

    /**
     * Inicializate the componentes of this view.
     */
    private void inicializateView() {

        this.routeListLinear = findViewById(R.id.route_list_linear);
        this.routeListProgress = findViewById(R.id.route_list_progress);

        if (user.getType() == TypePerson.DRIVER) this.routeListNavigation = findViewById(R.id.route_list_nav_driver);
        else this.routeListNavigation = findViewById(R.id.route_list_nav_passenger);

        this.routeListNavigation.setVisibility(View.VISIBLE);
        this.routeListDrawer = findViewById(R.id.route_list_drawer);

        this.routeListRecycler = findViewById(R.id.route_list_recycler);
    }

    /**
     * Show the progress bar component visible and put invisble the rest of the view.
     */
    protected void setProgressBar () {

        this.routeListProgress.setVisibility(View.VISIBLE);
        this.routeListLinear.setVisibility(View.GONE);
    }

    /**
     * Show the view visible and put invisble progress bar component.
     */
    protected void removeProgressBar () {

        this.routeListProgress.setVisibility(View.GONE);
        this.routeListLinear.setVisibility(View.VISIBLE);
    }

    /**
     * Inicializate the components to put the menu in the view.
     */
    private void setMenuDrawer(){

        Toolbar toolbar = findViewById(R.id.route_list_toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, this.routeListDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        this.routeListDrawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    /**
     * Put the personal data about the current user.
     */
    private void setCredencials(){

        View hView =  routeListNavigation.getHeaderView(0);

        TextView nav_name_text = hView.findViewById(R.id.menu_nav_header_name);
        TextView nav_email_text = hView.findViewById(R.id.menu_nav_header_email);

        String complete_name = user.getName() + " " + user.getSurname();
        nav_name_text.setText(complete_name);
        nav_email_text.setText(user.getEmail());
    }

    /**
     * Build a JSON for to allow get all routes by current user.
     */
    private void buildJSON() {

        JSONObject dataUser = new JSONObject();
        this.getRoutes = new JSONObject();

        try {

            dataUser.put("email", Session.getInstance(getApplicationContext()).getUser().getEmail());
            dataUser.put("password", Session.getInstance(getApplicationContext()).getUser().getPassword());
            this.getRoutes.put("user", dataUser);

        }
        catch (JSONException e) { throwToast(R.string.err); }
    }

    /**
     * Throw the event that allow to get a list of all routes in the server.
     */
    private void throwEventGetAllRoutes(){

        EventDispatcher.getInstance(getApplicationContext())
        .dispatchEvent(Event.GETALLROUTESBYUSER, this.getRoutes)
        .addOnCompleteListener(task -> {

            if (!task.isSuccessful() || task.getResult() == null) throwToast(R.string.errConexion);
            else if (task.getResult().containsKey("error")) throwToast(R.string.errServer);
            else {

                HashMap<?, ?> result = task.getResult();
                ArrayList<HashMap<?, ?>> list = (ArrayList<HashMap<?, ?>>) result.get("routes");
                this.listRoutes = new ArrayList<>();

                assert list != null;
                for (int i = 0; i < list.size(); ++i) {

                    Route route = new Route();

                    route.setId((String) list.get(i).get("id"));
                    route.setOrigin((String) list.get(i).get("originName"));
                    route.setHour((String) list.get(i).get("hour"));
                    route.setPassengersNumber(Integer.parseInt(String.valueOf(list.get(i).get("passengersNumber"))));
                    route.setMax(Integer.parseInt(String.valueOf(list.get(i).get("max"))));

                    if (this.user.getType() == TypePerson.DRIVER) route.setDestination(Integer.parseInt(String.valueOf(list.get(i).get("destination"))));
                    else route.setDestination(String.valueOf(list.get(i).get("destinationName")));

                    this.listRoutes.add(route);
                }

                createListView();
                removeProgressBar();
            }
        });
    }

    /**
     * Inicializate the componentes and the adapter to put the list of routes.
     */
    private void createListView(){

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        this.routeListRecycler.setLayoutManager(layoutManager);
        RecyclerView.Adapter<RecyclerViewAdapterRoute.RouteViewHolder> adapter = new RecyclerViewAdapterRoute(this.listRoutes);
        this.routeListRecycler.setAdapter(adapter);
    }

    protected void throwToast(int msg) { Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show(); }

    /**
     * Listen the action components of the view.
     */
    protected abstract void listeners();

    @Override
    public void onBackPressed() {

        if (this.routeListDrawer.isDrawerOpen(GravityCompat.START)) this.routeListDrawer.closeDrawer(GravityCompat.START);
        else finish();
    }
}
