package es.tfg.shuttle.logic.events;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import org.json.JSONObject;


public class EventDispatcher {

    private static EventDispatcher ourInstance = null;
    private FirebaseFunctions mFunctions;


    private EventDispatcher() {}


    public static EventDispatcher getInstance() {

        if(ourInstance == null){

            ourInstance = new EventDispatcher();
            ourInstance.mFunctions = FirebaseFunctions.getInstance();
        }

        return ourInstance;
    }


    public Task<String> dispatchEvent(Event event, JSONObject data){

        switch(event){

            case SIGNIN:

                return this.mFunctions
                        .getHttpsCallable("addMessage")
                        .call(data)
                        .continueWith(new Continuation<HttpsCallableResult, String>() {
                            @Override
                            public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {

                                String result = (String) task.getResult().getData();

                                return result;
                            }
                        });
        }//switch

        return null;
    }
}
