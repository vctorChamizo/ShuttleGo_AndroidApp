package tfg.shuttlego.activities.origin;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Objects;
import tfg.shuttlego.R;
import tfg.shuttlego.activities.person.admin.AdminMain;
import tfg.shuttlego.model.events.Event;
import tfg.shuttlego.model.events.EventDispatcher;
import tfg.shuttlego.model.transfers.origin.Origin;
import tfg.shuttlego.model.transfers.person.Person;

public class EditOrigin extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout editOriginRelative;
    private ProgressBar editOriginProgress;
    private String id_origin;
    private TextView editOriginTextView;
    private Button deleteOriginButton, editOriginButton;
    private Person user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_origin_edit);

        id_origin = (String) getIntent().getExtras().get("origin");
        user = (Person) getIntent().getExtras().get("user");

        inicializateView();
        listeners();
        JSONObject origin = buildGetOriginJson(id_origin);
        setProgressBar();
        throwEventGetOrigin(origin);
        removeProgressBar();
    }//onCreate

    /**
     *
     */
    private void inicializateView() {

        editOriginRelative = findViewById(R.id.relative_origin_edit);
        editOriginProgress = findViewById(R.id.progress);
        editOriginTextView = findViewById(R.id.name_origin_text);
        deleteOriginButton = findViewById(R.id.btn_delete_origin);
        editOriginButton = findViewById(R.id.btn_edit_origin);
    }//inicializateView

    /**
     *
     */
    private void listeners() {

        deleteOriginButton.setOnClickListener(this);
        editOriginButton.setOnClickListener(this);
    }//listeners

    /**
     *
     */
    private void setProgressBar () {

        editOriginProgress.setVisibility(View.VISIBLE);
        editOriginRelative.setVisibility(View.GONE);
    }//setProgressBar

    /**
     *
     */
    private void removeProgressBar () {

        editOriginProgress.setVisibility(View.GONE);
        editOriginRelative.setVisibility(View.VISIBLE);
    }//removeProgressBar

    /**
     *
     * @param data
     * @return
     */
    private JSONObject buildGetOriginJson(String data) {

        JSONObject id = new JSONObject();
        JSONObject getOrigin = new JSONObject();

        try {

            id.put("id", id_origin);
            getOrigin.put("origin", id);
        }
        catch (JSONException e) { throwToast(R.string.err); }

        return getOrigin;
    }//buildGetOriginJson

    /**
     *
     * @return
     */
    private JSONObject buildDeleteOriginJson() {

        JSONObject dataUser = new JSONObject();
        JSONObject dataOrigin = new JSONObject();
        JSONObject deleteOrigin = new JSONObject();

        try {

            dataUser.put("email", user.getEmail());
            dataUser.put("password", user.getPassword());
            dataOrigin.put("id", id_origin);
            deleteOrigin.put("user", dataUser);
            deleteOrigin.put("origin", dataOrigin);
        }
        catch (JSONException e) { throwToast(R.string.err); }

        return deleteOrigin;
    }//buildDeleteOriginJson

    /**
     *
     */
    private void throwEventGetOrigin(JSONObject origin) {

        EventDispatcher.getInstance(getApplicationContext())
        .dispatchEvent(Event.GETORIGINBYID, origin)
        .addOnCompleteListener(new OnCompleteListener<HashMap<String, String>>() {
            @Override
            public void onComplete(@NonNull Task<HashMap<String, String>> task) {

                if (!task.isSuccessful() || task.getResult() == null) throwToast(R.string.errConexion);
                else if (task.getResult().containsKey("error")) throwToast(R.string.errServer);
                else {

                    HashMap<?, ?> hm_origin = task.getResult();
                    Origin origin = new Origin((String)hm_origin.get("id"), (String)hm_origin.get("name"));
                    editOriginTextView.setText(origin.getName());
                }
            }//onComplete
        });
    }//throwEventGetOrigin

    /**
     *
     */
    private void throwEventRemoveOrigin() {

        setProgressBar();

        JSONObject deleteOrigin = buildDeleteOriginJson();

        EventDispatcher.getInstance(getApplicationContext())
        .dispatchEvent(Event.DELETEORIGIN, deleteOrigin)
        .addOnCompleteListener(new OnCompleteListener<HashMap<String, String>>() {
            @Override
            public void onComplete(@NonNull Task<HashMap<String, String>> task) {

                if (!task.isSuccessful() || task.getResult() == null) {
                    removeProgressBar();
                    throwToast(R.string.errConexion);
                }
                else if (task.getResult().containsKey("error")) {
                    removeProgressBar();
                    throwToast(R.string.errServer);
                }
                else {

                    Intent intent = new Intent(EditOrigin.this, AdminMain.class);
                    intent.putExtra("user", user);
                    startActivity(intent);
                }
            }
        });
    }//throwEventRemoveOrigin

    /**
     *
     * @param msg
     */
    private void throwToast(int msg) { Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show(); }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.btn_delete_origin: throwEventRemoveOrigin();break;
            case R.id.btn_edit_origin: break;
        }
    }
}
