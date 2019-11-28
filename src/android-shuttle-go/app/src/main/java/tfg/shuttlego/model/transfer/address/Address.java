package tfg.shuttlego.model.transfer.address;

import com.mapbox.geojson.Point;
import java.io.Serializable;
import java.util.List;

public class Address implements Serializable {

    private String fullAddress;
    private String postalCode;
    private Point point;

    public Address(String fullAddress, String postalCode, Point point){

        this.fullAddress = fullAddress;
        this.postalCode = postalCode;
        this.point = point;
    }

    public String getAddress(){
        return this.fullAddress;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public List<Double> getCoordinates(){return this.point.coordinates();}


    public void setPostalCode(String postalCode){ this.postalCode = postalCode; }
}
