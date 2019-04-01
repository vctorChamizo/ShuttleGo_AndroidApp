package tfg.shuttlego.activities.account;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
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

    private ProgressBar loginMainProgress;
    private ScrollView loginMainScroll;
    private EditText loginMainTextEmail, loginMainTextPassword;
    private Button loginMainButtonSignin, loginMainButtonRegister;
    private Class loginMainNextClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_login);

        inicializateView();

        this.loginMainButtonRegister.setOnClickListener(this);
        this.loginMainButtonSignin .setOnClickListener(this);
    }

    /**
     * Inicializate the components of this view
     */
    private void inicializateView() {

        this.loginMainScroll = findViewById(R.id.main_login_scroll);
        this.loginMainProgress = findViewById(R.id.main_login_progress);
        this.loginMainTextEmail = findViewById(R.id.main_login_email);
        this.loginMainTextPassword = findViewById(R.id.main_login_password);

        this.loginMainButtonRegister = findViewById(R.id.main_login_register_btn);
        this.loginMainButtonSignin = findViewById(R.id.main_login_signin_btn);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    /**
     * Show the progress bar component visible and put invisble the rest of the view
     */
    private void setProgressBar () {
        this.loginMainProgress.setVisibility(View.VISIBLE);
        this.loginMainScroll.setVisibility(View.GONE);
    }

    /**
     * Show the view visible and put invisble progress bar component
     */
    private void removeProgressBar () {
        this.loginMainProgress.setVisibility(View.GONE);
        this.loginMainScroll.setVisibility(View.VISIBLE);
    }

    /**
     * Build a JSON for to allow make a create the credential to login a user.
     *
     * @return JSON with information to login a user.
     */
    private JSONObject buildJson() {

        JSONObject json = new JSONObject();
        JSONObject user = new JSONObject();

        try {
            String email = this.loginMainTextEmail.getText().toString();
            String password = this.loginMainTextPassword.getText().toString();

            json.put("email", email);
            json.put("password", password);
            user.put("user", json);

        } catch (JSONException e) { throwToast(R.string.err); }

        return user;
    }

    /**
     * Throw the event that allow to check the user credentials and start the application.
     *
     * @param data JSON with information to check a login user.
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
                    case "server": throwToast(R.string.errServer); break;
                }
            }
            else {

                Person user = parserTypePerson(task.getResult());
                Session.getInstance(getApplicationContext()).setUser(user);
                startActivity(new Intent(LoginMain.this, this.loginMainNextClass));
                finish();
            }
        });
    }

    /**
     * Parser the credential to the new user.
     *
     * @param p data to build a user that will use the application
     *
     * @return  user object whit data to current user.
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
                this.loginMainNextClass = PassengerMain.class;
                break;

            case "driver":
                typePerson = TypePerson.DRIVER;
                this.loginMainNextClass = DriverMain.class;
                break;

            default:
                typePerson = TypePerson.ADMIN;
                this.loginMainNextClass = AdminMain.class;
                break;
        }

        return new Person(email, password, name, surname, phone, typePerson, id);
    }

    private void throwToast(int msg) { Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show(); }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.main_login_register_btn:
                startActivity(new Intent(LoginMain.this, RegisterMain.class));
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                break;

            case R.id.main_login_signin_btn:

                if (this.loginMainTextEmail.getText().toString().isEmpty() ||
                    this.loginMainTextPassword.getText().toString().isEmpty()) throwToast(R.string.errDataEmpty);
                else {
                    setProgressBar();
                    throwEventLoginUser(buildJson());
                }
                break;
        }
    }

    @Override
    public void onBackPressed() { finish(); }
}
