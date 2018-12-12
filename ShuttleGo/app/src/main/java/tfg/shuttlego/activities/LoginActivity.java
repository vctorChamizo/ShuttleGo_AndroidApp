package tfg.shuttlego.activities;


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
import tfg.shuttlego.logic.events.Event;
import tfg.shuttlego.logic.events.EventDispatcher;
import tfg.shuttlego.logic.person.Person;
import tfg.shuttlego.logic.person.TypePerson;


/**
 * Control the login of the application.
 * Allows you to enter the application if the account entered by the user is correct.
 */
public class LoginActivity extends AppCompatActivity {

    private ProgressBar pBar;
    private RelativeLayout relFormLogin;
    private EditText emailText, passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        relFormLogin = findViewById(R.id.relative_form_login);
        pBar = findViewById(R.id.progress);
        emailText = findViewById(R.id.email_login);
        passwordText = findViewById(R.id.password_login);
        Button signupButton = findViewById(R.id.btn_signup_login);
        Button signInButton = findViewById(R.id.btn_signin_login);


        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                relFormLogin.setVisibility(View.GONE);
                pBar.setVisibility(View.VISIBLE);

                String email = emailText.getText().toString();
                String password = passwordText.getText().toString();

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
                    public void onComplete(@NonNull Task<HashMap<String, String>> task) {

                        if (!task.isSuccessful() || task.getResult() == null) {

                            relFormLogin.setVisibility(View.VISIBLE);
                            pBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                        } else if (task.getResult().containsKey("error")) {

                            relFormLogin.setVisibility(View.VISIBLE);
                            pBar.setVisibility(View.GONE);

                            switch (Objects.requireNonNull(task.getResult().get("error"))) {

                                case "incorrectSignin":
                                case "userDoesntExists":
                                    Toast.makeText(getApplicationContext(), "Usuario/contraseña incorrectos", Toast.LENGTH_SHORT).show();
                                    break;

                                case "server":
                                    Toast.makeText(getApplicationContext(), "Error del servidor", Toast.LENGTH_SHORT).show();
                                    break;

                                default:
                                    Toast.makeText(getApplicationContext(), "Error desconocido: " + task.getResult().get("error"), Toast.LENGTH_SHORT).show();
                                    break;
                            }

                        }// else if
                        else {

                            HashMap<String, ?> p = task.getResult();
                            TypePerson typePerson;
                            Class nextClass;

                            String email = Objects.requireNonNull(p.get("email")).toString();
                            String password = Objects.requireNonNull(p.get("password")).toString();
                            String name = Objects.requireNonNull(p.get("name")).toString();
                            String surname = Objects.requireNonNull(p.get("surname")).toString();
                            int phone = Integer.parseInt(p.get("number").toString()); // ¿try - catch?
                            String type = Objects.requireNonNull(p.get("type")).toString();

                            switch (type) {
                                case "passenger":
                                    typePerson = TypePerson.USER;
                                    //nextClass = PassengerStartActivity.class;
                                    break;

                                case "driver":
                                    typePerson = TypePerson.DRIVER;
                                    //nextClass = DriverStartActivity.class;
                                    break;

                                default:
                                    typePerson = TypePerson.ADMIN;
                                    //nextClass = AdminStartActivity.class;
                                    break;
                            }

                            Person user = new Person(email, password, name, surname, phone, typePerson);

                            /*
                            Intent logIntent = new Intent(LoginActivity.this, nextClass);
                            logIntent.putExtra("user", user);
                            startActivity(logIntent);
                            */

                            //Captura del parametro pasado en la siguiente actvidad.
                            //Person user = (Person)getIntent().getExtras().getSerializable("user");
                        }
                    }//onComplete
                });
            }
        });
    }
}
