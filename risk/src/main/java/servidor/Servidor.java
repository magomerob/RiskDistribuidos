package servidor;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import protocolo.Protocolo;
public class Servidor {

    private static final int puerto = 42000;

    private static List<ClientHandler> clients = new CopyOnWriteArrayList<>();
    protected static List<Sala> salas = new CopyOnWriteArrayList<>();
    private static ServerSocket serverSocket;
    private static ExecutorService pool;

    public static void main(String[] args) {
        iniciarServidor();
    }

    private static void iniciarServidor(){
        
        pool = Executors.newCachedThreadPool();

        try (ServerSocket _serverSocket = new ServerSocket(puerto)) {
            serverSocket = _serverSocket;
            System.out.println("Servidor iniciado en el puerto " + puerto);
            
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                pool.submit(clientHandler);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected static void broadcastSalas(){
        for (ClientHandler clientHandler : clients) {
            System.out.println("mandando enviar salas");
            clientHandler.broadcast(salas);
            if(!clientHandler.isEnSala()){
                
            }            
        }        
    }

    protected static boolean crearSala(String nombre, int capacidad, Socket usuario){
        if(nombre.contains(Protocolo.DEL)){return false;}
        for (Sala sala : salas) {
            if(sala.getNombre().equals(nombre)){
                return false;
            }
        }
        Sala nuevaSala = new Sala(nombre, capacidad);
        nuevaSala.addJugador(usuario);
        salas.add(nuevaSala);
        broadcastSalas();
        return true;
    }

    protected static void unirClienteASala(Socket cliente, String nombreSala){
        for (Sala sala : salas) {
            if(sala.getNombre().equals(nombreSala)){
                sala.addJugador(cliente);
            }
        }
    }
}
