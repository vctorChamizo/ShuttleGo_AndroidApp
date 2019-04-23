package tfg.shuttlego.model.transfer.origin;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Origin implements Serializable {

    private String id;
    private String name;
    private List<Double> coordinates = new ArrayList<Double>();

    public Origin() {}

    public Origin (String id, String name){

        this.id = id;
        this.name = name;
    }

    public Origin (String id, String name, String coordinates){

        this.id = id;
        this.name = name;

        for(String coordinate:coordinates.split(","))
            this.coordinates.add(Double.parseDouble(coordinate));

    }
    public String getId() { return id; }

    public String getName() { return name; }

    public void setId(String id) { this.id = id;    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Double> getCoordinates() { return coordinates; }

    public void setCoordinates(String coordinates) {

        for(String coordinate:coordinates.split(","))
            this.coordinates.add(Double.parseDouble(coordinate));
    }
}
