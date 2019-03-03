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

        //Array de rutas
        routes =  (ArrayList<Route>)getIntent().getSerializableExtra("routes");

        //ruta del usuario
        userAddress = (Address) getIntent().getSerializableExtra("userAddress");

        //nombre del origen de la ruta (en Route solo viene el id)
        originName = getIntent().getStringExtra("originName");

        //Array de strings para meter en la lista
        ArrayList<String> listStrings = new ArrayList<String>();

        for(int i = 0; i<routes.size();i++) //ejemplo "Ruta 1 - Plazas libres 10/20
            listStrings.add("Ruta "+(i+1)+" - Plazas libres: "+(routes.get(i).getMax()-routes.get(i).getPassengersNumber())+"/"+routes.get(i).getMax());

        ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listStrings);
        routeResults.setAdapter(adapter);

        this.title.setText("Origen: "+originName+"\n"+"Destino: "+userAddress.getAddress());

    }
}
