package servidor;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import protocolo.Mensaje;
import protocolo.Protocolo;
public class Servidor {

    private static final int puerto = 42000;

    private static List<ClientHandler> clients = new CopyOnWriteArrayList<>();
    protected static List<Sala> salas = new CopyOnWriteArrayList<>();
    private static ConcurrentHashMap<String, List<ClientHandler>> clientesEnSala;
    private static ServerSocket serverSocket;
    private static ExecutorService pool;

    public static void main(String[] args) {
        iniciarServidor();
    }

    private static void iniciarServidor(){
        
        pool = Executors.newCachedThreadPool();
        clientesEnSala = new ConcurrentHashMap<String, List<ClientHandler>>();
        try (ServerSocket _serverSocket = new ServerSocket(puerto)) {
            serverSocket = _serverSocket;
            System.out.println("Servidor iniciado en el puerto " + puerto);
            
            while (!Thread.interrupted()) {
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
                clientesEnSala.replace(nombreSala, l);
                cliente.broadcast(""); //Itera el bucle de lectura de LobbyView para salir de él
                actualizarSala(sala);
            }
        }
        broadcastSalas();
    }

    public static void actualizarSala(Sala s){
        List<ClientHandler> clientesSala = clientesEnSala.get(s.getNombre());
        for (ClientHandler ch : clientesSala) {
            String msg = Mensaje.actualizarSala(s);
            ch.broadcast(msg);
        }
        if(s.getListos()==s.getCapacidad()){
            for (ClientHandler ch : clientesSala) {
                String msg = Protocolo.SALA_LISTA;
                ch.broadcast(msg);
                ch.cerrarConexion();
            }
            salas.remove(s);
            broadcastSalas();
        }
    }

    public static void actualizarListoSala(Jugador j, String nombreSala){
        for (int i = 0; i < salas.size(); i++) {
            Sala sala = salas.get(i);
            if(sala.getNombre().equals(nombreSala)){
                sala.updateListoJugador(j);
                if(j.isListo()){
                    sala.setListos(sala.getListos()+1);
                }else{
                    sala.setListos(sala.getListos()-1);
                }
                salas.set(i, sala);
                actualizarSala(sala);
            }
        }
    }

    public static void jugadorSaleSala(Jugador j, String s){
        for (int i = 0; i < salas.size(); i++) {
            Sala sala = salas.get(i);
            if(sala.getNombre().equals(s)){
                sala.remJugador(j);
                salas.set(i, sala);
                sala.setListos(sala.getListos()-1);
                actualizarSala(sala);
            }
        }
        broadcastSalas();
    }

    public static void broadcastMensaje(String nSala, String nJugador, String msg){
        List<ClientHandler> clientesSala = clientesEnSala.get(nSala);
        for (ClientHandler ch : clientesSala) {
            ch.broadcast(Protocolo.MENSAJE+Protocolo.DEL+nJugador+Protocolo.DEL+msg);
        }
    }
}
