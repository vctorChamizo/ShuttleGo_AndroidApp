package tfg.shuttlego.model.maps;

public class Direction {
    private String fullDirection;
    private int postalCode;

    public Direction(String fullDirection,String postalCode){
        this.fullDirection = fullDirection;
        this.postalCode = Integer.parseInt(postalCode);
    }
    public Direction(String fullDirection,int postalCode){
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
