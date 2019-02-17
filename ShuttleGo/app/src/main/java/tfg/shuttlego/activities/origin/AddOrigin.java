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
import tfg.shuttlego.model.events.Event;
import tfg.shuttlego.model.events.EventDispatcher;
import tfg.shuttlego.model.transfers.person.Person;

public class AddOrigin extends AppCompatActivity implements View.OnClickListener {

    private Person user;
    private EditText origin;
    private RelativeLayout relFormOrigin;
    private ProgressBar pBar;
    private Button addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_origin_add);

        user = (Person)Objects.requireNonNull(getIntent().getExtras()).getSerializable("user");

        inicializateView();

        addButton.setOnClickListener(this);
    }//onCreate

    /**
     *
     */
    private void inicializateView() {
        addButton = findViewById(R.id.btn_origin_add);
        origin = findViewById(R.id.origin_add);
        relFormOrigin = findViewById(R.id.relative_form_add_origin);
        pBar = findViewById(R.id.progress);
    }//inicializateView

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

        } catch (JSONException e) {
            throwToast("ERROR. Vuelva a intentarlo");
        }

        return createOrigin;
    }//buildJson

    /**
     *
     * @param createOrigin
     */
    private void throwEvent(JSONObject createOrigin) {

        EventDispatcher.getInstance(getApplicationContext())
        .dispatchEvent(Event.CREATEORIGIN, createOrigin)
        .addOnCompleteListener(new OnCompleteListener<HashMap<String, String>>() {
            @Override
            public void onComplete(@NonNull Task<HashMap<String, String>> task) {

                if (!task.isSuccessful() || task.getResult() == null) {
                    changeVisibility(pBar, relFormOrigin);
                    throwToast("Error de conexion");
                } else if (task.getResult().containsKey("error")){

                    changeVisibility(pBar, relFormOrigin);

                    switch (Objects.requireNonNull(task.getResult().get("error"))) {

                        case "badRequestForm":
                            throwToast("Formato de datos incorrecto");
                            break;

                        case "originAlreadyExists":
                            throwToast("El origen ya existe");
                            break;

                        case "server":
                            throwToast("Error del servidor");
                            break;

                        default:
                            throwToast("Error desconocido: " + task.getResult().get("error"));
                            break;
                    }//switch
                }
                else {
                    Intent logIntent = new Intent(AddOrigin.this, AdminMain.class);
                    logIntent.putExtra("user", user);
                    startActivity(logIntent);
                }//else
            }//onComplete
        });
    }//throwEvent

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

        boolean empty = false;

        switch (v.getId()){

            case R.id.btn_origin_add:

                if (origin.getText().toString().isEmpty()) empty = true;

                if (!empty) {

                    changeVisibility(relFormOrigin, pBar);
                    JSONObject createOrigin = buildJson();
                    throwEvent(createOrigin);
                }
                else throwToast("Introduzca un origen");
                break;
        }//switch
    }//onClick
}
