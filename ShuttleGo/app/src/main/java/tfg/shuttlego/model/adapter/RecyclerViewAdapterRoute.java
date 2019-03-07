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
import tfg.shuttlego.model.transfer.address.Address;
import tfg.shuttlego.model.transfer.person.Person;
import tfg.shuttlego.model.transfer.route.Route;

public class RecyclerViewAdapterRoute extends RecyclerView.Adapter<RecyclerViewAdapterRoute.RouteViewHolder> {

    private ArrayList<Route> routeList;
    private Person user;
    private AppCompatActivity activity;
    private Address userAddress;
    private int index = -1;

    public RecyclerViewAdapterRoute(ArrayList<Route> routeList, Person user, AppCompatActivity activity,Address userAddress) {

        this.routeList = routeList;
        this.user = user;
        this.activity = activity;
        this.userAddress = userAddress;
    }

    public static class RouteViewHolder extends RecyclerView.ViewHolder {

        Context context;
        CardView routeCard;
        TextView freePlacesText, hourText;
        String routeId;
        AppCompatActivity activity;
        private Address userAddress;

        RouteViewHolder(View v,String id,AppCompatActivity activity,Address userAddress) {

            super(v);
            context = v.getContext();
            routeCard = v.findViewById(R.id.route_list_passenger_cardview_cardview);
            freePlacesText = v.findViewById(R.id.route_list_passenger_cardview_freeplaces);
            hourText = v.findViewById(R.id.route_list_passenger_cardview_hour);
            this.routeId = id;
            this.activity = activity;
            this.userAddress = userAddress;
        }

        void setOnClickListeners() {

            AppCompatActivity activityTmp = this.activity;
            routeCard.setOnClickListener(view -> {
               // throwEventGetRoute(view.getContext().getApplicationContext(),buildJson(routeId));
                Intent getRoute = new Intent(activityTmp,RouteMainPassenger.class);
                getRoute.putExtra("route", this.routeId);
                getRoute.putExtra("userAddress", this.userAddress);
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
        index++;
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.route_list_passenger_cardview, viewGroup, false);
        return new RouteViewHolder(v,this.routeList.get(index).getId(), this.activity,this.userAddress);
    }

    @Override
    public void onBindViewHolder(@NonNull RouteViewHolder routeHolder, int i) {

        routeHolder.freePlacesText.setText(""+(this.routeList.get(i).getMax()-this.routeList.get(i).getPassengerNumber()));
        routeHolder.hourText.setText("00:00");

        routeHolder.setOnClickListeners();
    }

    @Override
    public int getItemCount() { return routeList.size(); }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) { super.onAttachedToRecyclerView(recyclerView); }
}