package tfg.shuttlego.activities.person.driver;

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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import tfg.shuttlego.R;
import tfg.shuttlego.activities.account.LoginMain;
import tfg.shuttlego.activities.route.routeList.RouteListDriver;
import tfg.shuttlego.activities.route.routeMain.RouteMainDriver;
import tfg.shuttlego.model.event.Event;
import tfg.shuttlego.model.event.EventDispatcher;
import tfg.shuttlego.model.session.Session;
import tfg.shuttlego.model.transfer.person.Person;

public class DriverMain extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, View.OnFocusChangeListener {

    private ProgressBar driverMainProgress;
    private LinearLayout driverMainLinear;

    private NavigationView driverMainNavigation;
    private DrawerLayout driverMainDrawer;

    private EditText driverMainLimit, driverMainPassenger, driverMainHour;
    private AutoCompleteTextView driverMainOrigin;
    private Button driverMainButton;

    private String idOrigin;
    private Person user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_main);

        this.user = Session.getInstance().getUser();

        inicializateView();
        setProgressBar();
        setMenuDrawer();
        setCredencials();
        throwEventGerAllOrigins();

        this.driverMainButton.setOnClickListener(this);
        this.driverMainHour.setOnClickListener(this);
        this.driverMainOrigin.setOnFocusChangeListener(this);
        this.driverMainNavigation.setNavigationItemSelectedListener(this);
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

        this.driverMainProgress = findViewById(R.id.driver_main_progress);
        this.driverMainLinear = findViewById(R.id.driver_main_linear);

        this.driverMainNavigation = findViewById(R.id.driver_main_nav);
        this.driverMainDrawer = findViewById(R.id.driver_main_drawer);

        this.driverMainLimit = findViewById(R.id.driver_main_limit);
        this.driverMainPassenger = findViewById(R.id.driver_main_passenger);
        this.driverMainHour = findViewById(R.id.driver_main_hour);
        this.driverMainOrigin = findViewById(R.id.driver_main_origin);
        this.driverMainButton = findViewById(R.id.driver_main_button);
    }

    /**
     * Show the progress bar component visible and put invisble the rest of the view
     */
    private void setProgressBar () {

        this.driverMainProgress.setVisibility(View.VISIBLE);
        this.driverMainLinear.setVisibility(View.GONE);
    }

    /**
     * Show the view visible and put invisble progress bar component
     */
    private void removeProgressBar () {

        this.driverMainProgress.setVisibility(View.GONE);
        this.driverMainLinear.setVisibility(View.VISIBLE);
    }

    /**
     * Inicializate the components to put the menu in the view
     */
    private void setMenuDrawer() {

        Toolbar toolbar = findViewById(R.id.driver_main_toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, driverMainDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        this.driverMainDrawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    /**
     * Put the personal data about the current user
     */
    private void setCredencials() {

        View hView =  this.driverMainNavigation.getHeaderView(0);

        TextView nav_name_text = hView.findViewById(R.id.menu_nav_header_name);
        TextView nav_email_text = hView.findViewById(R.id.menu_nav_header_email);

        String complete_name = this.user.getName() + " " + this.user.getSurname();
        nav_name_text.setText(complete_name);
        nav_email_text.setText(this.user.getEmail());
    }

    /**
     * Throw the event that allow to get a list of all origins in the server.
     *
     */
    private void throwEventGerAllOrigins(){

        EventDispatcher.getInstance(getApplicationContext())
        .dispatchEvent(Event.GETORIGINS, null)
        .addOnCompleteListener(task -> {

            if (!task.isSuccessful() || task.getResult() == null) throwToast(R.string.errConexion);
            else if (task.getResult().containsKey("error")) throwToast(R.string.errServer);
            else {

                HashMap<?, ?> result = task.getResult();
                ArrayList<HashMap<?, ?>> originMap = (ArrayList<HashMap<?, ?>>) result.get("origins");
                ArrayList<String> originList = new ArrayList<>();

                assert originMap != null;
                for (HashMap<?, ?> l : originMap) originList.add((String) l.get("name"));

                setAutoCompleteTextView(originList);
            }

            removeProgressBar();
        });
    }

    /**
     * Write the options in the auto edit text.
     */
    private void setAutoCompleteTextView(ArrayList<String> originList) {

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, originList);
        this.driverMainOrigin.setThreshold(1);
        this.driverMainOrigin.setAdapter(adapter);
    }

    /**
     * Build a JSON to get a route
     *
     * @return JSON with information about the current route
     */
    private JSONObject buildJson() {

        JSONObject json = new JSONObject();
        JSONObject userJson = new JSONObject();
        JSONObject routeJson = new JSONObject();

        try {

            int codePostal = Integer.parseInt(driverMainLimit.getText().toString());
            int passengers = Integer.parseInt(driverMainPassenger.getText().toString());

            userJson.put("email", user.getEmail());
            userJson.put("password", user.getPassword());
            routeJson.put("max", passengers);
            routeJson.put("origin", idOrigin);
            routeJson.put("destination", codePostal);
            routeJson.put("hour", driverMainHour.getText());

            json.put("user", userJson);
            json.put("route", routeJson);

        } catch (JSONException e) { throwToast(R.string.err);}

        return json;
    }

    /**
     * Throw the event that allow to get a route.
     *
     * @param origin The data necessary to throw the event.
     */
    private void throwEventGetRouteByName(JSONObject origin) {

        EventDispatcher.getInstance(getApplicationContext())
        .dispatchEvent(Event.GETORIGINBYNAME, origin )
        .addOnCompleteListener(task -> {

            if (!task.isSuccessful() || task.getResult() == null) {

                removeProgressBar();
                throwToast(R.string.errConexion);
            }
            else if (task.getResult().containsKey("error")) {

                removeProgressBar();
                throwToast(R.string.errServer);
            }
            else {

                this.idOrigin = task.getResult().get("id");
                throwEventCreteRoute(buildJson());
            }
        });
    }

    /**
     * Throw the event that allow to create a route.
     *
     * @param route The data necessary to throw the event.
     */
    private void throwEventCreteRoute(JSONObject route) {

        EventDispatcher.getInstance(getApplicationContext())
        .dispatchEvent(Event.CREATEROUTE, route)
        .addOnCompleteListener(task -> {

            if (!task.isSuccessful() || task.getResult() == null) {

                removeProgressBar();
                throwToast(R.string.errConexion);
            }
            else if (task.getResult().containsKey("error")) {

                removeProgressBar();
                throwToast(R.string.errServer);
            }
            else {

                Intent logIntent = new Intent(DriverMain.this, RouteMainDriver.class);
                logIntent.putExtra("route", task.getResult().get("id"));
                startActivity(logIntent);
                throwToast(R.string.createRouteSucessful);
                finish();
            }
        });
    }

    private void throwToast(int msg) { Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show(); }

    @Override
    public void onFocusChange(View v, boolean hasFocus) { if(getCurrentFocus() == this.driverMainOrigin) this.driverMainOrigin.showDropDown(); }

    @Override
    public void onClick(View v) {

        if (this.driverMainOrigin.getText().toString().isEmpty() && this.driverMainLimit.getText().toString().isEmpty() && this.driverMainPassenger.getText().toString().isEmpty() && this.driverMainHour.getText().toString().isEmpty()) throwToast(R.string.errDataRouteDriverEmpty);
        else if (this.driverMainOrigin.getText().toString().isEmpty()) throwToast(R.string.errOriginDriverEmpty);
        else if (this.driverMainLimit.getText().toString().isEmpty()) throwToast(R.string.errLimitDriverEmpty);
        else if (this.driverMainPassenger.getText().toString().isEmpty()) throwToast(R.string.errPassengerDriverEmpty);
        else if (this.driverMainHour.getText().toString().isEmpty()) throwToast(R.string.errHourDriverEmpty);
        else {

            setProgressBar();

            try { throwEventGetRouteByName(new JSONObject().put("origin", new JSONObject().put("name", this.driverMainOrigin.getText().toString()))); }
            catch (JSONException e) { throwToast(R.string.err);}
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {

            case R.id.driver_drawer_list: startActivity(new Intent(DriverMain.this, RouteListDriver.class)); break;
        }

        this.driverMainDrawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onBackPressed() {

        if (this.driverMainDrawer.isDrawerOpen(GravityCompat.START)) this.driverMainDrawer.closeDrawer(GravityCompat.START);
        else {

            startActivity(new Intent(DriverMain.this, LoginMain.class));
            finish();
        }
    }
}
