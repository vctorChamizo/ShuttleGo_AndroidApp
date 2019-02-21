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

import java.util.ArrayList;

import tfg.shuttlego.R;
import tfg.shuttlego.activities.origin.OriginMain;
import tfg.shuttlego.model.transfer.origin.Origin;
import tfg.shuttlego.model.transfer.person.Person;

/**
 *
 */
public class RecyclerViewAdapterRoute extends RecyclerView.Adapter<RecyclerViewAdapterRoute.RouteViewHolder> {

    private ArrayList<Origin> routeList;
    private static Person user;

    /**
     *
     * @param originList
     * @param user
     */
    public RecyclerViewAdapterRoute(ArrayList<Origin> originList, Person user) {

        this.routeList = originList;
        this.user = user;
    }//RecyclerViewAdapterOrigin

    /**
     *
     */
    public static class RouteViewHolder extends RecyclerView.ViewHolder {

        Context context;
        CardView originRoute;
        Button nameText;
        TextView idText;

        /**
         *
         * @param v
         */
        public RouteViewHolder(View v) {

            super(v);
            context = v.getContext();
            nameText = v.findViewById(R.id.route_cardview_button);
            originRoute = v.findViewById(R.id.route_cardview_cardview);
            idText = v.findViewById(R.id.route_cardview_textview);
        }//OriginViewHolder

        /**
         *
         */
        public void setOnClickListeners() {

            nameText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context, OriginMain.class);
                    intent.putExtra("origin", idText.getText());
                    intent.putExtra("user", user);
                    context.startActivity(intent);
                }
            });
        }//setOnClickListeners
    }//OriginViewHolder

    @Override
    public RouteViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.origin_cardview, viewGroup, false);
        RouteViewHolder vh = new RouteViewHolder(v);
        return vh;
    }//OriginViewHolder

    @Override
    public void onBindViewHolder(RouteViewHolder originHolder, int i) {

        originHolder.nameText.setText(routeList.get(i).getName());
        originHolder.idText.setText(routeList.get(i).getId());

        originHolder.setOnClickListeners();
    }//onBindViewHolder

    @Override
    public int getItemCount() {
        return routeList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }//onAttachedToRecyclerView
}