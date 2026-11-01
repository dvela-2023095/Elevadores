package simulador.modelos;

public class PeticionExterna extends Requerimiento{
    private String direccion;
    
    public PeticionExterna(int destino,String direccion){
        super(destino);
        this.direccion=direccion;
    }

    public String getDireccion(){
        return this.direccion;
    }
}
