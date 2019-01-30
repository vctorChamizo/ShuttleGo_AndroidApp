package tfg.shuttlego.model.maps;
import android.content.Context;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.mapbox.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.api.geocoding.v5.models.CarmenContext;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;

import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Maps {
    private static Maps ourInstance = null;
    private static final String accessToken = "pk.eyJ1IjoiY2FybG9zY2hhcmxpZSIsImEiOiJjanB1YXVjYW0wNXQ4NDhsZndtcWFiYnF3In0.7ogR1zvHYQfdn-ldKf10mA";
    public static Maps getInstance(Context applicationContext) {

        if(ourInstance == null) {
            Mapbox.getInstance(applicationContext, accessToken);
            ourInstance = new Maps();
        }
        return ourInstance;
    }

    private Maps() {}

    public Task<Direction> getPlace(String address){

        TaskCompletionSource<Direction> taskCompletionSource = new TaskCompletionSource<>();

        MapboxGeocoding query = MapboxGeocoding.builder()
            .accessToken(Maps.accessToken)
            .query(address)
            .geocodingTypes(GeocodingCriteria.TYPE_ADDRESS,GeocodingCriteria.TYPE_POSTCODE) //esto se puede cambiar
            .build();

        query.enqueueCall(new Callback<GeocodingResponse>() {
            @Override
            public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {

                List<CarmenFeature> result = response.body().features();
                String postalCode = "";

                for(int i = 0; postalCode.equals("") && i<result.get(0).context().size();i++)
                    if(result.get(0).context().get(i).id().matches("postcode.*"))
                        postalCode = result.get(0).context().get(i).text();

                taskCompletionSource.setResult(result.size()>0 ? new Direction(result.get(0).placeName(),postalCode) : null);
            }


            @Override
            public void onFailure(Call<GeocodingResponse> call, Throwable t) {
                System.out.print("fallo");
            }
        });

        return taskCompletionSource.getTask();
    };
}
