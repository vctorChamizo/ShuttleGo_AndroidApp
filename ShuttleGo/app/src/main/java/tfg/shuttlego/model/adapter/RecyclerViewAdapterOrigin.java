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
import tfg.shuttlego.activities.origin.EditOrigin;
import tfg.shuttlego.model.transfer.origin.Origin;
import tfg.shuttlego.model.transfer.person.Person;

/**
 *
 */
public class RecyclerViewAdapterOrigin extends RecyclerView.Adapter<RecyclerViewAdapterOrigin.OriginViewHolder> {

    private ArrayList<Origin> originList;
    private static Person user;

    /**
     *
     * @param originList
     * @param user
     */
    public RecyclerViewAdapterOrigin(ArrayList<Origin> originList, Person user) {

        this.originList = originList;
        this.user = user;
    }//RecyclerViewAdapterOrigin

    /**
     *
     */
    public static class OriginViewHolder extends RecyclerView.ViewHolder {

        Context context;

        CardView originCard;
        Button nameText;
        TextView idText;

        /**
         *
         * @param v
         */
        public OriginViewHolder(View v) {

            super(v);

            context = v.getContext();

            nameText = v.findViewById(R.id.admin_origin_carview_button);
            originCard = v.findViewById(R.id.origin_card_view);
            idText = v.findViewById(R.id.admin_origin_carview_textview);
        }//OriginViewHolder

        /**
         *
         */
        public void setOnClickListeners() {

            nameText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context, EditOrigin.class);
                    intent.putExtra("origin", idText.getText());
                    intent.putExtra("user", user);
                    context.startActivity(intent);
                }
            });
        }//setOnClickListeners
    }//OriginViewHolder

    @Override
    public OriginViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.admin_origin_cardiew, viewGroup, false);
        OriginViewHolder vh = new OriginViewHolder(v);
        return vh;
    }//OriginViewHolder

    @Override
    public void onBindViewHolder(OriginViewHolder originHolder, int i) {
        originHolder.nameText.setText(originList.get(i).getName());
        originHolder.idText.setText(originList.get(i).getId());

        originHolder.setOnClickListeners();
    }//onBindViewHolder

    @Override
    public int getItemCount() {
        return originList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }//onAttachedToRecyclerView
}