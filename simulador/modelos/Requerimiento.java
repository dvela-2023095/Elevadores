package simulador.modelos;

public class Requerimiento {
    private int pisoDestino;

    public Requerimiento(int pisoDestino){
        this.pisoDestino=pisoDestino;
    }

    public int getDestino(){
        return this.pisoDestino;
    }
}
