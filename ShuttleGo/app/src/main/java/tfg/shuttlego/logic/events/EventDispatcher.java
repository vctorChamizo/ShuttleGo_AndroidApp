package tfg.shuttlego.logic.events;

import android.support.annotation.NonNull;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import org.json.JSONObject;
import java.util.HashMap;


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


    public Task<HashMap<String,String>> dispatchEvent(Event event, JSONObject data){

        switch(event){

            case SIGNIN:

                return this.mFunctions
                        .getHttpsCallable("signin")
                        .call(data)
                        .continueWith(new Continuation<HttpsCallableResult, HashMap<String, String>>() {
                            @Override
                            public HashMap<String, String> then(@NonNull Task<HttpsCallableResult> task) {
                                return  (HashMap<String,String>)task.getResult().getData();
                            }
                        });//signin

            case SIGNUP:
                break;

            case SIGNOUT:
                break;

            default: return null;
        }//switch

        return null;
    }
}
