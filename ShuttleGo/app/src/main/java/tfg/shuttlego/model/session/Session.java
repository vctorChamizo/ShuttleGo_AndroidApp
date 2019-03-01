package tfg.shuttlego.model.session;

import android.content.Context;

import tfg.shuttlego.model.transfer.person.Person;

/**
 * Authenticate user data while using the application.
 */
public class Session{

    private static Session ourInstance = null;
    private Person user = null;

    private Session() {}

    public static Session getInstance(Context applicationContext) {

        if(ourInstance == null) ourInstance = new Session();

        return ourInstance;
    }

    public void setUser(Person user) {this.user = user; }
    public Person getUser() { return this.user; }
}
