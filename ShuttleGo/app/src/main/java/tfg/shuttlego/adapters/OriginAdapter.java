package tfg.shuttlego.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
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

    public OriginAdapter(ArrayList<Origin> originList) { this.originList = originList; }

    public static class OriginViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        Context context;

        CardView originCard;
        TextView nameText;
        TextView idText;
        FloatingActionButton deleteOrigin;
        FloatingActionButton editOrigin;

        public OriginViewHolder(View v) {

            super(v);

            context = v.getContext();

            originCard = v.findViewById(R.id.origin_card_view);
            nameText = v.findViewById(R.id.name_origin_text);
            idText = v.findViewById(R.id.id_origin_text);
            deleteOrigin = v.findViewById(R.id.btn_delete_origin);
            editOrigin = v.findViewById(R.id.btn_edit_origin);
        }

        public void setOnClickListeners() {

            deleteOrigin.setOnClickListener(this);
            editOrigin.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            switch (v.getId()){

                case R.id.btn_delete_origin:
                    
                    break;

                case R.id.btn_edit_origin:

                    //Codigo preparado para implementar esta opcion.
                    /*
                    Intent intent = new Intent(context, ...);
                    intent.putExtra("id_orign", idText.getText());
                    context.startActivity(intent);
                    */
                    break;
            }//switch

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