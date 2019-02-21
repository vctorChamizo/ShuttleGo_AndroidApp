package tfg.shuttlego.model.transfer.adress;

public class Address {
    private String fullAddress;
    private int postalCode;

    public Address(String fullAddress,String postalCode){
        this.fullAddress = fullAddress;
        this.postalCode = Integer.parseInt(postalCode);
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
}
