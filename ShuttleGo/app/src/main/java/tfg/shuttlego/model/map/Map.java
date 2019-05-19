package tfg.shuttlego.model.map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import com.mapbox.geojson.Point;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tfg.shuttlego.R;
import tfg.shuttlego.model.transfer.address.Address;

@SuppressLint("StaticFieldLeak")
public class Map {

    private static Map ourInstance = null;
    private static String accessToken;
    private static Context context;
    public static Map getInstance(Context applicationContext) {

        if(ourInstance == null) {

            Mapbox.getInstance(applicationContext, applicationContext.getString(R.string.access_token));

            accessToken =applicationContext.getString(R.string.access_token);
            ourInstance = new Map();
            context = applicationContext;
        }

        return ourInstance;
    }

    private Map() {}

    public Task<Integer> calculateRoute(Point origin, ArrayList<Point>waypoints, MapView mapView, MapboxMap mapboxMap){

        TaskCompletionSource<Integer> taskCompletionSource = new TaskCompletionSource<>();

        NavigationRoute.Builder builder = NavigationRoute.builder(context).accessToken(accessToken).profile(DirectionsCriteria.PROFILE_DRIVING).origin(origin);

        for(Point waypoint:waypoints) builder = builder.addWaypoint(waypoint);

        builder.build().getRoute(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(@NonNull Call<DirectionsResponse> call, @NonNull Response<DirectionsResponse> response) {

                if (response.body() == null) taskCompletionSource.setResult(R.string.errConexion);
                else if (response.body().routes().size() < 1) taskCompletionSource.setResult(R.string.errRuteNotFound);
                else {

                    DirectionsRoute route = response.body().routes().get(0);
                    NavigationMapRoute nmr = new NavigationMapRoute(null, mapView, mapboxMap);
                    nmr.addRoute(route);
                    taskCompletionSource.setResult(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<DirectionsResponse> call, @NonNull Throwable t) { taskCompletionSource.setResult(R.string.errConexion); }
        });

        return taskCompletionSource.getTask();
    }
    /**
     * Return the full address with the postcode that have been found
     * @param address the address to search
     * @return a task with the found address list or null if there is an error
     */
    public Task<List<Address>> getFullAddress(String address){

        TaskCompletionSource<List<Address>> taskCompletionSource = new TaskCompletionSource<>();

        MapboxGeocoding query = MapboxGeocoding.builder()
                .accessToken(Map.accessToken)
                .query(address)
                .languages("es")
                .build();

        query.enqueueCall(new Callback<GeocodingResponse>() {

            @Override
            public void onResponse(@NonNull Call<GeocodingResponse> call, @NonNull Response<GeocodingResponse> response) {

                List<CarmenFeature> results = Objects.requireNonNull(response.body()).features();
                List<Address> addresses = new ArrayList<>();

                for(CarmenFeature result:results) {

                    for (int i = 0; result.context() != null && i < Objects.requireNonNull(result.context()).size(); i++)
                        if (Objects.requireNonNull(Objects.requireNonNull(result.context()).get(i).id()).matches("postcode.*"))
                            addresses.add(new Address(result.placeName(),Objects.requireNonNull(result.context()).get(i).text(),result.center()));
                }

                taskCompletionSource.setResult(addresses);
            }

            @Override
            public void onFailure(@NonNull Call<GeocodingResponse> call, @NonNull Throwable t) { taskCompletionSource.setResult(null); }
        });

        return taskCompletionSource.getTask();
    }
}
