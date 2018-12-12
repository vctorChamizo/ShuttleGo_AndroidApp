package tfg.shuttlego.activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
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

public class RegisterActivity extends AppCompatActivity {

    private ProgressBar pBar;
    private RelativeLayout relative1, relative2, relative3;
    private EditText emailText;
    private EditText passwordText;
    private EditText nameText;
    private EditText surnameText;
    private EditText phoneText;
    private RadioButton driverButton, passengerButton;
    private String email;
    private String name;
    private String surname;
    private int phone;
    private String password;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button next1Button = findViewById(R.id.btn_next1_register);
        Button next2Button = findViewById(R.id.btn_next2_register);
        Button finishButton = findViewById(R.id.btn_finish_register);
        relative1 = findViewById(R.id.relative1_form_register);
        relative2 = findViewById(R.id.relative2_form_register);
        relative3 = findViewById(R.id.relative3_form_register);
        emailText = findViewById(R.id.et_email_register);
        nameText = findViewById(R.id.et_name_register);
        surnameText = findViewById(R.id.et_surname_register);
        phoneText = findViewById(R.id.et_phone_register);
        passwordText = findViewById(R.id.et_password_register);
        driverButton = findViewById(R.id.rb1_register);
        passengerButton = findViewById(R.id.rb2_register);
        pBar = findViewById(R.id.progress);

        next1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean empty = false;

                if (emailText.getText().toString().isEmpty()) empty = true;
                if (!driverButton.isChecked() && !passengerButton.isChecked()) empty = true;
                if (!empty){
                    Animation animationIn = AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.left_in);
                    Animation animationOut = AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.left_out);
                    relative1.startAnimation(animationOut);
                    relative2.startAnimation(animationIn);
                    relative1.setVisibility(View.GONE);
                    relative2.setVisibility(View.VISIBLE);
                }
                else Toast.makeText(getApplicationContext(), "Introduzca todos los datos", Toast.LENGTH_SHORT).show();
            }
        });

        next2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean empty = false;

                if (nameText.getText().toString().isEmpty()) empty = true;
                if (surnameText.getText().toString().isEmpty()) empty = true;
                if (phoneText.getText().toString().isEmpty()) empty = true;
                if (!empty){
                    Animation animationIn = AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.left_in);
                    Animation animationOut = AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.left_out);
                    relative2.startAnimation(animationOut);
                    relative3.startAnimation(animationIn);
                    relative2.setVisibility(View.GONE);
                    relative3.setVisibility(View.VISIBLE);
                }
                else Toast.makeText(getApplicationContext(), "Introduzca todos los datos", Toast.LENGTH_SHORT).show();
            }
        });

        //Capture data and call server.
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            boolean empty = false;

            if (passwordText.getText().toString().isEmpty()) empty = true;
            if (!empty){

                relative3.setVisibility(View.GONE);
                pBar.setVisibility(View.VISIBLE);

                JSONObject json = new JSONObject();
                JSONObject user = new JSONObject();

                email = emailText.getText().toString();
                name = nameText.getText().toString();
                surname = surnameText.getText().toString();
                password = passwordText.getText().toString();

                try {

                    phone = Integer.parseInt(phoneText.getText().toString());
                }
                catch (NumberFormatException e){

                    Toast.makeText(getApplicationContext(), "Introduzca un telefono correcto", Toast.LENGTH_SHORT).show();
                }

                if (driverButton.isChecked()) type = "driver";
                else type = "passenger";

                try {

                    json.put("email", email);
                    json.put("name", name);
                    json.put("surname", surname);
                    json.put("number", phone);
                    json.put("password", password);
                    json.put("type", type);
                    user.put("user", json);

                } catch (JSONException e) {

                    relative1.setVisibility(View.VISIBLE);
                    relative3.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "ERROR. Vuelva a intentarlo", Toast.LENGTH_SHORT).show();
                }

                EventDispatcher.getInstance(getApplicationContext())
                .dispatchEvent(Event.SIGNUP, user)
                .addOnCompleteListener(new OnCompleteListener<HashMap<String, String>>() {
                    @Override
                    public void onComplete(@NonNull Task<HashMap<String, String>> task) {

                        if (!task.isSuccessful() || task.getResult() == null) {

                            relative3.setVisibility(View.VISIBLE);
                            pBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                        } else if (task.getResult().containsKey("error")) {

                            relative3.setVisibility(View.VISIBLE);
                            pBar.setVisibility(View.GONE);

                            switch (Objects.requireNonNull(task.getResult().get("error"))) {

                                case "badRequestForm":
                                    Toast.makeText(getApplicationContext(), "Formato de datos incorrecto", Toast.LENGTH_SHORT).show();
                                    break;

                                case "userAlreadyExists":
                                    Toast.makeText(getApplicationContext(), "Usuario ya existente", Toast.LENGTH_SHORT).show();
                                    break;

                                case "server":
                                    Toast.makeText(getApplicationContext(), "Error del servidor", Toast.LENGTH_SHORT).show();
                                    break;

                                default:
                                    Toast.makeText(getApplicationContext(), "Error desconocido: " + task.getResult().get("error"), Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }//else if
                        else {

                            TypePerson typePerson;
                            Class nextClass;

                            switch (type) {
                                case "passenger":
                                    typePerson = TypePerson.USER;
                                    //nextClass = PassengerStartActivity.class;
                                    break;

                                default:
                                    typePerson = TypePerson.DRIVER;
                                    //nextClass = DriverStartActivity.class;
                                    break;
                            }

                            Person user = new Person(email, password, name, surname, phone, typePerson);

                            /*
                            Intent logIntent = new Intent(RegisterActivity.this, nextClass);
                            logIntent.putExtra("user", user);
                            startActivity(logIntent);
                            */
                        }
                    }//onComlete
                });
            }//if
            else Toast.makeText(getApplicationContext(), "Introduzca una contraseña", Toast.LENGTH_SHORT).show();
        }//onClick
    });
    }
}
