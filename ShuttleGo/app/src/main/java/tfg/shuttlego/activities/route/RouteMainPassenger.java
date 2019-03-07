package tfg.shuttlego.activities.route;

import android.support.annotation.NonNull;
import android.text.Html;
import android.text.Spanned;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import tfg.shuttlego.R;
import tfg.shuttlego.model.event.Event;
import tfg.shuttlego.model.event.EventDispatcher;
import tfg.shuttlego.model.session.Session;
import tfg.shuttlego.model.transfer.address.Address;

public class RouteMainPassenger extends RouteMain implements View.OnClickListener {


    private String routeId;
    private Address userAddress;

    @Override
    protected void listeners() {

        routeMainRemoveButton.setOnClickListener(this);
        routeMainCloseButton.setOnClickListener(this);
    }

    @Override
    protected void setDataText(HashMap<?, ?> resultEvent) {
        this.routeMainRemoveButton.setText(getText(R.string.book));
        this.routeId = (String) resultEvent.get("id");
        this.userAddress = (Address) getIntent().getSerializableExtra("userAddress");
        String origin = routeMainOrigin.getText() + " " + resultEvent.get("origin");
        String limit = getString(R.string.destiny) + ": " + userAddress.getAddress().split(",")[0];

        int freePlaces = ((Integer)resultEvent.get("max"))-((Integer)resultEvent.get("passengersNumber"));
        Spanned  passengers;

        if(freePlaces == 0){
            passengers = Html.fromHtml(getText(R.string.freePlaces)+": <font color='#EE0000'>"+0+"</font>");
        }
        else passengers =  Html.fromHtml(getText(R.string.freePlaces)+": "+freePlaces);

        String phone = routeMainPhone.getText() + " " + String.valueOf(resultEvent.get("driverNumber"));
        String driverNameComplete = routeMainDriver.getText() + " " +
                resultEvent.get("driverSurname") + " " +
                resultEvent.get("driverName");

        routeMainOrigin.setText(origin);
        routeMainLimit.setText(limit);
        routeMainPassenger.setText(passengers);
        routeMainDriver.setText(driverNameComplete);
        routeMainPhone.setText(phone);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.route_main_delete_btn:
                throwAddToRoute();
                break;

            case R.id.route_main_close_btn: finish(); break;
        }
    }

    private void throwAddToRoute() {

        EventDispatcher.getInstance(getApplicationContext()).dispatchEvent(Event.ADDTOROUTE,buildJson(this.routeId,this.userAddress)).addOnCompleteListener(new OnCompleteListener<HashMap<String, String>>() {
            @Override
            public void onComplete(@NonNull Task<HashMap<String, String>> task) {

                removeProgressBar();

                if (!task.isSuccessful() || task.getResult() == null) {
                    throwToast(R.string.errConexion);
                }
                else if (task.getResult().containsKey("error")) {
                    if(task.getResult().get("error").equals("routeSoldOut")) throwToast(R.string.errSoldOut);
                    else if(task.getResult().get("error").equals("userAlreadyAdded"))throwToast(R.string.errUserAlreadyAdded);
                    else throwToast(R.string.errServer);
                }
                else {
                    throwToast(R.string.successfullyAdded);
                    finish();
                }
            }
        });
    }

    private JSONObject buildJson(String routeId,Address address) {

        JSONObject data = new JSONObject();

        try {
            JSONObject route = new JSONObject();
            route.put("id", routeId);

            JSONObject user = new JSONObject();
            user.put("email", Session.getInstance().getUser().getEmail());
            user.put("password", Session.getInstance().getUser().getPassword());

            data.put("user", user);
            data.put("route", route);
            data.put("address", address.getAddress());
            data.put("coordinates", address.getCoordinates().get(0) + "," + address.getCoordinates().get(1));


        } catch (JSONException e) {
            throwToast(R.string.err);
        }

        System.out.println(data.toString());
        return data;
    }
}
