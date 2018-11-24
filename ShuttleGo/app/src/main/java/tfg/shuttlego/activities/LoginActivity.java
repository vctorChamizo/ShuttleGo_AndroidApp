package tfg.shuttlego.activities;

import android.annotation.SuppressLint;
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
import com.google.firebase.FirebaseApp;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import tfg.shuttlego.R;
import tfg.shuttlego.logic.events.Event;
import tfg.shuttlego.logic.events.EventDispatcher;


/**
 * Control the login of the application.
 * Allows you to enter the application if the account entered by the user is correct.
 */
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button signInButton = findViewById(R.id.btn_signin_login);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = ((EditText)findViewById(R.id.email_login)).getText().toString();
                String password = ((EditText)findViewById(R.id.password_login)).getText().toString();

                JSONObject json = new JSONObject();
                JSONObject user = new JSONObject();

                try {

                    json.put("email", email);
                    json.put("password", password);
                    user.put("user", json);

                } catch (JSONException e) {

                    Toast.makeText(getApplicationContext(), "ERROR. Vuelva a intentarlo", Toast.LENGTH_SHORT).show();
                }

                EventDispatcher.getInstance(getApplicationContext())
                .dispatchEvent(Event.SIGNIN, user)
                .addOnCompleteListener(new OnCompleteListener<HashMap<String, String>>() {
                    @Override
                    public void onComplete(@NonNull Task<HashMap<String,String>> task) {

                        if (!task.isSuccessful() || task.getResult() == null || task.getResult().containsKey("error"))
                            Toast.makeText(getApplicationContext(), "Usuario/contraseña incorrectos", Toast.LENGTH_SHORT).show();

                        else startActivity(new Intent(LoginActivity.this, WelcomeActivity.class));
                    }//onComplete
                });
            }
        });
    }
}
