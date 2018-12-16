package tfg.shuttlego.model.transfers.origin;


import java.io.Serializable;

public class Origin implements Serializable {

    private String id;
    private String name;

    public Origin() {}

    public Origin (String id, String name){

        this.id = id;
        this.name = name;
    }

    public String getId() { return id; }

    public String getName() { return name; }

    public void setId(String id) { this.id = id;    }

    public void setName(String name) {
        this.name = name;
    }
}
