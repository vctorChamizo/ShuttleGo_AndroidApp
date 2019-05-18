package tfg.shuttlego.activities.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import tfg.shuttlego.R;
import tfg.shuttlego.activities.route.routeMain.RouteMainPassengerChoose;
import tfg.shuttlego.model.transfer.address.Address;
import tfg.shuttlego.model.transfer.route.Route;

public class RecyclerViewAdapterChooseRoute extends RecyclerView.Adapter<RecyclerViewAdapterChooseRoute.RouteViewHolder> {

    private ArrayList<Route> routeList;
    private Address userAddress;

    public RecyclerViewAdapterChooseRoute(ArrayList<Route> routeList, Address userAddress) {

        this.routeList = routeList;
        this.userAddress = userAddress;
    }

    public static class RouteViewHolder extends RecyclerView.ViewHolder {

        private Address userAddress;

        Context context;
        CardView routeCard;
        TextView freePlacesText, hourText, idText;

        RouteViewHolder(View v, Address userAddress) {

            super(v);

            this.context = v.getContext();

            this.userAddress = userAddress;
            this.routeCard = v.findViewById(R.id.route_choose_passenger_cardview_cardview);
            this.freePlacesText = v.findViewById(R.id.route_choose_passenger_cardview_freeplaces);
            this.hourText = v.findViewById(R.id.route_choose_passenger_cardview_hour);
            this.idText = v.findViewById(R.id.route_choose_passenger_cardview_id);
        }

        void setOnClickListeners() {

            this.routeCard.setOnClickListener(view -> {

                Intent getRoute = new Intent(this.context, RouteMainPassengerChoose.class);

                getRoute.putExtra("route", this.idText.getText());
                getRoute.putExtra("userAddress", this.userAddress);

                this.context.startActivity(getRoute);
            });
        }
    }

    @NonNull
    @Override
    public RouteViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.route_choose_passenger_cardview, viewGroup, false);
        return new RouteViewHolder(v, this.userAddress);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RouteViewHolder routeHolder, int i) {

        routeHolder.freePlacesText.setText("" + (this.routeList.get(i).getMax() - this.routeList.get(i).getPassengerNumber()));
        routeHolder.hourText.setText(this.routeList.get(i).getHour());
        routeHolder.idText.setText(this.routeList.get(i).getId());

        routeHolder.setOnClickListeners();
    }

    @Override
    public int getItemCount() { return routeList.size(); }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) { super.onAttachedToRecyclerView(recyclerView); }
}