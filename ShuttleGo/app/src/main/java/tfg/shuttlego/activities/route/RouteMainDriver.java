package tfg.shuttlego.activities.route;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.view.MenuItem;
import android.view.View;

import java.util.HashMap;
import java.util.Objects;

import tfg.shuttlego.R;
import tfg.shuttlego.activities.person.driver.DriverMain;
import tfg.shuttlego.model.session.Session;

@SuppressLint("Registered")
public class RouteMainDriver extends RouteMain implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void listeners() {
        routeMainRemoveButton.setOnClickListener(this);
        routeMainCloseButton.setOnClickListener(this);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void setDataText(HashMap<?,?> resultEvent) {

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
                // Falta implementar eliminar ruta
                break;

            case R.id.route_main_close_btn: startActivity(new Intent(RouteMainDriver.this, DriverMain.class)); break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        //Falta listar rutas
        return false;
    }

    @Override
    public void onBackPressed() {
        if (routeDriverMainDrawer.isDrawerOpen(GravityCompat.START)) routeDriverMainDrawer.closeDrawer(GravityCompat.START);
        else startActivity(new Intent(RouteMainDriver.this, DriverMain.class));
    }
}
