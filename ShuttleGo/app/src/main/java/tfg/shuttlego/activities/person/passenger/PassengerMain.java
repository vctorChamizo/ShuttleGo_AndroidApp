package tfg.shuttlego.activities.person.passenger;

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
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import tfg.shuttlego.R;
import tfg.shuttlego.activities.map.MapMain;
import tfg.shuttlego.model.adapters.ListViewAdapterOrigin;
import tfg.shuttlego.model.events.Event;
import tfg.shuttlego.model.events.EventDispatcher;
import tfg.shuttlego.model.transfers.origin.Origin;
import tfg.shuttlego.model.transfers.person.Person;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

/**
 *
 */
public class PassengerMain extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, PermissionsListener, MapboxMap.OnMapClickListener, SearchView.OnQueryTextListener {

    private NavigationView navigationView;
    private Person user;
    private MapView mapView;
    private MapboxMap mapboxMap;
    // for adding location layer
    private PermissionsManager permissionsManager;
    private LocationComponent locationComponent;

    private ListView list;
    private ListViewAdapterOrigin adapter;
    private SearchView editsearch;
    private ArrayList<Origin> originList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Mapbox.getInstance(this, getString(R.string.access_token));
        user = (Person)Objects.requireNonNull(getIntent().getExtras()).getSerializable("user");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.passenger_main);

        setMenuDrawer();
        setCredencials();
        setSearchSpinner();

        mapView = findViewById(R.id.passenger_main_content_map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    /**
     *
     */
    private void setSearchSpinner() {

        EventDispatcher.getInstance(getApplicationContext())
                .dispatchEvent(Event.GETORIGINS, null)
                .addOnCompleteListener(new OnCompleteListener<HashMap<String, String>>() {
                    @Override
                    public void onComplete(@NonNull Task<HashMap<String, String>> task) {

                        if (!task.isSuccessful() || task.getResult() == null) {
                            //changeVisibility();
                            throwToast("Error de conexion");
                        } else if (task.getResult().containsKey("error")) {

                            switch (Objects.requireNonNull(task.getResult().get("error"))) {
                                case "server":
                                    throwToast("Error del servidor");
                                    break;

                                default:
                                    throwToast("Error desconocido: " + task.getResult().get("error"));
                                    break;
                            }//switch
                        } else {

                            HashMap<?, ?> result = task.getResult();
                            ArrayList<HashMap<?, ?>> list = (ArrayList<HashMap<?, ?>>) result.get("origins");
                            originList = new ArrayList<>();

                            assert list != null;
                            for (int i = 0; i < list.size(); ++i) {
                                Origin origin = new Origin();
                                origin.setId((String) list.get(i).get("id"));
                                origin.setName((String) list.get(i).get("name"));
                                originList.add(origin);
                            }//for

                            createListView();
                        }//else
                    }//onComplete
                });
    }//getOriginList

    /**
     *
     */
    private void createListView() {

        list = findViewById(R.id.passenger_main_content_listview);
        adapter = new ListViewAdapterOrigin(this, originList);
        editsearch = findViewById(R.id.passenger_main_content_search);
        editsearch.setOnQueryTextListener(this);
    }//createListView

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        String text = newText;
        list.setVisibility(View.VISIBLE);
        list.setAdapter(adapter);
        adapter.filter(text);

        return false;
    }

    /*
        ***********************************   Map Code   *******************************************
     */

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

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

    /*
    *********************************************************************************************
     */

    /*
     ***********************************   Menu Code   ******************************************
     */

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
        if (drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START);
        else super.onBackPressed();
    }//onBackPressed

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_settings_admin) { }
        else if (id == R.id.nav_signout_admin) { }

        DrawerLayout drawer = findViewById(R.id.passenger_main);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }//onNavigationItemSelected
}