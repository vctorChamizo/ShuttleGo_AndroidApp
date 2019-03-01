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
        setContentView(R.layout.origin_card);

        originMainIdOrigin = (String)Objects.requireNonNull(getIntent().getExtras()).getSerializable("origin");

        inicializateView();
        setProgressBar();
        throwEventGetOrigin(buildGetOriginJson());
        listeners();
    }

    /**
     *
     */
    private void inicializateView() {

        originMainLinear = findViewById(R.id.origin_card_linear1);
        originMainProgress = findViewById(R.id.origin_card_progress);
        originMainTextName = findViewById(R.id.origin_card_name_text);
        originMainDelteButton = findViewById(R.id.origin_card_delete_btn);
        originMainEditButton = findViewById(R.id.origin_card_edit_btn);
        originMainCloseButton = findViewById(R.id.origin_card_close_btn);
    }//inicializateView

    /**
     *
     */
    private void setProgressBar () {

        originMainProgress.setVisibility(View.VISIBLE);
        originMainLinear.setVisibility(View.GONE);
    }//setProgressBar

    /**
     *
     */
    private void removeProgressBar () {

        originMainProgress.setVisibility(View.GONE);
        originMainLinear.setVisibility(View.VISIBLE);
    }//removeProgressBar

    /**
     *
     * @return JSON with information about origin to getOrigin
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
     *
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
    }//throwEventGetOrigin

    /**
     *
     */
    private void listeners() {

        originMainDelteButton.setOnClickListener(this);
        originMainEditButton.setOnClickListener(this);
        originMainCloseButton.setOnClickListener(this);
    }//listeners

    /**
     *
     * @return JSON with information about origin to deleteOrigin
     */
    private JSONObject buildDeleteOriginJson() {

        JSONObject dataUser = new JSONObject();
        JSONObject dataOrigin = new JSONObject();
        JSONObject deleteOrigin = new JSONObject();

        try {

            //dataUser.put("email", );
            //dataUser.put("password", );
            dataOrigin.put("id", orginMainOriginObject.getId());
            deleteOrigin.put("user", dataUser);
            deleteOrigin.put("origin", dataOrigin);
        }
        catch (JSONException e) { throwToast(R.string.err); }

        return deleteOrigin;
    }//buildDeleteOriginJson

    /**
     *
     * @return JSON with information about origin to editOrigin
     */
    private JSONObject buildEditOriginJson() {

        JSONObject dataUser = new JSONObject();
        JSONObject dataOrigin = new JSONObject();
        JSONObject editOrigin = new JSONObject();

        try {

            //dataUser.put("email", );
            //dataUser.put("password", );
            dataOrigin.put("id", orginMainOriginObject.getId());
            dataOrigin.put("name", orginMainOriginObject.getName());
            editOrigin.put("user", dataUser);
            editOrigin.put("origin", dataOrigin);
        }
        catch (JSONException e) { throwToast(R.string.err); }

        return editOrigin;

    }//buildDeleteOriginJson*/

    /**
     *
     */
   private void throwEventDeleteOrigin(JSONObject data) {

        EventDispatcher.getInstance(getApplicationContext())
        .dispatchEvent(Event.DELETEORIGIN, data)
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
    }//throwEventDeleteOrigin

    /**
     *
     */
    private void throwEventEditOrigin() {

        Intent intent = new Intent(OriginMain.this, AdminMain.class);
        intent.putExtra("origin", orginMainOriginObject);
        startActivity(intent);

    }//throwEventEditOrigin

    /**
     *
     * @param msg
     */
    private void throwToast(int msg) { Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show(); }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.origin_card_delete_btn:

                break;

            case R.id.origin_card_edit_btn:

                break;

            case R.id.origin_card_close_btn:

                break;
        }
    }
}
