package tfg.shuttlego.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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

import tfg.shuttlego.R;
import tfg.shuttlego.logic.events.Event;
import tfg.shuttlego.logic.events.EventDispatcher;

public class RegisterActivity extends AppCompatActivity {

    private ProgressBar pBar;
    private Button next1Button, next2Button, finishButton;
    private RelativeLayout relative1, relative2, relative3;
    private EditText emailText, nameText, surnameText, phoneText, passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        next1Button = findViewById(R.id.btn_next1_register);
        next2Button = findViewById(R.id.btn_next2_register);
        finishButton = findViewById(R.id.btn_finish_register);
        relative1 = findViewById(R.id.relative1_form_register);
        relative2 = findViewById(R.id.relative2_form_register);
        relative3 = findViewById(R.id.relative3_form_register);
        emailText = findViewById(R.id.et_email_register);
        nameText = findViewById(R.id.et_name_register);
        surnameText = findViewById(R.id.et_surname_register);
        phoneText = findViewById(R.id.et_phone_register);
        passwordText = findViewById(R.id.et_password_register);
        pBar = findViewById(R.id.progress);


        // Capture the email
        next1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation animationIn = AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.left_in);
                Animation animationOut = AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.left_out);
                relative1.startAnimation(animationOut);
                relative2.startAnimation(animationIn);
                relative1.setVisibility(View.GONE);
                relative2.setVisibility(View.VISIBLE);
            }
        });

        //Capture the name, surname and phone
        next2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation animationIn = AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.left_in);
                Animation animationOut = AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.left_out);
                relative2.startAnimation(animationOut);
                relative3.startAnimation(animationIn);
                relative2.setVisibility(View.GONE);
                relative3.setVisibility(View.VISIBLE);
            }
        });

        //Capture the password and call server.
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                relative3.setVisibility(View.GONE);
                pBar.setVisibility(View.VISIBLE);

                JSONObject json = new JSONObject();
                JSONObject user = new JSONObject();

                String email = emailText.getText().toString();
                String name = emailText.getText().toString();
                String surname = surnameText.getText().toString();
                Editable phone = phoneText.getText();               //Controlar bien el tipo de dato que devuelve.
                String password = passwordText.getText().toString();

                try {

                    json.put("email", email);
                    json.put("name", name);
                    json.put("surname", surname);
                    json.put("phone", phone);
                    json.put("password", password);
                    user.put("user", json);

                } catch (JSONException e) {

                    Toast.makeText(getApplicationContext(), "ERROR. Vuelva a intentarlo", Toast.LENGTH_SHORT).show();
                }

                EventDispatcher.getInstance(getApplicationContext())
                .dispatchEvent(Event.SIGNUP, user)
                .addOnCompleteListener(new OnCompleteListener<HashMap<String, String>>() {
                    @Override
                   public void onComplete(@NonNull Task<HashMap<String, String>> task) {

                        //Insercción de control de respuestas
                   }
                });

                startActivity(new Intent(RegisterActivity.this, WelcomeActivity.class));
                overridePendingTransition(R.anim.left_in, R.anim.left_out);


            }
        });
    }
}
