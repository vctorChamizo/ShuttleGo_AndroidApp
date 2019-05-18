package tfg.shuttlego.model.session;

import tfg.shuttlego.model.transfer.person.Person;

public class Session{

    private static Session ourInstance = null;
    private Person user = null;

    private Session() {}

    public static Session getInstance() {

        if(ourInstance == null) ourInstance = new Session();

        return ourInstance;
    }

    public Person getUser() { return this.user; }

    public void setUser(Person user) {this.user = user; }
}
