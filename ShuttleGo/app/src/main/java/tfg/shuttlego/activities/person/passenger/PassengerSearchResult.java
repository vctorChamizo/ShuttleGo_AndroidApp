package tfg.shuttlego.activities.person.passenger;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import tfg.shuttlego.R;
import tfg.shuttlego.model.adapter.RecyclerViewAdapterRoute;
import tfg.shuttlego.model.session.Session;
import tfg.shuttlego.model.transfer.address.Address;
import tfg.shuttlego.model.transfer.person.Person;
import tfg.shuttlego.model.transfer.route.Route;

public class PassengerSearchResult extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView;
    private LinearLayout routeListLinear;
    private ProgressBar routeListProgress;
    private ArrayList<Route> listRoutes;
    private DrawerLayout routeListDrawer;
    private Person user;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_list_passengers);

        this.user = Session.getInstance(getApplicationContext()).getUser();
        this.listRoutes =  (ArrayList<Route>)getIntent().getSerializableExtra("routes");

        inicializateView();
        setProgressBar();
        setMenuDrawer();
        setCredencials();
        createListView();
        removeProgressBar();
    }

    /**
     * Inicializate the componentes of this view
     */
    private void inicializateView() {

        routeListDrawer = findViewById(R.id.route_list_passenger_drawer);
        routeListLinear = findViewById(R.id.route_list_passenger_linear);
        routeListProgress = findViewById(R.id.route_list_passenger_progress);
    }

    /**
     * Show the progress bar component visible and put invisble the rest of the view
     */
    private void setProgressBar() {

        routeListProgress.setVisibility(View.VISIBLE);
        routeListLinear.setVisibility(View.GONE);
    }

    /**
     * Show the view visible and put invisble progress bar component
     */
    private void removeProgressBar() {

        routeListProgress.setVisibility(View.GONE);
        routeListLinear.setVisibility(View.VISIBLE);
    }

    /**
     * Inicializate the components to put the menu in the view
     */
    private void setMenuDrawer() {

        navigationView = findViewById(R.id.route_list_passenger_nav);
        navigationView.setNavigationItemSelectedListener(this);
        this.toolbar = findViewById(R.id.route_list_passenger_toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, routeListDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        routeListDrawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    /**
     * Put the personal data about the current user
     */
    @SuppressLint("SetTextI18n")
    private void setCredencials() {

        View hView =  navigationView.getHeaderView(0);
        TextView nav_name_text = hView.findViewById(R.id.menu_nav_header_name);
        TextView nav_email_text = hView.findViewById(R.id.menu_nav_header_email);
        nav_name_text.setText(user.getName() + " " + user.getSurname());
        nav_email_text.setText(user.getEmail());
    }

    /**
     * Inicializate the componentes and the adapter to put the list of routes
     */
    private void createListView() {

        RecyclerView recycler = findViewById(R.id.route_list_passenger_recycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recycler.setLayoutManager(layoutManager);

        String originName = getIntent().getStringExtra("originName");
        Address userAddress = (Address) getIntent().getSerializableExtra("userAddress");
        String shortAddress = userAddress.getAddress().split(",")[0];
        this.toolbar.setTitle(originName);
        this.toolbar.setSubtitle(shortAddress);
        this.toolbar.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        RecyclerView.Adapter<RecyclerViewAdapterRoute.RouteViewHolder> adapter = new RecyclerViewAdapterRoute(this.listRoutes, this.user);
        recycler.setAdapter(adapter);
    }

    private void throwToast(int msg) { Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show(); }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {

            default: break;
        }

        routeListDrawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onBackPressed() {
        if (routeListDrawer.isDrawerOpen(GravityCompat.START)) routeListDrawer.closeDrawer(GravityCompat.START);
    }
}
