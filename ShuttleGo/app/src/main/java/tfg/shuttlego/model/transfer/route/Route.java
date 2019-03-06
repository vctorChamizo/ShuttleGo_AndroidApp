package tfg.shuttlego.model.transfer.route;

import java.io.Serializable;

public class Route implements Serializable {

    private int destination, max;
    private String driver, origin;
    private int passengersNumber;
    private String id;

    public Route (String origin, int destination, String driver, int max) {

        this.origin = origin;
        this.destination = destination;
        this.driver = driver;
        this.max = max;

    }

    public Route (String id, String origin, int destination, String driver, int max, int passengersNumber) {

        this.origin = origin;
        this.destination = destination;
        this.driver = driver;
        this.max = max;
        this.passengersNumber = passengersNumber;
        this.id = id;
    }

    public String getOrigin() {
        return this.origin;
    }

    public int getDestination() {
        return this.destination;
    }

    public int getMax() {
        return this.max;
    }

    public String getDriver() { return this.driver; }

    public int getPassengerNumber() {
        return this.passengersNumber;
    }


    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public void setDestination(int destination) {
        this.destination = destination;
    }

    public void setDriver(String driver) { this.driver = driver; }

    public void setMax(int max) {
        this.max = max;
    }

    public void setPassengersNumber(int passengersNumber) { this.passengersNumber = passengersNumber; }

    public String getId(){ return this.id; }

}
