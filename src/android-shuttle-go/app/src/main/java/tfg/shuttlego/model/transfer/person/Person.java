package tfg.shuttlego.model.transfer.person;

import java.io.Serializable;

public class Person implements Serializable {

    private String name;
    private String surname;
    private int phone;
    private String email;
    private String password;
    private TypePerson type;
    private String id;

    public Person (String email, String password, String name, String surname, int phone, TypePerson type){

        this.email = email;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.phone = phone;
        this.type = type;
    }

    public Person (String email, String password, String name, String surname, int phone, TypePerson type, String id){

        this.email = email;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.phone = phone;
        this.type = type;
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public String getSurname() {
        return this.surname;
    }

    public int getPhone() {
        return this.phone;
    }

    public String getEmail() { return this.email; }

    public String getPassword() {
        return this.password;
    }

    public TypePerson getType() {
        return this.type;
    }

    public String getId() { return this.id; }


    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setType(TypePerson type) {
        this.type = type;
    }

    public void setType(String id) { this.id = id; }
}
