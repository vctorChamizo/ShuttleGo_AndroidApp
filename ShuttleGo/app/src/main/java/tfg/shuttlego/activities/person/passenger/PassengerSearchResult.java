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
import android.widget.TextView;

import java.util.ArrayList;

import tfg.shuttlego.R;
import tfg.shuttlego.model.transfer.address.Address;
import tfg.shuttlego.model.transfer.route.Route;

public class PassengerSearchResult extends AppCompatActivity {

    private ListView routeResults;
    private ArrayList<Route> routes;
    private Address userAddress;
    private String originName;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.passenger_search_result);
        inicializateView();
    }

    private void inicializateView(){
        routeResults = findViewById(R.id.routeResults);
        this.title = findViewById(R.id.routeTitle);

        routes =  (ArrayList<Route>)getIntent().getSerializableExtra("routes");
        userAddress = (Address) getIntent().getSerializableExtra("userAddress");
        originName = getIntent().getStringExtra("originName");

        ArrayList<String> listStrings = new ArrayList<String>();

        for(int i = 0; i<routes.size();i++)
            listStrings.add("Ruta "+i+1+" plazas libres:");

        ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listStrings);
        routeResults.setAdapter(adapter);

        this.title.setText(originName+"\n"+userAddress.getAddress());

    }
}
