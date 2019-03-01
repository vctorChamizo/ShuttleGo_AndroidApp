package tfg.shuttlego.model.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
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

public class RecyclerViewAdapterOrigin extends RecyclerView.Adapter<RecyclerViewAdapterOrigin.OriginViewHolder> {

    private ArrayList<Origin> originList;

    public RecyclerViewAdapterOrigin(ArrayList<Origin> originList) {

        this.originList = originList;
    }

    public static class OriginViewHolder extends RecyclerView.ViewHolder {

        Context context;
        CardView originCard;
        Button originNameButton;
        TextView originIdText;

        OriginViewHolder(View v) {

            super(v);
            context = v.getContext();
            originCard = v.findViewById(R.id.origin_cardview_cardview);
            originNameButton = v.findViewById(R.id.origin_cardview_button);
            originIdText = v.findViewById(R.id.origin_cardview_textview);
        }


        void setOnClickListeners() {

            originNameButton.setOnClickListener(v -> {

                Intent intent = new Intent(context, OriginMain.class);
                intent.putExtra("origin", originIdText.getText());
                context.startActivity(intent);
            });
        }
    }//OriginViewHolder

    @NonNull
    @Override
    public OriginViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.origin_cardview, viewGroup, false);
        return new OriginViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OriginViewHolder originHolder, int i) {
        originHolder.originNameButton.setText(originList.get(i).getName());
        originHolder.originIdText.setText(originList.get(i).getId());
        originHolder.setOnClickListeners();
    }

    @Override
    public int getItemCount() {
        return originList.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}