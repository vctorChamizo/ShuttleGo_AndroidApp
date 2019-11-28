package tfg.shuttlego.activities.route;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import java.util.ArrayList;
import tfg.shuttlego.R;
import tfg.shuttlego.activities.person.passenger.PassengerMain;
import tfg.shuttlego.activities.route.routeList.RouteListPassenger;
import tfg.shuttlego.activities.adapter.RecyclerViewAdapterChooseRoute;
import tfg.shuttlego.model.session.Session;
import tfg.shuttlego.model.transfer.address.Address;
import tfg.shuttlego.model.transfer.person.Person;
import tfg.shuttlego.model.transfer.route.Route;

public class RouteChoosePassenger extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView routeChoosePassengerNavigation;
    private DrawerLayout routeChoosePassengerDrawer;
    private Toolbar routeChoosePassengerToolbar;

    private LinearLayout routeChoosePassengerLinear;
    private ProgressBar routeChoosePassengerProgress;

    private RecyclerView routeChoosePassengerRecycler;

    private ArrayList<Route> routeChoosePassengerListRoutes;
    private String routeChoosePassengerOrigin;
    private Address routeChoosePassengerAdress;
    private Person user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_choose_passenger);

        this.user = Session.getInstance().getUser();
        this.routeChoosePassengerListRoutes =  (ArrayList<Route>)getIntent().getSerializableExtra("routes");
        this.routeChoosePassengerOrigin = (String) getIntent().getSerializableExtra("originName");
        this.routeChoosePassengerAdress = (Address) getIntent().getSerializableExtra("userAddress");

        inicializateView();
        setProgressBar();
        setMenuDrawer();
        setCredencials();
        createListView();
        removeProgressBar();

        routeChoosePassengerNavigation.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onStart() { super.onStart(); }

    @Override
    protected void onPause() { super.onPause(); }

    @Override
    protected void onStop() { super.onStop(); }

    @Override
    protected void onDestroy() { super.onDestroy(); }

    @Override
    public void onLowMemory() { super.onLowMemory(); }

    @Override
    protected void onResume() { super.onResume(); }

    /**
     * Inicializate the componentes of this view
     */
    private void inicializateView() {

        this.routeChoosePassengerNavigation = findViewById(R.id.route_choose_passenger_nav);
        this.routeChoosePassengerDrawer = findViewById(R.id.route_choose_passenger_drawer);
        this.routeChoosePassengerToolbar = findViewById(R.id.route_choose_passenger_toolbar);

        this.routeChoosePassengerRecycler = findViewById(R.id.route_choose_passenger_recycler);

        this.routeChoosePassengerLinear = findViewById(R.id.route_choose_passenger_linear);
        this.routeChoosePassengerProgress = findViewById(R.id.route_choose_passenger_progress);

        String shortAddress = this.routeChoosePassengerAdress.getAddress().split(",")[0];
        this.routeChoosePassengerToolbar.setTitle(this.routeChoosePassengerOrigin);
        this.routeChoosePassengerToolbar.setSubtitle(shortAddress);
        this.routeChoosePassengerToolbar.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
    }

    /**
     * Show the progress bar component visible and put invisble the rest of the view
     */
    private void setProgressBar() {

        this.routeChoosePassengerProgress.setVisibility(View.VISIBLE);
        this.routeChoosePassengerLinear.setVisibility(View.GONE);
    }

    /**
     * Show the view visible and put invisble progress bar component
     */
    private void removeProgressBar() {

        this.routeChoosePassengerProgress.setVisibility(View.GONE);
        this.routeChoosePassengerLinear.setVisibility(View.VISIBLE);
    }

    /**
     * Inicializate the components to put the menu in the view
     */
    private void setMenuDrawer() {

        setSupportActionBar(routeChoosePassengerToolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, routeChoosePassengerDrawer, routeChoosePassengerToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        this.routeChoosePassengerDrawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    /**
     * Put the personal data about the current user
     */
    @SuppressLint("SetTextI18n")
    private void setCredencials() {

        View hView =  this.routeChoosePassengerNavigation.getHeaderView(0);

        TextView nav_name_text = hView.findViewById(R.id.menu_nav_header_name);
        TextView nav_email_text = hView.findViewById(R.id.menu_nav_header_email);

        String complete_name = this.user.getName() + " " + this.user.getSurname();
        nav_name_text.setText(complete_name);
        nav_email_text.setText(this.user.getEmail());
    }

    /**
     * Inicializate the componentes and the adapter to put the list of routes
     */
    private void createListView() {

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        this.routeChoosePassengerRecycler.setLayoutManager(layoutManager);
        RecyclerView.Adapter<RecyclerViewAdapterChooseRoute.RouteViewHolder> adapter = new RecyclerViewAdapterChooseRoute(this.routeChoosePassengerListRoutes, this.routeChoosePassengerAdress);
        this.routeChoosePassengerRecycler.setAdapter(adapter);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {

            case R.id.passenger_drawer_home:
                startActivity(new Intent(RouteChoosePassenger.this, PassengerMain.class));
                break;
            case R.id.passenger_drawer_list:
                startActivity(new Intent(RouteChoosePassenger.this, RouteListPassenger.class));
                break;
        }

        this.routeChoosePassengerDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {

        if (this.routeChoosePassengerDrawer.isDrawerOpen(GravityCompat.START)) this.routeChoosePassengerDrawer.closeDrawer(GravityCompat.START);
        else finish();
    }
}
