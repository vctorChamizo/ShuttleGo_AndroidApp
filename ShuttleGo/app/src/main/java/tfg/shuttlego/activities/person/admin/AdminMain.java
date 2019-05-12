package tfg.shuttlego.activities.person.admin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import tfg.shuttlego.R;
import tfg.shuttlego.activities.account.LoginMain;
import tfg.shuttlego.activities.origin.OriginList;
import tfg.shuttlego.activities.origin.OriginMain;
import tfg.shuttlego.model.event.Event;
import tfg.shuttlego.model.event.EventDispatcher;
import tfg.shuttlego.model.map.Map;
import tfg.shuttlego.model.session.Session;
import tfg.shuttlego.model.transfer.address.Address;
import tfg.shuttlego.model.transfer.person.Person;

public class AdminMain extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, OnMapReadyCallback, PermissionsListener, AdapterView.OnItemClickListener, TextWatcher {

    private Person user;

    private DrawerLayout admiMainDrawer;
    private NavigationView navigationView;

    private ProgressBar adminMainProgress;
    private LinearLayout adminMainLinear;

    private EditText adminMainOriginText;
    private AutoCompleteTextView adminMainOriginAutocomplete;
    private Button adminMainButton;

    private List<Address> adminMainSearchResult;
    private Address adminMainOrigin;
    private Boolean adminMainDestinySelected;

