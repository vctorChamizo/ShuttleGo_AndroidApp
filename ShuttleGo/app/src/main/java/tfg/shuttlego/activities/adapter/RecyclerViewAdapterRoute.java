package tfg.shuttlego.activities.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import tfg.shuttlego.R;
import tfg.shuttlego.activities.route.routeMain.RouteMainDriver;
import tfg.shuttlego.activities.route.routeMain.RouteMainPassengerInformation;
import tfg.shuttlego.model.session.Session;
import tfg.shuttlego.model.transfer.person.TypePerson;
import tfg.shuttlego.model.transfer.route.Route;

public class RecyclerViewAdapterRoute extends RecyclerView.Adapter<RecyclerViewAdapterRoute.RouteViewHolder> {

    private ArrayList<Route> routeList;

    public RecyclerViewAdapterRoute(ArrayList<Route> routeList) { this.routeList = routeList; }

    public static class RouteViewHolder extends RecyclerView.ViewHolder {

        Context context;
        CardView routeCard;
        TextView originText, destinyText, hourText, idText, passengerText;
        TextView destinyTittle;
        ImageView destinyImage;
        LinearLayout passengerLinear;

        RouteViewHolder(View v) {

            super(v);

            this.context = v.getContext();

            this.routeCard = v.findViewById(R.id.route_list_cardview_cardview);
            this.originText = v.findViewById(R.id.route_list_cardview_origin);
            this.destinyText = v.findViewById(R.id.route_list_cardview_destiny);
            this.hourText = v.findViewById(R.id.route_list_cardview_hour);
            this.passengerText = v.findViewById(R.id.route_list_cardview_passengers);
            this.destinyTittle = v.findViewById(R.id.route_list_cardview_destiny_text);
            this.destinyImage = v.findViewById(R.id.route_list_cardview_image_destiny);
            this.passengerLinear = v.findViewById(R.id.route_list_cardview_passenger_linear);
            this.idText = v.findViewById(R.id.route_list_cardview_id);
        }

        void setOnClickListeners() {

            this.routeCard.setOnClickListener(v -> {

                Intent intent;

                if (Session.getInstance().getUser().getType() == TypePerson.DRIVER) intent = new Intent(context, RouteMainDriver.class);
                else intent = new Intent(context, RouteMainPassengerInformation.class);

                intent.putExtra("route", this.idText.getText());

                this.context.startActivity(intent);
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

        String destiny;

        String origin = " " + this.routeList.get(i).getOrigin();
        routeHolder.originText.setText(origin);

        String hour = " " + String.valueOf(this.routeList.get(i).getHour());
        routeHolder.hourText.setText(hour);

        if (Session.getInstance().getUser().getType() == TypePerson.DRIVER) {

            destiny = " " + String.valueOf(this.routeList.get(i).getDestination());

            routeHolder.destinyTittle.setText(routeHolder.context.getString(R.string.limitCardview));
            routeHolder.destinyImage.setImageDrawable(routeHolder.context.getDrawable(R.drawable.ic_limit_blue));

            String passengers = " " + String.valueOf(this.routeList.get(i).getPassengerNumber()) + " / " + String.valueOf(this.routeList.get(i).getMax());
            routeHolder.passengerText.setText(passengers);
        }
        else {

            destiny = " " + String.valueOf(this.routeList.get(i).getDestination()).split(",")[0];

            routeHolder.passengerLinear.setVisibility(View.GONE);
        }

        routeHolder.destinyText.setText(destiny);
        routeHolder.idText.setText(this.routeList.get(i).getId());

        routeHolder.setOnClickListeners();
    }

    @Override
    public int getItemCount() { return routeList.size(); }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) { super.onAttachedToRecyclerView(recyclerView); }
}
