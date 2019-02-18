package tfg.shuttlego.activities.origin;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;

import tfg.shuttlego.R;
import tfg.shuttlego.activities.person.admin.AdminMain;
import tfg.shuttlego.model.event.Event;
import tfg.shuttlego.model.event.EventDispatcher;
import tfg.shuttlego.model.transfer.person.Person;

public class AddOrigin extends AppCompatActivity implements View.OnClickListener {

    private Person user;
    private EditText origin;
    private RelativeLayout addOriginRelative;
    private ProgressBar addOriginProgress;
    private Button addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_origin_add);

        user = (Person)Objects.requireNonNull(getIntent().getExtras()).getSerializable("user");
        inicializateView();
        listeners();

    }//onCreate

    /**
     *
     */
    private void inicializateView() {

        addButton = findViewById(R.id.btn_origin_add);
        origin = findViewById(R.id.origin_add);
        addOriginRelative = findViewById(R.id.relative_form_add_origin);
        addOriginProgress = findViewById(R.id.progress);
    }//inicializateView

    /**
     *
     */
    private void listeners() {

        addButton.setOnClickListener(this);
    }//listeners

    /**
     *
     */
    private void setProgressBar () {

        addOriginProgress.setVisibility(View.VISIBLE);
        addOriginRelative.setVisibility(View.GONE);
    }//setProgressBar

    /**
     *
     */
    private void removeProgressBar () {

        addOriginProgress.setVisibility(View.GONE);
        addOriginRelative.setVisibility(View.VISIBLE);
    }//removeProgressBar

    /**
     *
     * @return
     */
    private JSONObject buildJson() {

        JSONObject dataUser = new JSONObject();
        JSONObject dataOrigin = new JSONObject();
        JSONObject createOrigin = new JSONObject();

        try {

            dataUser.put("email", user.getEmail());
            dataUser.put("password", user.getPassword());
            dataOrigin.put("name", origin.getText());
            createOrigin.put("user", dataUser);
            createOrigin.put("origin", dataOrigin);

        } catch (JSONException e) { throwToast(R.string.err); }

        return createOrigin;
    }//buildJson

    /**
     *
     * @param createOrigin
     */
    private void throwEventAddOrigin(JSONObject createOrigin) {

        EventDispatcher.getInstance(getApplicationContext())
        .dispatchEvent(Event.CREATEORIGIN, createOrigin)
        .addOnCompleteListener(new OnCompleteListener<HashMap<String, String>>() {
            @Override
            public void onComplete(@NonNull Task<HashMap<String, String>> task) {

                if (!task.isSuccessful() || task.getResult() == null) {

                    removeProgressBar();
                    throwToast(R.string.errConexion);
                } else if (task.getResult().containsKey("error")){

                    removeProgressBar();

                    switch (Objects.requireNonNull(task.getResult().get("error"))) {
                        case "badRequestForm": throwToast(R.string.errBadFormat); break;
                        case "originAlreadyExists": throwToast(R.string.errOriginExisit); break;
                        case "server": throwToast(R.string.errServer); break;
                    }
                }
                else {
                    Intent logIntent = new Intent(AddOrigin.this, AdminMain.class);
                    logIntent.putExtra("user", user);
                    startActivity(logIntent);
                }
            }
        });
    }//throwEvent

    /**
     *
     * @param msg
     */
    private void throwToast(int msg) { Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show(); }

    @Override
    public void onClick(View v) {

        boolean empty = false;

        switch (v.getId()){

            case R.id.btn_origin_add:
                if (origin.getText().toString().isEmpty()) empty = true;

                if (!empty) {

                    setProgressBar();
                    JSONObject createOrigin = buildJson();
                    throwEventAddOrigin(createOrigin);
                }
                else throwToast(R.string.errDataEmpty);
                break;
        }
    }
}
