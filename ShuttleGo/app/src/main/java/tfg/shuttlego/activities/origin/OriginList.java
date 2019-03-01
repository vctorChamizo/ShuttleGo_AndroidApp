package tfg.shuttlego.activities.origin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import tfg.shuttlego.R;
import tfg.shuttlego.activities.person.admin.AdminMain;
import tfg.shuttlego.model.adapter.RecyclerViewAdapterOrigin;
import tfg.shuttlego.model.event.Event;
import tfg.shuttlego.model.event.EventDispatcher;
import tfg.shuttlego.model.transfer.origin.Origin;

public class OriginList extends AppCompatActivity {

    private LinearLayout originListLinear;
    private ProgressBar originListProgress;
    private ArrayList<Origin> listOrigins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.origin_list);

        inicializateView();
        setProgressBar();
        throwEventGetAllOrigins();
    }

    /**
     *
     */
    private void inicializateView() {

        originListLinear = findViewById(R.id.origin_list_linear);
        originListProgress = findViewById(R.id.origin_list_progress);
    }//inicializateView

    /**
     *
     */
    private void setProgressBar() {

        originListProgress.setVisibility(View.VISIBLE);
        originListLinear.setVisibility(View.GONE);
    }//setProgressBar

    /**
     *
     */
    private void removeProgressBar() {

        originListProgress.setVisibility(View.GONE);
        originListLinear.setVisibility(View.VISIBLE);
    }//removeProgressBar

    /**
     *
     */
    private void throwEventGetAllOrigins() {

        EventDispatcher.getInstance(getApplicationContext())
        .dispatchEvent(Event.GETORIGINS, null)
        .addOnCompleteListener(task -> {

            if (!task.isSuccessful() || task.getResult() == null) {

                throwToast(R.string.errConexion);
                startActivity(new Intent(OriginList.this, AdminMain.class));
            }
            else if (task.getResult().containsKey("error")) {
                throwToast(R.string.errServer);
                startActivity(new Intent(OriginList.this, AdminMain.class));
            }
            else {

                HashMap<?, ?> result = task.getResult();
                ArrayList<HashMap<?, ?>> list = (ArrayList<HashMap<?, ?>>) result.get("origins");
                listOrigins = new ArrayList<>();

                assert list != null;
                for (int i = 0; i < list.size(); ++i) {
                    Origin origin = new Origin();
                    origin.setId((String) list.get(i).get("id"));
                    origin.setName((String) list.get(i).get("name"));
                    listOrigins.add(origin);
                }

                createListView();
                removeProgressBar();
            }
        });
    }//throwEventGetAllOrigins

    /**
     *
     */
    private void createListView() {

        RecyclerView recycler = findViewById(R.id.origin_list_recycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recycler.setLayoutManager(layoutManager);
        RecyclerView.Adapter<RecyclerViewAdapterOrigin.OriginViewHolder> adapter = new RecyclerViewAdapterOrigin(listOrigins);
        recycler.setAdapter(adapter);
    }

    private void throwToast(int msg) { Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show(); }
}
