package tfg.shuttlego.model.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import tfg.shuttlego.R;
import tfg.shuttlego.activities.route.RouteMain;
import tfg.shuttlego.activities.route.RouteMainPassenger;
import tfg.shuttlego.model.event.Event;
import tfg.shuttlego.model.event.EventDispatcher;
import tfg.shuttlego.model.transfer.person.Person;
import tfg.shuttlego.model.transfer.route.Route;

public class RecyclerViewAdapterRoute extends RecyclerView.Adapter<RecyclerViewAdapterRoute.RouteViewHolder> {

    private ArrayList<Route> routeList;
    private Person user;
    private AppCompatActivity activity;

    public RecyclerViewAdapterRoute(ArrayList<Route> routeList, Person user, AppCompatActivity activity) {

        this.routeList = routeList;
        this.user = user;
        this.activity = activity;
    }

    public static class RouteViewHolder extends RecyclerView.ViewHolder {

        Context context;
        CardView routeCard;
        TextView freePlacesText, hourText;
        String routeId;
        AppCompatActivity activity;

        RouteViewHolder(View v,String id,AppCompatActivity activity) {

            super(v);
            context = v.getContext();
            routeCard = v.findViewById(R.id.route_list_passenger_cardview_cardview);
            freePlacesText = v.findViewById(R.id.route_list_passenger_cardview_freeplaces);
            hourText = v.findViewById(R.id.route_list_passenger_cardview_hour);
            routeId = id;
            this.activity = activity;
        }

        void setOnClickListeners() {

            AppCompatActivity activityTmp = this.activity;
            routeCard.setOnClickListener(view -> {
               // throwEventGetRoute(view.getContext().getApplicationContext(),buildJson(routeId));
                Intent getRoute = new Intent(activityTmp,RouteMainPassenger.class);
                getRoute.putExtra("route", this.routeId);
                this.activity.startActivity(getRoute);
            });
        }

        private void throwEventGetRoute(Context applicationContext,org.json.JSONObject routeId){
            EventDispatcher.getInstance(applicationContext).dispatchEvent(Event.GETORIGINBYID,routeId).addOnCompleteListener(new OnCompleteListener<HashMap<String, String>>() {
                @Override
                public void onComplete(@NonNull Task<HashMap<String, String>> task) {
                }
            });
        }
        private JSONObject buildJson(String route) {

            JSONObject json = new JSONObject();
            JSONObject routeJson = new JSONObject();

            try {

                routeJson.put("id", route);
                json.put("route", routeJson);

            } catch (JSONException e) {}

            return json;
        }//buildJson
    }

    @NonNull
    @Override
    public RouteViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.route_list_passenger_cardview, viewGroup, false);
        return new RouteViewHolder(v,this.routeList.get(i).getId(), this.activity);
    }

    @Override
    public void onBindViewHolder(@NonNull RouteViewHolder routeHolder, int i) {

        //Este metodo introudce los datos que quieres renderizar en cada uno de los cardview.
        //Los elementos están inicializados más arriba de eso no tienes que preocuparte.

        //Hay un problema, la ruta muestra el id del origen, debe mostrar el nombre.
            // Se me ocurren dos soluciones:
                // 1. Hacer una llamada al servidor gtOriginByID por cada uno de los id´s que te entren.
                // 2. Hacer una llamada a getAllOrigins en el constructor de esta clase y crear un objeto
                //    HashMap desde el cual realices busquedas con el id que te propociona la ruta.
            // En mi opinion es mejor la segunda ya que solo hacemos una llamada al servidor, pero quizá tenga
            // problemas esta alternativa, no lo sé, valoralo tu.

        routeHolder.freePlacesText.setText(""+(this.routeList.get(i).getMax()-this.routeList.get(i).getPassengerNumber()));
        routeHolder.hourText.setText("00:00");

        // Aqui se introducen los demas componentes de la cardview.
        // De primeras también veo dos problemas:
            // Hay objetos que vienen como int por tanto deberás convertirlos a string para que te deje renderizarlo.
            // El destino como tal, tiene demasiados datos (nombre, codigo postal, ciudad, numero, etc). Si lo metemos
            // dentro del cardview va a quedar muy feo y apelotnado. Yo creo que los mas conveniente es cortarlo
            // mediante un splice() o algo por el estilo, para que solo quede reflejado el nombre del detino.

        routeHolder.setOnClickListeners();
    }

    @Override
    public int getItemCount() { return routeList.size(); }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) { super.onAttachedToRecyclerView(recyclerView); }
}