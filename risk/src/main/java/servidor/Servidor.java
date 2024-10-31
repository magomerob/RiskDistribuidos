package servidor;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Servidor {

    private static final int puerto = 42000;
    private static List<ClientHandler> clients = new CopyOnWriteArrayList<>();
    protected static List<Sala> salas = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        ExecutorService pool = Executors.newCachedThreadPool();
        
        salas.add(new Sala("Sala1", 2));

        try (ServerSocket serverSocket = new ServerSocket(puerto)) {
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

    public static void broadcastSalas(){
        for (ClientHandler clientHandler : clients) {
            clientHandler.broadcast(salas);
        }        
    }
}
