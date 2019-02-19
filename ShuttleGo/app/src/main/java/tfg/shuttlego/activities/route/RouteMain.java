package tfg.shuttlego.activities.route;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import java.util.Objects;

import tfg.shuttlego.R;

public class RouteMain extends AppCompatActivity {

    String route;
    private Button reouteMainRemoveButton, reouteMainCloseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_card);

        route = (String)Objects.requireNonNull(getIntent().getExtras()).getSerializable("route");
        inicializateView();

    }

    private void inicializateView() {


    }
}
