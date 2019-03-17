package tfg.shuttlego.model.adapter;

import android.annotation.SuppressLint;
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
import java.util.ArrayList;
import tfg.shuttlego.R;
import tfg.shuttlego.activities.route.RouteMainPassenger;
import tfg.shuttlego.model.transfer.address.Address;
import tfg.shuttlego.model.transfer.route.Route;

public class RecyclerViewAdapterChooseRoute extends RecyclerView.Adapter<RecyclerViewAdapterChooseRoute.RouteViewHolder> {

    private ArrayList<Route> routeList;
    private AppCompatActivity activity;
    private Address userAddress;
    private int index = -1;

    public RecyclerViewAdapterChooseRoute(ArrayList<Route> routeList, AppCompatActivity activity, Address userAddress) {

        this.routeList = routeList;
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
            routeCard = v.findViewById(R.id.route_choose_passenger_cardview_cardview);
            freePlacesText = v.findViewById(R.id.route_choose_passenger_cardview_freeplaces);
            hourText = v.findViewById(R.id.route_choose_passenger_cardview_hour);
            this.routeId = id;
            this.activity = activity;
            this.userAddress = userAddress;
        }

        void setOnClickListeners() {

            AppCompatActivity activityTmp = this.activity;
            routeCard.setOnClickListener(view -> {
                Intent getRoute = new Intent(activityTmp,RouteMainPassenger.class);
                getRoute.putExtra("route", this.routeId);
                getRoute.putExtra("userAddress", this.userAddress);
                this.activity.startActivity(getRoute);
            });
        }
    }

    @NonNull
    @Override
    public RouteViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        index++;
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.route_choose_passenger_cardview, viewGroup, false);
        return new RouteViewHolder(v,this.routeList.get(index).getId(), this.activity,this.userAddress);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RouteViewHolder routeHolder, int i) {

        routeHolder.freePlacesText.setText("" + (this.routeList.get(i).getMax() - this.routeList.get(i).getPassengerNumber()));
        routeHolder.hourText.setText("00:00");

        routeHolder.setOnClickListeners();
    }

    @Override
    public int getItemCount() { return routeList.size(); }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) { super.onAttachedToRecyclerView(recyclerView); }
}