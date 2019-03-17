package tfg.shuttlego.activities.route;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import tfg.shuttlego.R;
import tfg.shuttlego.model.adapter.RecyclerViewAdapterOrigin;
import tfg.shuttlego.model.transfer.origin.Origin;
import tfg.shuttlego.model.transfer.route.Route;

public abstract class RouteList extends AppCompatActivity {

    protected LinearLayout routeListLinear;
    protected ProgressBar routeListProgress;
    protected NavigationView routeListNavigation;
    protected DrawerLayout routeListDrawer;

    protected ArrayList<Route> listRoutes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_main);

        inicializateView();
        setProgressBar();
    }

    /**
     * Inicializate the componentes of this view
     */
    private void inicializateView() {

        routeListLinear = findViewById(R.id.route_list_linear);
        routeListProgress = findViewById(R.id.route_list_progress);
        routeListNavigation = findViewById(R.id.route_list_nav);
        routeListDrawer = findViewById(R.id.route_list_drawer);
    }

    /**
     * Show the progress bar component visible and put invisble the rest of the view
     */
    protected void setProgressBar () {

        routeListProgress.setVisibility(View.VISIBLE);
        routeListLinear.setVisibility(View.GONE);
    }

    /**
     * Show the view visible and put invisble progress bar component
     */
    protected void removeProgressBar () {

        routeListProgress.setVisibility(View.GONE);
        routeListLinear.setVisibility(View.VISIBLE);
    }

    protected void throwToast(int msg) { Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show(); }

    /**
     * Inicializate the components to put the menu in the view
     */
    abstract protected void setMenuDrawer();

    /**
     * Put the personal data about the current user
     */
    abstract protected void setCredencials();

    /**
     * Throw the event that allow to get a list of all routes in the server
     *
     */
    abstract protected void throwEventGetAllRoutes();

    /**
     * Inicializate the componentes and the adapter to put the list of routes
     */
    abstract protected void createListView();
}
