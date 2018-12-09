package tfg.shuttlego.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import tfg.shuttlego.R;
import tfg.shuttlego.logic.person.Person;
import tfg.shuttlego.logic.person.TypePerson;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Person user = (Person)getIntent().getExtras().getSerializable("user");

        if (user.getType() == TypePerson.USER) startActivity(new Intent(StartActivity.this, PassengerStartActivity.class));
        else if (user.getType() == TypePerson.DRIVER) startActivity(new Intent(StartActivity.this, DriverStartActivity.class));
        else startActivity(new Intent(StartActivity.this, AdminStartActivity.class));

    }
}
