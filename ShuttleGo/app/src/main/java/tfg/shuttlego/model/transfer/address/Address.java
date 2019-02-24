package tfg.shuttlego.model.transfer.address;

import com.mapbox.geojson.Point;

import java.util.List;

public class Address {
    private String fullAddress;
    private int postalCode;
    private Point point;

    public Address(String fullAddress, String postalCode, Point point){
        this.fullAddress = fullAddress;
        this.postalCode = Integer.parseInt(postalCode);
        this.point = point;
    }
    public Address(String fullAddress,int postalCode){
        this.fullAddress = fullAddress;
        this.postalCode = postalCode;
    }

    public void setAddress(String Address){
        this.fullAddress = Address;
    }

    public void setPostaCode(int postalCode){
        this.postalCode = postalCode;
    }

    public String getAddress(){
        return this.fullAddress;
    }

    public int getPostalCode() {
        return postalCode;
    }

    public List<Double> getCoordinates(){return this.point.coordinates();};
}
