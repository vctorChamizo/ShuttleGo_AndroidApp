package tfg.shuttlego.activities.route;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Objects;
import tfg.shuttlego.R;
import tfg.shuttlego.activities.person.driver.DriverMain;
import tfg.shuttlego.model.event.Event;
import tfg.shuttlego.model.event.EventDispatcher;
import tfg.shuttlego.model.session.Session;
import tfg.shuttlego.model.transfer.person.Person;

public class RouteMain extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private Button routeMainRemoveButton, routeMainCloseButton, routeMainEditButton;
    private TextView routeMainOrigin, routeMainLimit, routeMainPassenger, routeMainDriver, routeMainPhone;
    private LinearLayout routeMainLinear;
    private ProgressBar routeMainProgress;
    private NavigationView navigationView;
    private DrawerLayout routeDriverMainDrawer;
    private Person user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_main_driver);

        String route = (String)Objects.requireNonNull(getIntent().getExtras()).getSerializable("route");
        user = Session.getInstance(getApplicationContext()).getUser();

        inicializateView();
        setProgressBar();
        setMenuDrawer();
        setCredencials();
        throwEventGetRoute(buildJson(route));

        routeMainRemoveButton.setOnClickListener(this);
        routeMainCloseButton.setOnClickListener(this);
        routeMainEditButton.setOnClickListener(this);
    }

    /**
     * Inicializate the componentes of this view
     */
    private void inicializateView() {

        routeMainLinear = findViewById(R.id.route_main_driver_linear);
        routeMainProgress = findViewById(R.id.route_main_driver_progress);
        routeMainRemoveButton = findViewById(R.id.route_main_driver_delete_btn);
        routeMainCloseButton = findViewById(R.id.route_main_driver_close_btn);
        routeMainEditButton = findViewById(R.id.route_main_driver_edit_btn);
        routeMainOrigin = findViewById(R.id.route_main_driver_origin);
        routeMainLimit = findViewById(R.id.route_main_driver_limit);
        routeMainPassenger = findViewById(R.id.route_main_driver_passengers);
        routeMainDriver = findViewById(R.id.route_main_driver_driver);
        routeMainPhone = findViewById(R.id.route_main_driver_phone);
        routeDriverMainDrawer = findViewById(R.id.route_main_driver_drawer);
    }

    /**
     * Show the progress bar component visible and put invisble the rest of the view
     */
    private void setProgressBar () {

        routeMainProgress.setVisibility(View.VISIBLE);
        routeMainLinear.setVisibility(View.GONE);
    }

    /**
     * Show the view visible and put invisble progress bar component
     */
    private void removeProgressBar () {

        routeMainProgress.setVisibility(View.GONE);
        routeMainLinear.setVisibility(View.VISIBLE);
    }

    /**
     * Inicializate the components to put the menu in the view
     */
    private void setMenuDrawer() {

        navigationView = findViewById(R.id.route_main_driver_nav);
        navigationView.setNavigationItemSelectedListener(this);
        Toolbar toolbar = findViewById(R.id.route_main_driver_toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, routeDriverMainDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        routeDriverMainDrawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    /**
     * Put the personal data about the current user
     */
    private void setCredencials() {

        View hView =  navigationView.getHeaderView(0);
        TextView nav_name_text = hView.findViewById(R.id.menu_nav_header_name);
        TextView nav_email_text = hView.findViewById(R.id.menu_nav_header_email);
        String complete_name = user.getName() + " " + user.getSurname();
        nav_name_text.setText(complete_name);
        nav_email_text.setText(user.getEmail());
    }

    private JSONObject buildJson(String route) {

        JSONObject json = new JSONObject();
        JSONObject routeJson = new JSONObject();

        try {

            routeJson.put("id", route);
            json.put("route", routeJson);

        } catch (JSONException e) { throwToast(R.string.err);}

        return json;
    }

    private void throwEventGetRoute(JSONObject route) {

        EventDispatcher.getInstance(getApplicationContext())
        .dispatchEvent(Event.GETROUTEBYID, route)
        .addOnCompleteListener(task -> {

            if (!task.isSuccessful() || task.getResult() == null) throwToast(R.string.errConexion);
            else if (task.getResult().containsKey("error")) throwToast(R.string.errServer);
            else {

                String origin = routeMainOrigin.getText() + " " + task.getResult().get("origin");
                String limit = routeMainLimit.getText() + " " + String.valueOf(task.getResult().get("destination"));
                String passengers = routeMainPassenger.getText() + " " + String.valueOf(task.getResult().get("max"));
                String phone = routeMainPhone.getText() + " " + String.valueOf(task.getResult().get("driverNumber"));
                String driverNameComplete = routeMainDriver.getText() + " " +
                                            task.getResult().get("driverSurname") + " " +
                                            task.getResult().get("driverName");

                routeMainOrigin.setText(origin);
                routeMainLimit.setText(limit);
                routeMainPassenger.setText(passengers);
                routeMainDriver.setText(driverNameComplete);
                routeMainPhone.setText(phone);

                removeProgressBar();
            }
        });
    }

    private void throwToast(int msg) { Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show(); }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.route_main_driver_delete_btn:
                break;

            case R.id.route_main_driver_close_btn:
                break;

            case R.id.route_main_driver_edit_btn:
                startActivity(new Intent(RouteMain.this, DriverMain.class));
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        return false;
    }

    @Override
    public void onBackPressed() {
        if (routeDriverMainDrawer.isDrawerOpen(GravityCompat.START)) routeDriverMainDrawer.closeDrawer(GravityCompat.START);
        else startActivity(new Intent(RouteMain.this, DriverMain.class));
    }
}
