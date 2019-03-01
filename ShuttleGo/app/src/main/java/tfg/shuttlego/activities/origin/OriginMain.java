package tfg.shuttlego.activities.origin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Objects;
import tfg.shuttlego.R;
import tfg.shuttlego.activities.person.admin.AdminMain;
import tfg.shuttlego.model.event.Event;
import tfg.shuttlego.model.event.EventDispatcher;
import tfg.shuttlego.model.session.Session;
import tfg.shuttlego.model.transfer.origin.Origin;

public class OriginMain extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout originMainLinear;
    private ProgressBar originMainProgress;
    private String originMainIdOrigin;
    private TextView originMainTextName;
    private Button originMainDelteButton, originMainEditButton, originMainCloseButton;
    private Origin orginMainOriginObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.origin_main);

        originMainIdOrigin = (String)Objects.requireNonNull(getIntent().getExtras()).getSerializable("origin");

        inicializateView();
        setProgressBar();
        throwEventGetOrigin(buildGetOriginJson());
        listeners();
    }

    /**
     * Inicializate the componentes of this view
     */
    private void inicializateView() {

        originMainLinear = findViewById(R.id.origin_main_linear);
        originMainProgress = findViewById(R.id.origin_main_progress);
        originMainTextName = findViewById(R.id.origin_main_name_text);
        originMainDelteButton = findViewById(R.id.origin_main_delete_btn);
        originMainEditButton = findViewById(R.id.origin_main_edit_btn);
        originMainCloseButton = findViewById(R.id.origin_main_close_btn);
    }

    /**
     * Show the progress bar component visible and put invisble the rest of the view
     */
    private void setProgressBar () {

        originMainProgress.setVisibility(View.VISIBLE);
        originMainLinear.setVisibility(View.GONE);
    }

    /**
     * Show the view visible and put invisble progress bar component
     */
    private void removeProgressBar () {

        originMainProgress.setVisibility(View.GONE);
        originMainLinear.setVisibility(View.VISIBLE);
    }

    /**
     * Build a JSON to get a origin
     *
     * @return JSON with information about the current origin
     */
    private JSONObject buildGetOriginJson() {

        JSONObject id = new JSONObject();
        JSONObject getOrigin = new JSONObject();

        try {

            id.put("id", originMainIdOrigin);
            getOrigin.put("origin", id);
        }
        catch (JSONException e) { throwToast(R.string.err); }

        return getOrigin;
    }//buildGetOriginJson

    /**
     * Throw the event that allow get a origin
     *
     * @param origin JSON with information to get a origin
     */
    private void throwEventGetOrigin(JSONObject origin) {

        //onComplete
        EventDispatcher.getInstance(getApplicationContext())
        .dispatchEvent(Event.GETORIGINBYID, origin)
        .addOnCompleteListener(task -> {

            if (!task.isSuccessful() || task.getResult() == null) throwToast(R.string.errConexion);
            else if (task.getResult().containsKey("error")) throwToast(R.string.errServer);
            else {

                HashMap<?, ?> hm_origin = task.getResult();
                orginMainOriginObject = new Origin((String)hm_origin.get("id"), (String)hm_origin.get("name"));
                originMainTextName.setText(orginMainOriginObject.getName());

                removeProgressBar();
            }
        });
    }

    /**
     * Have the listeners to the action components in the view
     */
    private void listeners() {

        originMainDelteButton.setOnClickListener(this);
        originMainEditButton.setOnClickListener(this);
        originMainCloseButton.setOnClickListener(this);
    }

    /**
     * Build a JSON to delete the current origin
     *
     * @return JSON with information about the current origin
     */
    private JSONObject buildDeleteOriginJson() {

        JSONObject dataUser = new JSONObject();
        JSONObject dataOrigin = new JSONObject();
        JSONObject deleteOrigin = new JSONObject();

        try {

            dataUser.put("email", Session.getInstance(getApplicationContext()).getUser().getEmail());
            dataUser.put("password", Session.getInstance(getApplicationContext()).getUser().getPassword());
            dataOrigin.put("id", orginMainOriginObject.getId());
            deleteOrigin.put("user", dataUser);
            deleteOrigin.put("origin", dataOrigin);
        }
        catch (JSONException e) { throwToast(R.string.err); }

        return deleteOrigin;
    }

    /**
     * Throw the event that allow delete the current origin
     *
     * @param origin JSON with information about the current origin
     */
   private void throwEventDeleteOrigin(JSONObject origin) {

        EventDispatcher.getInstance(getApplicationContext())
        .dispatchEvent(Event.DELETEORIGIN, origin)
        .addOnCompleteListener(task -> {

            if (!task.isSuccessful() || task.getResult() == null) {
                removeProgressBar();
                throwToast(R.string.errConexion);
            }
            else if (task.getResult().containsKey("error")) {
                removeProgressBar();
                throwToast(R.string.errServer);
            }
            else {

                throwToast(R.string.deleteOriginSuccesful);
                startActivity(new Intent(OriginMain.this, AdminMain.class));
            }
        });
    }

    private void throwToast(int msg) { Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show(); }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.origin_main_delete_btn:
                setProgressBar();
                throwEventDeleteOrigin(buildDeleteOriginJson());
                break;

            case R.id.origin_main_edit_btn:
                Intent intent = new Intent(OriginMain.this, OriginEdit.class);
                intent.putExtra("origin", orginMainOriginObject);
                startActivity(intent);
                break;

            case R.id.origin_main_close_btn:
                startActivity(new Intent(OriginMain.this, AdminMain.class));
                break;
        }
    }

    @Override
    public void onBackPressed() { startActivity(new Intent(OriginMain.this, AdminMain.class)); }
}
