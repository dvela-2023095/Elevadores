import elevator.*;
import java.util.*;
import simulador.modelos.*;

public class ElevatorManager implements Runnable{
    private List<Elevator> elevadores;
    private Queue<PeticionExterna> peticionesPendientes;
    boolean funcionando=true;

    public ElevatorManager(int nElevadores,int totalPisos, int tMovimiento, int tEspera){
        this.elevadores = new ArrayList<>(nElevadores);
        this.peticionesPendientes=new LinkedList<>();
        for (int i = 0; i < nElevadores; i++) {
            Elevator e = new Elevator(i+1,1,totalPisos,tMovimiento,tEspera);
            this.elevadores.add(e);
            new Thread(e).start();
        }
    }

    public List<Elevator> getElevadores(){
        return elevadores;
    }
    
    public void solicitarElevador(int pisoLLamada,String direccion){
        PeticionExterna nuevoPE=new PeticionExterna(pisoLLamada, direccion);
        peticionesPendientes.add(nuevoPE);
    }
    @Override
    public void run(){
        while(funcionando){
            try {
                if(!peticionesPendientes.isEmpty()){
                    asignarElevador(peticionesPendientes.poll());
                }
                Thread.sleep(200);
            } catch (Exception e) {
                System.out.println(e);
            }

        }
    }

    private void asignarElevador(PeticionExterna peticion){
        Elevator mejorElevador=null;
        for (Elevator elevador:elevadores) {
            if(elevador.getDireccion().equals(peticion.getDireccion())){
                switch(elevador.getDireccion()){
                    case"hacia arriba":
                        if(elevador.getPisoActual()<=peticion.getDestino()){
                            if(mejorElevador==null){
                                mejorElevador=elevador;
                            }else if(mejorElevador.getPisoActual()<elevador.getPisoActual()){
                                mejorElevador=elevador;
                            }
                        }
                    break;
                    case"hacia abajo":
                        if(elevador.getPisoActual()>=peticion.getDestino()){
                            if(mejorElevador==null){
                                mejorElevador=elevador;
                            }else if(mejorElevador.getPisoActual()>elevador.getPisoActual()){
                                mejorElevador=elevador;
                            }
                        }
                    break;
                }
            }

        }

        if(mejorElevador==null){
            for(Elevator elevador: elevadores){
                if(elevador.isLibre()){
                    mejorElevador=elevador;
                    break;
                }
            }
        }

        if(mejorElevador==null){
            for(Elevator elevador:elevadores){
                if(mejorElevador==null){
                    mejorElevador=elevador;
                }else if(mejorElevador.getCantComandos()>elevador.getCantComandos()){
                    mejorElevador=elevador;
                }
            }
        }

        mejorElevador.agregarComando(peticion);
    }

    public void resetear(){
        peticionesPendientes.clear();

        for(Elevator elevador: elevadores){
            elevador.reset();
        }
    }

    public void peticionInterna(int elevador,int destino){
        System.out.println(elevadores.size());
        if(elevador>0&&elevador<=elevadores.size()){
            Elevator cabina=elevadores.get(elevador-1);
            PeticionInterna peticion=new PeticionInterna(destino);
            cabina.agregarComando(peticion);
        }else{
            System.out.println("El elevador "+elevador+" no existe");
        }
    }

    public void detenerElevadores(){
        for(Elevator elevador: elevadores){
            elevador.detener();
        }
    }

}