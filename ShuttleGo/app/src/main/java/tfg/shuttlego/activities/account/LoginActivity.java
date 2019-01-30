package tfg.shuttlego.activities.account;

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
import tfg.shuttlego.activities.admin.AdminMain;
import tfg.shuttlego.activities.driver.DriverMain;
import tfg.shuttlego.activities.passenger.PassengerMain;
import tfg.shuttlego.model.events.Event;
import tfg.shuttlego.model.events.EventDispatcher;
import tfg.shuttlego.model.transfers.person.Person;
import tfg.shuttlego.model.transfers.person.TypePerson;

/**
 * Control the login of the application.
 * Allows you to enter the application if the account entered by the user is correct.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private ProgressBar pBar;
    private RelativeLayout relFormLogin;
    private EditText emailText, passwordText;
    private Button signupButton, signInButton;
    private String email, name, surname, password, type;
    private int phone;
    private TypePerson typePerson;
    private Class nextClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_login);

        inicializateView();

        signupButton.setOnClickListener(this);
        signInButton.setOnClickListener(this);
    }

    /**
     *
     */
    private void inicializateView() {
        relFormLogin = findViewById(R.id.relative_form_login);
        pBar = findViewById(R.id.progress);
        emailText = findViewById(R.id.email_login);
        passwordText = findViewById(R.id.password_login);
        signupButton = findViewById(R.id.btn_signup_login);
        signInButton = findViewById(R.id.btn_signin_login);
    }//inicializateView

    /**
     *
     * @return
     */
    private JSONObject buildJson() {

        JSONObject json = new JSONObject();
        JSONObject user = new JSONObject();

        try {
            String email = emailText.getText().toString();
            String password = passwordText.getText().toString();

            json.put("email", email);
            json.put("password", password);
            user.put("user", json);

        } catch (JSONException e) {
            throwToast("ERROR. Vuelva a intentarlo");
        }

        return user;
    }//makeJson

    /**
     *
     * @param data
     */
    private void throwEvent(JSONObject data) {

        EventDispatcher.getInstance(getApplicationContext())
        .dispatchEvent(Event.SIGNIN, data)
        .addOnCompleteListener(new OnCompleteListener<HashMap<String, String>>() {
            @Override
            public void onComplete(@NonNull Task<HashMap<String, String>> task) {

                if (!task.isSuccessful() || task.getResult() == null) {
                    changeVisibility(pBar, relFormLogin);
                    throwToast("Error de conexion");
                }
                else if (task.getResult().containsKey("error")) {

                    changeVisibility(pBar, relFormLogin);

                    switch (Objects.requireNonNull(task.getResult().get("error"))) {

                        case "incorrectSignin":
                        case "userDoesntExists":
                            throwToast("Usuario/contraseña incorrecto");
                            break;

                        case "server":
                            throwToast("Error del servidor");
                            break;

                        default:
                            throwToast("Error desconocido: " + task.getResult().get("error"));
                            break;
                    }//switch

                }// else if
                else {

                    HashMap<String, ?> p = task.getResult();

                    email = Objects.requireNonNull(p.get("email")).toString();
                    password = Objects.requireNonNull(p.get("password")).toString();
                    name = Objects.requireNonNull(p.get("name")).toString();
                    surname = Objects.requireNonNull(p.get("surname")).toString();
                    type = Objects.requireNonNull(p.get("type")).toString();
                    phone = Integer.parseInt(Objects.requireNonNull(p.get("number")).toString());

                    parserTypePerson();
                    Person user = new Person(email, password, name, surname, phone, typePerson);

                    Intent logIntent = new Intent(LoginActivity.this, nextClass);
                    logIntent.putExtra("user", user);
                    startActivity(logIntent);
                }
            }//onComplete

            /**
             *
             */
            private void parserTypePerson() {

                switch (type) {
                    case "passenger":
                        typePerson = TypePerson.USER;
                        nextClass = PassengerMain.class;
                        break;

                    case "driver":
                        typePerson = TypePerson.DRIVER;
                        nextClass = DriverMain.class;
                        break;

                    default:
                        typePerson = TypePerson.ADMIN;
                        nextClass = AdminMain.class;
                        break;
                }//switch
            }
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

        switch (v.getId()) {
            case R.id.btn_signup_login:
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                break;

            case R.id.btn_signin_login:

                if (emailText.getText().toString().isEmpty()) empty = true;
                if (passwordText.getText().toString().isEmpty()) empty = true;

                if (!empty) {
                    changeVisibility(relFormLogin, pBar);
                    JSONObject user = buildJson();
                    throwEvent(user);
                }
                else throwToast("Introduzca todos los datos");
                break;

            default:
                break;
        }//switch
    }//onClick
}
