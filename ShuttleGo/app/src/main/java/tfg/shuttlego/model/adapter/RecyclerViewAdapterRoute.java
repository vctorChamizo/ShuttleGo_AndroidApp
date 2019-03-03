package tfg.shuttlego.model.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import tfg.shuttlego.R;
import tfg.shuttlego.activities.origin.OriginMain;
import tfg.shuttlego.model.transfer.origin.Origin;
import tfg.shuttlego.model.transfer.person.Person;
import tfg.shuttlego.model.transfer.route.Route;

public class RecyclerViewAdapterRoute extends RecyclerView.Adapter<RecyclerViewAdapterRoute.RouteViewHolder> {

    private ArrayList<Route> routeList;
    private static Person user;

    public RecyclerViewAdapterRoute(ArrayList<Route> originList, Person user) {

        this.routeList = originList;
        this.user = user;
    }

    public static class RouteViewHolder extends RecyclerView.ViewHolder {

        Context context;
        CardView routeCard;
        TextView originText, destinyText;

        public RouteViewHolder(View v) {

            super(v);
            context = v.getContext();
            routeCard = v.findViewById(R.id.route_cardview_cardview);
            originText = v.findViewById(R.id.route_list_cardview_origin);
            destinyText = v.findViewById(R.id.route_list_cardview_destiny);

        }

        public void setOnClickListeners() {

            routeCard.setOnClickListener(view -> {

            });
        }
    }

    @Override
    public RouteViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.origin_list_cardview, viewGroup, false);
        RouteViewHolder vh = new RouteViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RouteViewHolder originHolder, int i) {

        originHolder.originText.setText(originHolder.originText.getText() + " " + routeList.get(i).getOrigin());
        originHolder.destinyText.setText(originHolder.destinyText.getText() + " " + routeList.get(i).getDestination());

        originHolder.setOnClickListeners();
    }

    @Override
    public int getItemCount() { return routeList.size(); }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}