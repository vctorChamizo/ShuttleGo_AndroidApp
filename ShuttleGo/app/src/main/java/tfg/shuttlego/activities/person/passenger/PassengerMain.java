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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import tfg.shuttlego.R;
import tfg.shuttlego.activities.map.MapMain;
import tfg.shuttlego.model.event.Event;
import tfg.shuttlego.model.event.EventDispatcher;
import tfg.shuttlego.model.transfer.person.Person;

/**
 *
 */
public class PassengerMain extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, PermissionsListener, MapboxMap.OnMapClickListener, View.OnClickListener {

    private NavigationView navigationView;
    private Person user;
    private MapView mapView;
    private MapboxMap mapboxMap;
    private PermissionsManager permissionsManager;
    private LocationComponent locationComponent;
    private ProgressBar passengerMainProgress;
    private LinearLayout passengerMainLinear;
    private EditText passengerMainDestiny;
    private Button passengerMainButton;
    private AutoCompleteTextView passengerMainOrigin;
    private ArrayList<String> originList;
    private ArrayList<HashMap<?, ?>> originMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Mapbox.getInstance(this, getString(R.string.access_token));
        user = (Person)Objects.requireNonNull(getIntent().getExtras()).getSerializable("user");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.passenger_main);
        inicializateView();
        setProgressBar();
        mapView.onCreate(savedInstanceState);
        setMenuDrawer();
        setCredencials();
        throwEventGerAllOrigins();
        listeners();
    }

    /**
     *
     */
    private void inicializateView() {

        mapView = findViewById(R.id.passenger_main_content_map);
        passengerMainProgress = findViewById(R.id.passenger_main_content_progress);
        passengerMainLinear = findViewById(R.id.passenger_main_content_linear1);
        passengerMainDestiny = findViewById(R.id.passenger_main_content_destiny);
        passengerMainOrigin = findViewById(R.id.passenger_main_content_autocomplete);
        passengerMainButton  = findViewById(R.id.passenger_main_content_button);
    }//inicializateView

    /**
     *
     */
    private void setProgressBar () {

        passengerMainProgress.setVisibility(View.VISIBLE);
        passengerMainLinear.setVisibility(View.GONE);
    }//setProgressBar

    /**
     *
     */
    private void removeProgressBar () {

        passengerMainProgress.setVisibility(View.GONE);
        passengerMainLinear.setVisibility(View.VISIBLE);
    }//removeProgressBar

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
    private void setCredencials() {

        View hView =  navigationView.getHeaderView(0);
        TextView nav_name_text = hView.findViewById(R.id.menu_nav_header_name);
        TextView nav_email_text = hView.findViewById(R.id.menu_nav_header_email);
        nav_name_text.setText(user.getName() + " " + user.getSurname());
        nav_email_text.setText(user.getEmail());
    }//setCredencials

    /**
     *
     */
    private void throwEventGerAllOrigins(){

        EventDispatcher.getInstance(getApplicationContext())
        .dispatchEvent(Event.GETORIGINS, null)
        .addOnCompleteListener(new OnCompleteListener<HashMap<String, String>>() {
            @Override
            public void onComplete(@NonNull Task<HashMap<String, String>> task) {

                if (!task.isSuccessful() || task.getResult() == null) throwToast(R.string.errConexion);
                else if (task.getResult().containsKey("error")) throwToast(R.string.errServer);
                else {

                    HashMap<?, ?> result = task.getResult();
                    originMap = (ArrayList<HashMap<?, ?>>) result.get("origins");
                    originList = new ArrayList<String>();

                    for (HashMap<?, ?> l : originMap) originList.add((String) l.get("name"));

                    setAutoCompleteTextView();
                    removeProgressBar();
                }
            }
        });
    }//throwEventGerAllOrigins

    /**
     *
     */
    private void setAutoCompleteTextView() {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, originList);
        passengerMainOrigin.setThreshold(1);
        passengerMainOrigin.setAdapter(adapter);
    }//setAutoCompleteTextView

    /**
     *
     * @return
     */
    private JSONObject buildJson() {
        return null;
    }//buildJson

    /**
     *
     */
    private void listeners() {

        mapView.getMapAsync(this);
        passengerMainButton.setOnClickListener(this);
    }//listeners

    /**
     *
     * @param msg
     */
    private void throwToast(int msg) { Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show(); }

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

        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            locationComponent = mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(this, loadedMapStyle);
            locationComponent.setLocationComponentEnabled(true);
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
        if (granted) enableLocationComponent(mapboxMap.getStyle());
        else {
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

    @Override
    public void onClick(View v) {

        boolean empty = false;

        switch (v.getId()) {

            case R.id.passenger_main_content_button:
                if (passengerMainDestiny.getText().toString().isEmpty()) empty = true;
                if (passengerMainOrigin.getText().toString().isEmpty()) empty = true;

                if (!empty) {

                    setProgressBar();
                    JSONObject route = buildJson();
                }
                else throwToast(R.string.errDataEmpty);
                break;
        }
    }
}