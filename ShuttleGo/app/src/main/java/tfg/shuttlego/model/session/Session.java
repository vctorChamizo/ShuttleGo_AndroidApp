package tfg.shuttlego.model.session;

import org.json.JSONObject;


/**
 * Authenticate user data while using the application.
 */
public class Session{

    private static final Session ourInstance = new Session();
    private JSONObject user = null;

    private Session() {}

    static Session getInstance() {
        return ourInstance;
    }

    public JSONObject getUser(){
        return user;
    }

    public void setUser(JSONObject user) {
        this.user = user;
    }
}
