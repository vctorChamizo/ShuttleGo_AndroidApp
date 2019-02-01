package tfg.shuttlego.model.maps;

public class Address {
    private String fullDirection;
    private int postalCode;

    public Address(String fullDirection,String postalCode){
        this.fullDirection = fullDirection;
        this.postalCode = Integer.parseInt(postalCode);
    }
    public Address(String fullDirection,int postalCode){
        this.fullDirection = fullDirection;
        this.postalCode = postalCode;
    }

    public String getDirection(){
        return this.fullDirection;
    }

    public int getPostalCode() {
        return postalCode;
    }
}
