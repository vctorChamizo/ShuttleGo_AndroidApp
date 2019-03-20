package tfg.shuttlego.activities.route;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.BoundingBox;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tfg.shuttlego.R;
import tfg.shuttlego.model.event.Event;
import tfg.shuttlego.model.event.EventDispatcher;
import tfg.shuttlego.model.session.Session;
import tfg.shuttlego.model.transfer.origin.Origin;
import tfg.shuttlego.model.transfer.person.Person;
import tfg.shuttlego.model.transfer.person.TypePerson;

public class RouteCalculate extends AppCompatActivity implements OnMapReadyCallback, MapboxMap.OnMapClickListener, PermissionsListener {

    MapView mapView;
    private MapboxMap mapboxMap;
    private LocationComponent locationComponent;
    private PermissionsManager permissionsManager;
    private String routeId;
    private LinearLayout routeCalculateLinear;
    private ProgressBar routeCalculateProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.route_calculate);
        inicializateView();
        mapView.onCreate(savedInstanceState);
        setProgressBar();
        listeners();
        super.onCreate(savedInstanceState);

    }

    private void listeners() {
        mapView.getMapAsync(this);
    }

    private void inicializateView(){
        mapView = findViewById(R.id.route_calculate_map);
        routeId = getIntent().getExtras().getString("routeId");
    }
    @Override
    protected  void onStart(){
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onPause(){
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onStop(){
        mapView.onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy(){
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onResume(){
        mapView.onResume();
        super.onResume();
    }

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
        return false;
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {

        this.mapboxMap = mapboxMap;

        this.mapboxMap.getUiSettings().setLogoEnabled(false);

        mapboxMap.setStyle(Style.LIGHT, new Style.OnStyleLoaded() {

            @Override
            public void onStyleLoaded(@NonNull Style style) {

                enableLocationComponent(style);
                mapboxMap.addOnMapClickListener(RouteCalculate.this);
            }
        });


        CameraPosition cp = new CameraPosition.Builder()
                .zoom(25)
                .tilt(20)
                .build();

        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp),1000);
        throwEventGetPoints();
    }

    protected void setProgressBar () {
    /*
        routeCalculateProgress.setVisibility(View.VISIBLE);
        routeCalculateLinear.setVisibility(View.GONE);
        */
    }

    /**
     * Show the view visible and put invisble progress bar component
     */
    protected void removeProgressBar () {
    /*
        routeCalculateProgress.setVisibility(View.GONE);
        routeCalculateLinear.setVisibility(View.VISIBLE);
    */
    }

    private void throwEventGetPoints() {
        EventDispatcher.getInstance(getApplicationContext()).dispatchEvent(Event.GETROUTEPOINTS,buildJson()).addOnCompleteListener(new OnCompleteListener<HashMap<String, String>>() {
            @Override
            public void onComplete(@NonNull Task<HashMap<String, String>> task) {
                removeProgressBar();

                if (!task.isSuccessful() || task.getResult() == null) {
                    throwToast(R.string.errConexion);
                }
                else if (task.getResult().containsKey("error")) {
                    if(task.getResult().get("error").equals("routeDoesntExists")) throwToast(R.string.errRuteNotFound);
                    else throwToast(R.string.errServer);
                }
                else {

                    HashMap<?, ?> result = task.getResult();
                    HashMap<?, ?> points = (HashMap<?, ?>) result.get("points");
                    HashMap<?, ?> originHas = (HashMap<?, ?>)points.get("origin");

                    Origin origin = new Origin();
                    origin.setId((String) originHas.get("id"));
                    origin.setName((String) originHas.get("name"));
                    origin.setCoordinates((String) originHas.get("coordinates"));

                    ArrayList<HashMap<?,?>>waypointsHas = (ArrayList<HashMap<?,?>>)points.get("waypoints");

                    if(waypointsHas.size()==0){
                        throwToast(R.string.NoWaypoints);
                        finish();
                    }
                    else {
                        ArrayList<Point> waypoints = new ArrayList<>();

                        for (HashMap<?, ?> waypoint : waypointsHas) {
                            String[] coordinatesString = ((String) waypoint.get("coordinates")).split(",");
                            List<Double> coordinates = new ArrayList<Double>();
                            coordinates.add(Double.parseDouble(coordinatesString[1]));
                            coordinates.add(Double.parseDouble(coordinatesString[0]));
                            waypoints.add(createPoint(coordinates));
                        }

                        Point originPoint = createPoint(origin.getCoordinates());

                        tfg.shuttlego.model.map.Map.getInstance(getApplicationContext()).calculateRoute(originPoint, waypoints, mapView, mapboxMap);
                    }

                }
            }
        });
    }

    private Point createPoint(List<Double>coordinates) {

        return new Point() {
            @NonNull
            @Override
            public String type() {
                return "Point";
            }

            @Nullable
            @Override
            public BoundingBox bbox() {
                return null;
            }

            @NonNull
            @Override
            public List<Double> coordinates() {
                return coordinates;
            }
        };
    }

    private JSONObject buildJson() {

        JSONObject result = new JSONObject();

        try {
            JSONObject route = new JSONObject();
            route.put("id", routeId);

            JSONObject user = new JSONObject();
            user.put("email", Session.getInstance().getUser().getEmail());
            user.put("password", Session.getInstance().getUser().getPassword());

            result.put("route", route);
            result.put("user", user);

        }catch (JSONException exception){
            throwToast(R.string.err);
        }

        return result;
    }

    protected void throwToast(int msg) { Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show(); }

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
    }



}
