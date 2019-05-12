package tfg.shuttlego.activities.person.passenger;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import tfg.shuttlego.R;
import tfg.shuttlego.activities.account.LoginMain;
import tfg.shuttlego.activities.route.RouteChoosePassenger;
import tfg.shuttlego.activities.route.routeList.RouteListPassenger;
import tfg.shuttlego.model.event.Event;
import tfg.shuttlego.model.event.EventDispatcher;
import tfg.shuttlego.model.map.Map;
import tfg.shuttlego.model.session.Session;
import tfg.shuttlego.model.transfer.address.Address;
import tfg.shuttlego.model.transfer.origin.Origin;
import tfg.shuttlego.model.transfer.person.Person;
import tfg.shuttlego.model.transfer.route.Route;
import static tfg.shuttlego.model.event.Event.SEARCHROUTE;

public class PassengerMain extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, PermissionsListener, MapboxMap.OnMapClickListener, View.OnClickListener, TextWatcher, AdapterView.OnItemClickListener, View.OnFocusChangeListener {

    private NavigationView passengerMainNavigtion;
    private DrawerLayout passengerMainDrawer;

    private ProgressBar passengerMainProgress;
    private LinearLayout passengerMainLinear;

    private AutoCompleteTextView passengerMainOrigin;
    private AutoCompleteTextView passengerMainDestiny;
    private Button passengerMainButton;

    /** Mapbox **/
    private MapboxMap mapboxMap;
    private MapView mapView;
    private PermissionsManager permissionsManager;

    private ArrayList<Origin> originList;
    private HashMap<String,String> originIds;
    private ArrayList<HashMap<?, ?>> originMap;
    private List<Address> destinySearchResult;

