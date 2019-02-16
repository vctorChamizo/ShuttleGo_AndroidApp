package tfg.shuttlego.activities.launcher;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import tfg.shuttlego.R;
import tfg.shuttlego.activities.account.LoginActivity;

/**
 * Initial activity of the application.
 * It is responsible for generating the necessary view when the application starts.
 */
public class MainActivity extends AppCompatActivity {

    private final int DURATION_SPLASH = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_splash);

        new Handler().postDelayed(new Runnable(){
            public void run(){

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            };
        },DURATION_SPLASH);
    }
}
