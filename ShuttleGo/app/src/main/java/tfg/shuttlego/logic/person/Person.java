package tfg.shuttlego.logic.person;


import java.io.Serializable;

public class Person implements Serializable {

    private String name;
    private String surname;
    private int phone;
    private String email;
    private String password;
    private TypePerson type;


    public Person() {}

    public Person (String email, String password){

        this.email = email;
        this.password = password;
    }

    public Person (String email, String password, String name, String surname, int phone, TypePerson type){

        this.email = email;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.phone = phone;
        this.type = type;
    }


    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public int getPhone() {
        return phone;
    }

    public String getEmail() { return email; }

    public String getPassword() {
        return password;
    }

    public TypePerson getType() {
        return type;
    }

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
}