    private boolean destinySelected;
    private Address destination;
    private String originName;
    private Person user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Mapbox.getInstance(this, getString(R.string.access_token));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.passenger_main);

        this.user = Session.getInstance().getUser();

        inicializateView();
        setProgressBar();

        this.mapView.onCreate(savedInstanceState);

        setMenuDrawer();
        setCredencials();

        throwEventGerAllOrigins();

        this.mapView.getMapAsync(this);
        this.passengerMainNavigtion.setNavigationItemSelectedListener(this);
        this.passengerMainButton.setOnClickListener(this);
        this.passengerMainDestiny.addTextChangedListener(this);
        this.passengerMainDestiny.setOnItemClickListener(this);
        this.passengerMainOrigin.setOnFocusChangeListener(this);
        this.passengerMainOrigin.setOnItemClickListener(this);
    }

    @Override
    protected void onRestart(){
        this.mapView.onStart();
        removeProgressBar();
        super.onRestart();
    }

    @Override
    protected void onStart() {
        this.mapView.onStart();
        super.onStart();

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
    private void inicializateView() {

        this.passengerMainNavigtion = findViewById(R.id.passenger_main_nav);
        this.passengerMainDrawer = findViewById(R.id.passenger_main_drawer);

        this.passengerMainProgress = findViewById(R.id.passenger_main_progress);
        this.passengerMainLinear = findViewById(R.id.passenger_main_linear);

        this.mapView = findViewById(R.id.passenger_main_map);

        this.passengerMainDestiny = findViewById(R.id.passenger_main_autocomplete_destiny);
        this.passengerMainOrigin = findViewById(R.id.passenger_main_autocomplete_origin);
        this.passengerMainButton  = findViewById(R.id.passenger_main_button);
        this.destinySelected = false;
    }

    /**
     * Show the progress bar component visible and put invisble the rest of the view
     */
    private void setProgressBar () {

        this.passengerMainProgress.setVisibility(View.VISIBLE);
        this.passengerMainLinear.setVisibility(View.GONE);
    }

    /**
     * Show the view visible and put invisble progress bar component
     */
    private void removeProgressBar () {

        this.passengerMainProgress.setVisibility(View.GONE);
        this.passengerMainLinear.setVisibility(View.VISIBLE);
    }

    /**
     * Inicializate the components to put the menu in the view
     */
    private void setMenuDrawer() {

        Toolbar toolbar = findViewById(R.id.passenger_main_toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, this.passengerMainDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        this.passengerMainDrawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    /**
     * Put the personal data about the current user
     */
    private void setCredencials() {

        View hView =  this.passengerMainNavigtion.getHeaderView(0);

        TextView nav_name_text = hView.findViewById(R.id.menu_nav_header_name);
        TextView nav_email_text = hView.findViewById(R.id.menu_nav_header_email);

        String complete_name = this.user.getName() + " " + this.user.getSurname();
        nav_name_text.setText(complete_name);
        nav_email_text.setText(this.user.getEmail());
    }

    /**
     * Throw the event that allow to get a list of origin
     *
     */
    private void throwEventGerAllOrigins(){

        EventDispatcher.getInstance(getApplicationContext())
        .dispatchEvent(Event.GETORIGINS, null)
        .addOnCompleteListener(task-> {

            if (!task.isSuccessful() || task.getResult() == null) throwToast(R.string.errConexion);
            else if (task.getResult().containsKey("error")) throwToast(R.string.errServer);
            else {

                HashMap<?, ?> result = task.getResult();
                this.originMap = (ArrayList<HashMap<?, ?>>) result.get("origins");
                this.originList = new ArrayList<>();
                this.originIds = new HashMap<>();

                for (HashMap<?, ?> l : this.originMap){

                    this.originList.add(new Origin((String)l.get("id"),(String) l.get("name"),(String)Objects.requireNonNull(l.get("coordinates"))));
                    this.originIds.put((String)l.get("name"),(String)l.get("id"));
                }

                setAutoCompleteTextView();
                removeProgressBar();
            }
        });
    }

    /**
     * Build a JSON to get the routes
     *
     * @param origin The name of origin
     * @param destiny The name of destiny
     *
     * @return JSON with information about the current route
     */
    private JSONObject buildJson(String origin, String destiny) {

        JSONObject route = new JSONObject();
        JSONObject  chooseRoute = new JSONObject();

        try {

            route.put("origin",origin);
            route.put("destination",destiny);
            chooseRoute.put("route",route);

        }
        catch (JSONException e) { throwToast(R.string.err); }

        return chooseRoute;
    }

    /**
     *
     *
     */

    /**
     * Throw the event that allow to looj for a route match with address
     *
     * @param jsonObject The data necessary to throw the event
     */
    private void throwEventSearchRoute(JSONObject jsonObject) {

        EventDispatcher.getInstance(getApplicationContext())
        .dispatchEvent(SEARCHROUTE,jsonObject)
        .addOnCompleteListener(task ->  {

            if (!task.isSuccessful() || task.getResult() == null) throwToast(R.string.errConexion);
            else if (task.getResult().containsKey("error")) throwToast(R.string.errServer);
            else {

                HashMap<?,?> result= task.getResult();
                ArrayList<HashMap<?,?>> list = (ArrayList<HashMap<?,?>>) result.get("routes");
                assert list != null;
                ArrayList<Route> RouteList = routesParser(list);

                Intent logIntent = new Intent(PassengerMain.this, RouteChoosePassenger.class);
                logIntent.putExtra("userAddress",destination);
                logIntent.putExtra("routes", RouteList);
                logIntent.putExtra("originName",originName);
                startActivity(logIntent);
            }
        });
    }

    /**
     * Write the options in the auto edit text.
     */
    private void setAutoCompleteTextView() {

        ArrayList<String> originListNames = new ArrayList<>();

        for(Origin origin : this.originList) originListNames.add(origin.getName());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, originListNames);

        this.passengerMainOrigin.setThreshold(1);
        this.passengerMainOrigin.setAdapter(adapter);
    }

    /**
     * Check the address of routes.
     *
     * @param routes A lists of routes
     *
     * @return The parsed route of list
     */
    private ArrayList<Route> routesParser(ArrayList<HashMap<?,?>>routes){

        ArrayList<Route> r = new ArrayList<>();

        for(HashMap<?,?> route:routes) r.add(new Route((String)route.get("id"),(String)route.get("origin"), (Integer) route.get("destination"),(String)route.get("driver"),(Integer)route.get("max"),(Integer)route.get("passengersNumber"),(String)route.get("hour")));

        return r;
    }

    private void throwToast(int msg) { Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show(); }

    /*******************************************************************************************************************************
    AUTOCOMPLETE TEXT VIEW TO FIND THE DETINY **/

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}
    @Override
    public void afterTextChanged(Editable s) {

        if(this.destinySelected) this.destinySelected = false;
        else if(getCurrentFocus() == this.passengerMainDestiny){

            String value = s.toString();
            PassengerMain aux = this;

            if (value.matches(".*\\s")) {

                Map.getInstance(getApplicationContext()).getFullAddress(value).addOnCompleteListener(task ->  {

                    this.destinySearchResult = task.getResult();
                    ArrayList<String> fullAddresses = new ArrayList<>();

                    for (Address address : this.destinySearchResult) fullAddresses.add(address.getAddress());

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(aux, android.R.layout.simple_list_item_1, fullAddresses);
                    this.passengerMainDestiny.setThreshold(1);
                    this.passengerMainDestiny.setAdapter(adapter);
                    this.passengerMainDestiny.showDropDown();
                });
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        this.destinySelected=true;

        if(Objects.requireNonNull(getCurrentFocus()).getId() == this.passengerMainDestiny.getId()) {

            int i = 0;
            String text = this.passengerMainDestiny.getText().toString();

            while (i < this.destinySearchResult.size() && !destinySearchResult.get(i).getAddress().equals(text)) i++;

            if (i >= this.destinySearchResult.size()) throwToast(R.string.errDestinyNotExisit);
            else {

                this.destination = this.destinySearchResult.get(i);
                moveMap(this.destinySearchResult.get(i).getCoordinates(),"finish");
            }
        }
        else{

            int i = 0;
            String text = this.passengerMainOrigin.getText().toString();

            while (i < this.originList.size() && !this.originList.get(i).getName().equals(text)) i++;

            if (i >= this.originList.size()) throwToast(R.string.errOriginNotExist);
            else moveMap(this.originList.get(i).getCoordinates(),"start");
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) { if(getCurrentFocus() == this.passengerMainOrigin) this.passengerMainOrigin.showDropDown(); }

    /********************************************************************************************************************************
    MAPBOX **/

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {

        this.mapboxMap = mapboxMap;

        this.mapboxMap.getUiSettings().setCompassEnabled(false);
        this.mapboxMap.getUiSettings().setLogoEnabled(false);

        mapboxMap.setStyle(Style.OUTDOORS, style ->  {

            enableLocationComponent(style);
            mapboxMap.addOnMapClickListener(PassengerMain.this);
        });
    }

    @SuppressLint("MissingPermission")
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {

        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            LocationComponent locationComponent = this.mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(this, loadedMapStyle);
            locationComponent.setLocationComponentEnabled(true);
            locationComponent.setCameraMode(CameraMode.NONE);
        }
        else {

            this.permissionsManager = new PermissionsManager(this);
            this.permissionsManager.requestLocationPermissions(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) { this.permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults); }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) { Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show(); }

    @Override
    public void onPermissionResult(boolean granted) {

        if (granted) enableLocationComponent(Objects.requireNonNull(mapboxMap.getStyle()));
        else {

            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public boolean onMapClick(@NonNull LatLng point) { return false; }

    private void moveMap(List<Double> coordinates,String type) {

        CameraPosition cp = new CameraPosition.Builder()
                .target(new LatLng(coordinates.get(1), coordinates.get(0)))
                .zoom(17)
                .tilt(20)
                .build();

        this.mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp),1000);

        ArrayList<Feature> point = new ArrayList<>();

        point.add(Feature.fromGeometry(Point.fromLngLat(coordinates.get(0), coordinates.get(1))));

        if(Objects.requireNonNull(this.mapboxMap.getStyle()).getImage(type) == null) {

            Bitmap bitmap;

            switch(type){

                case "finish": bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.finish); break;

                default: bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.start); break;
            }

            this.mapboxMap.getStyle().addImage(type, bitmap);
        }

        if(this.mapboxMap.getStyle().getLayer(type+"-layer")!=null) this.mapboxMap.getStyle().removeLayer(type+"-layer");

        if(this.mapboxMap.getStyle().getSource(type+"-source") != null) this.mapboxMap.getStyle().removeSource(type+"-source");

        this.mapboxMap.getStyle().addSource(new GeoJsonSource(type+"-source", FeatureCollection.fromFeatures(point)));

        this.mapboxMap.getStyle().addLayer(new SymbolLayer(type+"-layer", type+"-source").withProperties(PropertyFactory.iconImage(type)));
    }


    /********************************************************************************************************************************/

    @Override
    public void onClick(View v) {

        if (this.passengerMainDestiny.getText().toString().isEmpty() && this.passengerMainOrigin.getText().toString().isEmpty()) throwToast(R.string.errDataRoutePassengerEmpty);
        else if (this.passengerMainOrigin.getText().toString().isEmpty()) throwToast(R.string.errOriginPassengerEmpty);
        else if (this.passengerMainDestiny.getText().toString().isEmpty()) throwToast(R.string.errDestinyPassengerEmpty);
        else {

            setProgressBar();

            this.originName = this.passengerMainOrigin.getText().toString();

            throwEventSearchRoute(buildJson(this.originIds.get(this.passengerMainOrigin.getText().toString()), this.destination.getPostalCode()));
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {

            case R.id.passenger_drawer_list: startActivity(new Intent(PassengerMain.this, RouteListPassenger.class));break;
        }

        this.passengerMainDrawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onBackPressed() {

        if (this.passengerMainDrawer.isDrawerOpen(GravityCompat.START)) this.passengerMainDrawer.closeDrawer(GravityCompat.START);
        else {

            startActivity(new Intent(PassengerMain.this, LoginMain.class));
            finish();
        }
    }
}