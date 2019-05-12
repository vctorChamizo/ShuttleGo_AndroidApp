package tfg.shuttlego.activities.route;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
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
import java.util.Objects;

import tfg.shuttlego.R;
import tfg.shuttlego.activities.route.routeList.RouteListDriver;
import tfg.shuttlego.activities.route.routeMain.RouteMainDriver;
import tfg.shuttlego.model.event.Event;
import tfg.shuttlego.model.event.EventDispatcher;
import tfg.shuttlego.model.session.Session;
import tfg.shuttlego.model.transfer.origin.Origin;

public class RouteCalculate extends AppCompatActivity implements OnMapReadyCallback, MapboxMap.OnMapClickListener, PermissionsListener, View.OnClickListener {

    private MapView mapView;
    private MapboxMap mapboxMap;
    private LocationComponent locationComponent;
    private String routeId;
    private LinearLayout routeCalculateLinear;
    private LinearLayout routeCalculateProgress;
    private Point originPoint;
    private ArrayList<Point> waypoints;
    private TextView textLoading;
    private Button start;
    private boolean started = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Mapbox.getInstance(this, getString(R.string.access_token));

        this.routeId = Objects.requireNonNull(getIntent().getExtras()).getString("routeId");

        setContentView(R.layout.route_calculate);
        super.onCreate(savedInstanceState);

        inicializateView();
        mapView.onCreate(savedInstanceState);
        textLoading.setText(R.string.loadingMap);

        this.mapView.getMapAsync(RouteCalculate.this);
        this.start.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        mapView.onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onResume() {
        mapView.onResume();
        super.onResume();
    }

    private void inicializateView() {

        this.routeCalculateLinear = findViewById(R.id.route_calculate_content_linear1);
        this.textLoading = findViewById(R.id.loading_text);
        this.mapView = findViewById(R.id.route_calculate_map);
        this.routeCalculateProgress = findViewById(R.id.route_calculate_progress);
        this.start = findViewById(R.id.route_calculate_start);
    }

    protected void setProgressBar() {

        routeCalculateProgress.setVisibility(View.VISIBLE);
        routeCalculateLinear.setVisibility(View.GONE);
    }

    /**
     * Show the view visible and put invisble progress bar component
     */
    protected void removeProgressBar() {
        routeCalculateProgress.setVisibility(View.GONE);
        routeCalculateLinear.setVisibility(View.VISIBLE);
    }

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
    public boolean onMapClick(@NonNull LatLng point) {
        return false;
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {

        setProgressBar();

        this.mapboxMap = mapboxMap;

        this.mapboxMap.getUiSettings().setLogoEnabled(false);

        mapboxMap.setStyle(Style.OUTDOORS, style -> {

            enableLocationComponent(style);
            this.mapboxMap.addOnMapClickListener(RouteCalculate.this);
            throwEventGetPoints();
        });
    }

    private void throwCalculateRoute() {

        textLoading.setText(R.string.calculatingRoute);

        tfg.shuttlego.model.map.Map.getInstance(getApplicationContext()).calculateRoute(originPoint, waypoints, mapView, mapboxMap).addOnCompleteListener(task -> {

            removeProgressBar();
            locationComponent.setCameraMode(CameraMode.NONE);
            moveMap(originPoint.coordinates());
        });
    }

    private void throwEventGetPoints() {

        this.textLoading.setText(R.string.gettingPoints);
        EventDispatcher.getInstance(getApplicationContext()).dispatchEvent(Event.GETROUTEPOINTS, buildJson()).addOnCompleteListener(task -> {

            if (!task.isSuccessful() || task.getResult() == null) throwToast(R.string.errConexion);
            else if (task.getResult().containsKey("error")) {

                if (Objects.requireNonNull(task.getResult().get("error")).equals("routeDoesntExists")) throwToast(R.string.errRuteNotFound);
                else throwToast(R.string.errServer);
            }
            else {

                HashMap<?, ?> result = task.getResult();
                HashMap<?, ?> points = (HashMap<?, ?>) result.get("points");
                HashMap<?, ?> originHas = (HashMap<?, ?>) Objects.requireNonNull(points).get("origin");

                Origin origin = new Origin();
                origin.setId((String) Objects.requireNonNull(originHas).get("id"));
                origin.setName((String) originHas.get("name"));
                origin.setCoordinates((String) originHas.get("coordinates"));

                ArrayList<HashMap<?, ?>> waypointsHas = (ArrayList<HashMap<?, ?>>) points.get("waypoints");

                if (Objects.requireNonNull(waypointsHas).size() == 0) {

                    throwToast(R.string.NoWaypoints);

                    Intent intent = new Intent(RouteCalculate.this, RouteMainDriver.class);
                    intent.putExtra("route", this.routeId);
                    startActivity(intent);
                    finish();
                }
                else {

                    waypoints = new ArrayList<>();

                    for (HashMap<?, ?> waypoint : waypointsHas) {

                        String[] coordinatesString = ((String) Objects.requireNonNull(waypoint.get("coordinates"))).split(",");
                        List<Double> coordinates = new ArrayList<>();
                        coordinates.add(Double.parseDouble(coordinatesString[0]));
                        coordinates.add(Double.parseDouble(coordinatesString[1]));
                        waypoints.add(createPoint(coordinates));
                    }

                    originPoint = createPoint(origin.getCoordinates());
                    throwCalculateRoute();
                }
            }
        });
    }

    private Point createPoint(List<Double> coordinates) {

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

        }
        catch (JSONException exception) { throwToast(R.string.err); }

        return result;
    }

    protected void throwToast(int msg) { Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show(); }

    @SuppressLint("MissingPermission")
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {

        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            locationComponent = mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(this, loadedMapStyle);
            locationComponent.setLocationComponentEnabled(true);
            locationComponent.setCameraMode(CameraMode.TRACKING_GPS_NORTH);

        }
        else {

            PermissionsManager permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    private void moveMap(List<Double> coordinates) {

        CameraPosition cp = new CameraPosition.Builder()
                .target(new LatLng(coordinates.get(1), coordinates.get(0)))
                .zoom(17)
                .tilt(20)
                .build();

        this.mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp), 1000);
    }

    @Override
    public void onClick(View v) {

        if (!this.started) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) { throwToast(R.string.cantAccessToLocation); }

            started = true;
            locationComponent.setCameraMode(CameraMode.TRACKING_COMPASS);

            Location a = new Location("");
            Location b = new Location("");

            a.setLatitude(Objects.requireNonNull(locationComponent.getLastKnownLocation()).getLatitude());
            a.setLongitude(locationComponent.getLastKnownLocation().getLongitude());

            b.setLatitude(originPoint.latitude());
            b.setLongitude(originPoint.longitude());

            double distance = a.distanceTo(b);
            double MAX_DISTANCE = 100;

            if (distance > MAX_DISTANCE) throwToast(R.string.tooFar);

            this.start.setText(R.string.cancel);

        }
        else {

            started = false;
            this.start.setText(R.string.start);
            locationComponent.setCameraMode(CameraMode.NONE);
            moveMap(originPoint.coordinates());
        }
    }
}
