package tfg.shuttlego.activities.person.passenger;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import tfg.shuttlego.R;
import tfg.shuttlego.model.transfer.address.Address;
import tfg.shuttlego.model.transfer.route.Route;

public class PassengerSearchResult extends AppCompatActivity {

    private ListView routeResults;
    private ArrayList<Route> routes;
    private Address userAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.passenger_search_result);
        inicializateView();
    }

    private void inicializateView(){
        routeResults = findViewById(R.id.routeResults);

        routes =  (ArrayList<Route>)getIntent().getSerializableExtra("routes");
        userAddress = (Address) getIntent().getSerializableExtra("address");

        ArrayList<String> listStrings = new ArrayList<String>();

        for(Route r:routes) listStrings.add(r.getOrigin()+"-"+userAddress.getAddress());

        ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listStrings);
        routeResults.setAdapter(adapter);
    }
}
