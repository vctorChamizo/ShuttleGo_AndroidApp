package es.tfg.shuttle.logic.events;

import android.content.Context;

import com.google.firebase.functions.FirebaseFunctions;

import org.json.JSONObject;

public class EventDispatcher {

    private static EventDispatcher ourInstance = null;
    private EventDispatcher() {};

    private FirebaseFunctions mFunctions;

    static EventDispatcher getInstance(Context ApplicationContext) {

        if(ourInstance == null){
            ourInstance = new EventDispatcher();
            ourInstance.mFunctions = FirebaseFunctions.getInstance();
        }

        return ourInstance;
    }


    public void dispatchEvent(Events event, JSONObject data){

        switch(event){
            case SIGNIN:
               // this.mFunctions.getHttpsCallable("signin").call(data).getResult
                break;
        }
    }
}
