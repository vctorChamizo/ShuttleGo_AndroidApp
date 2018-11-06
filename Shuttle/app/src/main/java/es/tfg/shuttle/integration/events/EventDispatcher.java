package es.tfg.shuttle.integration.events;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

class EventDispatcher {

    private static EventDispatcher ourInstance = null;
    private RequestQueue queue = null;
    private EventDispatcher() {};


    static EventDispatcher getInstance(Context context) {

        if(ourInstance == null){
            ourInstance = new EventDispatcher();
            ourInstance.queue = Volley.newRequestQueue(context);

        }

        return ourInstance;
    }


    public void dispatchEvent(Events event, JSONObject data){

        Request request = null;
        
        switch(event){
            case SIGNIN:

                request = new JsonObjectRequest(Request.Method.POST, "aqui hay que meter la url",data,

                    new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {


                    }
                });


            break;
        }

        this.queue.add(request);
    }
}
