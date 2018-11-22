package tfg.shuttlego.logic.session;

import org.json.JSONObject;

public class Session{

    private static final Session ourInstance = new Session();

    static Session getInstance() {
        return ourInstance;
    }

    private JSONObject user = null;

    private Session() {
    }

    public JSONObject getUser(){
        return user;
    }

    public void setUser(JSONObject user) {
        this.user = user;
    }
}
