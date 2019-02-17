package tfg.shuttlego.activities;

import android.app.Dialog;
import android.content.Context;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;

import tfg.shuttlego.R;
import tfg.shuttlego.model.adapters.ListViewAdapterOrigin;
import tfg.shuttlego.model.transfers.origin.Origin;

public class CuadroDialogo implements SearchView.OnQueryTextListener {

    private ListView list;
    private ListViewAdapterOrigin adapter;
    private SearchView editsearch;

    public CuadroDialogo (Context context, ArrayList<Origin> originArrayList) {

        final Dialog dialogo = new Dialog (context);
        dialogo.setContentView(R.layout.driver_listview_origin);

        list = dialogo.findViewById(R.id.driver_listview_origin_list);
        editsearch = dialogo.findViewById(R.id.driver_listview_origin_search);

        adapter = new ListViewAdapterOrigin(context, originArrayList);
        list.setAdapter(adapter);
        editsearch.setOnQueryTextListener(this);

        /*list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick (AdapterView<?> parent, View view, int position, long id) {

            }
        });*/

        dialogo.show();
    }

    @Override
    public boolean onQueryTextSubmit(String query) { return false; }

    @Override
    public boolean onQueryTextChange(String newText) {

        String text = newText;
        adapter.filter(text);

        return false;
    }
}
