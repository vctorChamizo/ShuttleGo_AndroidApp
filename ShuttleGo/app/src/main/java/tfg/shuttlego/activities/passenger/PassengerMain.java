package tfg.shuttlego.activities.passenger;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

import java.util.List;
import java.util.Objects;

import tfg.shuttlego.R;
import tfg.shuttlego.activities.map.MapMain;
import tfg.shuttlego.model.transfers.person.Person;

/**
 *
 */
public class PassengerMain extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, PermissionsListener, MapboxMap.OnMapClickListener {

    private NavigationView navigationView;
    private Person user;
    private MapView mapView;
    private MapboxMap mapboxMap;
    // for adding location layer
    private PermissionsManager permissionsManager;
    private LocationComponent locationComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Mapbox.getInstance(this, getString(R.string.access_token));
        user = (Person)Objects.requireNonNull(getIntent().getExtras()).getSerializable("user");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.passenger_main);

        setMenuDrawer();
        setCredencials();

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {

        this.mapboxMap = mapboxMap;

        mapboxMap.setStyle(Style.LIGHT, new Style.OnStyleLoaded() {

            @Override
            public void onStyleLoaded(@NonNull Style style) {

                enableLocationComponent(style);
                mapboxMap.addOnMapClickListener(PassengerMain.this);
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {

        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            // Activate the MapboxMap LocationComponent to show user location
            // Adding in LocationComponentOptions is also an optional parameter
            locationComponent = mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(this, loadedMapStyle);
            locationComponent.setLocationComponentEnabled(true);
            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);
        }
        else {

            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }//enableLocationComponent

    /**
     *
     */
    private void setMenuDrawer() {

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        DrawerLayout drawer = findViewById(R.id.passenger_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }//setMenuDrawer

    /**
     *
     */
    @SuppressLint("SetTextI18n")
    private void setCredencials() {

        View hView =  navigationView.getHeaderView(0);
        TextView nav_name_text = hView.findViewById(R.id.menu_nav_header_name);
        TextView nav_email_text = hView.findViewById(R.id.menu_nav_header_email);
        nav_name_text.setText(user.getName() + " " + user.getSurname());
        nav_email_text.setText(user.getEmail());
    }//setCredencials

    /**
     *
     * @param msg
     */
    private void throwToast(String msg) { Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show(); }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.passenger_main);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }//onBackPressed

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        /*if (id == R.id.nav_add_origin) { }
        else if (id == R.id.nav_settings_admin) { }
        else if (id == R.id.nav_signout_admin) { }*/

        DrawerLayout drawer = findViewById(R.id.passenger_main);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }//onNavigationItemSelected

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocationComponent(mapboxMap.getStyle());
        } else {
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public boolean onMapClick(@NonNull LatLng point) {

        Intent logIntent = new Intent(PassengerMain.this, MapMain.class);
        startActivity(logIntent);

        return true;
    }
}