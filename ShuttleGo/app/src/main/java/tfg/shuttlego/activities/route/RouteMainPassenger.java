package tfg.shuttlego.activities.route;

import android.view.View;
import android.widget.Button;

import tfg.shuttlego.R;

public class RouteMainPassenger extends RouteMain {
    private Button routeMainRemoveButton;
    @Override
    protected void inicializateView(){
        super.inicializateView();
        routeMainRemoveButton = findViewById(R.id.route_main_delete_btn);
        routeMainRemoveButton.setText("Aceptar");
    }

    @Override
    protected void listeners(){
        routeMainRemoveButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
    }
}
