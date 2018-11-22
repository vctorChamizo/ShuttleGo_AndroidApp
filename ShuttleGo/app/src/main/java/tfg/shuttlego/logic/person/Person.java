package tfg.shuttlego.logic.person;

public class Person {

    private String name;
    private String surname;
    private int number;
    private String email;
    private String password;
    private TypePerson type;


    public Person() {}

    public Person (String email, String password){

        this.email = email;
        this.password = password;
    }



    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public int getNumber() {
        return number;
    }

    public String getEmail() {
        return email;
    }

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

    public void setNumber(int number) {
        this.number = number;
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
