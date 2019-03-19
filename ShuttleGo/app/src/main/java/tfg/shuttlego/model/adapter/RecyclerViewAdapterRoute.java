package tfg.shuttlego.model.adapter;

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
import tfg.shuttlego.activities.route.RouteMain;
import tfg.shuttlego.model.transfer.route.Route;

public class RecyclerViewAdapterRoute extends RecyclerView.Adapter<RecyclerViewAdapterRoute.RouteViewHolder> {

    private ArrayList<Route> routeList;

    public RecyclerViewAdapterRoute(ArrayList<Route> routeList) { this.routeList = routeList; }

    public static class RouteViewHolder extends RecyclerView.ViewHolder {

        Context context;
        CardView routeCard;
        TextView originText, destinyText, hourText, idText;

        RouteViewHolder(View v) {

            super(v);
            context = v.getContext();
            routeCard = v.findViewById(R.id.route_list_cardview_cardview);
            originText = v.findViewById(R.id.route_list_cardview_origin);
            destinyText = v.findViewById(R.id.route_list_cardview_destiny);
            hourText = v.findViewById(R.id.route_list_cardview_hour);
            idText = v.findViewById(R.id.route_list_cardview_id);
        }

        void setOnClickListeners() {

            routeCard.setOnClickListener(v -> {

                Intent intent = new Intent(context, RouteMain.class);
                intent.putExtra("route", idText.getText());
                context.startActivity(intent);
            });
        }
    }

    @NonNull
    @Override
    public RouteViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.route_list_cardview, viewGroup, false);
        return new RouteViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RouteViewHolder routeHolder, int i) {
        routeHolder.originText.setText(this.routeList.get(i).getOrigin());
        routeHolder.destinyText.setText(this.routeList.get(i).getDestination());
        routeHolder.hourText.setText(this.routeList.get(i).getHour());
        routeHolder.idText.setText(this.routeList.get(i).getId());
        routeHolder.setOnClickListeners();
    }

    @Override
    public int getItemCount() {
        return routeList.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) { super.onAttachedToRecyclerView(recyclerView); }
}