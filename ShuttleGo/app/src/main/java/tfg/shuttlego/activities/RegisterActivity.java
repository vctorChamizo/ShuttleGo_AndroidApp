package tfg.shuttlego.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.RelativeLayout;

import tfg.shuttlego.R;

public class RegisterActivity extends AppCompatActivity {

    private Button next1Button, next2Button, finishButton;
    private RelativeLayout relative1, relative2, relative3;

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

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                findViewById(R.id.relative3_form_register).setVisibility(View.GONE);
                findViewById(R.id.progress).setVisibility(View.VISIBLE);
                startActivity(new Intent(RegisterActivity.this, WelcomeActivity.class));
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
            }
        });
    }
}
