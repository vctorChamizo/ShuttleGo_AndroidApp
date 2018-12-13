package tfg.shuttlego.logic;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import tfg.shuttlego.R;
import tfg.shuttlego.logic.origin.Origin;

public class OriginAdapter extends RecyclerView.Adapter<OriginAdapter.OriginViewHolder> {

    private ArrayList<Origin> originList;

    public OriginAdapter(ArrayList<Origin> originList) {
        this.originList = originList;
    }

    public static class OriginViewHolder extends RecyclerView.ViewHolder {

        CardView originCard;
        TextView nameText;
        TextView idText;

        public OriginViewHolder(View v) {
            super(v);
            originCard = v.findViewById(R.id.origin_card_view);
            nameText = v.findViewById(R.id.name_origin_text);
            idText = v.findViewById(R.id.id_origin_text);
        }
    }

    @Override
    public OriginViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.origin_cardview, viewGroup, false);
        OriginViewHolder vh = new OriginViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(OriginViewHolder originHolder, int i) {
        originHolder.nameText.setText(originList.get(i).getName());
        originHolder.idText.setText(originList.get(i).getId());
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