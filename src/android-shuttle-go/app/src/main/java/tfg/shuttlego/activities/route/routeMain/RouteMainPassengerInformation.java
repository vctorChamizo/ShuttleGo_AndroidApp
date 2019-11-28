package tfg.shuttlego.activities.route.routeMain;

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
import tfg.shuttlego.activities.person.passenger.PassengerMain;
import tfg.shuttlego.activities.route.routeList.RouteListPassenger;
import tfg.shuttlego.model.event.Event;
import tfg.shuttlego.model.event.EventDispatcher;

public class RouteMainPassengerInformation extends RouteMain implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    /**
     * Build a JSON to delete a route
     *
     * @param route necesary data to make the correct JSON
     *
     * @return JSON with information about the current route
     */
    private JSONObject buildJson(String route) {

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

    /**
     * Throw the event that allow to delete a route
     *
     * @param route JSON with information to add route
     */
    private void throwEventDeleteRoute(JSONObject route) {

        EventDispatcher.getInstance(getApplicationContext())
        .dispatchEvent(Event.REMOVEPASSENGERFROMROUTE, route)
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

                throwToast(R.string.cancelRouteSuccesful);
                finish();
            }
        });
    }

    @Override
    protected void listeners() {

        this.routeMainMainButton.setOnClickListener(this);
        this.routeMainNavigation.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void setDataText(HashMap<?, ?> resultEvent) {

        this.routeMainSecondaryButton.setVisibility(View.GONE);
        this.routeMainMainButton.setText(getString(R.string.cancelRoute));

        String origin = this.routeMainOrigin.getText() + " " + resultEvent.get("origin");
        this.routeMainOrigin.setText(origin);

        String[] destinyShort = String.valueOf(resultEvent.get("destinationName")).split(",");
        String destiny = this.routeMainLimit.getText() + " " + destinyShort[0];
        this.routeMainLimit.setText(destiny);

        String passengersMax = this.routeMainPassengerMax.getText() + " " + String.valueOf(resultEvent.get("max"));
        this.routeMainPassengerMax.setText(passengersMax);

        String passengersCurrent = this.routeMainPassengerCurrent.getText() + " " + String.valueOf(resultEvent.get("passengersNumber"));
        this.routeMainPassengerCurrent.setText(passengersCurrent);

        String phone = this.routeMainPhone.getText() + " " + String.valueOf(resultEvent.get("driverNumber"));
        this.routeMainPhone.setText(phone);

        String driverNameComplete = this.routeMainDriver.getText() + " " + resultEvent.get("driverName") + " " + resultEvent.get("driverSurname");
        this.routeMainDriver.setText(driverNameComplete);
    }

    @Override
    public void onClick(View v) {

        setProgressBar();
        throwEventDeleteRoute(buildJson(routeMainIdRoute));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {

            case R.id.driver_drawer_list:

                startActivity(new Intent(RouteMainPassengerInformation.this, RouteListPassenger.class));
                finish();
                break;

            case R.id.driver_drawer_home:

                startActivity(new Intent(RouteMainPassengerInformation.this, PassengerMain.class));
                finish();
                break;
        }

        routeMainDrawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onBackPressed() {

        if (routeMainDrawer.isDrawerOpen(GravityCompat.START)) routeMainDrawer.closeDrawer(GravityCompat.START);
        else finish();
    }
}