package tfg.shuttlego.model.event;

import android.content.Context;
import android.support.annotation.NonNull;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import org.json.JSONObject;
import java.util.HashMap;

/**
 * Access the business data to create the content of the view with respect to the returned data.
 */
public class EventDispatcher {

    private static EventDispatcher ourInstance = null;
    private FirebaseFunctions mFunctions;

    private EventDispatcher() {}

    /**
     *
     * @param applicationContext
     * @return
     */
    public static EventDispatcher getInstance(Context applicationContext) {

        if(ourInstance == null){

            ourInstance = new EventDispatcher();
            FirebaseApp.initializeApp(applicationContext);
            ourInstance.mFunctions = FirebaseFunctions.getInstance();
           //ourInstance.mFunctions.useFunctionsEmulator("http://10.0.2.2:8010");
        }
        return ourInstance;
    }

    /**
     *
     * @param event
     * @param data
     * @return
     */
    public Task<HashMap<String,String>> dispatchEvent(Event event, JSONObject data){

        switch(event){

            /* ACCOUNT */
            case SIGNIN: return throwEvent("signin", data);
            case SIGNUP: return throwEvent("signup", data);
            case SIGNOUT: break;

            /* ORIGIN */
            case GETORIGINS: return throwEvent("getAllOrigins", data);
            case CREATEORIGIN: return throwEvent("createOrigin", data);
            case GETORIGINBYID: return throwEvent("getOrigin", data);
            case GETORIGINBYNAME: return throwEvent("getOriginByName", data);
            case DELETEORIGIN: return throwEvent("deleteOrigin", data);
            case MODIFYORIGIN: return throwEvent("modifyOrigin", data);

            /* ROUTE */
            case CREATEROUTE: return throwEvent("createRoute", data);
            case SEARCHROUTE: return throwEvent("searchRoute", data);
            case ADDTOROUTE: return throwEvent("addRoute", data);
            case GETROUTEBYID: return throwEvent("getRouteById", data);
        }

        return null;
    }

    private Task<HashMap<String,String>> throwEvent(String nameFunction, JSONObject data){

        return this.mFunctions
        .getHttpsCallable(nameFunction).call(data)
        .continueWith(new Continuation<HttpsCallableResult, HashMap<String, String>>() {

            @Override
            public HashMap<String, String> then(@NonNull Task<HttpsCallableResult> task) {
                return  (HashMap<String,String>)task.getResult().getData();
            }
        });
    }//throwEvent
}
