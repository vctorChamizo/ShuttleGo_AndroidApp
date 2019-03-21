package tfg.shuttlego.activities.route.routeMain;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Objects;
import tfg.shuttlego.R;
import tfg.shuttlego.model.event.Event;
import tfg.shuttlego.model.event.EventDispatcher;
import tfg.shuttlego.model.session.Session;
import tfg.shuttlego.model.transfer.person.Person;

public abstract class RouteMain extends AppCompatActivity {

    protected LinearLayout routeMainLinearDriver, routeMainLinearPhone;
    protected Button routeMainMainButton, routeMainSecondaryButton;
    protected TextView routeMainOrigin, routeMainLimit, routeMainPassengerMax, routeMainPassengerCurrent, routeMainDriver, routeMainPhone;
    protected ImageView routeMainImage;
    protected NavigationView routeMainNavigation;
    protected DrawerLayout routeMainDrawer;

    private LinearLayout routeMainLinear;
    private ProgressBar routeMainProgress;

    protected String routeMainIdRoute;
    protected Person user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_main);

        this.routeMainIdRoute = (String)Objects.requireNonNull(getIntent().getExtras()).getSerializable("route");
        this.user = Session.getInstance(getApplicationContext()).getUser();

        inicializateView();
        setProgressBar();
        setMenuDrawer();
        setCredencials();
        throwEventGetRoute(buildJson(routeMainIdRoute));

        listeners();
    }

    /**
     * Inicializate the componentes of this view
     */
    private void inicializateView() {

        this.routeMainLinear = findViewById(R.id.route_main_linear);
        this.routeMainProgress = findViewById(R.id.route_main_progress);

        this.routeMainDrawer = findViewById(R.id.route_main_drawer);
        this.routeMainNavigation = findViewById(R.id.route_main_nav);

        this.routeMainMainButton = findViewById(R.id.route_main_main_btn);
        this.routeMainSecondaryButton = findViewById(R.id.route_main_secondary_btn);

        this.routeMainOrigin = findViewById(R.id.route_main_origin);
        this.routeMainLimit = findViewById(R.id.route_main_limit);
        this.routeMainPassengerMax = findViewById(R.id.route_main_passengers_max);
        this.routeMainPassengerCurrent = findViewById(R.id.route_main_passengers);
        this.routeMainDriver = findViewById(R.id.route_main_driver);
        this.routeMainPhone = findViewById(R.id.route_main_phone);

        this.routeMainLinearDriver = findViewById(R.id.route_main_linear_driver);
        this.routeMainLinearPhone = findViewById(R.id.route_main_linear_phone);

        this.routeMainImage = findViewById(R.id.route_main_ic_destiny);
    }

    /**
     * Show the progress bar component visible and put invisble the rest of the view
     */
    protected void setProgressBar () {

        routeMainProgress.setVisibility(View.VISIBLE);
        routeMainLinear.setVisibility(View.GONE);
    }

    /**
     * Show the view visible and put invisble progress bar component
     */
    protected void removeProgressBar () {

        routeMainProgress.setVisibility(View.GONE);
        routeMainLinear.setVisibility(View.VISIBLE);
    }

    /**
     * Inicializate the components to put the menu in the view
     */
    private void setMenuDrawer() {

        Toolbar toolbar = findViewById(R.id.route_main_toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, routeMainDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        routeMainDrawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    /**
     * Put the personal data about the current user
     */
    private void setCredencials() {

        View hView =  routeMainNavigation.getHeaderView(0);

        TextView nav_name_text = hView.findViewById(R.id.menu_nav_header_name);
        TextView nav_email_text = hView.findViewById(R.id.menu_nav_header_email);

        String complete_name = user.getName() + " " + user.getSurname();
        nav_name_text.setText(complete_name);
        nav_email_text.setText(user.getEmail());
    }

    /**
     * Build a JSON to get a route
     *
     * @param route necesary data to make the correct JSON
     *
     * @return JSON with information about the current origin
     */
    private JSONObject buildJson(String route) {

        JSONObject getRouteJSON = new JSONObject();
        JSONObject userJSON = new JSONObject();
        JSONObject routeJSON = new JSONObject();

        try {

            userJSON.put("email", this.user.getEmail());
            userJSON.put("password", this.user.getPassword());
            routeJSON.put("id", route);

            getRouteJSON.put("user", userJSON);
            getRouteJSON.put("route", routeJSON);


        } catch (JSONException e) { throwToast(R.string.err);}

        return getRouteJSON;
    }

    /**
     * Throw the event that allow to get a route
     *
     * @param route JSON with information to get origin
     */
    private void throwEventGetRoute(JSONObject route) {

        EventDispatcher.getInstance(getApplicationContext())
        .dispatchEvent(Event.GETROUTEBYID, route)
        .addOnCompleteListener(task -> {

            if (!task.isSuccessful() || task.getResult() == null || task.getResult().containsKey("error")) {

                finish();
                throwToast(R.string.err);
            }
            else {

                setDataText(task.getResult());
                removeProgressBar();
            }
        });
    }

    protected void throwToast(int msg) { Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show(); }

    abstract protected void listeners();

    abstract protected void setDataText(HashMap<?,?> resultEvent);
}
