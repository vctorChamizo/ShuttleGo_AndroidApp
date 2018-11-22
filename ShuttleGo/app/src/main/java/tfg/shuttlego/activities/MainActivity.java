package tfg.shuttlego.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import tfg.shuttlego.R;

/**
 * Initial activity of the application.
 * It is responsible for generating the necessary view when the application starts.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }
}
