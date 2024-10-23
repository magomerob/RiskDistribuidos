package servidor;

import java.io.*;
import java.net.*;

public class Servidor {

    public static void main(String[] args) {
        int puerto = 12345; // Puerto en el que el servidor escuchará las conexiones
        try (ServerSocket serverSocket = new ServerSocket(puerto)) {
            System.out.println("Servidor escuchando en el puerto " + puerto);

            while (true) {
                // Espera la conexión del cliente
                Socket clientSocket = serverSocket.accept();
                System.out.println("Cliente conectado");

                // Manejar la conexión en un nuevo hilo
                new Thread(new ClientHandler(clientSocket)).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

// Clase que maneja la comunicación con un cliente
class ClientHandler implements Runnable {
    private Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try (
            BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String mensaje;
            while ((mensaje = input.readLine()) != null) {
                System.out.println("Recibido del cliente: " + mensaje);
                // Respuesta al cliente
                output.println("Servidor recibió: " + mensaje);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
