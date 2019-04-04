package tfg.shuttlego.activities.account;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Objects;
import tfg.shuttlego.R;
import tfg.shuttlego.activities.person.driver.DriverMain;
import tfg.shuttlego.activities.person.passenger.PassengerMain;
import tfg.shuttlego.model.event.Event;
import tfg.shuttlego.model.event.EventDispatcher;
import tfg.shuttlego.model.session.Session;
import tfg.shuttlego.model.transfer.person.Person;
import tfg.shuttlego.model.transfer.person.TypePerson;

public class RegisterMain extends AppCompatActivity implements View.OnClickListener {

    private ProgressBar registerMainProgress;
    private LinearLayout registerMainLinear1, registerMainLinear2;
    private EditText registerMainTextEmail, registerMainTextPassword, registerMainTextName, registerMainTextSurname, registerMainTextPhone;
    private RadioButton registerMainRBDriver, registerMainRBPassenger;
    private Button registerMainButtonNext, registerMainButtonFinish;
    private Class registerMainNextClass;
    private Animation registerMainAnimationLeftIn, registerMainAnimationLeftOut, registerMainAnimationRightIn, registerMainAnimationRightOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_register);

        inicializateView();

        this.registerMainButtonNext.setOnClickListener(this);
        this.registerMainButtonFinish.setOnClickListener(this);
    }

    /**
     * Inicializate the componentes of this view.
     */
    private void inicializateView() {

        this.registerMainProgress = findViewById(R.id.main_register_progress);
        this.registerMainLinear1 = findViewById(R.id.main_register_linear1);
        this.registerMainLinear2 = findViewById(R.id.main_register_linear2);

        this.registerMainButtonNext = findViewById(R.id.main_register_next_btn);
        this.registerMainButtonFinish = findViewById(R.id.main_register_finish_btn);

        this.registerMainTextEmail = findViewById(R.id.main_register_email);
        this.registerMainTextPassword = findViewById(R.id.main_register_password);
        this.registerMainTextName = findViewById(R.id.main_register_name);
        this.registerMainTextSurname = findViewById(R.id.main_register_surname);
        this.registerMainTextPhone = findViewById(R.id.main_register_phone);
        this.registerMainRBDriver = findViewById(R.id.main_register_driver_rb);
        this.registerMainRBPassenger = findViewById(R.id.main_register_passenger_rb);

        this.registerMainAnimationLeftIn = AnimationUtils.loadAnimation(RegisterMain.this, R.anim.left_in);
        this.registerMainAnimationLeftOut = AnimationUtils.loadAnimation(RegisterMain.this, R.anim.left_out);

        this.registerMainAnimationRightIn = AnimationUtils.loadAnimation(RegisterMain.this, R.anim.right_in);
        this.registerMainAnimationRightOut = AnimationUtils.loadAnimation(RegisterMain.this, R.anim.right_out);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    /**
     * Show the progress bar component visible and put invisble the rest of the view.
     */
    private void setProgressBar () {
        this.registerMainProgress.setVisibility(View.VISIBLE);
        this.registerMainLinear2.setVisibility(View.GONE);
    }

    /**
     * Show the view visible and put invisble progress bar component.
     */
    private void removeProgressBar () {
        this.registerMainProgress.setVisibility(View.GONE);
        this.registerMainLinear2.setVisibility(View.VISIBLE);
    }

    /**
     * It makes visible the layout that enters as a parameter.
     */
    private void setVisible (View v) { v.setVisibility(View.VISIBLE);}

    /**
     * It makes invisible the layout that enters as a parameter.
     */
    private void setGone (View v) { v.setVisibility(View.GONE);}

    /**
     * Start the animation between with layouts that enters as a parameter.
     */
    private void startLeftAnimation(View rOut, View rIn) {

        rOut.startAnimation(this.registerMainAnimationLeftOut);
        rIn.startAnimation(this.registerMainAnimationLeftIn);
    }

    /**
     * Start the animation between with layouts that enters as a parameter.
     */
    private void startRightAnimation(View rOut, View rIn) {

        rOut.startAnimation(this.registerMainAnimationRightOut);
        rIn.startAnimation(this.registerMainAnimationRightIn);
    }

    /**
     * Build a JSON for to allow make a create a new user
     *
     * @return JSON with information to create user
     */
    private JSONObject buildJson() {

        String type;
        JSONObject json = new JSONObject();
        JSONObject user = new JSONObject();

        try {
            String email = this.registerMainTextEmail.getText().toString();
            String name = this.registerMainTextName.getText().toString();
            String surname = this.registerMainTextSurname.getText().toString();
            String password = this.registerMainTextPassword.getText().toString();

            Integer phone = Integer.parseInt(this.registerMainTextPhone.getText().toString());

            if (this.registerMainRBDriver.isChecked()) type = "driver";
            else type = "passenger";

            json.put("email", email);
            json.put("name", name);
            json.put("surname", surname);
            json.put("number", phone);
            json.put("password", password);
            json.put("type", type);
            user.put("user", json);
        }
        catch (NumberFormatException e) { throwToast(R.string.errPhoneNumber); }
        catch (JSONException e) { throwToast(R.string.err); }

        return user;
    }

    /**
     * Throw the event that allow create a new user
     *
     * @param data JSON with information to create a user
     */
    private void throwEventRegisterUser(JSONObject data) {

        EventDispatcher.getInstance(getApplicationContext())
        .dispatchEvent(Event.SIGNUP, data)
        .addOnCompleteListener(task -> {

            if (!task.isSuccessful() || task.getResult() == null) {
                removeProgressBar();
                throwToast(R.string.errConexion);
            }
            else if (task.getResult().containsKey("error")) {

                removeProgressBar();

                switch (Objects.requireNonNull(task.getResult().get("error"))) {
                    case "badRequestForm": throwToast(R.string.errBadFormat); break;
                    case "userAlreadyExists": throwToast(R.string.errUserExisit);break;
                    case "server": throwToast(R.string.errServer);break;
                }
            }
            else {

                try {
                    Session.getInstance(getApplicationContext()).setUser(parserTypePerson(data));
                    startActivity(new Intent(RegisterMain.this, this.registerMainNextClass));
                    finish();
                }
                catch (JSONException e) {
                    throwToast(R.string.err);
                    removeProgressBar();
                }
            }
        });
    }

    /**
     * Parser the credential to the new user.
     *
     * @param data data to build a user that will use the application
     *
     * @return  user object whit data to current user.
     */
    private Person parserTypePerson(JSONObject data) throws JSONException {

        TypePerson typePerson = null;
        JSONObject user = data.getJSONObject("user");

        switch (user.getString("type")) {

            case "passenger":
                typePerson = TypePerson.USER;
                this.registerMainNextClass = PassengerMain.class;
                break;

            case "driver":
                typePerson = TypePerson.DRIVER;
                this.registerMainNextClass = DriverMain.class;
                break;
        }

        return new Person(user.getString("email"), user.getString("password"), user.getString("name"), user.getString("surname"), user.getInt("number"), typePerson);
    }

    private void throwToast(int msg) { Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show(); }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.main_register_next_btn:

                if (this.registerMainTextEmail.getText().toString().isEmpty() ||
                    this.registerMainTextPassword.getText().toString().isEmpty() ||
                    (!this.registerMainRBDriver.isChecked() && !this.registerMainRBPassenger.isChecked())) throwToast(R.string.errDataEmpty);
                else {

                    startLeftAnimation(this.registerMainLinear1, this.registerMainLinear2);
                    setGone(registerMainLinear1);
                    setVisible(registerMainLinear2);
                }
                break;

            case R.id.main_register_finish_btn:
                if (this.registerMainTextName.getText().toString().isEmpty() ||
                    this.registerMainTextSurname.getText().toString().isEmpty() ||
                    this.registerMainTextPhone.getText().toString().isEmpty()) throwToast(R.string.errDataEmpty);
                else {

                    setProgressBar();
                    JSONObject user = buildJson();
                    throwEventRegisterUser(user);
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {

        if (this.registerMainLinear2.getVisibility() == View.VISIBLE) {

            startRightAnimation(this.registerMainLinear2, this.registerMainLinear1);
            setGone(registerMainLinear2);
            setVisible(registerMainLinear1);
        }
        else {
            finish();
            overridePendingTransition(R.anim.left_in, R.anim.left_out);
        }
    }
}
