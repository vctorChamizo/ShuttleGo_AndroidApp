package tfg.shuttlego.activities.person.passenger;

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
import tfg.shuttlego.model.adapter.RecyclerViewAdapterOrigin;
import tfg.shuttlego.model.adapter.RecyclerViewAdapterRoute;
import tfg.shuttlego.model.session.Session;
import tfg.shuttlego.model.transfer.origin.Origin;
import tfg.shuttlego.model.transfer.person.Person;
import tfg.shuttlego.model.transfer.route.Route;

public class PassengerSearchResult extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView;
    private LinearLayout routeListLinear;
    private ProgressBar routeListProgress;
    private ArrayList<Route> listRoutes;
    private DrawerLayout routeListDrawer;
    private Person user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_list_passengers);

        user = Session.getInstance(getApplicationContext()).getUser();

        inicializateView();
        setProgressBar();
        setMenuDrawer();
        setCredencials();

        // Debes rellenar listRoutes y el metodo siguiente se encargará de generar el adaptador que renderice la lista.
        this.listRoutes =  (ArrayList<Route>)getIntent().getSerializableExtra("routes");
        createListView();
    }

    /**
     * Inicializate the componentes of this view
     */
    private void inicializateView() {

        routeListDrawer = findViewById(R.id.route_list_drawer);
        routeListLinear = findViewById(R.id.route_list_linear);
        routeListProgress = findViewById(R.id.route_list_progress);
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

        navigationView = findViewById(R.id.route_list_nav);
        navigationView.setNavigationItemSelectedListener(this);
        Toolbar toolbar = findViewById(R.id.route_list_toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, routeListDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        routeListDrawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    /**
     * Put the personal data about the current user
     */
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

        RecyclerView recycler = findViewById(R.id.route_list_recycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recycler.setLayoutManager(layoutManager);
        RecyclerView.Adapter<RecyclerViewAdapterRoute.RouteViewHolder> adapter = new RecyclerViewAdapterRoute(this.listRoutes,Session.getInstance(getApplicationContext()).getUser());
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

 /*routeResults = findViewById(R.id.routeResults);
        this.title = findViewById(R.id.routeTitle);

        //Array de rutas
        routes =  (ArrayList<Route>)getIntent().getSerializableExtra("routes");

        //ruta del usuario
        userAddress = (Address) getIntent().getSerializableExtra("userAddress");

        //nombre del origen de la ruta (en Route solo viene el id)
        originName = getIntent().getStringExtra("originName");

        //Array de strings para meter en la lista
        ArrayList<String> listStrings = new ArrayList<String>();

        for(int i = 0; i<routes.size();i++) //ejemplo "Ruta 1 - Plazas libres 10/20
            listStrings.add("Ruta "+(i+1)+" - Plazas libres: "+(routes.get(i).getMax()-routes.get(i).getPassengersNumber())+"/"+routes.get(i).getMax());

        ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listStrings);
        routeResults.setAdapter(adapter);

        this.title.setText("Origen: "+originName+"\n"+"Destino: "+userAddress.getAddress());*/