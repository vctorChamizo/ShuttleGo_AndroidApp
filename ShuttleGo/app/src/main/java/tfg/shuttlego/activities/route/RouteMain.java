package tfg.shuttlego.activities.route;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import tfg.shuttlego.R;
import tfg.shuttlego.activities.person.driver.DriverMain;
import tfg.shuttlego.model.event.Event;
import tfg.shuttlego.model.event.EventDispatcher;

public class RouteMain extends AppCompatActivity implements View.OnClickListener {

    private Button routeMainRemoveButton, routeMainCloseButton;
    private TextView routeMainExit, routeMainArrive, routeMainPassenger, routeMainDriver, routeMainPhone;
    private LinearLayout routeMainLinear;
    private ProgressBar routeMainProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_main);

        String route = (String)Objects.requireNonNull(getIntent().getExtras()).getSerializable("route");
        inicializateView();
        setProgressBar();
        throwEventGetRoute(buildJson(route));
        listeners();

    }

    private void inicializateView() {

        routeMainLinear = findViewById(R.id.route_main_linear);
        routeMainProgress = findViewById(R.id.route_main_progress);
        routeMainRemoveButton = findViewById(R.id.route_main_delete_btn);
        routeMainCloseButton = findViewById(R.id.route_main_close_btn);
        routeMainExit = findViewById(R.id.route_main_exit);
        routeMainArrive = findViewById(R.id.route_main_arrive);
        routeMainPassenger = findViewById(R.id.route_main_passengers);
        routeMainDriver = findViewById(R.id.route_main_driver);
        routeMainPhone = findViewById(R.id.route_main_phone);
    }

    /**
     *
     */
    private void setProgressBar () {

        routeMainProgress.setVisibility(View.VISIBLE);
        routeMainLinear.setVisibility(View.GONE);
    }//setProgressBar

    /**
     *
     */
    private void removeProgressBar () {

        routeMainProgress.setVisibility(View.GONE);
        routeMainLinear.setVisibility(View.VISIBLE);
    }//removeProgressBar

    /**
     *
     * @return
     */
    private JSONObject buildJson(String route) {

        JSONObject json = new JSONObject();
        JSONObject routeJson = new JSONObject();

        try {

            routeJson.put("id", route);
            json.put("route", routeJson);

        } catch (JSONException e) { throwToast(R.string.err);}

        return json;
    }//buildJson

    private void throwEventGetRoute(JSONObject route) {

        EventDispatcher.getInstance(getApplicationContext())
        .dispatchEvent(Event.GETROUTEBYID, route)
        .addOnCompleteListener(new OnCompleteListener<HashMap<String, String>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<HashMap<String, String>> task) {

                if (!task.isSuccessful() || task.getResult() == null) throwToast(R.string.errConexion);
                else if (task.getResult().containsKey("error")) throwToast(R.string.errServer);
                else {

                    String exit = task.getResult().get("origin");
                    String arrive = String.valueOf(task.getResult().get("destination"));
                    String passengers = String.valueOf(task.getResult().get("max"));
                    String phone = String.valueOf(task.getResult().get("driverNumber"));
                    String driverName = task.getResult().get("driverName");
                    String driverSurname = task.getResult().get("driverSurname");
                    String driverNameComplete = driverName + " " + driverSurname;
                    String driverEmail = task.getResult().get("driverEmail");
                    String driverId = task.getResult().get("driver");

                    routeMainExit.setText(routeMainExit.getText() + " " + exit);
                    routeMainArrive.setText(routeMainArrive.getText() + " " + arrive);
                    routeMainPassenger.setText(routeMainPassenger.getText() + " " + passengers);
                    routeMainDriver.setText(routeMainDriver.getText() + " " + driverNameComplete);
                    routeMainPhone.setText(routeMainPhone.getText() + " " + phone);

                    removeProgressBar();
                }
            }
        });
    }

    private void listeners() {

        routeMainRemoveButton.setOnClickListener(this);
        routeMainCloseButton.setOnClickListener(this);
    }

    /**
     *
     * @param msg
     */
    private void throwToast(int msg) { Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show(); }

    @Override
    public void onClick(View v) {

        boolean empty = false;

        switch (v.getId()) {

        }
    }
}
