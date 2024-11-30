package servidor;

import java.io.*;
import java.net.*;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import protocolo.Mensaje;
import protocolo.Protocolo;
public class Servidor {

    private static final int puerto = 42000;

    private static List<ClientHandler> clients = new CopyOnWriteArrayList<>();
    protected static List<Sala> salas = new CopyOnWriteArrayList<>();
    private static HashMap<String, List<ClientHandler>> clientesEnSala;
    private static ServerSocket serverSocket;
    private static ExecutorService pool;

    public static void main(String[] args) {
        iniciarServidor();
    }

    private static void iniciarServidor(){
        
        pool = Executors.newCachedThreadPool();
        clientesEnSala = new HashMap<String, List<ClientHandler>>();
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
            if(!clientHandler.isEnSala()){
                String msg = Mensaje.listaSalas(salas);
                clientHandler.broadcast(msg);
            }
        }        
    }

    protected static boolean crearSala(String nombre, int capacidad, ClientHandler cliente){
        Jugador jugador = cliente.getJugador();
        if(nombre.contains(Protocolo.DEL)){return false;}
        for (Sala sala : salas) {
            if(sala.getNombre().equals(nombre)){
                return false;
            }
        }
        Sala nuevaSala = new Sala(nombre, capacidad);
        salas.add(nuevaSala);
        broadcastSalas();
        List<ClientHandler> l = new ArrayList<>();
        clientesEnSala.put(nombre, l);
        return true;
    }

    protected static void unirClienteASala(ClientHandler cliente, String nombreSala){
        Jugador jugador = cliente.getJugador();
        for (int i = 0; i < salas.size(); i++) {
            Sala sala = salas.get(i);
            if(sala.getNombre().equals(nombreSala)){
                sala.addJugador(jugador);
                salas.set(i, sala);
                List<ClientHandler> l = clientesEnSala.get(sala.getNombre());
                l.add(cliente);
                clientesEnSala.put(nombreSala, l);
                actualizarSala(sala);
            }
        }
    }

    public static void actualizarSala(Sala s){
        List<ClientHandler> clientesSala = clientesEnSala.get(s.getNombre());
        for (ClientHandler clientHandler : clientesSala) {
            String msg = Mensaje.actualizarSala(s);
            clientHandler.broadcast(msg);
        }
    }
}
