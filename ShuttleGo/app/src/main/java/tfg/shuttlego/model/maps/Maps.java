package tfg.shuttlego.model.maps;

import android.content.Context;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.mapbox.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.mapboxsdk.Mapbox;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tfg.shuttlego.model.transfers.adress.Address;

public class Maps {

    private static Maps ourInstance = null;
    private static final String accessToken = "pk.eyJ1Ijoic2h1dHRsZWdvdGZnIiwiYSI6ImNqczNnYTBkejBnN2k0M254bnV4MG5hNXYifQ.NoH-PJACPLpQWZ_2A1vvMg";
    public static Maps getInstance(Context applicationContext) {

        if(ourInstance == null) {
            Mapbox.getInstance(applicationContext, accessToken);
            ourInstance = new Maps();
        }
        return ourInstance;
    }

    private Maps() {}

    /**
     * Return the full address with the postcode that have been found
     * @param address the address to search
     * @return a task with the found address list or null if there is an error
     */
    public Task<List<Address>> getFullAddress(String address){

        TaskCompletionSource<List<Address>> taskCompletionSource = new TaskCompletionSource<List<Address>>();

        MapboxGeocoding query = MapboxGeocoding.builder()
                .accessToken(Maps.accessToken)
                .query(address)
                .geocodingTypes(GeocodingCriteria.TYPE_ADDRESS,GeocodingCriteria.TYPE_POSTCODE) //esto se puede cambiar
                .build();

        query.enqueueCall(new Callback<GeocodingResponse>() {
            @Override
            public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {

                List<CarmenFeature> results = response.body().features();
                List<Address> addresses = new ArrayList<Address>();
                String postalCode;

                for(CarmenFeature result:results) {
                    postalCode="";
                    for (int i = 0; postalCode.equals("") && i < result.context().size(); i++)
                        if (result.context().get(i).id().matches("postcode.*"))
                            addresses.add(new Address(result.placeName(),result.context().get(i).text()));
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
