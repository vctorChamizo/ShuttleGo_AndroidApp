package tfg.shuttlego.model.maps;
import android.content.Context;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.mapbox.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;

import java.util.List;

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

    public Task<String> getPlace(String address){

        TaskCompletionSource<String> taskCompletionSource = new TaskCompletionSource<>();

        MapboxGeocoding query = MapboxGeocoding.builder()
            .accessToken(Maps.accessToken)
            .query(address)
            .geocodingTypes(GeocodingCriteria.TYPE_DISTRICT) //esto se puede cambiar
            .build();

        query.enqueueCall(new Callback<GeocodingResponse>() {
            @Override
            public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {

                List<CarmenFeature> result = response.body().features();
                taskCompletionSource.setResult("prueba");
            }


            @Override
            public void onFailure(Call<GeocodingResponse> call, Throwable t) {
                System.out.print("fallo");
            }
        });

        return taskCompletionSource.getTask();
    };
}
