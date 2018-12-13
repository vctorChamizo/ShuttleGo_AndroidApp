package tfg.shuttlego.activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class EditOriginActivity extends AppCompatActivity {

    String id_origin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_origin);

        id_origin = (String) getIntent().getExtras().get("origin");

        JSONObject origin = makeJson(id_origin);

        EventDispatcher.getInstance(getApplicationContext())
        .dispatchEvent(Event.GETORIGINBYID, origin)
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

                    Object r = task.getResult();

                }
            }//onComplete
        });
    }

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
