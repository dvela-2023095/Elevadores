package elevator;

public class Main {

    public static void main(String[] args) {

        // Configuracion de la simulacion
        int totalPisos = 10;
        int tiempoMovimiento = 1000;
        int tiempoEspera = 2000;

        // Crear dos elevadores
        Elevator elevador1 = new Elevator(
            1, 1, totalPisos, tiempoMovimiento, tiempoEspera
        );

        Elevator elevador2 = new Elevator(
            2, 1, totalPisos, tiempoMovimiento, tiempoEspera
        );

        // Crear un hilo independiente para cada elevador
        Thread hilo1 = new Thread(elevador1);
        Thread hilo2 = new Thread(elevador2);

        // Iniciar los elevadores
        hilo1.start();
        hilo2.start();

        // Agregar comandos al primer elevador
        elevador1.agregarComando(5);
        elevador1.agregarComando(8);
        elevador1.agregarComando(3);

        // Agregar comandos al segundo elevador
        elevador2.agregarComando(7);
        elevador2.agregarComando(4);
        elevador2.agregarComando(2);

        // Mostrar los comandos pendientes
        elevador1.mostrarComandos();
        elevador2.mostrarComandos();
    }
}