package tfg.shuttlego.model.adapters;

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
import tfg.shuttlego.activities.person.admin.origin.EditOrigin;
import tfg.shuttlego.model.transfers.origin.Origin;
import tfg.shuttlego.model.transfers.person.Person;

/**
 *
 */
public class OriginAdapter extends RecyclerView.Adapter<OriginAdapter.OriginViewHolder> {

    private ArrayList<Origin> originList;
    private static Person user;

    /**
     *
     * @param originList
     * @param user
     */
    public OriginAdapter(ArrayList<Origin> originList, Person user) {
        this.originList = originList;
        this.user = user;
    }

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

            nameText = v.findViewById(R.id.btndddd);
            originCard = v.findViewById(R.id.origin_card_view);
            idText = v.findViewById(R.id.id_origin_text);
        }

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
        }
    }

    @Override
    public OriginViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.admin_origin_cardiew, viewGroup, false);
        OriginViewHolder vh = new OriginViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(OriginViewHolder originHolder, int i) {
        originHolder.nameText.setText(originList.get(i).getName());
        originHolder.idText.setText(originList.get(i).getId());

        originHolder.setOnClickListeners();
    }

    @Override
    public int getItemCount() {
        return originList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}