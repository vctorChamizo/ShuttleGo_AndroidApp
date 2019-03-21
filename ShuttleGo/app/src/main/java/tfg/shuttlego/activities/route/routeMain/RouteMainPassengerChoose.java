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

public class RouteMainPassengerChoose extends RouteMain implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

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

    private void throwEventAddToRoute(JSONObject route) {

        EventDispatcher.getInstance(getApplicationContext())
        .dispatchEvent(Event.ADDTOROUTE, route)
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

                startActivity(new Intent(RouteMainPassengerChoose.this, PassengerMain.class));
                throwToast(R.string.successfullyAdded);
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

        this.routeMainSecondaryButton.setVisibility(View.INVISIBLE);
        this.routeMainMainButton.setText(getString(R.string.add));

        String origin = this.routeMainOrigin.getText() + " " + resultEvent.get("origin");
        this.routeMainOrigin.setText(origin);

        String limit = this.routeMainLimit.getText() + " " + String.valueOf(resultEvent.get("destinationName"));
        this.routeMainLimit.setText(limit);

        String passengersMax = this.routeMainPassengerMax.getText() + " " + String.valueOf(resultEvent.get("max"));
        this.routeMainPassengerMax.setText(passengersMax);

        String passengersCurrent = this.routeMainPassengerCurrent.getText() + " " + String.valueOf(resultEvent.get("passengersNumber"));
        this.routeMainPassengerCurrent.setText(passengersCurrent);

        this.routeMainLinearDriver.setVisibility(View.INVISIBLE);
        this.routeMainLinearPhone.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View v) { throwEventAddToRoute(buildJson(routeMainIdRoute)); }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.passenger_drawer_list: startActivity(new Intent(RouteMainPassengerChoose.this, RouteListPassenger.class)); finish(); break;
            case R.id.passenger_drawer_home: startActivity(new Intent(RouteMainPassengerChoose.this, PassengerMain.class)); finish(); break;
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
