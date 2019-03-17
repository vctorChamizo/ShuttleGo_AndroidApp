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
import tfg.shuttlego.R;
import tfg.shuttlego.model.transfer.route.Route;

public class RecyclerViewAdapterRoute extends RecyclerView.Adapter<RecyclerViewAdapterRoute.OriginViewHolder> {

    private ArrayList<Route> routeList;

    public RecyclerViewAdapterRoute(ArrayList<Route> routeList) { this.routeList = routeList; }

    public static class OriginViewHolder extends RecyclerView.ViewHolder {

        Context context;
        CardView routeCard;
        TextView originText, destinyText, hourText, idText;

        OriginViewHolder(View v) {

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

                // Accion al pulsr sobre el cardview deseado
            });
        }
    }

    @NonNull
    @Override
    public OriginViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.origin_list_cardview, viewGroup, false);
        return new OriginViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OriginViewHolder originHolder, int i) {
        originHolder.originText.setText(this.routeList.get(i).getOrigin());
        originHolder.destinyText.setText(this.routeList.get(i).getDestination());
        originHolder.hourText.setText(this.routeList.get(i).getHour());
        originHolder.idText.setText(this.routeList.get(i).getId());
        originHolder.setOnClickListeners();
    }

    @Override
    public int getItemCount() {
        return routeList.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) { super.onAttachedToRecyclerView(recyclerView); }
}