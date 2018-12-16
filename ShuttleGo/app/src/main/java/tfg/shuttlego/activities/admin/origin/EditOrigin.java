package tfg.shuttlego.activities.admin.origin;

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
import tfg.shuttlego.activities.admin.AdminMain;
import tfg.shuttlego.model.events.Event;
import tfg.shuttlego.model.events.EventDispatcher;
import tfg.shuttlego.model.transfers.origin.Origin;
import tfg.shuttlego.model.transfers.person.Person;

public class EditOrigin extends AppCompatActivity implements View.OnClickListener {

    RelativeLayout rel_layout;
    ProgressBar pB;
    String id_origin;
    TextView name_origin_tetxt;
    Button deleteOriginButton, editOriginButton;
    Person user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_origin_edit);

        id_origin = (String) getIntent().getExtras().get("origin");
        user = (Person) getIntent().getExtras().get("user");

        inicializateView();
        changeVisibility(rel_layout, pB);
        throwGetOriginEvent();
        changeVisibility(pB, rel_layout);

        deleteOriginButton.setOnClickListener(this);
        editOriginButton.setOnClickListener(this);

    }//onCreate

    /**
     *
     */
    private void inicializateView() {
        rel_layout = findViewById(R.id.relative_origin_edit);
        pB = findViewById(R.id.progress);
        name_origin_tetxt = findViewById(R.id.name_origin_text);
        deleteOriginButton = findViewById(R.id.btn_delete_origin);
        editOriginButton = findViewById(R.id.btn_edit_origin);
    }//inicializateView

    /**
     *
     */
    private void throwGetOriginEvent() {

        JSONObject origin = buildGetOriginJson(id_origin);

        EventDispatcher.getInstance(getApplicationContext())
        .dispatchEvent(Event.GETORIGINBYID, origin)
        .addOnCompleteListener(new OnCompleteListener<HashMap<String, String>>() {
            @Override
            public void onComplete(@NonNull Task<HashMap<String, String>> task) {

                if (!task.isSuccessful() || task.getResult() == null) {
                    throwToast("Error de conexion");
                }
                else if (task.getResult().containsKey("error")) {

                    switch (Objects.requireNonNull(task.getResult().get("error"))) {
                        case "server":
                            throwToast("Error del servidor");
                            break;

                        default:
                            throwToast("Error desconocido: " + task.getResult().get("error"));
                            break;
                    }//switch
                }
                else {
                    HashMap<?, ?> hm_origin = task.getResult();
                    Origin origin = new Origin((String)hm_origin.get("id"), (String)hm_origin.get("name"));
                    name_origin_tetxt.setText(origin.getName());
                }
            }//onComplete
        });
    }//throwGetOriginEvent

    /**
     *
     */
    private void throwDeleteEvent() {

        changeVisibility(rel_layout, pB);

        JSONObject deleteOrigin = buildDeleteOriginJson();

        EventDispatcher.getInstance(getApplicationContext())
        .dispatchEvent(Event.DELETEORIGIN, deleteOrigin)
        .addOnCompleteListener(new OnCompleteListener<HashMap<String, String>>() {
            @Override
            public void onComplete(@NonNull Task<HashMap<String, String>> task) {

            if (!task.isSuccessful() || task.getResult() == null) {
                changeVisibility(pB, rel_layout);
                throwToast("Error de conexion");
            }
            else if (task.getResult().containsKey("error")) {

                changeVisibility(pB, rel_layout);

                switch (Objects.requireNonNull(task.getResult().get("error"))) {
                    case "server":
                        throwToast("Error del servidor");
                        break;

                    default:
                        throwToast("Error desconocido: " + task.getResult().get("error"));
                        break;
                }//switch
            }
            else {

                Intent intent = new Intent(EditOrigin.this, AdminMain.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        }//onComplete
    });
    }//throwDeleteEvent

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
        catch (JSONException e) {
            throwToast("ERROR. Vuelva a intentarlo");
        }

        return getOrigin;
    }//buildJson

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
        catch (JSONException e) {
            throwToast("ERROR. Vuelva a intentarlo");
        }

        return deleteOrigin;
    }//buildJson

    /**
     *
     * @param vGone
     * @param vVisible
     */
    private void changeVisibility(View vGone, View vVisible) {
        vGone.setVisibility(View.GONE);
        vVisible.setVisibility(View.VISIBLE);
    }//changeVisibility

    /**
     *
     * @param msg
     */
    private void throwToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }//throwToast

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.btn_delete_origin:

                throwDeleteEvent();
                break;

            case R.id.btn_edit_origin:
                break;

        }//switch
    }//onClick
}
