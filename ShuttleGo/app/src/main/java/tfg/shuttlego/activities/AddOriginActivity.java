package tfg.shuttlego.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;

import tfg.shuttlego.R;
import tfg.shuttlego.logic.events.Event;
import tfg.shuttlego.logic.events.EventDispatcher;
import tfg.shuttlego.logic.person.Person;

public class AddOriginActivity extends AppCompatActivity {

    private Person user;
    private EditText origin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_origin);

        user = (Person)Objects.requireNonNull(getIntent().getExtras()).getSerializable("user");

        Button addButton = findViewById(R.id.btn_origin_add);
        origin = findViewById(R.id.origin_add);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

                    Toast.makeText(getApplicationContext(), "ERROR. Vuelva a intentarlo", Toast.LENGTH_SHORT).show();
                }

                EventDispatcher.getInstance(getApplicationContext())
                .dispatchEvent(Event.CREATEORIGIN, createOrigin)
                .addOnCompleteListener(new OnCompleteListener<HashMap<String, String>>() {
                    @Override
                    public void onComplete(@NonNull Task<HashMap<String, String>> task) {

                        if (!task.isSuccessful() || task.getResult() == null) {
                            Toast.makeText(getApplicationContext(), "Error de conexión", Toast.LENGTH_SHORT).show();

                        } else if (task.getResult().containsKey("error"))
                            switch (Objects.requireNonNull(task.getResult().get("error"))) {
                                case "server":
                                    Toast.makeText(getApplicationContext(), "Error del servidor", Toast.LENGTH_SHORT).show();
                                    break;

                                case "originAlreadyExists":
                                    Toast.makeText(getApplicationContext(), "El origen ya existe", Toast.LENGTH_SHORT).show();
                                    break;

                                default:
                                    Toast.makeText(getApplicationContext(), "Error desconocido: " + task.getResult().get("error"), Toast.LENGTH_SHORT).show();
                                    break;
                            }//else if
                        else {
                            Intent logIntent = new Intent(AddOriginActivity.this, AdminStartActivity.class);
                            logIntent.putExtra("user", user);
                            startActivity(logIntent);
                        }//else
                    }//onComplete
                });
            }
        });
    }
}
