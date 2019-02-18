package tfg.shuttlego.model.transfer.adress;

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

    public void setDirection(String direction){
        this.fullDirection = direction;
    }

    public void setPostaCode(int postalCode){
        this.postalCode = postalCode;
    }

    public String getDirection(){
        return this.fullDirection;
    }

    public int getPostalCode() {
        return postalCode;
    }
}
