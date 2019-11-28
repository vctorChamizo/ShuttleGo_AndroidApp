package tfg.shuttlego.model.event;

import android.content.Context;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.functions.FirebaseFunctions;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Objects;

public class EventDispatcher {

    private static EventDispatcher ourInstance = null;
    private FirebaseFunctions mFunctions;

    private EventDispatcher() {}

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
     * Manager the petitions to server.
     *
     * @param event The name of event to make a petition.
     * @param data The data object with the information.
     *
     * @return The promise threw to server.
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
            case ADDTOROUTE: return throwEvent("addToRoute", data);
            case GETROUTEBYID: return throwEvent("getRouteById", data);
            case DELETEROUTEBYID: return throwEvent("removeRoute", data);
            case REMOVEPASSENGERFROMROUTE: return throwEvent("removePassengerFromRoute", data);
            case GETALLROUTESBYUSER: return throwEvent("getRoutesByUser", data);
            case GETROUTEPOINTS: return throwEvent("getRoutePoints",data);
        }

        return null;
    }

    /**
     * Throw the event to get as parameter to make the petition to server.
     *
     * @param nameFunction The name to execute function in server.
     * @param data The data object with the information.
     *
     * @return The promise threw to server.
     */
    private Task<HashMap<String,String>> throwEvent(String nameFunction, JSONObject data){

        return this.mFunctions
        .getHttpsCallable(nameFunction).call(data)
        .continueWith(task-> (HashMap<String,String>)Objects.requireNonNull(task.getResult()).getData());
    }
}