    private MapView mapView;
    private MapboxMap mapboxMap;
    private PermissionsManager permissionsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Mapbox.getInstance(this, getString(R.string.access_token));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_main);

        this.user = Session.getInstance().getUser();

        incializateView();
        setProgressBar();

        this.mapView.onCreate(savedInstanceState);

        setMenuDrawer();
        setCredencials();

        removeProgressBar();

        this.navigationView.setNavigationItemSelectedListener(this);
        this.mapView.getMapAsync(this);
        this.adminMainOriginAutocomplete.addTextChangedListener(this);
        this.adminMainOriginAutocomplete.setOnItemClickListener(this);
        this.adminMainButton.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        this.mapView.onStart();
        super.onStart();
    }

    @Override
    protected void onRestart(){
        this.mapView.onStart();
        removeProgressBar();
        super.onRestart();
    }

    @Override
    protected void onPause() {
        this.mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        this.mapView.onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        this.mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        this.mapView.onLowMemory();
        super.onLowMemory();
    }

    @Override
    protected void onResume() {
        this.mapView.onResume();
        super.onResume();
    }

    /**
     * Inicializate the componentes of this view
     */
    private void incializateView() {

        this.admiMainDrawer = findViewById(R.id.admin_main_drawer);
        this.navigationView = findViewById(R.id.admin_main_nav);

        this.adminMainProgress = findViewById(R.id.admin_main_progress);
        this.adminMainLinear = findViewById(R.id.admin_main_linear);

        this.adminMainOriginText = findViewById(R.id.admin_main_origin);
        this.adminMainOriginAutocomplete = findViewById(R.id.admin_main_autocomplete);
        this.adminMainButton = findViewById(R.id.admin_main_button);

        this.mapView = findViewById(R.id.admin_main_map);

        this.adminMainDestinySelected = false;

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    /**
     * Show the progress bar component visible and put invisble the rest of the view
     */
    private void setProgressBar () {
        this.adminMainProgress.setVisibility(View.VISIBLE);
        this.adminMainLinear.setVisibility(View.GONE);
    }

    /**
     * Show the view visible and put invisble progress bar component
     */
    private void removeProgressBar () {
        this.adminMainProgress.setVisibility(View.GONE);
        this.adminMainLinear.setVisibility(View.VISIBLE);
    }

    /**
     * Inicializate the components to put the menu in the view
     */
    private void setMenuDrawer() {

        Toolbar toolbar = findViewById(R.id.admin_main_toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, this.admiMainDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        this.admiMainDrawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    /**
     * Put the personal data about the current user
     */
    private void setCredencials() {

        View hView =  this.navigationView.getHeaderView(0);

        TextView nav_name_text = hView.findViewById(R.id.menu_nav_header_name);
        TextView nav_email_text = hView.findViewById(R.id.menu_nav_header_email);

        String complete_name = this.user.getName() + " " + user.getSurname();
        nav_name_text.setText(complete_name);
        nav_email_text.setText(this.user.getEmail());
    }

    /**
     * Build a JSON for to allow make a create a new origin
     *
     * @param nameOrigin Name to create a new origin
     *
     * @return JSON with information to create origin
     */
    private JSONObject buildJson(String nameOrigin, Address adress) {

        JSONObject dataUser = new JSONObject();
        JSONObject dataOrigin = new JSONObject();
        JSONObject createOrigin = new JSONObject();

        try {

            dataUser.put("email", this.user.getEmail());
            dataUser.put("password", this.user.getPassword());
            dataOrigin.put("name", nameOrigin);
            dataOrigin.put("coordAlt", adress.getCoordinates().get(0));
            dataOrigin.put("coordLong", adress.getCoordinates().get(1));
            createOrigin.put("user", dataUser);
            createOrigin.put("origin", dataOrigin);

        } catch (JSONException e) { throwToast(R.string.err); }

        return createOrigin;
    }

    /**
     * Throw the event that allow create a new origin
     *
     * @param createOrigin JSON with information to create a origin
     */
    private void throwEventAddOrigin(JSONObject createOrigin) {

        EventDispatcher.getInstance(getApplicationContext())
        .dispatchEvent(Event.CREATEORIGIN, createOrigin)
        .addOnCompleteListener(task -> {

            if (!task.isSuccessful() || task.getResult() == null) {

                removeProgressBar();
                throwToast(R.string.errConexion);

            } else if (task.getResult().containsKey("error")){

                removeProgressBar();

                switch (Objects.requireNonNull(task.getResult().get("error"))) {

                    case "badRequestForm": throwToast(R.string.errBadFormat); break;
                    case "originAlreadyExists": throwToast(R.string.errOriginAlreadyExists); break;
                    case "server": throwToast(R.string.errServer); break;
                }
            }
            else {

                Intent logIntent = new Intent(AdminMain.this, OriginMain.class);
                logIntent.putExtra("origin", task.getResult().get("id"));
                throwToast(R.string.createOriginSuccesful);
                startActivity(logIntent);
            }
        });
    }

    private void throwToast(int msg) { Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show(); }

    /*********************************************************************************************************************
    Bar to put the destiny into de map **/

    @Override
    public void afterTextChanged(Editable s) {

        if (this.adminMainDestinySelected) this.adminMainDestinySelected = false;
        else {

            if (s.toString().matches(".*\\s")) Map.getInstance(getApplicationContext()).getFullAddress(s.toString()).addOnCompleteListener(task-> {

                this.adminMainSearchResult = task.getResult();
                ArrayList<String> fullAddresses = new ArrayList<>();

                for (Address address : this.adminMainSearchResult) fullAddresses.add(address.getAddress());

                ArrayAdapter<String> adapter = new ArrayAdapter<>(AdminMain.this, android.R.layout.simple_list_item_1, fullAddresses);
                this.adminMainOriginAutocomplete.setThreshold(1);
                this.adminMainOriginAutocomplete.setAdapter(adapter);
                this.adminMainOriginAutocomplete.showDropDown();
            });
        }
    }
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        this.adminMainDestinySelected = true;

        int i = 0;
        String text = this.adminMainOriginAutocomplete.getText().toString();

        while(i < this.adminMainSearchResult.size() && !adminMainSearchResult.get(i).getAddress().equals(text)) i++;

        if(i >= this.adminMainSearchResult.size()) throwToast(R.string.errDestinyNotExisit);
        else {

            this.adminMainOriginAutocomplete.setSelection(0);

            this.adminMainOrigin = this.adminMainSearchResult.get(i);
            moveMap(this.adminMainSearchResult.get(i).getCoordinates());
        }
    }

    /*********************************************************************************************************************
     MAPBOX **/

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {

        this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(Style.OUTDOORS);

        this.mapboxMap.getUiSettings().setCompassEnabled(false);
        this.mapboxMap.getUiSettings().setLogoEnabled(false);

        CameraPosition cp = new CameraPosition.Builder()
                .target(new LatLng(40.0000000, -4.0000000))
                .zoom(4)
                .tilt(20)
                .build();

        this.mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp),3000);
    }

    @SuppressLint("MissingPermission")
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {

        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            LocationComponent locationComponent = this.mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(this, loadedMapStyle);
            locationComponent.setLocationComponentEnabled(true);
            locationComponent.setCameraMode(CameraMode.TRACKING);
        }
        else {
            this.permissionsManager = new PermissionsManager(this);
            this.permissionsManager.requestLocationPermissions(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        this.permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) enableLocationComponent(Objects.requireNonNull(this.mapboxMap.getStyle()));
        else { Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show(); finish(); }
    }

    private void moveMap(List<Double> coordinates) {

        CameraPosition cp = new CameraPosition.Builder()
                .target(new LatLng(coordinates.get(1), coordinates.get(0)))
                .zoom(17)
                .tilt(20)
                .build();

        this.mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp),1000);

        ArrayList<Feature> point = new ArrayList<>();

        point.add(Feature.fromGeometry(Point.fromLngLat(coordinates.get(0), coordinates.get(1))));

        if(Objects.requireNonNull(this.mapboxMap.getStyle()).getImage("start")==null){

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.start);
            Objects.requireNonNull(mapboxMap.getStyle()).addImage("start", bitmap);
        }

        if(this.mapboxMap.getStyle().getLayer("start-layer")!=null) mapboxMap.getStyle().removeLayer("start-layer");

        if(this.mapboxMap.getStyle().getSource("start-source") != null) mapboxMap.getStyle().removeSource("start-source");

        this.mapboxMap.getStyle().addSource(new GeoJsonSource("start-source",FeatureCollection.fromFeatures(point)));

        this.mapboxMap.getStyle().addLayer(new SymbolLayer("start-layer", "start-source").withProperties(PropertyFactory.iconImage("start")));
    }

    /*********************************************************************************************************************
    Activity´s action */


    @Override
    public void onClick(View v) {

        if (this.adminMainOriginText.getText().toString().isEmpty() && (this.adminMainOrigin == null || this.adminMainOriginAutocomplete.getText().toString().isEmpty())) throwToast(R.string.errDataOriginEmpty);
        else if (this.adminMainOriginText.getText().toString().isEmpty()) throwToast(R.string.errNameOriginEmpty);
        else if (this.adminMainOrigin == null || this.adminMainOriginAutocomplete.getText().toString().isEmpty()) throwToast(R.string.errAddressOriginEmpty);
        else {

            setProgressBar();
            throwEventAddOrigin(buildJson(String.valueOf(this.adminMainOriginText.getText()), this.adminMainOrigin));
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {

            case R.id.admin_drawer_list: startActivity(new Intent(AdminMain.this, OriginList.class));break;
        }

        this.admiMainDrawer.closeDrawer(GravityCompat.START);

        if (menuItem.getItemId() != R.id.admin_drawer_settings &&
            menuItem.getItemId() != R.id.admin_drawer_signout &&
            menuItem.getItemId() != R.id.admin_drawer_home) finish();

        return true;
    }

    @Override
    public void onBackPressed() {

        if (this.admiMainDrawer.isDrawerOpen(GravityCompat.START)) this.admiMainDrawer.closeDrawer(GravityCompat.START);
        else {

            startActivity(new Intent(AdminMain.this, LoginMain.class));
            finish();
        }
    }
}
