package tfg.shuttlego.model.transfer.route;

public class Route {

    private int destination, max;
    private String driver, origin;

    public Route (String origin, int destination, String driver, int max) {

        this.origin = origin;
        this.destination = destination;
        this.driver = driver;
        this.max = max;

    }

    public String getOrigin() {
        return this.origin;
    }

    public String getDriver() { return this.driver; }

    public int getDestination() {
        return this.destination;
    }

    public int getMax() {
        return this.max;
    }

    public void setName(String origin) {
        this.origin = origin;
    }

    public void setSurname(int destination) {
        this.destination = destination;
    }

    public void setEmail(String driver) { this.driver = driver; }

    public void setPhone(int max) {
        this.max = max;
    }
}
