package tfg.shuttlego.model.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Calendar;

import tfg.shuttlego.R;
import tfg.shuttlego.model.transfer.person.Person;
import tfg.shuttlego.model.transfer.route.Route;

public class RecyclerViewAdapterRoute extends RecyclerView.Adapter<RecyclerViewAdapterRoute.RouteViewHolder> {

    private ArrayList<Route> routeList;
    private Person user;

    public RecyclerViewAdapterRoute(ArrayList<Route> routeList, Person user) {

        this.routeList = routeList;
        this.user = user;
    }

    public static class RouteViewHolder extends RecyclerView.ViewHolder {

        Context context;
        CardView routeCard;
        TextView freePlacesText, hourText;

        RouteViewHolder(View v) {

            super(v);
            context = v.getContext();
            routeCard = v.findViewById(R.id.route_list_passenger_cardview_cardview);
            freePlacesText = v.findViewById(R.id.route_list_passenger_cardview_freeplaces);
            hourText = v.findViewById(R.id.route_list_passenger_cardview_hour);
        }

        void setOnClickListeners() {

            routeCard.setOnClickListener(view -> {

                // Este es el evento que escucha cuando se selecciona un carview.
                // Esto deberá redirigir a la actividad que muestra los datos completos de la ruta
                // y darle la opcion de al pasajero de seleccionarla o por el contrario cancelar y volver a mostrar la lista

                // Ese layout aún no está empezado.
            });
        }
    }

    @NonNull
    @Override
    public RouteViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.route_list_passenger_cardview, viewGroup, false);
        return new RouteViewHolder(v);
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

        routeHolder.freePlacesText.setText((this.routeList.get(i).getMax()-this.routeList.get(i).getPassengerNumber())+"/"+this.routeList.get(i).getMax());
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