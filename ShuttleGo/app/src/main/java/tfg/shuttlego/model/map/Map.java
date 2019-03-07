package tfg.shuttlego.model.map;

import android.content.Context;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
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
import com.mapbox.geojson.Point;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tfg.shuttlego.R;
import tfg.shuttlego.model.transfer.address.Address;

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

    public Task<String> calculateRoute(Point origin, ArrayList<Point>waypoints, MapView mapView, MapboxMap mapboxMap){

        TaskCompletionSource<String> taskCompletionSource = new TaskCompletionSource<String>();

        NavigationRoute.Builder builder = NavigationRoute.builder(this.context)
                .accessToken(this.accessToken)
                .origin(origin);

        for(Point waypoint:waypoints) builder = builder.addWaypoint(waypoint);

        builder.build().getRoute(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                if (response.body() == null) {
                    taskCompletionSource.setResult(Map.context.getString(R.string.errConexion));
                } else if (response.body().routes().size() < 1) {
                    taskCompletionSource.setResult(Map.context.getString(R.string.errRuteNotFound));
                }else {
                    DirectionsRoute route = response.body().routes().get(0);
                    NavigationMapRoute nmr = new NavigationMapRoute(null, mapView, mapboxMap);
                    nmr.addRoute(route);
                    taskCompletionSource.setResult(null);
                }
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                taskCompletionSource.setResult(Map.context.getString(R.string.errConexion));
            }
        });

        return taskCompletionSource.getTask();
    }
    /**
     * Return the full address with the postcode that have been found
     * @param address the address to search
     * @return a task with the found address list or null if there is an error
     */
    public Task<List<Address>> getFullAddress(String address){

        TaskCompletionSource<List<Address>> taskCompletionSource = new TaskCompletionSource<List<Address>>();

        MapboxGeocoding query = MapboxGeocoding.builder()
                .accessToken(Map.accessToken)
                .query(address)
                .languages("es")
                .build();

        query.enqueueCall(new Callback<GeocodingResponse>() {
            @Override
            public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {

                List<CarmenFeature> results = response.body().features();
                List<Address> addresses = new ArrayList<Address>();
                String postalCode;

                for(CarmenFeature result:results) {
                    postalCode="";
                    for (int i = 0; postalCode.equals("") && result.context()!=null && i < result.context().size(); i++)
                        if (result.context().get(i).id().matches("postcode.*"))
                            addresses.add(new Address(result.placeName(),result.context().get(i).text(),result.center()));
                }

                taskCompletionSource.setResult(addresses);
            }


            @Override
            public void onFailure(Call<GeocodingResponse> call, Throwable t) {
                taskCompletionSource.setResult(null);
            }
        });

        return taskCompletionSource.getTask();
    };
}
