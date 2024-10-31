package servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private ObjectOutputStream output;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try{
            this.output = new ObjectOutputStream(clientSocket.getOutputStream());

            BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
            System.out.println("Cliente conectado");

            Servidor.broadcastSalas();

            String newSala;

                // Read new items from the client and broadcast them
                while ((newSala = input.readLine()) != null) {
                    Servidor.salas.add(new Sala(newSala, 4));
                    Servidor.broadcastSalas();
                }

            clientSocket.close();
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

    protected void broadcast(List<Sala> salas) {
        try {
            output.reset();
            output.writeObject(salas);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
