package tfg.shuttlego.activities.route.routeMain;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.view.MenuItem;
import android.view.View;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Objects;
import tfg.shuttlego.R;
import tfg.shuttlego.activities.person.passenger.PassengerMain;
import tfg.shuttlego.activities.route.routeList.RouteListPassenger;
import tfg.shuttlego.model.event.Event;
import tfg.shuttlego.model.event.EventDispatcher;
import tfg.shuttlego.model.transfer.address.Address;

public class RouteMainPassengerChoose extends RouteMain implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private Address address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        this.searching = true;
        this.address = (Address)Objects.requireNonNull(getIntent().getExtras()).getSerializable("userAddress");

        super.onCreate(savedInstanceState);
    }

    private JSONObject buildJson(String idRoute) {

        JSONObject addToRoute = new JSONObject();
        JSONObject route = new JSONObject();
        JSONObject user = new JSONObject();

        try {

            route.put("id", idRoute);

            user.put("email", this.user.getEmail());
            user.put("password", this.user.getPassword());

            addToRoute.put("user", user);
            addToRoute.put("route", route);
            addToRoute.put("address", address.getAddress());
            addToRoute.put("coordinates", address.getCoordinates().get(0) + "," + address.getCoordinates().get(1));

        } catch (JSONException e) { throwToast(R.string.err); }

        return addToRoute;
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
                if(task.getResult().get("error").equals("userAlreadyAdded"))
                    throwToast(R.string.errUserAlreadyAdded);
                else if(task.getResult().get("error").equals("routeSoldOut"))
                    throwToast(R.string.errSoldOut);
                else throwToast(R.string.errServer);
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

        this.routeMainMainButton.setText(getString(R.string.add));
        this.routeMainSecondaryButton.setVisibility(View.GONE);

        String origin = this.routeMainOrigin.getText() + " " + resultEvent.get("origin");
        this.routeMainOrigin.setText(origin);

        String limit = this.routeMainLimit.getText() + " " + this.address.getAddress().split(",")[0];
        this.routeMainLimit.setText(limit);

        String passengersMax = this.routeMainPassengerMax.getText() + " " + String.valueOf(resultEvent.get("max"));
        this.routeMainPassengerMax.setText(passengersMax);

        String passengersCurrent = this.routeMainPassengerCurrent.getText() + " " + String.valueOf(resultEvent.get("passengersNumber"));
        this.routeMainPassengerCurrent.setText(passengersCurrent);

        this.routeMainLinearDriver.setVisibility(View.INVISIBLE);
        this.routeMainLinearPhone.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View v) {

        setProgressBar();
        throwEventAddToRoute(buildJson(this.routeMainIdRoute));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.driver_drawer_list:
                startActivity(new Intent(RouteMainPassengerChoose.this, RouteListPassenger.class));
                break;
            case R.id.driver_drawer_home:
                startActivity(new Intent(RouteMainPassengerChoose.this, PassengerMain.class));
                break;
        }
        routeMainDrawer.closeDrawer(GravityCompat.START);

        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
