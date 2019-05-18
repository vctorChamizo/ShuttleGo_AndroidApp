package tfg.shuttlego.activities.launcher;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import tfg.shuttlego.R;
import tfg.shuttlego.activities.account.LoginMain;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_splash);

        int DURATION_SPLASH = 3000;

        new Handler().postDelayed(() -> {

            Intent intent = new Intent(MainActivity.this, LoginMain.class);
            startActivity(intent);
            finish();
        },
        DURATION_SPLASH);
    }
}
