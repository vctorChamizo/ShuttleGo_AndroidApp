package tfg.shuttlego.model.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import tfg.shuttlego.R;
import tfg.shuttlego.model.transfers.origin.Origin;

public class ListViewAdapterOrigin extends BaseAdapter {

    Context mContext;
    LayoutInflater inflater;

    private List<Origin> originList = null;
    private ArrayList<Origin> originArrayList;

    public ListViewAdapterOrigin(Context context, List<Origin> originList) {

        mContext = context;
        inflater = LayoutInflater.from(mContext);
        this.originList = originList;
        this.originArrayList = new ArrayList<Origin>();
        this.originArrayList.addAll(originList);
    }

    public class ViewHolder { TextView name; }

    @Override
    public int getCount() { return originList.size(); }

    @Override
    public Origin getItem(int position) { return originList.get(position); }

    @Override
    public long getItemId(int position) { return position;}

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        final ViewHolder holder;

        if (view == null) {

            holder = new ViewHolder();
            view = inflater.inflate(R.layout.driver_origin_listview, null);
            holder.name = view.findViewById(R.id.driver_origin_name);
            view.setTag(holder);
        }
        else holder = (ViewHolder) view.getTag();

        holder.name.setText(originList.get(position).getName());
        return view;
    }

    public void filter(String charText) {

        charText = charText.toLowerCase(Locale.getDefault());
        originList.clear();

        if (charText.length() > 0)
            for (Origin or : originArrayList)
                if (or.getName().toLowerCase(Locale.getDefault()).contains(charText))
                    originList.add(or);

        notifyDataSetChanged();
    }//filter
}
