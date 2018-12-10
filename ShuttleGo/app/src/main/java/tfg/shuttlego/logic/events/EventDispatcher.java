package tfg.shuttlego.logic.events;

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

    public static EventDispatcher getInstance(Context applicationContext) {


        if(ourInstance == null){

            ourInstance = new EventDispatcher();
            FirebaseApp.initializeApp(applicationContext);
            ourInstance.mFunctions = FirebaseFunctions.getInstance();

            //descomentar para llamar al servidor en local NO BORRAR!
            //ourInstance.mFunctions.useFunctionsEmulator("http://10.0.2.2:8010");
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
                return this.mFunctions
                        .getHttpsCallable("signup")
                        .call(data)
                        .continueWith(new Continuation<HttpsCallableResult, HashMap<String, String>>() {
                            @Override
                            public HashMap<String, String> then(@NonNull Task<HttpsCallableResult> task) {
                                return  (HashMap<String,String>)task.getResult().getData();
                            }
                        });//signin

            case SIGNOUT:
                break;

            default: return null;
        }//switch

        return null;
    }
}
