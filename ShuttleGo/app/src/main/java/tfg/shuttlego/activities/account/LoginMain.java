package tfg.shuttlego.activities.account;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Objects;

import tfg.shuttlego.R;
import tfg.shuttlego.activities.person.admin.AdminMain;
import tfg.shuttlego.activities.person.driver.DriverMain;
import tfg.shuttlego.activities.person.passenger.PassengerMain;
import tfg.shuttlego.model.event.Event;
import tfg.shuttlego.model.event.EventDispatcher;
import tfg.shuttlego.model.session.Session;
import tfg.shuttlego.model.transfer.person.Person;
import tfg.shuttlego.model.transfer.person.TypePerson;

public class LoginMain extends AppCompatActivity implements View.OnClickListener {

    private ProgressBar LoginMainProgress;
    private RelativeLayout LoginMainRelative;
    private EditText LoginMainTextEmail, LoginMainTextPassword;
    private Button LoginMainButtonSignin, LoginMainButtonRegister;
    private Class nextClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_login);

        inicializateView();

        LoginMainButtonRegister.setOnClickListener(this);
        LoginMainButtonSignin.setOnClickListener(this);
    }

    /**
     * Inicializate the componentes of this view
     */
    private void inicializateView() {

        LoginMainRelative = findViewById(R.id.main_login_relative);
        LoginMainProgress = findViewById(R.id.main_login_progress);
        LoginMainTextEmail = findViewById(R.id.main_login_email);
        LoginMainTextPassword = findViewById(R.id.main_login_password);
        LoginMainButtonRegister = findViewById(R.id.main_login_register_btn);
        LoginMainButtonSignin = findViewById(R.id.main_login_signin_btn);
    }

    /**
     * Show the progress bar component visible and put invisble the rest of the view
     */
    private void setProgressBar () {
        LoginMainProgress.setVisibility(View.VISIBLE);
        LoginMainRelative.setVisibility(View.GONE);
    }

    /**
     * Show the view visible and put invisble progress bar component
     */
    private void removeProgressBar () {
        LoginMainProgress.setVisibility(View.GONE);
        LoginMainRelative.setVisibility(View.VISIBLE);
    }

    private JSONObject buildJson() {

        JSONObject json = new JSONObject();
        JSONObject user = new JSONObject();

        try {
            String email = LoginMainTextEmail.getText().toString();
            String password = LoginMainTextPassword.getText().toString();

            json.put("email", email);
            json.put("password", password);
            user.put("user", json);

        } catch (JSONException e) { throwToast(R.string.err); }

        return user;
    }

    /**
     * Throw the event that allow to check the user credentials and start the application.
     *
     */
    private void throwEventLoginUser(JSONObject data) {

        EventDispatcher.getInstance(getApplicationContext())
        .dispatchEvent(Event.SIGNIN, data)
        .addOnCompleteListener(task -> {

            if (!task.isSuccessful() || task.getResult() == null) {

                removeProgressBar();
                throwToast(R.string.errConexion);
            }
            else if (task.getResult().containsKey("error")) {

                removeProgressBar();

                switch (Objects.requireNonNull(task.getResult().get("error"))) {
                    case "incorrectSignin": throwToast(R.string.errIncorrectSignin); break;
                    case "userDoesntExists": throwToast(R.string.errUserDontExist); break;
                    case "server": throwToast(R.string.errServer);break;
                }
            }
            else {

                Person user = parserTypePerson(task.getResult());
                Session.getInstance(getApplicationContext()).setUser(user);
                startActivity(new Intent(LoginMain.this, nextClass));
                finish();
            }
        });
    }

    /**
     * Parser the credential to the new user.
     *
     * @param p data to build a user that will use the application
     * @return the credentials to the curretn user.
     */
    private Person parserTypePerson (HashMap<String, ?> p) {

        TypePerson typePerson;

        String email = Objects.requireNonNull(p.get("email")).toString();
        String password = Objects.requireNonNull(p.get("password")).toString();
        String name = Objects.requireNonNull(p.get("name")).toString();
        String surname = Objects.requireNonNull(p.get("surname")).toString();
        Integer phone = Integer.parseInt(Objects.requireNonNull(p.get("number")).toString());
        String id = Objects.requireNonNull(p.get("id")).toString();

        String type = Objects.requireNonNull(p.get("type")).toString();

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
        }

        return new Person(email, password, name, surname, phone, typePerson, id);
    }

    private void throwToast(int msg) { Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show(); }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.main_login_register_btn: startActivity(new Intent(LoginMain.this, RegisterActivity.class)); break;

            case R.id.main_login_signin_btn:

                if (LoginMainTextEmail.getText().toString().isEmpty() ||
                    LoginMainTextPassword.getText().toString().isEmpty()) throwToast(R.string.errDataEmpty);
                else {

                    setProgressBar();
                    throwEventLoginUser(buildJson());
                }
                break;
        }
    }
}
