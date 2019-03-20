package tfg.shuttlego.activities.route;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
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
import tfg.shuttlego.R;
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

        routeListLinear = findViewById(R.id.route_list_linear);
        routeListProgress = findViewById(R.id.route_list_progress);

        if (user.getType() == TypePerson.DRIVER) routeListNavigation = findViewById(R.id.route_list_nav_driver);
        else routeListNavigation = findViewById(R.id.route_list_nav_passenger);

        routeListNavigation.setVisibility(View.VISIBLE);
        routeListDrawer = findViewById(R.id.route_list_drawer);

        routeListRecycler = findViewById(R.id.route_list_recycler);
    }

    /**
     * Show the progress bar component visible and put invisble the rest of the view.
     */
    protected void setProgressBar () {

        routeListProgress.setVisibility(View.VISIBLE);
        routeListLinear.setVisibility(View.GONE);
    }

    /**
     * Show the view visible and put invisble progress bar component.
     */
    protected void removeProgressBar () {

        routeListProgress.setVisibility(View.GONE);
        routeListLinear.setVisibility(View.VISIBLE);
    }

    /**
     * Inicializate the components to put the menu in the view.
     */
    private void setMenuDrawer(){

        Toolbar toolbar = findViewById(R.id.route_list_toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, routeListDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        routeListDrawer.addDrawerListener(toggle);
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

    protected void throwToast(int msg) { Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show(); }

    /**
     * Inicializate the componentes and the adapter to put the list of routes.
     */
    abstract protected void createListView();

    /**
     * Throw the event that allow to get a list of all routes in the server.
     */
    abstract protected void throwEventGetAllRoutes();

    /**
     * Listen the action components of the view.
     */
    protected abstract void listeners();
}
