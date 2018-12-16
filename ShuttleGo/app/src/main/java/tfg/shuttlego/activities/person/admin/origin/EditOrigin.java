package tfg.shuttlego.activities.person.admin.origin;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class EditOrigin extends AppCompatActivity {

    String id_origin;
    TextView name_origin_tetxt;
    Button deleteOriginButton;
    Button editOrigin;
    JSONObject origin;
    Person user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_origin_edit);

        id_origin = (String) getIntent().getExtras().get("origin");
        user = (Person) getIntent().getExtras().get("user");

        inicializateItems();
        origin = makeJson(id_origin);
        getDataOrigin(origin);

        deleteOriginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JSONObject dataUser = new JSONObject();
                JSONObject dataOrigin = new JSONObject();
                JSONObject createOrigin = new JSONObject();

                try {

                    dataUser.put("email", user.getEmail());
                    dataUser.put("password", user.getPassword());
                    dataOrigin.put("id", id_origin);
                    createOrigin.put("user", dataUser);
                    createOrigin.put("origin", dataOrigin);

                } catch (JSONException e) {

                    Toast.makeText(getApplicationContext(), "ERROR. Vuelva a intentarlo", Toast.LENGTH_SHORT).show();
                }

                deleteOrigin(createOrigin);
            }
        });

    }//onCreate

    private void deleteOrigin(JSONObject data) {

        EventDispatcher.getInstance(getApplicationContext())
        .dispatchEvent(Event.DELEEORIGIN, data)
        .addOnCompleteListener(new OnCompleteListener<HashMap<String, String>>() {
            @Override
            public void onComplete(@NonNull Task<HashMap<String, String>> task) {

                if (!task.isSuccessful() || task.getResult() == null) {

                    Toast.makeText(getApplicationContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                } else if (task.getResult().containsKey("error")) {


                    switch (Objects.requireNonNull(task.getResult().get("error"))) {
                        case "server":
                            Toast.makeText(getApplicationContext(), "Error del servidor", Toast.LENGTH_SHORT).show();
                            break;

                        default:
                            Toast.makeText(getApplicationContext(), "Error desconocido: " + task.getResult().get("error"), Toast.LENGTH_SHORT).show();
                            break;
                    }

                }// else if
                else {

                    Intent intent = new Intent(EditOrigin.this, AdminMain.class);
                    intent.putExtra("user", user);
                    startActivity(intent);
                }
            }//onComplete
        });
    }

    private void getDataOrigin(JSONObject data) {

        EventDispatcher.getInstance(getApplicationContext())
        .dispatchEvent(Event.GETORIGINBYID, data)
        .addOnCompleteListener(new OnCompleteListener<HashMap<String, String>>() {
            @Override
            public void onComplete(@NonNull Task<HashMap<String, String>> task) {

                if (!task.isSuccessful() || task.getResult() == null) {

                    Toast.makeText(getApplicationContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                } else if (task.getResult().containsKey("error")) {


                    switch (Objects.requireNonNull(task.getResult().get("error"))) {
                        case "server":
                            Toast.makeText(getApplicationContext(), "Error del servidor", Toast.LENGTH_SHORT).show();
                            break;

                        default:
                            Toast.makeText(getApplicationContext(), "Error desconocido: " + task.getResult().get("error"), Toast.LENGTH_SHORT).show();
                            break;
                    }

                }// else if
                else {

                    HashMap<?, ?> hm_origin = task.getResult();
                    Origin origin = new Origin((String)hm_origin.get("id"), (String)hm_origin.get("name"));
                    name_origin_tetxt.setText(origin.getName());
                }
            }//onComplete
        });
    }

    private void inicializateItems() {

        name_origin_tetxt = findViewById(R.id.name_origin_text);
        deleteOriginButton = findViewById(R.id.btn_delete_origin);
        editOrigin = findViewById(R.id.btn_edit_origin);
    }//inicializateItems

    private JSONObject makeJson(String data) {

        JSONObject id = new JSONObject();
        JSONObject origin = new JSONObject();

        try {

            id.put("id", id_origin);
            origin.put("origin", id);

        } catch (JSONException e) {

            Toast.makeText(getApplicationContext(), "ERROR. Vuelva a intentarlo", Toast.LENGTH_SHORT).show();
        }
        return origin;
    }//makeJson

}
