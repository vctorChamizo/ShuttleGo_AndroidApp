package tfg.shuttlego.activities.route;

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
import tfg.shuttlego.model.event.Event;
import tfg.shuttlego.model.event.EventDispatcher;

public class RouteMainPassengerInformation extends RouteMain implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

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

    private void throwDeleteRoute(JSONObject route) {

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

                throwToast(R.string.deleteRouteSuccesful);
                startActivity(new Intent(RouteMainPassengerInformation.this, PassengerMain.class));
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
    protected void setDataText(HashMap<?, ?> resultEvent) {

        this.routeMainBeginButton.setVisibility(View.INVISIBLE);

        String origin = routeMainOrigin.getText() + " " + resultEvent.get("origin");
        String limit = routeMainLimit.getText() + " " + String.valueOf(resultEvent.get("destination"));
        String passengers = routeMainPassenger.getText() + " " + String.valueOf(resultEvent.get("max"));
        String phone = routeMainPhone.getText() + " " + String.valueOf(resultEvent.get("driverNumber"));
        String driverNameComplete = routeMainDriver.getText() + " " +
                resultEvent.get("driverSurname") + " " +
                resultEvent.get("driverName");

        routeMainOrigin.setText(origin);
        routeMainLimit.setText(limit);
        routeMainPassenger.setText(passengers);
        routeMainDriver.setText(driverNameComplete);
        routeMainPhone.setText(phone);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.route_main_delete_btn:
                throwDeleteRoute(buildJson(routeMainIdRoute));
                break;

            case R.id.route_main_close_btn: finish(); break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {

            case R.id.passenger_drawer_list:
                startActivity(new Intent(RouteMainPassengerInformation.this, RouteListPassenger.class));
                finish();
                break;

            case R.id.passenger_drawer_home:
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

/*

this.routeMainBeginButton.setVisibility(View.INVISIBLE);

        this.routeMainRemoveButton.setText(getText(R.string.book));
        this.routeId = (String) resultEvent.get("id");
        this.userAddress = (Address) getIntent().getSerializableExtra("userAddress");
        String origin = routeMainOrigin.getText() + " " + resultEvent.get("origin");
        String limit = getString(R.string.destiny) + ": " + userAddress.getAddress().split(",")[0];

        int freePlaces = ((Integer)resultEvent.get("max"))-((Integer)resultEvent.get("passengersNumber"));
        Spanned  passengers;

        if(freePlaces == 0){
            passengers = Html.fromHtml(getText(R.string.freePlaces)+": <font color='#EE0000'>"+0+"</font>");
        }
        else passengers =  Html.fromHtml(getText(R.string.freePlaces)+": "+freePlaces);

        String phone = routeMainPhone.getText() + " " + String.valueOf(resultEvent.get("driverNumber"));
        String driverNameComplete = routeMainDriver.getText() + " " +
                resultEvent.get("driverSurname") + " " +
                resultEvent.get("driverName");

        routeMainOrigin.setText(origin);
        routeMainLimit.setText(limit);
        routeMainPassenger.setText(passengers);
        routeMainDriver.setText(driverNameComplete);
        routeMainPhone.setText(phone);

*/