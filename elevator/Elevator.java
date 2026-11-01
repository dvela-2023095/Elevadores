package elevator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import simulador.modelos.*;

public class Elevator implements Runnable {

    private int ID;
    private String direccion;
    private int pisoActual;

    private int totalPisos;
    private int tiempoMovimiento;
    private int tiempoEspera;

    private Queue<Requerimiento> comandos;
    private boolean funcionando;

    private Logger logger;

    private boolean resetearon=false;

    public Elevator(int ID, int pisoActual, int totalPisos,
                    int tiempoMovimiento, int tiempoEspera) {

        this.ID = ID;
        this.pisoActual = pisoActual;
        this.totalPisos = totalPisos;
        this.tiempoMovimiento = tiempoMovimiento;
        this.tiempoEspera = tiempoEspera;

        this.direccion = "hacia arriba";
        this.comandos = new LinkedList<>();
        this.funcionando = true;

        configurarLogger();
    }

    // Configura los registros del elevador
    private void configurarLogger() {

        try {
            logger = Logger.getLogger("Elevador" + ID);

            logger.setUseParentHandlers(false);

            FileHandler archivo = new FileHandler(
                "elevador" + ID + ".log", true
            );

            archivo.setFormatter(new SimpleFormatter());

            ConsoleHandler consola = new ConsoleHandler();
            consola.setFormatter(new SimpleFormatter());

            logger.addHandler(archivo);
            logger.addHandler(consola);

        } catch (IOException e) {
            System.out.println(
                "Error al crear el archivo de registro del elevador " + ID
            );
        }
    }

    // Agrega un piso a la cola de comandos
    public synchronized void agregarComando(Requerimiento req) {
        int piso = req.getDestino();
        if (piso < 1 || piso > totalPisos) {
            System.out.println("El piso solicitado no es valido.");
            return;
        }

        comandos.add(req);

        logger.info("Nuevo comando recibido: visitar piso " + piso);
        notifyAll();
    }

    // Sube un piso
    private void subir() throws InterruptedException {

        int siguientePiso = pisoActual + 1;

        cambiarDireccion("hacia arriba");

        logger.info(
            "Se mueve del piso " + pisoActual +
            " hacia el piso " + siguientePiso
        );

        Thread.sleep(tiempoMovimiento);

        pisoActual = siguientePiso;

        logger.info("Llego al piso " + pisoActual);
    }

    // Baja un piso
    private void bajar() throws InterruptedException {

        int siguientePiso = pisoActual - 1;

        cambiarDireccion("hacia abajo");

        logger.info(
            "Se mueve del piso " + pisoActual +
            " hacia el piso " + siguientePiso
        );

        Thread.sleep(tiempoMovimiento);

        pisoActual = siguientePiso;

        logger.info("Llego al piso " + pisoActual);
    }

    // Registra los cambios de direccion
    private void cambiarDireccion(String nuevaDireccion) {

        if (!direccion.equals(nuevaDireccion)) {

            direccion = nuevaDireccion;

            logger.info("Cambio de direccion: " + direccion);
        }
    }

    // Ejecuta el movimiento del elevador
    @Override
    public void run() {

        logger.info("Elevador " + ID + " iniciado.");

        while (funcionando) {

            try {

                int destino;

                // Obtiene el siguiente comando
                synchronized (this) {

                    while (comandos.isEmpty() && funcionando &&!resetearon) {
                        wait();
                    }

                    if (!funcionando) {
                        break;
                    }

                    if(resetearon){
                        pisoActual=1;
                        cambiarDireccion("hacia arriba");
                        resetearon=false;
                        continue;
                    }

                    destino = comandos.peek().getDestino();
                }

                // Se mueve hasta llegar al destino
                while (pisoActual != destino && funcionando) {

                    if(comandos.isEmpty()){
                        break;
                    }

                    if (pisoActual < destino) {
                        subir();
                    } else {
                        bajar();
                    }
                }

                // Comprueba que llego al destino
                if (funcionando && pisoActual == destino && !comandos.isEmpty()) {

                    synchronized (this) {
                        comandos.poll();
                    }

                    logger.info(
                        "Parada en el piso " + pisoActual
                    );

                    Thread.sleep(tiempoEspera);
                }

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();
                break;
            }
        }

        logger.info("Elevador " + ID + " detenido.");
    }

    public synchronized void reset(){
        comandos.clear();
        resetearon=true;
        funcionando=true;
        pisoActual = 1;
        cambiarDireccion("hacia arriba");
        logger.info("RESET DEL SISTEMA: Elevador regresó al piso 1 y vació su cola de comandos");
        notifyAll();
    }

    // Detiene la ejecucion del elevador
    public synchronized void detener() {

        funcionando = false;
        notifyAll();
    }

    // Permite consultar el ID
    public int getID() {
        return ID;
    }

    // Permite consultar la direccion
    public synchronized String getDireccion() {
        return direccion;
    }

    // Permite consultar el piso actual
    public synchronized int getPisoActual() {
        return pisoActual;
    }

    // Muestra los comandos pendientes
    public synchronized void mostrarComandos() {
        System.out.println(
            "Comandos del elevador " + ID + ": " + comandos
        );
    }

    public boolean isLibre(){
        return this.comandos.isEmpty();
    }

    public int getCantComandos(){
        return this.comandos.size();
    }

    public synchronized List<Integer> getColaComandos(){
        List<Integer> destinos = new ArrayList<>();
        for(Requerimiento req: comandos){
            destinos.add(req.getDestino());
        }
        return destinos;
    }
}