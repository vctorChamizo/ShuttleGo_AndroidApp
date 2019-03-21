package tfg.shuttlego.activities.route.routeMain;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.view.MenuItem;
import android.view.View;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import tfg.shuttlego.R;
import tfg.shuttlego.activities.person.driver.DriverMain;
import tfg.shuttlego.activities.route.RouteCalculate;
import tfg.shuttlego.activities.route.routeList.RouteListDriver;
import tfg.shuttlego.model.event.Event;
import tfg.shuttlego.model.event.EventDispatcher;

@SuppressLint("Registered")
public class RouteMainDriver extends RouteMain implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private JSONObject buildJSONDeleteRoute(String route) {

        JSONObject dataUser = new JSONObject();
        JSONObject dataRoute = new JSONObject();
        JSONObject deleteRoute = new JSONObject();

        try {

        dataUser.put("email", this.user.getEmail());
        dataUser.put("password", this.user.getPassword());

        dataRoute.put("id", route);

        deleteRoute.put("user", dataUser);
        deleteRoute.put("route", dataRoute);
        }
        catch (JSONException e) { throwToast(R.string.err); }

        return deleteRoute;
    }

    private void throwEventDeleteRoute(JSONObject route) {

        EventDispatcher.getInstance(getApplicationContext())
        .dispatchEvent(Event.DELETEROUTEBYID, route)
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

                throwToast(R.string.deleteRouteSuccesful);
                startActivity(new Intent(RouteMainDriver.this, DriverMain.class));
                finish();
            }
        });
    }

    @Override
    protected void listeners() {

        routeMainRemoveButton.setOnClickListener(this);
        routeMainCloseButton.setOnClickListener(this);
        routeMainNavigation.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void setDataText(HashMap<?,?> resultEvent) {

        this.routeMainLinearDriver.setVisibility(View.INVISIBLE);
        this.routeMainLinearPhone.setVisibility(View.INVISIBLE);

        String origin = this.routeMainOrigin.getText() + " " + resultEvent.get("origin");

        this.routeMainImage.setImageDrawable(getDrawable(R.drawable.ic_limit));
        String limit = getString(R.string.limitCardview) + " " + String.valueOf(resultEvent.get("destination"));

        String passengersMax = this.routeMainPassengerMax.getText() + " " + String.valueOf(resultEvent.get("max"));
        String passengersCurrent = this.routeMainPassengerCurrent.getText() + " " + String.valueOf(resultEvent.get("passengersNumber"));

        this.routeMainOrigin.setText(origin);
        this.routeMainLimit.setText(limit);
        this.routeMainPassengerMax.setText(passengersMax);
        this.routeMainPassengerCurrent.setText(passengersCurrent);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.route_main_begin_btn:

                Intent intent = new Intent(RouteMainDriver.this, RouteCalculate.class);
                intent.putExtra("routeId", this.routeMainIdRoute);
                startActivity(intent);
                finish();
                break;

            case R.id.route_main_delete_btn:
                setProgressBar();
                throwEventDeleteRoute(buildJSONDeleteRoute(routeMainIdRoute));
                break;

            case R.id.route_main_close_btn:
                startActivity(new Intent(RouteMainDriver.this, DriverMain.class));
                finish();
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {

            case R.id.driver_drawer_list:
                startActivity(new Intent(RouteMainDriver.this, RouteListDriver.class));
                finish();
                break;

            case R.id.driver_drawer_home:
                startActivity(new Intent(RouteMainDriver.this, DriverMain.class));
                finish();
                break;
        }

        routeMainDrawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onBackPressed() {

        if (routeMainDrawer.isDrawerOpen(GravityCompat.START)) routeMainDrawer.closeDrawer(GravityCompat.START);
        else {
            startActivity(new Intent(RouteMainDriver.this, DriverMain.class));
            finish();
        }
    }
}
